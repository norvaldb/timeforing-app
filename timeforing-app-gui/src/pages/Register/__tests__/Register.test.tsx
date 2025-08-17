import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { Register } from '../Register';
import { userService } from '@/services/userService';

// Mock the userService
vi.mock('@/services/userService', () => ({
  userService: {
    register: vi.fn(),
  },
}));

// Mock react-router-dom navigate
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

// Mock notification hook
const mockShowToast = vi.fn();
const mockError = vi.fn();
const mockSuccess = vi.fn();
const mockCustomError = vi.fn();

vi.mock('@/components/notifications/NotificationToast', () => ({
  useNotification: () => ({
    showToast: mockShowToast,
    error: mockError,
    success: mockSuccess,
    customError: mockCustomError,
  }),
}));

const MockedUserService = vi.mocked(userService);

const renderWithRouter = (component: React.ReactElement) => {
  return render(
    <BrowserRouter>
      {component}
    </BrowserRouter>
  );
};

describe('Register', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render registration form in Norwegian', () => {
    renderWithRouter(<Register />);
    
    expect(screen.getByRole('heading', { name: 'Opprett bruker' })).toBeInTheDocument();
    expect(screen.getByText('Fyll inn informasjonen din for å komme i gang')).toBeInTheDocument();
    
    // Check form fields by input names
    expect(screen.getByPlaceholderText('Skriv inn ditt navn')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('+47 123 45 678')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('din@epost.no')).toBeInTheDocument();
    
    // Check submit button
    expect(screen.getByRole('button', { name: 'Opprett bruker' })).toBeInTheDocument();
  });

  it('should render link to login page', () => {
    renderWithRouter(<Register />);
    
    expect(screen.getByText('Allerede bruker?')).toBeInTheDocument();
    
    const dashboardLink = screen.getByRole('button', { name: 'Gå til dashboard' });
    expect(dashboardLink).toBeInTheDocument();
  });

  it('should handle successful registration', async () => {
    const user = userEvent.setup();
    
    // Mock successful registration
    MockedUserService.register.mockResolvedValueOnce({
      id: '1',
      navn: 'Test Bruker',
      mobil: '+4741234567',
      epost: 'test@example.com',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    });

    renderWithRouter(<Register />);
    
    // Fill out the form using placeholder text
    await user.type(screen.getByPlaceholderText('Skriv inn ditt navn'), 'Test Bruker');
    await user.type(screen.getByPlaceholderText('+47 123 45 678'), '41234567');
    await user.type(screen.getByPlaceholderText('din@epost.no'), 'test@example.com');
    
    // Submit the form
    const submitButton = screen.getByRole('button', { name: 'Opprett bruker' });
    await user.click(submitButton);
    
    await waitFor(() => {
      expect(MockedUserService.register).toHaveBeenCalledWith({
        navn: 'Test Bruker',
        mobil: '+4741234567',
        epost: 'test@example.com',
      });
    });

    // Check success notification and navigation
    await waitFor(() => {
      expect(mockSuccess).toHaveBeenCalledWith('brukerOpprettet');
    });

    // Wait for the 1.5s navigation delay
    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/');
    }, { timeout: 2000 });
  });

  it('should handle registration error', async () => {
    const user = userEvent.setup();
    
    // Mock registration error
    MockedUserService.register.mockRejectedValueOnce({
      message: 'Epost addressen er allerede registrert',
      code: 'DUPLICATE_EMAIL',
      status: 409
    });

    renderWithRouter(<Register />);
    
    // Fill out the form using placeholder text
    await user.type(screen.getByPlaceholderText('Skriv inn ditt navn'), 'Test Bruker');
    await user.type(screen.getByPlaceholderText('+47 123 45 678'), '41234567');
    await user.type(screen.getByPlaceholderText('din@epost.no'), 'existing@example.com');
    
    // Submit the form
    const submitButton = screen.getByRole('button', { name: 'Opprett bruker' });
    await user.click(submitButton);
    
    await waitFor(() => {
      expect(MockedUserService.register).toHaveBeenCalled();
    });

    // Check error notification
    await waitFor(() => {
      expect(mockError).toHaveBeenCalledWith('epostAlleredeRegistrert');
      expect(mockNavigate).not.toHaveBeenCalled();
    });
  });

  it('should show loading state during registration', async () => {
    const user = userEvent.setup();
    
    // Mock slow registration
    MockedUserService.register.mockImplementationOnce(
      () => new Promise(resolve => setTimeout(resolve, 1000))
    );

    renderWithRouter(<Register />);
    
    // Fill out the form using placeholder text
    await user.type(screen.getByPlaceholderText('Skriv inn ditt navn'), 'Test Bruker');
    await user.type(screen.getByPlaceholderText('+47 123 45 678'), '41234567');
    await user.type(screen.getByPlaceholderText('din@epost.no'), 'test@example.com');
    
    // Submit the form
    const submitButton = screen.getByRole('button', { name: 'Opprett bruker' });
    await user.click(submitButton);
    
    // Check loading state - look for disabled button
    expect(screen.getByRole('button', { name: 'Opprett bruker' })).toBeDisabled();
  });

  it('should have proper accessibility attributes', () => {
    renderWithRouter(<Register />);
    
    // Check main heading (it's h2 not h1)
    const heading = screen.getByRole('heading', { name: 'Opprett bruker' });
    expect(heading).toHaveTextContent('Opprett bruker');
    
    // Check button accessibility
    const submitButton = screen.getByRole('button', { name: 'Opprett bruker' });
    expect(submitButton).toBeInTheDocument();
    
    // Check input fields are accessible
    expect(screen.getByLabelText('Navn')).toBeInTheDocument();
    expect(screen.getByLabelText('Mobilnummer')).toBeInTheDocument();
    expect(screen.getByLabelText('Epost')).toBeInTheDocument();
  });

  it('should handle network error gracefully', async () => {
    const user = userEvent.setup();
    
    // Mock network error
    MockedUserService.register.mockRejectedValueOnce(new Error('Network error'));

    renderWithRouter(<Register />);
    
    // Fill out the form using placeholder text
    await user.type(screen.getByPlaceholderText('Skriv inn ditt navn'), 'Test Bruker');
    await user.type(screen.getByPlaceholderText('+47 123 45 678'), '41234567');
    await user.type(screen.getByPlaceholderText('din@epost.no'), 'test@example.com');
    
    // Submit the form
    const submitButton = screen.getByRole('button', { name: 'Opprett bruker' });
    await user.click(submitButton);
    
    await waitFor(() => {
      expect(mockError).toHaveBeenCalledWith('noeGikkGalt');
    });
  });
});
