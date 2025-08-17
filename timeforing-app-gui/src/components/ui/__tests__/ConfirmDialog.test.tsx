import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import ConfirmDialog from '../ConfirmDialog';

describe('ConfirmDialog', () => {
  it('renders when open and calls callbacks', async () => {
    const onConfirm = vi.fn();
    const onCancel = vi.fn();

    const user = userEvent.setup();

    render(
      <div>
        <button>outside</button>
        <ConfirmDialog open={true} title="Tittel" description="Beskrivelse" onConfirm={onConfirm} onCancel={onCancel} />
      </div>
    );

    // dialog elements
    expect(screen.getByTestId('confirm-dialog')).toBeInTheDocument();
    expect(screen.getByText('Tittel')).toBeInTheDocument();
    expect(screen.getByText('Beskrivelse')).toBeInTheDocument();

    // confirm button exists and can be clicked
    await user.click(screen.getByTestId('confirm-ok'));
    expect(onConfirm).toHaveBeenCalled();

    // cancel button exists and can be clicked
    await user.click(screen.getByTestId('confirm-cancel'));
    expect(onCancel).toHaveBeenCalled();
  });

  it('closes on Escape key', async () => {
    const onConfirm = vi.fn();
    const onCancel = vi.fn();

    render(<ConfirmDialog open={true} onConfirm={onConfirm} onCancel={onCancel} />);

    // fire Escape
    await userEvent.keyboard('{Escape}');

    expect(onCancel).toHaveBeenCalled();
  });
});
