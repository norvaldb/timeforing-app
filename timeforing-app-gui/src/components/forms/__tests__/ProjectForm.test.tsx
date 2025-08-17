import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import ProjectForm from '../ProjectForm';

describe('ProjectForm', () => {
  it('renders form fields and buttons', () => {
    const mockOnSubmit = vi.fn();
    const mockOnCancel = vi.fn();

    render(<ProjectForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    expect(screen.getByLabelText('Navn')).toBeInTheDocument();
    expect(screen.getByLabelText('Beskrivelse')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Lagre' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Avbryt' })).toBeInTheDocument();
  });

  it('calls onCancel when cancel clicked', async () => {
    const user = userEvent.setup();
    const mockOnSubmit = vi.fn();
    const mockOnCancel = vi.fn();

    render(<ProjectForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    await user.click(screen.getByRole('button', { name: 'Avbryt' }));

    expect(mockOnCancel).toHaveBeenCalled();
  });
});
