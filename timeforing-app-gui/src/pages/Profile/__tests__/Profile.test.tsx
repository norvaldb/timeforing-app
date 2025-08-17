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

// Mock the notification hook
const mockSuccess = vi.fn();
const mockError = vi.fn();
const mockCustomError = vi.fn();

vi.mock('@/components/notifications/NotificationToast', () => ({
  useNotification: () => ({
    success: mockSuccess,
    error: mockError,
    customError: mockCustomError,
  }),
}));

// We'll interact with the ConfirmDialog component instead of mocking window.confirm

const MockedUserService = userService as any;

const mockUser = {
  id: '1',
  navn: 'Test Bruker',
  mobil: '+4741234567',
  epost: 'test@example.com',
  createdAt: '2024-01-01T00:00:00Z',
  updatedAt: '2024-01-01T00:00:00Z',
};

describe('Profile', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    MockedUserService.getProfile.mockResolvedValue(mockUser);
    MockedUserService.updateProfile.mockResolvedValue({
      ...mockUser,
      navn: 'Updated Name',
    });
    MockedUserService.deleteAccount.mockResolvedValue(undefined);
  });

  it('should render profile page in Norwegian', async () => {
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByRole('heading', { name: 'Min profil' })).toBeInTheDocument();
    });
    
    expect(screen.getByText('Administrer din brukerinformasjon')).toBeInTheDocument();
    expect(screen.getByText('Brukerinformasjon')).toBeInTheDocument();
    expect(screen.getByText('Innstillinger')).toBeInTheDocument();
  });

  it('should load user profile on mount', async () => {
    render(<Profile />);
    
    await waitFor(() => {
      expect(MockedUserService.getProfile).toHaveBeenCalled();
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
      expect(screen.getByText('+47 412 34 567')).toBeInTheDocument();
      expect(screen.getByText('test@example.com')).toBeInTheDocument();
    });
  });

  it('should show loading state while fetching profile', () => {
    render(<Profile />);
    
    // Check for loading spinner (by class rather than text since there's no loading text)
    expect(document.querySelector('.animate-spin')).toBeInTheDocument();
  });

  it('should handle profile loading error', async () => {
    MockedUserService.getProfile.mockRejectedValueOnce(new Error('Network error'));
    
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Kunne ikke laste profil')).toBeInTheDocument();
    });
    
    expect(screen.getByText('Prøv å laste siden på nytt')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Last på nytt' })).toBeInTheDocument();
  });

  it('should enable edit mode when edit button is clicked', async () => {
    const user = userEvent.setup();
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    const editButton = screen.getByRole('button', { name: 'Rediger' });
    await user.click(editButton);
    
    // Should show form fields
    expect(screen.getByDisplayValue('Test Bruker')).toBeInTheDocument();
    expect(screen.getByDisplayValue('+4741234567')).toBeInTheDocument();
    expect(screen.getByDisplayValue('test@example.com')).toBeInTheDocument();
  });

  it('should cancel edit mode when cancel button is clicked', async () => {
    const user = userEvent.setup();
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    // Enter edit mode
    const editButton = screen.getByRole('button', { name: 'Rediger' });
    await user.click(editButton);
    
    // Click cancel
    const cancelButton = screen.getByRole('button', { name: 'Avbryt' });
    await user.click(cancelButton);
    
    // Should be back in view mode
    expect(screen.getByText('Test Bruker')).toBeInTheDocument();
  });

  it('should update profile successfully', async () => {
    const user = userEvent.setup();
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    // Enter edit mode
    const editButton = screen.getByRole('button', { name: 'Rediger' });
    await user.click(editButton);
    
    // Update name field
    const nameInput = screen.getByDisplayValue('Test Bruker');
    await user.clear(nameInput);
    await user.type(nameInput, 'Updated Name');
    
    // Submit form
    const saveButton = screen.getByRole('button', { name: 'Lagre endringer' });
    await user.click(saveButton);
    
    await waitFor(() => {
      expect(MockedUserService.updateProfile).toHaveBeenCalledWith({
        navn: 'Updated Name',
        mobil: '+4741234567',
        epost: 'test@example.com',
      });
      expect(mockSuccess).toHaveBeenCalledWith('profilOppdatert');
    });
  });

  it('should handle update profile error', async () => {
    MockedUserService.updateProfile.mockRejectedValueOnce({
      code: 'DUPLICATE_EMAIL',
    });
    
    const user = userEvent.setup();
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    // Enter edit mode and submit
    const editButton = screen.getByRole('button', { name: 'Rediger' });
    await user.click(editButton);
    
    const saveButton = screen.getByRole('button', { name: 'Lagre endringer' });
    await user.click(saveButton);
    
    await waitFor(() => {
      expect(mockError).toHaveBeenCalledWith('epostAlleredeRegistrert');
    });
  });

  it('should show account deletion section', async () => {
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Slett konto')).toBeInTheDocument();
    });
    
    expect(screen.getByText('Permanent slett din konto og alle data')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Slett' })).toBeInTheDocument();
  });

  it('should show confirmation dialog when deleting account', async () => {
    const user = userEvent.setup();
    render(<Profile />);

    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });

    const deleteButton = screen.getByRole('button', { name: 'Slett' });
    await user.click(deleteButton);

    // Dialog should appear
    await waitFor(() => {
      expect(screen.getByTestId('confirm-dialog')).toBeInTheDocument();
    });
  });

  it('should delete account when confirmed', async () => {
    const user = userEvent.setup();

    render(<Profile />);

    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });

    const deleteButton = screen.getByRole('button', { name: 'Slett' });
    await user.click(deleteButton);

    // confirm using dialog
    await waitFor(() => expect(screen.getByTestId('confirm-ok')).toBeInTheDocument());
    await user.click(screen.getByTestId('confirm-ok'));

    await waitFor(() => {
      expect(MockedUserService.deleteAccount).toHaveBeenCalled();
      expect(mockSuccess).toHaveBeenCalledWith('slettet');
    });
  });

  it('should handle account deletion error', async () => {
    MockedUserService.deleteAccount.mockRejectedValueOnce(new Error('Server error'));

    const user = userEvent.setup();
    render(<Profile />);

    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });

    const deleteButton = screen.getByRole('button', { name: 'Slett' });
    await user.click(deleteButton);

    // confirm using dialog
    await waitFor(() => expect(screen.getByTestId('confirm-ok')).toBeInTheDocument());
    await user.click(screen.getByTestId('confirm-ok'));

    await waitFor(() => {
      expect(mockError).toHaveBeenCalledWith('noeGikkGalt');
    });
  });

  it('should have proper accessibility attributes', async () => {
    render(<Profile />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Bruker')).toBeInTheDocument();
    });
    
    // Check that form labels are properly associated
    expect(screen.getByText('Navn')).toBeInTheDocument();
    expect(screen.getByText('Mobilnummer')).toBeInTheDocument();
    expect(screen.getByText('Epost')).toBeInTheDocument();
    
    // Check that buttons have proper accessible names
    expect(screen.getByRole('button', { name: 'Rediger' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Slett' })).toBeInTheDocument();
  });
});
