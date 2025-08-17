import React, { useEffect, useRef } from 'react';
import { Button } from './Button';

interface ConfirmDialogProps {
  open: boolean;
  title?: string;
  description?: string;
  confirmLabel?: string;
  cancelLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
}

export const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
  open,
  title = 'Bekreft',
  description = '',
  confirmLabel = 'Ja',
  cancelLabel = 'Avbryt',
  onConfirm,
  onCancel,
}) => {
  const dialogRef = useRef<HTMLDivElement | null>(null);
  const previouslyFocused = useRef<HTMLElement | null>(null);

  useEffect(() => {
    if (!open) return;

    previouslyFocused.current = document.activeElement as HTMLElement | null;

    const dialog = dialogRef.current;
    if (!dialog) return;

    const focusable = dialog.querySelectorAll<HTMLElement>(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    );

    const first = focusable[0];
    const last = focusable[focusable.length - 1];

    // Focus the first focusable element (confirm button expected)
    if (first) first.focus();

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        e.preventDefault();
        onCancel();
        return;
      }

      if (e.key === 'Tab') {
        if (focusable.length === 0) {
          e.preventDefault();
          return;
        }

        const active = document.activeElement as HTMLElement;
        if (e.shiftKey) {
          if (first && last && active === first) {
            e.preventDefault();
            last.focus();
          }
        } else {
          if (first && last && active === last) {
            e.preventDefault();
            first.focus();
          }
        }
      }
    };

    document.addEventListener('keydown', handleKeyDown);

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      // Restore focus
      try {
        previouslyFocused.current?.focus();
      } catch {
        /* ignore */
      }
    };
  }, [open, onCancel]);

  if (!open) return null;

  const titleId = 'confirm-dialog-title';
  const descId = 'confirm-dialog-desc';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <div
        ref={dialogRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        aria-describedby={descId}
        className="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6 w-full max-w-md"
        data-testid="confirm-dialog"
      >
        <h3 id={titleId} className="text-lg font-medium mb-2">
          {title}
        </h3>
        {description && (
          <p id={descId} className="text-sm text-muted-foreground mb-4">
            {description}
          </p>
        )}

        <div className="flex justify-end gap-2">
          <Button variant="outline" onClick={onCancel} data-testid="confirm-cancel">
            {cancelLabel}
          </Button>
          <Button variant="destructive" onClick={onConfirm} data-testid="confirm-ok">
            {confirmLabel}
          </Button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmDialog;
