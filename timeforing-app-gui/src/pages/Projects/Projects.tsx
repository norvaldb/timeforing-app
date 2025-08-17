import { useEffect, useState } from 'react';
import projectService from '../../services/projectService';
import { useNotification } from '@/components/notifications/NotificationToast';
import type { ProjectDto } from '../../types/project';
import ProjectForm, { ProjectFormData } from '../../components/forms/ProjectForm';
import { Button } from '../../components/ui/Button';
import { useConfirm } from '@/components/confirm/ConfirmProvider';

export const Projects = () => {
  const [projects, setProjects] = useState<ProjectDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState<ProjectDto | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const confirm = useConfirm();

  const fetch = async () => {
    setLoading(true);
    try {
      const data = await projectService.getAll();
      setProjects(data);
    } catch (err) {
      // TODO: toast error
      console.error('Failed to load projects', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let mounted = true;
    if (mounted) fetch();
    return () => {
      mounted = false;
    };
  }, []);

  const openCreate = () => {
    setEditing(null);
    setShowForm(true);
  };

  const openEdit = (p: ProjectDto) => {
    setEditing(p);
    setShowForm(true);
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditing(null);
  };

  const handleSubmit = async (data: ProjectFormData) => {
    setSubmitting(true);
    try {
      if (editing) {
        const updated = await projectService.update(editing.id, data as any);
        setProjects((prev: ProjectDto[]) => prev.map((x) => (x.id === updated.id ? updated : x)));
        notify.success('lagret');
      } else {
        const created = await projectService.create(data as any);
        setProjects((prev: ProjectDto[]) => [created, ...prev]);
        notify.success('lagret');
      }
      setShowForm(false);
      setEditing(null);
    } catch (err) {
      console.error('Save error', err);
      notify.customError('Noe gikk galt, prøv igjen');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (p: ProjectDto) => {
    const ok = await confirm({
      title: 'Slett prosjekt',
      description: `Er du sikker på at du vil slette ${p.navn}?`,
      confirmLabel: 'Slett',
      cancelLabel: 'Avbryt',
    });

    if (!ok) return;

    try {
      await projectService.remove(p.id);
      setProjects((prev) => prev.filter((x) => x.id !== p.id));
      notify.success('slettet');
    } catch (err) {
      console.error('Delete failed', err);
      notify.customError('Noe gikk galt, prøv igjen');
    }
  };

  const notify = useNotification();

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Prosjekter</h1>
          <p className="text-muted-foreground">Administrer dine prosjekter</p>
        </div>
        <Button onClick={openCreate}>Legg til prosjekt</Button>
      </div>

  {/* ConfirmDialog provided globally via ConfirmProvider */}

      <div className="bg-card rounded-lg border p-6">
        {showForm && (
          <div className="mb-6 p-4 border rounded-md bg-background">
            <h2 className="text-lg font-medium mb-3">{editing ? 'Rediger prosjekt' : 'Nytt prosjekt'}</h2>
            <ProjectForm
              initial={(editing ?? undefined) as any}
              onCancel={handleCancel}
              onSubmit={handleSubmit}
              isSubmitting={submitting}
            />
          </div>
        )}

        {loading ? (
          <p className="text-muted-foreground text-center py-8">Laster prosjekter...</p>
        ) : projects.length === 0 ? (
          <p className="text-muted-foreground text-center py-8">Ingen prosjekter funnet.</p>
        ) : (
          <ul className="space-y-3">
            {projects.map((p) => (
              <li key={p.id} className="p-3 border rounded-md flex justify-between items-center">
                <div>
                  <div className="font-medium">{p.navn}</div>
                  {p.beskrivelse && <div className="text-sm text-muted-foreground">{p.beskrivelse}</div>}
                </div>
                <div className="space-x-2">
                  <button className="text-sm text-primary" onClick={() => openEdit(p)}>Rediger</button>
                  <button className="text-sm text-destructive" onClick={() => handleDelete(p)}>Slett</button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};
