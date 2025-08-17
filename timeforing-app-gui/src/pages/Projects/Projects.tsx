import { useEffect, useState } from 'react';
import projectService from '../../services/projectService';
import type { ProjectDto } from '../../types/project';

export const Projects = () => {
  const [projects, setProjects] = useState<ProjectDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;
    projectService.getAll()
      .then((data) => {
        if (mounted) setProjects(data);
      })
      .catch(() => {
        // TODO: show notification
      })
      .finally(() => {
        if (mounted) setLoading(false);
      });

    return () => {
      mounted = false;
    };
  }, []);

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Prosjekter</h1>
          <p className="text-muted-foreground">Administrer dine prosjekter</p>
        </div>
        <button className="bg-primary text-primary-foreground px-4 py-2 rounded-md font-medium hover:bg-primary/90 transition-colors">
          Legg til prosjekt
        </button>
      </div>

      <div className="bg-card rounded-lg border p-6">
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
                  <button className="text-sm text-primary">Rediger</button>
                  <button className="text-sm text-destructive">Slett</button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};
