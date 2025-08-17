import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import type { CreateProjectDto } from '@/types/project';

const projectSchema = z.object({
  navn: z.string().min(2, 'Navn må være minst 2 tegn').max(100, 'Navn er for langt'),
  beskrivelse: z.string().max(1000).optional().or(z.literal('')),
});

export type ProjectFormData = z.infer<typeof projectSchema>;

interface Props {
  initial?: Partial<CreateProjectDto>;
  onCancel: () => void;
  onSubmit: (data: ProjectFormData) => Promise<void> | void;
  isSubmitting?: boolean;
}

export const ProjectForm: React.FC<Props> = ({ initial, onCancel, onSubmit, isSubmitting = false }) => {
  const { register, handleSubmit, formState: { errors, isValid } } = useForm<ProjectFormData>({
    resolver: zodResolver(projectSchema),
    mode: 'onChange',
    defaultValues: {
      navn: initial?.navn || '',
      beskrivelse: initial?.beskrivelse || '',
    },
  });

  const submit = async (data: ProjectFormData) => {
    await onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit(submit)} className="space-y-4">
      <Input label="Navn" required {...register('navn')} error={errors.navn?.message} />

      <Input label="Beskrivelse" {...register('beskrivelse')} error={errors.beskrivelse?.message} />

      <div className="flex justify-end gap-2">
        <Button type="button" variant="outline" onClick={onCancel} disabled={isSubmitting}>
          Avbryt
        </Button>
        <Button type="submit" loading={isSubmitting} disabled={!isValid || isSubmitting}>
          Lagre
        </Button>
      </div>
    </form>
  );
};

export default ProjectForm;
