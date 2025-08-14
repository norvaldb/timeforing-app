import { Button } from '@/components/ui/Button';
import { Play, Square, Clock, Calendar, TrendingUp } from 'lucide-react';

export const Dashboard = () => {
  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
        <p className="text-muted-foreground">
          Oversikt over din timeføring i dag
        </p>
      </div>

      {/* Timer Widget */}
      <div className="bg-card rounded-lg border p-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-semibold mb-2">Aktiv timeføring</h2>
            <p className="text-2xl font-mono">00:00:00</p>
            <p className="text-sm text-muted-foreground mt-1">
              Velg et prosjekt for å starte
            </p>
          </div>
          <div className="flex gap-2">
            <Button size="lg" className="gap-2">
              <Play className="h-4 w-4" />
              Start dagen
            </Button>
            <Button variant="outline" size="lg" className="gap-2" disabled>
              <Square className="h-4 w-4" />
              Slutt dagen
            </Button>
          </div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-card rounded-lg border p-6">
          <div className="flex items-center gap-4">
            <div className="p-2 bg-primary/10 rounded-lg">
              <Clock className="h-6 w-6 text-primary" />
            </div>
            <div>
              <p className="text-sm text-muted-foreground">I dag</p>
              <p className="text-2xl font-bold">0.0t</p>
            </div>
          </div>
        </div>

        <div className="bg-card rounded-lg border p-6">
          <div className="flex items-center gap-4">
            <div className="p-2 bg-blue-500/10 rounded-lg">
              <Calendar className="h-6 w-6 text-blue-500" />
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Denne uken</p>
              <p className="text-2xl font-bold">0.0t</p>
            </div>
          </div>
        </div>

        <div className="bg-card rounded-lg border p-6">
          <div className="flex items-center gap-4">
            <div className="p-2 bg-green-500/10 rounded-lg">
              <TrendingUp className="h-6 w-6 text-green-500" />
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Denne måneden</p>
              <p className="text-2xl font-bold">0.0t</p>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Entries */}
      <div className="bg-card rounded-lg border p-6">
        <h3 className="text-lg font-semibold mb-4">Siste timeføringer</h3>
        <div className="text-center py-8 text-muted-foreground">
          <Clock className="h-12 w-12 mx-auto mb-4 opacity-50" />
          <p>Ingen timeføringer registrert ennå</p>
          <p className="text-sm mt-1">Start med å legge til et prosjekt</p>
        </div>
      </div>
    </div>
  );
};
