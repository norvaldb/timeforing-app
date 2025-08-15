import React, { useState, useEffect } from 'react';
import { User, Settings, Edit3 } from 'lucide-react';
import { UserForm } from '@/components/forms/UserForm';
import { Button } from '@/components/ui/Button';
import { useNotification } from '@/components/notifications/NotificationToast';
import { userService } from '@/services/userService';
import { UserFormData } from '@/utils/validation';
import { User as UserType, ApiError } from '@/types/user';
import { formatMobileNumber } from '@/utils/validation';

export const Profile: React.FC = () => {
  const [user, setUser] = useState<UserType | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const notification = useNotification();

  // Load user profile on component mount
  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    setIsLoading(true);
    try {
      const userData = await userService.getProfile();
      setUser(userData);
    } catch (error) {
      const apiError = error as ApiError;
      
      switch (apiError.code) {
        case 'NETWORK_ERROR':
          notification.error('kunneIkkeKontakteServer');
          break;
        case 'UNAUTHORIZED':
          notification.customError('Du må logge inn for å se profilen din');
          break;
        default:
          notification.error('noeGikkGalt');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdate = async (data: UserFormData) => {
    setIsUpdating(true);
    
    try {
      const updatedUser = await userService.updateProfile(data);
      setUser(updatedUser);
      setIsEditing(false);
      notification.success('profilOppdatert');
      
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
      setIsUpdating(false);
    }
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
  };

  const handleStartEdit = () => {
    setIsEditing(true);
  };

  const handleDeleteAccount = async () => {
    const confirmed = window.confirm(
      'Er du sikker på at du vil slette kontoen din? Denne handlingen kan ikke angres.'
    );
    
    if (!confirmed) return;
    
    try {
      await userService.deleteAccount();
      notification.success('slettet');
      // In a real app, this would typically redirect to login or home page
    } catch (error: unknown) {
      notification.error('noeGikkGalt');
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="text-center py-12">
        <User className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-2 text-sm font-medium text-gray-900 dark:text-gray-100">
          Kunne ikke laste profil
        </h3>
        <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
          Prøv å laste siden på nytt
        </p>
        <div className="mt-6">
          <Button onClick={loadProfile}>
            Last på nytt
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="h-10 w-10 rounded-full bg-primary flex items-center justify-center">
            <User className="h-5 w-5 text-primary-foreground" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">Min profil</h1>
            <p className="text-muted-foreground">
              Administrer din brukerinformasjon
            </p>
          </div>
        </div>
        
        {!isEditing && (
          <Button
            variant="outline"
            onClick={handleStartEdit}
            className="flex items-center gap-2"
          >
            <Edit3 className="h-4 w-4" />
            Rediger
          </Button>
        )}
      </div>

      {/* Profile Content */}
      <div className="bg-card rounded-lg border p-6">
        {isEditing ? (
          // Edit Mode
          <div className="space-y-6">
            <div className="flex items-center gap-2 text-primary mb-4">
              <Settings className="h-5 w-5" />
              <h2 className="text-lg font-semibold">Rediger profil</h2>
            </div>
            
            <UserForm
              onSubmit={handleUpdate}
              defaultValues={{
                navn: user.navn,
                mobil: user.mobil,
                epost: user.epost,
              }}
              isLoading={isUpdating}
              submitButtonText="Lagre endringer"
              showCancelButton={true}
              onCancel={handleCancelEdit}
              className="space-y-4"
            />
          </div>
        ) : (
          // View Mode
          <div className="space-y-6">
            <div className="flex items-center gap-2 text-primary mb-4">
              <User className="h-5 w-5" />
              <h2 className="text-lg font-semibold">Brukerinformasjon</h2>
            </div>
            
            <div className="grid gap-4">
              <div className="space-y-2">
                <label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                  Navn
                </label>
                <p className="text-gray-900 dark:text-gray-100 bg-gray-50 dark:bg-gray-800 px-3 py-2 rounded-md">
                  {user.navn}
                </p>
              </div>
              
              <div className="space-y-2">
                <label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                  Mobilnummer
                </label>
                <p className="text-gray-900 dark:text-gray-100 bg-gray-50 dark:bg-gray-800 px-3 py-2 rounded-md">
                  {formatMobileNumber(user.mobil)}
                </p>
              </div>
              
              <div className="space-y-2">
                <label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                  Epost
                </label>
                <p className="text-gray-900 dark:text-gray-100 bg-gray-50 dark:bg-gray-800 px-3 py-2 rounded-md">
                  {user.epost}
                </p>
              </div>
            </div>
            
            {/* Profile Stats */}
            <div className="border-t pt-6">
              <h3 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-3">
                Kontoinformasjon
              </h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="text-gray-500 dark:text-gray-400">Opprettet:</span>
                  <p className="text-gray-900 dark:text-gray-100">
                    {new Date(user.createdAt).toLocaleDateString('nb-NO', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric'
                    })}
                  </p>
                </div>
                <div>
                  <span className="text-gray-500 dark:text-gray-400">Sist oppdatert:</span>
                  <p className="text-gray-900 dark:text-gray-100">
                    {new Date(user.updatedAt).toLocaleDateString('nb-NO', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric'
                    })}
                  </p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Additional Settings */}
      {!isEditing && (
        <div className="bg-card rounded-lg border p-6">
          <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
            <Settings className="h-5 w-5" />
            Innstillinger
          </h3>
          
          <div className="space-y-4">
            <div className="flex items-center justify-between py-2">
              <div>
                <p className="font-medium">Varslinger</p>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  Administrer e-post og push-varslinger
                </p>
              </div>
              <Button variant="outline" size="sm">
                Konfigurer
              </Button>
            </div>
            
            <div className="flex items-center justify-between py-2">
              <div>
                <p className="font-medium">Eksporter data</p>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  Last ned dine timeføringsdata
                </p>
              </div>
              <Button variant="outline" size="sm">
                Eksporter
              </Button>
            </div>
            
            <div className="border-t pt-4">
              <div className="flex items-center justify-between py-2">
                <div>
                  <p className="font-medium text-red-600 dark:text-red-400">Slett konto</p>
                  <p className="text-sm text-gray-500 dark:text-gray-400">
                    Permanent slett din konto og alle data
                  </p>
                </div>
                <Button variant="destructive" size="sm" onClick={handleDeleteAccount}>
                  Slett
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
