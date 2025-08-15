import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserCheck, ArrowLeft } from 'lucide-react';
import { UserForm } from '@/components/forms/UserForm';
import { Button } from '@/components/ui/Button';
import { useNotification } from '@/components/notifications/NotificationToast';
import { userService } from '@/services/userService';
import { UserFormData } from '@/utils/validation';
import { ApiError } from '@/types/user';

export const Register: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const notification = useNotification();

  const handleSubmit = async (data: UserFormData) => {
    setIsLoading(true);
    
    try {
      await userService.register(data);
      notification.success('brukerOpprettet');
      
      // Navigate to dashboard after successful registration
      setTimeout(() => {
        navigate('/');
      }, 1500);
      
    } catch (error: unknown) {
      const apiError = error as ApiError;
      
      switch (apiError.code) {
        case 'DUPLICATE_EMAIL':
          notification.error('epostAlleredeRegistrert');
          break;
        case 'NETWORK_ERROR':
          notification.error('kunneIkkeKontakteServer');
          break;
        case 'VALIDATION_ERROR':
          notification.customError('Vennligst sjekk at alle feltene er fylt ut riktig');
          break;
        default:
          notification.error('noeGikkGalt');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleGoBack = () => {
    navigate(-1);
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        {/* Header */}
        <div className="text-center">
          <div className="mx-auto h-12 w-12 flex items-center justify-center rounded-full bg-primary">
            <UserCheck className="h-6 w-6 text-primary-foreground" />
          </div>
          <h2 className="mt-6 text-3xl font-bold tracking-tight text-gray-900 dark:text-white">
            Opprett bruker
          </h2>
          <p className="mt-2 text-sm text-gray-600 dark:text-gray-400">
            Fyll inn informasjonen din for å komme i gang
          </p>
        </div>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white dark:bg-gray-800 py-8 px-4 shadow sm:rounded-lg sm:px-10">
          {/* Back button */}
          <div className="mb-6">
            <Button
              variant="ghost"
              size="sm"
              onClick={handleGoBack}
              className="flex items-center gap-2 text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white"
            >
              <ArrowLeft className="h-4 w-4" />
              Tilbake
            </Button>
          </div>

          {/* Registration form */}
          <UserForm
            onSubmit={handleSubmit}
            isLoading={isLoading}
            submitButtonText="Opprett bruker"
            className="space-y-6"
          />

          {/* Additional information */}
          <div className="mt-6">
            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-300 dark:border-gray-600" />
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white dark:bg-gray-800 text-gray-500 dark:text-gray-400">
                  Allerede bruker?
                </span>
              </div>
            </div>

            <div className="mt-6 text-center">
              <Button
                variant="link"
                onClick={() => navigate('/')}
                className="text-primary hover:text-primary/80"
              >
                Gå til dashboard
              </Button>
            </div>
          </div>
        </div>

        {/* Footer information */}
        <div className="mt-8 text-center">
          <p className="text-xs text-gray-500 dark:text-gray-400">
            Ved å opprette en bruker godtar du våre{' '}
            <a href="#" className="text-primary hover:text-primary/80">
              vilkår for bruk
            </a>{' '}
            og{' '}
            <a href="#" className="text-primary hover:text-primary/80">
              personvernerklæring
            </a>
          </p>
        </div>
      </div>
    </div>
  );
};
