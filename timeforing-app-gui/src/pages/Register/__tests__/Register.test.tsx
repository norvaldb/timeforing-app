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
vi.mock('@/components/notifications/NotificationToast', () => ({
  useNotification: () => ({
    showToast: mockShowToast,
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
    expect(screen.getByText('Registrer deg for å komme i gang med timeføring.')).toBeInTheDocument();
    
    // Check form fields
    expect(screen.getByLabelText('Navn *')).toBeInTheDocument();
    expect(screen.getByLabelText('Mobilnummer *')).toBeInTheDocument();
    expect(screen.getByLabelText('Epost *')).toBeInTheDocument();
    
    // Check submit button
    expect(screen.getByRole('button', { name: 'Opprett bruker' })).toBeInTheDocument();
  });

  it('should render link to login page', () => {
    renderWithRouter(<Register />);
    
    expect(screen.getByText('Har du allerede en bruker?')).toBeInTheDocument();
    
    const loginLink = screen.getByRole('link', { name: 'Logg inn her' });
    expect(loginLink).toBeInTheDocument();
    expect(loginLink).toHaveAttribute('href', '/login');
  });

  it('should handle successful registration', async () => {
    const user = userEvent.setup();
    
    // Mock successful registration
    MockedUserService.register.mockResolvedValueOnce({
      id: '1',
      navn: 'Test Bruker',
      mobil: '+47 12345678',
      epost: 'test@example.com',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    });

    renderWithRouter(<Register />);
    
    // Fill out the form
    await user.type(screen.getByLabelText('Navn *'), 'Test Bruker');
    await user.type(screen.getByLabelText('Mobilnummer *'), '+47 12345678');
    await user.type(screen.getByLabelText('Epost *'), 'test@example.com');
    
    // Submit the form
    const submitButton = screen.getByRole('button', { name: 'Opprett bruker' });
    await user.click(submitButton);
    
    await waitFor(() => {
      expect(MockedUserService.register).toHaveBeenCalledWith({
        navn: 'Test Bruker',
        mobil: '+47 12345678',
        epost: 'test@example.com',
      });
    });

    // Check success notification and navigation
    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Bruker opprettet!', 'success');
      expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    });
  });

  it('should handle registration error', async () => {
    const user = userEvent.setup();
    
    // Mock registration error
    MockedUserService.register.mockRejectedValueOnce({
      response: {
        status: 409,
        data: { message: 'Email already exists' }
      }
    });

    renderWithRouter(<Register />);
    
    // Fill out the form
    await user.type(screen.getByLabelText('Navn *'), 'Test Bruker');
    await user.type(screen.getByLabelText('Mobilnummer *'), '+47 12345678');
    await user.type(screen.getByLabelText('Epost *'), 'existing@example.com');
    
    // Submit the form
    const submitButton = screen.getByRole('button', { name: 'Opprett bruker' });
    await user.click(submitButton);
    
    await waitFor(() => {
      expect(MockedUserService.register).toHaveBeenCalled();
    });

    // Check error notification
    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Epost addressen er allerede registrert', 'error');
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
    
    // Fill out the form
    await user.type(screen.getByLabelText('Navn *'), 'Test Bruker');
    await user.type(screen.getByLabelText('Mobilnummer *'), '+47 12345678');
    await user.type(screen.getByLabelText('Epost *'), 'test@example.com');
    
    // Submit the form
    const submitButton = screen.getByRole('button', { name: 'Opprett bruker' });
    await user.click(submitButton);
    
    // Check loading state
    expect(screen.getByRole('button', { name: 'Oppretter...' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Oppretter...' })).toBeDisabled();
  });

  it('should have proper accessibility attributes', () => {
    renderWithRouter(<Register />);
    
    // Check main heading
    const heading = screen.getByRole('heading', { level: 1 });
    expect(heading).toHaveTextContent('Opprett bruker');
    
    // Check form accessibility
    const form = screen.getByRole('form');
    expect(form).toBeInTheDocument();
    
    // Check link accessibility
    const loginLink = screen.getByRole('link', { name: 'Logg inn her' });
    expect(loginLink).toHaveAttribute('href', '/login');
  });

  it('should handle network error gracefully', async () => {
    const user = userEvent.setup();
    
    // Mock network error
    MockedUserService.register.mockRejectedValueOnce(new Error('Network error'));

    renderWithRouter(<Register />);
    
    // Fill out the form
    await user.type(screen.getByLabelText('Navn *'), 'Test Bruker');
    await user.type(screen.getByLabelText('Mobilnummer *'), '+47 12345678');
    await user.type(screen.getByLabelText('Epost *'), 'test@example.com');
    
    // Submit the form
    const submitButton = screen.getByRole('button', { name: 'Opprett bruker' });
    await user.click(submitButton);
    
    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Kunne ikke kontakte server', 'error');
    });
  });
});
