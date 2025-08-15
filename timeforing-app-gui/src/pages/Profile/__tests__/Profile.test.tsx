import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Profile } from '../Profile';
import { userService } from '@/services/userService';

// Mock the userService
vi.mock('@/services/userService', () => ({
  userService: {
    getProfile: vi.fn(),
    updateProfile: vi.fn(),
    deleteAccount: vi.fn(),
  },
}));

// Mock notification hook
const mockShowToast = vi.fn();
vi.mock('@/components/notifications/NotificationToast', () => ({
  useNotification: () => ({
    showToast: mockShowToast,
  }),
}));

const MockedUserService = vi.mocked(userService);

const mockUser = {
  id: '1',
  navn: 'Test Bruker',
  mobil: '+47 12345678',
  epost: 'test@example.com',
  createdAt: '2024-01-01T00:00:00Z',
  updatedAt: '2024-01-01T00:00:00Z',
};

describe('Profile', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    MockedUserService.getProfile.mockResolvedValue(mockUser);
  });

  it('should render profile page in Norwegian', async () => {
    render(<Profile />);
    
    expect(screen.getByRole('heading', { name: 'Profil' })).toBeInTheDocument();
    expect(screen.getByText('Administrer din profilinformasjon og kontoinnstillinger.')).toBeInTheDocument();
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
      expect(screen.getByText('+47 12345678')).toBeInTheDocument();
      expect(screen.getByText('test@example.com')).toBeInTheDocument();
    });
  });

  it('should load user profile on mount', async () => {
    render(<Profile />);
    
    await waitFor(() => {
      expect(MockedUserService.getProfile).toHaveBeenCalled();
    });
    
    expect(screen.getByText('Test Bruker')).toBeInTheDocument();
  });

  it('should show loading state while fetching profile', () => {
    MockedUserService.getProfile.mockImplementationOnce(
      () => new Promise(resolve => setTimeout(resolve, 1000))
    );
    
    render(<Profile />);
    
    expect(screen.getByText('Laster profil...')).toBeInTheDocument();
  });

  it('should handle profile loading error', async () => {
    MockedUserService.getProfile.mockRejectedValueOnce(new Error('Failed to fetch'));
    
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Kunne ikke laste profil')).toBeInTheDocument();
    });
  });

  it('should enable edit mode when edit button is clicked', async () => {
    const user = userEvent.setup();
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    const editButton = screen.getByRole('button', { name: 'Rediger profil' });
    await user.click(editButton);
    
    // Should show form fields
    expect(screen.getByDisplayValue('Test Bruker')).toBeInTheDocument();
    expect(screen.getByDisplayValue('+47 12345678')).toBeInTheDocument();
    expect(screen.getByDisplayValue('test@example.com')).toBeInTheDocument();
    
    // Should show save and cancel buttons
    expect(screen.getByRole('button', { name: 'Lagre endringer' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Avbryt' })).toBeInTheDocument();
  });

  it('should cancel edit mode when cancel button is clicked', async () => {
    const user = userEvent.setup();
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    // Enter edit mode
    const editButton = screen.getByRole('button', { name: 'Rediger profil' });
    await user.click(editButton);
    
    // Cancel edit mode
    const cancelButton = screen.getByRole('button', { name: 'Avbryt' });
    await user.click(cancelButton);
    
    // Should show read-only view again
    expect(screen.getByRole('button', { name: 'Rediger profil' })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Lagre endringer' })).not.toBeInTheDocument();
  });

  it('should update profile successfully', async () => {
    const user = userEvent.setup();
    const updatedUser = { ...mockUser, navn: 'Updated Name' };
    
    MockedUserService.updateProfile.mockResolvedValueOnce(updatedUser);
    
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    // Enter edit mode
    const editButton = screen.getByRole('button', { name: 'Rediger profil' });
    await user.click(editButton);
    
    // Update name field
    const nameInput = screen.getByDisplayValue('Test Bruker');
    await user.clear(nameInput);
    await user.type(nameInput, 'Updated Name');
    
    // Save changes
    const saveButton = screen.getByRole('button', { name: 'Lagre endringer' });
    await user.click(saveButton);
    
    await waitFor(() => {
      expect(MockedUserService.updateProfile).toHaveBeenCalledWith({
        navn: 'Updated Name',
        mobil: '+47 12345678',
        epost: 'test@example.com',
      });
    });

    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Profil oppdatert!', 'success');
    });
  });

  it('should handle update profile error', async () => {
    const user = userEvent.setup();
    
    MockedUserService.updateProfile.mockRejectedValueOnce({
      response: { status: 400, data: { message: 'Invalid data' } }
    });
    
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    // Enter edit mode and save
    const editButton = screen.getByRole('button', { name: 'Rediger profil' });
    await user.click(editButton);
    
    const saveButton = screen.getByRole('button', { name: 'Lagre endringer' });
    await user.click(saveButton);
    
    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Noe gikk galt, prøv igjen', 'error');
    });
  });

  it('should show account deletion section', async () => {
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    expect(screen.getByRole('heading', { name: 'Slett konto' })).toBeInTheDocument();
    expect(screen.getByText('Denne handlingen kan ikke angres.')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Slett konto' })).toBeInTheDocument();
  });

  it('should show confirmation dialog when deleting account', async () => {
    const user = userEvent.setup();
    
    // Mock window.confirm
    const mockConfirm = vi.fn(() => false);
    Object.defineProperty(window, 'confirm', { value: mockConfirm });
    
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    const deleteButton = screen.getByRole('button', { name: 'Slett konto' });
    await user.click(deleteButton);
    
    expect(mockConfirm).toHaveBeenCalledWith(
      'Er du sikker på at du vil slette kontoen din? Denne handlingen kan ikke angres.'
    );
  });

  it('should delete account when confirmed', async () => {
    const user = userEvent.setup();
    
    // Mock window.confirm to return true
    const mockConfirm = vi.fn(() => true);
    Object.defineProperty(window, 'confirm', { value: mockConfirm });
    
    MockedUserService.deleteAccount.mockResolvedValueOnce(undefined);
    
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    const deleteButton = screen.getByRole('button', { name: 'Slett konto' });
    await user.click(deleteButton);
    
    await waitFor(() => {
      expect(MockedUserService.deleteAccount).toHaveBeenCalled();
      expect(mockShowToast).toHaveBeenCalledWith('Konto slettet', 'success');
    });
  });

  it('should handle account deletion error', async () => {
    const user = userEvent.setup();
    
    // Mock window.confirm to return true
    const mockConfirm = vi.fn(() => true);
    Object.defineProperty(window, 'confirm', { value: mockConfirm });
    
    MockedUserService.deleteAccount.mockRejectedValueOnce(new Error('Failed to delete'));
    
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    const deleteButton = screen.getByRole('button', { name: 'Slett konto' });
    await user.click(deleteButton);
    
    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith('Noe gikk galt, prøv igjen', 'error');
    });
  });

  it('should have proper accessibility attributes', async () => {
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    // Check main heading
    const heading = screen.getByRole('heading', { level: 1 });
    expect(heading).toHaveTextContent('Profil');
    
    // Check section headings
    expect(screen.getByRole('heading', { level: 2, name: 'Profilinformasjon' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { level: 3, name: 'Slett konto' })).toBeInTheDocument();
    
    // Check buttons
    expect(screen.getByRole('button', { name: 'Rediger profil' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Slett konto' })).toBeInTheDocument();
  });
});
