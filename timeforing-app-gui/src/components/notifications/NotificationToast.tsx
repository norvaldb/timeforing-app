import React from 'react';
import { Toaster, toast } from 'react-hot-toast';
import { CheckCircle, XCircle, AlertCircle, Info } from 'lucide-react';

// Toast notification types
export type NotificationType = 'success' | 'error' | 'warning' | 'info';

// Norwegian toast messages
export const toastMessages = {
  success: {
    brukerOpprettet: 'Bruker opprettet!',
    profilOppdatert: 'Profil oppdatert!',
    lagret: 'Lagret!',
    slettet: 'Slettet!',
  },
  error: {
    kunneIkkeKontakteServer: 'Kunne ikke kontakte server',
    epostAlleredeRegistrert: 'Epost addressen er allerede registrert',
    noeGikkGalt: 'Noe gikk galt, prøv igjen',
    ugyldigData: 'Ugyldig data',
    ikkeTilgang: 'Du har ikke tilgang til denne ressursen',
  },
  warning: {
    advarsel: 'Advarsel',
    ulagretEndringer: 'Du har ulagrede endringer',
  },
  info: {
    laster: 'Laster...',
    sendt: 'Sendt!',
  },
} as const;

// Icon components for different toast types
const ToastIcon = ({ type }: { type: NotificationType }) => {
  const iconProps = { className: "w-5 h-5", "aria-hidden": true };
  
  switch (type) {
    case 'success':
      return <CheckCircle {...iconProps} className="w-5 h-5 text-green-500" />;
    case 'error':
      return <XCircle {...iconProps} className="w-5 h-5 text-red-500" />;
    case 'warning':
      return <AlertCircle {...iconProps} className="w-5 h-5 text-yellow-500" />;
    case 'info':
      return <Info {...iconProps} className="w-5 h-5 text-blue-500" />;
    default:
      return <Info {...iconProps} className="w-5 h-5 text-gray-500" />;
  }
};

// Custom toast function with Norwegian styling
export const showToast = {
  success: (message: string) => {
    toast.custom((t) => (
      <div
        className={`
          ${t.visible ? 'animate-enter' : 'animate-leave'}
          max-w-md w-full bg-white dark:bg-gray-800 shadow-lg rounded-lg pointer-events-auto 
          flex ring-1 ring-black ring-opacity-5 border border-green-200 dark:border-green-800
        `}
      >
        <div className="flex-1 w-0 p-4">
          <div className="flex items-start">
            <div className="flex-shrink-0">
              <ToastIcon type="success" />
            </div>
            <div className="ml-3 flex-1">
              <p className="text-sm font-medium text-gray-900 dark:text-gray-100">
                {message}
              </p>
            </div>
          </div>
        </div>
        <div className="flex border-l border-gray-200 dark:border-gray-700">
          <button
            onClick={() => toast.dismiss(t.id)}
            className="w-full border border-transparent rounded-none rounded-r-lg p-4 flex items-center justify-center text-sm font-medium text-green-600 dark:text-green-400 hover:text-green-500 focus:outline-none focus:ring-2 focus:ring-green-500"
          >
            ×
          </button>
        </div>
      </div>
    ), { duration: 3000 });
  },

  error: (message: string) => {
    toast.custom((t) => (
      <div
        className={`
          ${t.visible ? 'animate-enter' : 'animate-leave'}
          max-w-md w-full bg-white dark:bg-gray-800 shadow-lg rounded-lg pointer-events-auto 
          flex ring-1 ring-black ring-opacity-5 border border-red-200 dark:border-red-800
        `}
      >
        <div className="flex-1 w-0 p-4">
          <div className="flex items-start">
            <div className="flex-shrink-0">
              <ToastIcon type="error" />
            </div>
            <div className="ml-3 flex-1">
              <p className="text-sm font-medium text-gray-900 dark:text-gray-100">
                {message}
              </p>
            </div>
          </div>
        </div>
        <div className="flex border-l border-gray-200 dark:border-gray-700">
          <button
            onClick={() => toast.dismiss(t.id)}
            className="w-full border border-transparent rounded-none rounded-r-lg p-4 flex items-center justify-center text-sm font-medium text-red-600 dark:text-red-400 hover:text-red-500 focus:outline-none focus:ring-2 focus:ring-red-500"
          >
            ×
          </button>
        </div>
      </div>
    ), { duration: 4000 });
  },

  warning: (message: string) => {
    toast.custom((t) => (
      <div
        className={`
          ${t.visible ? 'animate-enter' : 'animate-leave'}
          max-w-md w-full bg-white dark:bg-gray-800 shadow-lg rounded-lg pointer-events-auto 
          flex ring-1 ring-black ring-opacity-5 border border-yellow-200 dark:border-yellow-800
        `}
      >
        <div className="flex-1 w-0 p-4">
          <div className="flex items-start">
            <div className="flex-shrink-0">
              <ToastIcon type="warning" />
            </div>
            <div className="ml-3 flex-1">
              <p className="text-sm font-medium text-gray-900 dark:text-gray-100">
                {message}
              </p>
            </div>
          </div>
        </div>
        <div className="flex border-l border-gray-200 dark:border-gray-700">
          <button
            onClick={() => toast.dismiss(t.id)}
            className="w-full border border-transparent rounded-none rounded-r-lg p-4 flex items-center justify-center text-sm font-medium text-yellow-600 dark:text-yellow-400 hover:text-yellow-500 focus:outline-none focus:ring-2 focus:ring-yellow-500"
          >
            ×
          </button>
        </div>
      </div>
    ), { duration: 3500 });
  },

  info: (message: string) => {
    toast.custom((t) => (
      <div
        className={`
          ${t.visible ? 'animate-enter' : 'animate-leave'}
          max-w-md w-full bg-white dark:bg-gray-800 shadow-lg rounded-lg pointer-events-auto 
          flex ring-1 ring-black ring-opacity-5 border border-blue-200 dark:border-blue-800
        `}
      >
        <div className="flex-1 w-0 p-4">
          <div className="flex items-start">
            <div className="flex-shrink-0">
              <ToastIcon type="info" />
            </div>
            <div className="ml-3 flex-1">
              <p className="text-sm font-medium text-gray-900 dark:text-gray-100">
                {message}
              </p>
            </div>
          </div>
        </div>
        <div className="flex border-l border-gray-200 dark:border-gray-700">
          <button
            onClick={() => toast.dismiss(t.id)}
            className="w-full border border-transparent rounded-none rounded-r-lg p-4 flex items-center justify-center text-sm font-medium text-blue-600 dark:text-blue-400 hover:text-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            ×
          </button>
        </div>
      </div>
    ), { duration: 3000 });
  },
};

// Toast provider component
export const NotificationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <>
      {children}
      <Toaster
        position="top-right"
        toastOptions={{
          className: '',
          style: {},
        }}
      />
    </>
  );
};

// Hook for using Norwegian toast messages
export const useNotification = () => {
  return {
    success: (key: keyof typeof toastMessages.success) => {
      showToast.success(toastMessages.success[key]);
    },
    error: (key: keyof typeof toastMessages.error) => {
      showToast.error(toastMessages.error[key]);
    },
    warning: (key: keyof typeof toastMessages.warning) => {
      showToast.warning(toastMessages.warning[key]);
    },
    info: (key: keyof typeof toastMessages.info) => {
      showToast.info(toastMessages.info[key]);
    },
    customSuccess: (message: string) => showToast.success(message),
    customError: (message: string) => showToast.error(message),
    customWarning: (message: string) => showToast.warning(message),
    customInfo: (message: string) => showToast.info(message),
  };
};
