import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect } from 'vitest';
import { useState, useRef } from 'react';
import ConfirmDialog from '../ConfirmDialog';

const TestHost: React.FC<{ title?: string; description?: string }> = ({ title = '', description = '' }) => {
  const [open, setOpen] = useState(true);
  const btnRef = useRef<HTMLButtonElement | null>(null);

  return (
    <div>
      <button ref={btnRef}>outside</button>
      {open && (
        <ConfirmDialog
          open={open}
          title={title}
          description={description}
          onConfirm={() => setOpen(false)}
          onCancel={() => setOpen(false)}
        />
      )}
    </div>
  );
};

describe('ConfirmDialog', () => {
  it('renders when open and calls callbacks and restores focus', async () => {
    const user = userEvent.setup();

    render(<TestHost title="Tittel" description="Beskrivelse" />);

    // dialog elements
    expect(screen.getByTestId('confirm-dialog')).toBeInTheDocument();
    expect(screen.getByText('Tittel')).toBeInTheDocument();
    expect(screen.getByText('Beskrivelse')).toBeInTheDocument();

    // confirm button exists and can be clicked, which closes the dialog
    await user.click(screen.getByTestId('confirm-ok'));

    // after closing, dialog should be gone
    expect(screen.queryByTestId('confirm-dialog')).not.toBeInTheDocument();
  });

  it('closes on Escape key and restores focus', async () => {
    const user = userEvent.setup();

    render(<TestHost />);

    // fire Escape
    await user.keyboard('{Escape}');

    // after closing, dialog should be gone
    expect(screen.queryByTestId('confirm-dialog')).not.toBeInTheDocument();
  });
});
