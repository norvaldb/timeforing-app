import React, { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import ConfirmDialog from '@/components/ui/ConfirmDialog';

type ConfirmOptions = {
  title?: string;
  description?: string;
  confirmLabel?: string;
  cancelLabel?: string;
};

type ConfirmFunction = (options?: ConfirmOptions) => Promise<boolean>;

const ConfirmContext = createContext<ConfirmFunction | null>(null);

// Module-level global confirm reference. This allows tests to mount a single
// ConfirmProvider instance (in setup.ts) and have other test-rendered React
// trees call the confirm function even when they aren't descendants of the
// provider. It's a pragmatic shim for the test environment.
let globalConfirm: ConfirmFunction | null = null;

export const useConfirm = (): ConfirmFunction => {
  const ctx = useContext(ConfirmContext);
  if (ctx) return ctx;
  if (globalConfirm) return globalConfirm;

  // Fallback: synchronous window.confirm wrapped in a Promise. This ensures
  // components won't crash when rendered outside of a provider in unusual
  // environments. Tests should prefer mounting the provider so the dialog
  // shows up in DOM and can be interacted with.
  return (options) =>
    Promise.resolve(
      // eslint-disable-next-line no-alert
      window.confirm(options?.description ?? options?.title ?? 'Bekreft') ?? false
    );
};

export const ConfirmProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [open, setOpen] = useState(false);
  const [opts, setOpts] = useState<ConfirmOptions>({});
  const [resolver, setResolver] = useState<((v: boolean) => void) | null>(null);

  const confirm: ConfirmFunction = (options) => {
    setOpts(options ?? {});
    setOpen(true);
    return new Promise<boolean>((resolve) => setResolver(() => resolve));
  };

  const handleConfirm = () => {
    setOpen(false);
    resolver?.(true);
    setResolver(null);
  };

  const handleCancel = () => {
    setOpen(false);
    resolver?.(false);
    setResolver(null);
  };

  // Expose the confirm function to the module-level global so tests that mount
  // a single provider can have other test render trees call confirm without
  // being inside the same React context tree.
  useEffect(() => {
    globalConfirm = confirm;
    return () => {
      if (globalConfirm === confirm) globalConfirm = null;
    };
  }, [confirm]);

  return (
    <ConfirmContext.Provider value={confirm}>
      {children}
      <ConfirmDialog
        open={open}
        title={opts.title ?? 'Bekreft'}
        description={opts.description ?? ''}
        confirmLabel={opts.confirmLabel ?? 'Ja'}
        cancelLabel={opts.cancelLabel ?? 'Avbryt'}
        onConfirm={handleConfirm}
        onCancel={handleCancel}
      />
    </ConfirmContext.Provider>
  );
};

export default ConfirmProvider;
