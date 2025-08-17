export const Projects = () => {
  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Prosjekter</h1>
          <p className="text-muted-foreground">
            Administrer dine prosjekter
          </p>
        </div>
        <button className="bg-primary text-primary-foreground px-4 py-2 rounded-md font-medium hover:bg-primary/90 transition-colors">
          Legg til prosjekt
        </button>
      </div>

      <div className="bg-card rounded-lg border p-6">
        <p className="text-muted-foreground text-center py-8">
          Prosjekt funksjonalitet kommer snart...
        </p>
      </div>
    </div>
  );
};
