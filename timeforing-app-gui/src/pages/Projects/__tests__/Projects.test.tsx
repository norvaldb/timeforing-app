import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';

// Mock projectService used by Projects.tsx
vi.mock('../../../services/projectService', () => ({
  __esModule: true,
  default: {
    getAll: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    remove: vi.fn(),
  },
}));

import projectService from '../../../services/projectService';
import { Projects } from '../Projects';

describe('Projects page', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders list of projects from service', async () => {
    const fake = [{ id: 1, navn: 'P1', beskrivelse: 'B1' }];
    (projectService.getAll as any).mockResolvedValueOnce(fake);

    render(<Projects />);

    expect(screen.getByText('Laster prosjekter...')).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('P1')).toBeInTheDocument();
    });
  });

  it('creates a project and displays it', async () => {
    (projectService.getAll as any).mockResolvedValueOnce([]);
    const created = { id: 2, navn: 'Ny', beskrivelse: 'Desc' };
    (projectService.create as any).mockResolvedValueOnce(created);

    const user = userEvent.setup();
    render(<Projects />);

    // Open create form
    await user.click(screen.getByRole('button', { name: 'Legg til prosjekt' }));

    // Fill form
    await user.type(screen.getByLabelText('Navn'), 'Ny');
    await user.type(screen.getByLabelText('Beskrivelse'), 'Desc');

    await user.click(screen.getByRole('button', { name: 'Lagre' }));

    await waitFor(() => {
      expect(projectService.create).toHaveBeenCalled();
      expect(screen.getByText('Ny')).toBeInTheDocument();
    });
  });

  it('deletes a project after confirmation', async () => {
    const fake = [{ id: 3, navn: 'ToDelete', beskrivelse: '' }];
    (projectService.getAll as any).mockResolvedValueOnce(fake);
    (projectService.remove as any).mockResolvedValueOnce({});

    render(<Projects />);

    await waitFor(() => {
      expect(screen.getByText('ToDelete')).toBeInTheDocument();
    });

    // Click delete which opens ConfirmDialog
    await userEvent.click(screen.getByText('Slett'));

    // Wait for dialog and confirm
    await waitFor(() => {
      expect(screen.getByTestId('confirm-ok')).toBeInTheDocument();
    });

    await userEvent.click(screen.getByTestId('confirm-ok'));

    await waitFor(() => {
      expect(projectService.remove).toHaveBeenCalledWith(3);
    });
  });
});
