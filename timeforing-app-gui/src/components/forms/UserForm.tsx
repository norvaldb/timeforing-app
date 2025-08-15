import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Loader2 } from 'lucide-react';
import { userFormSchema, UserFormData } from '@/utils/validation';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';

interface UserFormProps {
  onSubmit: (data: UserFormData) => Promise<void> | void;
  defaultValues?: Partial<UserFormData>;
  isLoading?: boolean;
  submitButtonText?: string;
  showCancelButton?: boolean;
  onCancel?: () => void;
  className?: string;
}

export const UserForm: React.FC<UserFormProps> = ({
  onSubmit,
  defaultValues,
  isLoading = false,
  submitButtonText = 'Lagre',
  showCancelButton = false,
  onCancel,
  className = '',
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting, isValid },
    setValue,
    watch,
  } = useForm<UserFormData>({
    resolver: zodResolver(userFormSchema),
    mode: 'onChange',
    defaultValues: {
      navn: defaultValues?.navn || '',
      mobil: defaultValues?.mobil || '',
      epost: defaultValues?.epost || '',
    },
  });

  const watchedMobil = watch('mobil');

  // Format mobile number as user types
  React.useEffect(() => {
    if (watchedMobil) {
      const cleaned = watchedMobil.replace(/\D/g, '');
      if (cleaned.length === 8 && !watchedMobil.includes('+47')) {
        setValue('mobil', `+47 ${cleaned.slice(0, 3)} ${cleaned.slice(3, 5)} ${cleaned.slice(5)}`, {
          shouldValidate: true,
        });
      }
    }
  }, [watchedMobil, setValue]);

  const handleFormSubmit = async (data: UserFormData) => {
    try {
      await onSubmit(data);
    } catch (error) {
      // Error handling is done by parent component
      console.error('Form submission error:', error);
    }
  };

  return (
    <form 
      onSubmit={handleSubmit(handleFormSubmit)} 
      className={`space-y-6 ${className}`}
      noValidate
    >
      <div className="space-y-4">
        <Input
          {...register('navn')}
          label="Navn"
          required
          placeholder="Skriv inn ditt navn"
          error={errors.navn?.message}
          disabled={isLoading || isSubmitting}
          autoComplete="name"
        />

        <Input
          {...register('mobil')}
          label="Mobilnummer"
          required
          type="tel"
          placeholder="+47 123 45 678"
          error={errors.mobil?.message}
          disabled={isLoading || isSubmitting}
          autoComplete="tel"
        />

        <Input
          {...register('epost')}
          label="Epost"
          required
          type="email"
          placeholder="din@epost.no"
          error={errors.epost?.message}
          disabled={isLoading || isSubmitting}
          autoComplete="email"
        />
      </div>

      <div className={`flex gap-3 ${showCancelButton ? 'justify-end' : 'justify-center'}`}>
        {showCancelButton && (
          <Button
            type="button"
            variant="outline"
            onClick={onCancel}
            disabled={isLoading || isSubmitting}
          >
            Avbryt
          </Button>
        )}
        
        <Button
          type="submit"
          disabled={!isValid || isLoading || isSubmitting}
          className="min-w-[120px]"
        >
          {(isLoading || isSubmitting) && (
            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
          )}
          {submitButtonText}
        </Button>
      </div>

      {/* Debug information in development */}
      {process.env.NODE_ENV === 'development' && (
        <div className="mt-4 p-2 bg-gray-100 dark:bg-gray-800 rounded text-xs">
          <details>
            <summary className="cursor-pointer">Debug Info</summary>
            <pre className="mt-2">
              {JSON.stringify({
                isValid,
                errors: Object.keys(errors),
                isSubmitting,
                isLoading,
              }, null, 2)}
            </pre>
          </details>
        </div>
      )}
    </form>
  );
};
