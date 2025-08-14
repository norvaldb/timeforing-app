export const Register = () => {
  return (
    <div className="max-w-md mx-auto space-y-8">
      <div className="text-center">
        <h1 className="text-3xl font-bold tracking-tight">Opprett konto</h1>
        <p className="text-muted-foreground mt-2">
          Registrer deg for å komme i gang med timeføring
        </p>
      </div>

      <div className="bg-card rounded-lg border p-6">
        <form className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-2">
              Navn *
            </label>
            <input
              type="text"
              placeholder="Ditt navn"
              className="w-full px-3 py-2 border border-input rounded-md bg-background"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2">
              Mobilnummer *
            </label>
            <input
              type="tel"
              placeholder="+47 123 45 678"
              className="w-full px-3 py-2 border border-input rounded-md bg-background"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2">
              Epost *
            </label>
            <input
              type="email"
              placeholder="din@epost.no"
              className="w-full px-3 py-2 border border-input rounded-md bg-background"
            />
          </div>

          <button
            type="submit"
            className="w-full bg-primary text-primary-foreground py-2 rounded-md font-medium hover:bg-primary/90 transition-colors"
          >
            Opprett konto
          </button>
        </form>
      </div>
    </div>
  );
};
