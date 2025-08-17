import { Link } from 'react-router-dom';

export const NotFound = () => {
  return (
    <div className="text-center py-16">
      <h1 className="text-4xl font-bold mb-4">404</h1>
      <p className="text-xl text-muted-foreground mb-8">
        Siden ble ikke funnet
      </p>
      <Link
        to="/"
        className="bg-primary text-primary-foreground px-6 py-3 rounded-md font-medium hover:bg-primary/90 transition-colors"
      >
        Tilbake til Dashboard
      </Link>
    </div>
  );
};
