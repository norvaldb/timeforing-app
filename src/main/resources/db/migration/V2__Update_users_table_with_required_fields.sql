-- Update users table to match Norwegian field requirements
-- Add missing fields: navn (name), mobil (mobile)
-- Rename fields to match Norwegian naming convention

-- Add new columns to existing users table
ALTER TABLE users ADD (
    navn VARCHAR2(255),
    mobil VARCHAR2(20)
);

-- Update column comments to reflect Norwegian naming
COMMENT ON COLUMN users.navn IS 'Brukerens fulle navn';
COMMENT ON COLUMN users.mobil IS 'Brukerens mobilnummer';
COMMENT ON COLUMN users.email IS 'Brukerens e-postadresse - må være unik';
COMMENT ON COLUMN users.created_date IS 'Opprettet dato';
COMMENT ON COLUMN users.updated_date IS 'Sist endret dato';

-- Create index for name searches
CREATE INDEX idx_users_navn ON users(navn);

-- Add constraint for mobile number format (Norwegian mobile numbers)
ALTER TABLE users ADD CONSTRAINT ck_users_mobil_format 
    CHECK (mobil IS NULL OR REGEXP_LIKE(mobil, '^(\+47)?[0-9]{8}$'));
