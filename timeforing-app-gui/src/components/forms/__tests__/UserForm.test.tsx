import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { UserForm } from '../UserForm';

const mockOnSubmit = vi.fn();

describe('UserForm', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render Norwegian form labels', () => {
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    // Labels without asterisk since CSS adds it
    expect(screen.getByLabelText('Navn')).toBeInTheDocument();
    expect(screen.getByLabelText('Mobilnummer')).toBeInTheDocument();
    expect(screen.getByLabelText('Epost')).toBeInTheDocument();
  });

  it('should render placeholders in Norwegian', () => {
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    expect(screen.getByPlaceholderText('Skriv inn ditt navn')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('+47 123 45 678')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('din@epost.no')).toBeInTheDocument();
  });

  it('should validate Norwegian mobile number format', async () => {
    const user = userEvent.setup();
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    const mobilInput = screen.getByPlaceholderText('+47 123 45 678');
    
    // Test invalid mobile number
    await user.type(mobilInput, '12345');
    await user.tab(); // Trigger validation
    
    await waitFor(() => {
      expect(screen.getByText('Ugyldig norsk mobilnummer')).toBeInTheDocument();
    });
  });

  it('should validate name field', async () => {
    const user = userEvent.setup();
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    const nameInput = screen.getByPlaceholderText('Skriv inn ditt navn');
    
    // Test too short name
    await user.type(nameInput, 'A');
    await user.tab();
    
    await waitFor(() => {
      expect(screen.getByText('Navn må være minst 2 tegn')).toBeInTheDocument();
    });
  });

  it('should validate email field', async () => {
    const user = userEvent.setup();
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    const emailInput = screen.getByPlaceholderText('din@epost.no');
    
    // Test invalid email
    await user.type(emailInput, 'invalid-email');
    await user.tab();
    
    await waitFor(() => {
      expect(screen.getByText('Ugyldig epost adresse')).toBeInTheDocument();
    });
  });

  it('should submit form with valid data', async () => {
    const user = userEvent.setup();
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    // Fill form with valid data step by step
    const nameInput = screen.getByLabelText('Navn');
    const mobileInput = screen.getByLabelText('Mobilnummer');
    const emailInput = screen.getByLabelText('Epost');
    
    await user.type(nameInput, 'Test Bruker');
    await user.type(emailInput, 'test@example.com');
    await user.clear(mobileInput);
    await user.type(mobileInput, '41234567'); // Valid Norwegian mobile starting with 4
    
    // Wait for form validation to resolve
    await waitFor(() => {
      expect(screen.queryByText('Ugyldig norsk mobilnummer')).not.toBeInTheDocument();
    }, { timeout: 3000 });
    
    // Now try to submit
    const submitButton = screen.getByRole('button', { name: 'Lagre' });
    await user.click(submitButton);
    
    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalled();
    });
  });

  it('should disable submit button when form is invalid', async () => {
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    const submitButton = screen.getByRole('button', { name: 'Lagre' });
    expect(submitButton).toBeDisabled();
  });

  it('should show loading state when submitting', async () => {
    render(<UserForm onSubmit={mockOnSubmit} isLoading={true} />);
    
    const submitButton = screen.getByRole('button', { name: 'Lagre' });
    expect(submitButton).toBeDisabled();
    // Check for loading spinner class
    expect(document.querySelector('.animate-spin')).toBeInTheDocument();
  });

  it('should show cancel button when showCancelButton is true', () => {
    const mockOnCancel = vi.fn();
    render(
      <UserForm 
        onSubmit={mockOnSubmit} 
        showCancelButton={true} 
        onCancel={mockOnCancel} 
      />
    );
    
    expect(screen.getByRole('button', { name: 'Avbryt' })).toBeInTheDocument();
  });

  it('should call onCancel when cancel button is clicked', async () => {
    const user = userEvent.setup();
    const mockOnCancel = vi.fn();
    
    render(
      <UserForm 
        onSubmit={mockOnSubmit} 
        showCancelButton={true} 
        onCancel={mockOnCancel} 
      />
    );
    
    const cancelButton = screen.getByRole('button', { name: 'Avbryt' });
    await user.click(cancelButton);
    
    expect(mockOnCancel).toHaveBeenCalled();
  });

  it('should populate form with default values', () => {
    const defaultValues = {
      navn: 'Existing User',
      mobil: '+47 87654321',
      epost: 'existing@example.com',
    };
    
    render(<UserForm onSubmit={mockOnSubmit} defaultValues={defaultValues} />);
    
    expect(screen.getByDisplayValue('Existing User')).toBeInTheDocument();
    expect(screen.getByDisplayValue('+47 87654321')).toBeInTheDocument();
    expect(screen.getByDisplayValue('existing@example.com')).toBeInTheDocument();
  });

  it('should format mobile number as user types', async () => {
    const user = userEvent.setup();
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    const mobilInput = screen.getByPlaceholderText('+47 123 45 678');
    
    // Type 8-digit number and verify it gets formatted
    await user.type(mobilInput, '12345678');
    
    // The UserForm component actually formats it
    expect(mobilInput).toHaveValue('+47 123 45 678');
  });

  it('should have proper accessibility attributes', () => {
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    // Check that inputs are present and have proper autocomplete
    const nameInput = screen.getByLabelText('Navn');
    const mobileInput = screen.getByLabelText('Mobilnummer');
    const emailInput = screen.getByLabelText('Epost');
    
    expect(nameInput).toHaveAttribute('autoComplete', 'name');
    expect(mobileInput).toHaveAttribute('autoComplete', 'tel');
    expect(emailInput).toHaveAttribute('autoComplete', 'email');
    
    // Check inputs are properly associated with labels
    expect(nameInput).toHaveAttribute('name', 'navn');
    expect(mobileInput).toHaveAttribute('name', 'mobil');
    expect(emailInput).toHaveAttribute('name', 'epost');
  });
});
