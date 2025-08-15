-- Insert test data for development and testing
-- This migration adds sample data to all tables

-- Insert test users (including Norwegian characters æ, ø, å)
INSERT INTO users (navn, email, mobil, status) VALUES 
    ('Ola Nordmann', 'ola.nordmann@example.com', '+4791234567', 'ACTIVE');

INSERT INTO users (navn, email, mobil, status) VALUES 
    ('Kari Hansen', 'kari.hansen@example.com', '+4798765432', 'ACTIVE');

INSERT INTO users (navn, email, mobil, status) VALUES 
    ('Erik Nilsen', 'erik.nilsen@example.com', '+4795555555', 'ACTIVE');

INSERT INTO users (navn, email, mobil, status) VALUES 
    ('Anne Larsen', 'anne.larsen@example.com', '90123456', 'INACTIVE');

-- Users with Norwegian characters
INSERT INTO users (navn, email, mobil, status) VALUES 
    ('Bjørn Sæterdal', 'bjorn.saterdal@example.com', '+4792345678', 'ACTIVE');

INSERT INTO users (navn, email, mobil, status) VALUES 
    ('Åse Røed', 'ase.roed@example.com', '+4793456789', 'ACTIVE');

INSERT INTO users (navn, email, mobil, status) VALUES 
    ('Per Åsmund Høiby', 'per.asmund.hoiby@example.com', '+4794567890', 'PENDING');

INSERT INTO users (navn, email, mobil, status) VALUES 
    ('Kjersti Øvrebø', 'kjersti.ovrebo@example.com', '91234567', 'SUSPENDED');

-- Get user IDs for foreign key references (using email to find users)
DECLARE
    v_ola_id NUMBER;
    v_kari_id NUMBER;
    v_erik_id NUMBER;
    v_anne_id NUMBER;
    v_bjorn_id NUMBER;
    v_ase_id NUMBER;
    v_per_id NUMBER;
    v_kjersti_id NUMBER;
BEGIN
    -- Get user IDs
    SELECT user_id INTO v_ola_id FROM users WHERE email = 'ola.nordmann@example.com';
    SELECT user_id INTO v_kari_id FROM users WHERE email = 'kari.hansen@example.com';
    SELECT user_id INTO v_erik_id FROM users WHERE email = 'erik.nilsen@example.com';
    SELECT user_id INTO v_anne_id FROM users WHERE email = 'anne.larsen@example.com';
    SELECT user_id INTO v_bjorn_id FROM users WHERE email = 'bjorn.saterdal@example.com';
    SELECT user_id INTO v_ase_id FROM users WHERE email = 'ase.roed@example.com';
    SELECT user_id INTO v_per_id FROM users WHERE email = 'per.asmund.hoiby@example.com';
    SELECT user_id INTO v_kjersti_id FROM users WHERE email = 'kjersti.ovrebo@example.com';

    -- Insert test projects
    INSERT INTO projects (navn, beskrivelse, aktiv, bruker_id) VALUES 
        ('Timeføring App', 'Utvikling av timeføringsapplikasjon med Spring Boot og Oracle', 1, v_ola_id);

    INSERT INTO projects (navn, beskrivelse, aktiv, bruker_id) VALUES 
        ('Kundehåndtering System', 'CRM system for håndtering av kunderelasjoner', 1, v_kari_id);

    INSERT INTO projects (navn, beskrivelse, aktiv, bruker_id) VALUES 
        ('Rapportering Dashboard', 'Business intelligence dashboard for rapportering', 1, v_erik_id);

    INSERT INTO projects (navn, beskrivelse, aktiv, bruker_id) VALUES 
        ('Legacy Modernisering', 'Modernisering av eldre systemer', 0, v_anne_id);

    INSERT INTO projects (navn, beskrivelse, aktiv, bruker_id) VALUES 
        ('Intern Opplæring', 'Utvikling av opplæringsmateriell for nye ansatte', 1, v_ola_id);

    -- Additional projects for users with Norwegian characters
    INSERT INTO projects (navn, beskrivelse, aktiv, bruker_id) VALUES 
        ('Løsning for Søkemotoroptimalisering', 'SEO verktøy for bedre søkeresultater', 1, v_bjorn_id);

    INSERT INTO projects (navn, beskrivelse, aktiv, bruker_id) VALUES 
        ('Kvalitetssikring og Testing', 'Automatiserte testløsninger for økt kvalitet', 1, v_ase_id);

    INSERT INTO projects (navn, beskrivelse, aktiv, bruker_id) VALUES 
        ('Utviklerrammeverk for API-er', 'Standardisert rammeverk for API-utvikling', 0, v_per_id);

    INSERT INTO projects (navn, beskrivelse, aktiv, bruker_id) VALUES 
        ('Sikkerhet og Personvern', 'GDPR-kompatible løsninger for datasikkerhet', 1, v_kjersti_id);

    -- Get project IDs for time entries
    DECLARE
        v_timeapp_id NUMBER;
        v_crm_id NUMBER;
        v_dashboard_id NUMBER;
        v_legacy_id NUMBER;
        v_training_id NUMBER;
        v_seo_id NUMBER;
        v_qa_id NUMBER;
        v_framework_id NUMBER;
        v_security_id NUMBER;
    BEGIN
        SELECT project_id INTO v_timeapp_id FROM projects WHERE navn = 'Timeføring App';
        SELECT project_id INTO v_crm_id FROM projects WHERE navn = 'Kundehåndtering System';
        SELECT project_id INTO v_dashboard_id FROM projects WHERE navn = 'Rapportering Dashboard';
        SELECT project_id INTO v_legacy_id FROM projects WHERE navn = 'Legacy Modernisering';
        SELECT project_id INTO v_training_id FROM projects WHERE navn = 'Intern Opplæring';
        SELECT project_id INTO v_seo_id FROM projects WHERE navn = 'Løsning for Søkemotoroptimalisering';
        SELECT project_id INTO v_qa_id FROM projects WHERE navn = 'Kvalitetssikring og Testing';
        SELECT project_id INTO v_framework_id FROM projects WHERE navn = 'Utviklerrammeverk for API-er';
        SELECT project_id INTO v_security_id FROM projects WHERE navn = 'Sikkerhet og Personvern';

        -- Insert time entries for the last month
        -- Ola's time entries
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_timeapp_id, v_ola_id, DATE '2025-08-01', 8.0, 'Sette opp database migrasjoner og tabeller');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_timeapp_id, v_ola_id, DATE '2025-08-02', 7.5, 'Implementere brukerautentisering');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_training_id, v_ola_id, DATE '2025-08-03', 4.0, 'Lage dokumentasjon for nye utviklere');

        -- Kari's time entries
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_crm_id, v_kari_id, DATE '2025-08-01', 8.0, 'Analyse av kundekrav og funksjonell spesifikasjon');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_crm_id, v_kari_id, DATE '2025-08-02', 6.5, 'Design av brukergrensesnitt');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_crm_id, v_kari_id, DATE '2025-08-05', 8.0, 'Implementering av kunde-API');

        -- Erik's time entries
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_dashboard_id, v_erik_id, DATE '2025-08-01', 7.0, 'Sette opp datakilder og ETL prosesser');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_dashboard_id, v_erik_id, DATE '2025-08-02', 8.0, 'Utvikling av rapportingskomponenter');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_dashboard_id, v_erik_id, DATE '2025-08-06', 5.5, 'Testing og optimalisering av ytelse');

        -- Some additional entries for current week
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_timeapp_id, v_ola_id, DATE '2025-08-12', 8.0, 'Implementere REST API for timeføring');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_timeapp_id, v_ola_id, DATE '2025-08-13', 7.5, 'Sette opp sikkerhet og autorisasjon');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_crm_id, v_kari_id, DATE '2025-08-13', 6.0, 'Integrere med ekstern API for kundedata');

        -- Time entries for users with Norwegian characters
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_seo_id, v_bjorn_id, DATE '2025-08-07', 8.0, 'Analyse av søkeord og konkurrenter');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_seo_id, v_bjorn_id, DATE '2025-08-08', 6.5, 'Optimalisering av metadata og URL-struktur');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_qa_id, v_ase_id, DATE '2025-08-09', 7.5, 'Utvikling av automatiserte enhetstester');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_qa_id, v_ase_id, DATE '2025-08-10', 8.0, 'Implementering av integrasjonstester for API-er');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_security_id, v_kjersti_id, DATE '2025-08-11', 7.0, 'GDPR-analyse og risikovurdering');
        
        INSERT INTO time_entries (prosjekt_id, bruker_id, dato, timer, kommentar) VALUES 
            (v_security_id, v_kjersti_id, DATE '2025-08-14', 5.5, 'Implementering av personvernpolicyer');

        -- Commit all changes
        COMMIT;
    END;
END;
/

-- Add some additional constraints after test data is inserted
-- This ensures our test data is valid and demonstrates constraint usage

-- Create unique constraint to prevent duplicate time entries on same day/project/user
ALTER TABLE time_entries ADD CONSTRAINT uk_time_entries_unique_day 
    UNIQUE (bruker_id, prosjekt_id, dato);

-- Note: This constraint would prevent multiple entries per day per project per user
-- In a real application, you might want to allow this, but for demo purposes this shows constraint usage
