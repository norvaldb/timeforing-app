import React, { useRef } from 'react';
import FocusTrap from 'focus-trap-react';
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

  if (!open) return null;

  const titleId = 'confirm-dialog-title';
  const descId = 'confirm-dialog-desc';

  // handle Enter when focus is on the container (fallback)
  const handleKeyDown: React.KeyboardEventHandler = (e) => {
    if (e.key === 'Enter') {
      const active = document.activeElement as HTMLElement | null;
      if (active === dialogRef.current) {
        e.preventDefault();
        onConfirm();
      }
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <FocusTrap
        active={open}
        focusTrapOptions={{
          onDeactivate: onCancel,
          escapeDeactivates: true,
          initialFocus: '#confirm-ok',
          // fallbackFocus prevents focus-trap errors in test environments
          fallbackFocus: () => dialogRef.current ?? document.body,
        }}
      >
        <div
          ref={dialogRef}
          role="dialog"
          aria-modal="true"
          aria-labelledby={titleId}
          aria-describedby={descId}
          className="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6 w-full max-w-md"
          data-testid="confirm-dialog"
          onKeyDown={handleKeyDown}
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
            <Button id="confirm-ok" variant="destructive" onClick={onConfirm} data-testid="confirm-ok">
              {confirmLabel}
            </Button>
          </div>
        </div>
      </FocusTrap>
    </div>
  );
};

export default ConfirmDialog;
