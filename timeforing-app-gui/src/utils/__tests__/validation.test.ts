import { describe, it, expect } from 'vitest';
import { userFormSchema, formatMobileNumber } from '../validation';

describe('validation', () => {
  describe('userFormSchema', () => {
    it('should validate correct user data', () => {
      const validData = {
        navn: 'Test Bruker',
        mobil: '+47 41234567',
        epost: 'test@example.com',
      };

      const result = userFormSchema.safeParse(validData);
      expect(result.success).toBe(true);
      if (result.success) {
        expect(result.data).toEqual({
          navn: 'Test Bruker',
          mobil: '+4741234567',
          epost: 'test@example.com',
        });
      }
    });

    it('should reject name that is too short', () => {
      const invalidData = {
        navn: 'A',
        mobil: '+47 41234567',
        epost: 'test@example.com',
      };

      const result = userFormSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues?.[0]?.message).toBe('Navn må være minst 2 tegn');
      }
    });

    it('should reject invalid Norwegian mobile numbers', () => {
      const testCases = [
        '12345',          // Too short
        '12345678901',    // Too long
        '+1 555-1234',    // Wrong country code
        'abc123456',      // Contains letters
        '47 12345678',    // Missing +
        '+47 12345',      // Too short
        '+47 12345678',   // Invalid prefix (1)
      ];

      testCases.forEach(invalidMobile => {
        const invalidData = {
          navn: 'Test Bruker',
          mobil: invalidMobile,
          epost: 'test@example.com',
        };

        const result = userFormSchema.safeParse(invalidData);
        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues?.[0]?.message).toBe('Ugyldig norsk mobilnummer');
        }
      });
    });

    it('should accept valid Norwegian mobile numbers', () => {
      const validMobiles = [
        '+47 41234567',   // Standard format
        '+47 91234567',   // 9 prefix
        '+47 48765432',   // 4 prefix
        '+47 59876543',   // 5 prefix
        '+47 67890123',   // 6 prefix
        '+47 78901234',   // 7 prefix
        '+47 89012345',   // 8 prefix
        '91234567',       // Without country code
        '004741234567',   // Alternative country code
        '4741234567',     // Country code without +
      ];

      validMobiles.forEach(validMobile => {
        const validData = {
          navn: 'Test Bruker',
          mobil: validMobile,
          epost: 'test@example.com',
        };

        const result = userFormSchema.safeParse(validData);
        expect(result.success).toBe(true);
      });
    });

    it('should reject invalid email addresses', () => {
      const invalidEmails = [
        'invalid-email',
        '@example.com',
        'test@',
        'test.example.com',
        'test@.com',
      ];

      invalidEmails.forEach(invalidEmail => {
        const invalidData = {
          navn: 'Test Bruker',
          mobil: '+47 41234567',
          epost: invalidEmail,
        };

        const result = userFormSchema.safeParse(invalidData);
        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues?.[0]?.message).toBe('Ugyldig epost adresse');
        }
      });

      // Test empty email separately - it should give "required" error first
      const emptyEmailData = {
        navn: 'Test Bruker',
        mobil: '+47 41234567',
        epost: '',
      };

      const result = userFormSchema.safeParse(emptyEmailData);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues?.[0]?.message).toBe('Epost er påkrevd');
      }
    });

    it('should accept valid email addresses', () => {
      const validEmails = [
        'test@example.com',
        'user.name@domain.co.uk',
        'test+label@example.org',
        'test123@test123.com',
        'a@b.co',
      ];

      validEmails.forEach(validEmail => {
        const validData = {
          navn: 'Test Bruker',
          mobil: '+47 41234567',
          epost: validEmail,
        };

        const result = userFormSchema.safeParse(validData);
        expect(result.success).toBe(true);
      });
    });

    it('should reject empty required fields', () => {
      const emptyData = {
        navn: '',
        mobil: '',
        epost: '',
      };

      const result = userFormSchema.safeParse(emptyData);
      expect(result.success).toBe(false);
      if (!result.success) {
        const messages = result.error.issues.map(issue => issue.message);
        expect(messages).toContain('Navn er påkrevd');
        expect(messages).toContain('Mobilnummer er påkrevd');
        expect(messages).toContain('Epost er påkrevd');
      }
    });
  });

  describe('formatMobileNumber', () => {
    it('should format 8-digit Norwegian mobile number', () => {
      expect(formatMobileNumber('41234567')).toBe('+47 412 34 567');
      expect(formatMobileNumber('91234567')).toBe('+47 912 34 567');
    });

    it('should handle numbers with existing country code', () => {
      expect(formatMobileNumber('+4741234567')).toBe('+47 412 34 567');
      expect(formatMobileNumber('4741234567')).toBe('+47 412 34 567');
      expect(formatMobileNumber('004741234567')).toBe('+47 412 34 567');
    });

    it('should handle numbers with spaces', () => {
      expect(formatMobileNumber('412 34 567')).toBe('+47 412 34 567');
      expect(formatMobileNumber('+47 412 34 567')).toBe('+47 412 34 567');
    });

    it('should return original string for invalid inputs', () => {
      expect(formatMobileNumber('abc')).toBe('abc');
      expect(formatMobileNumber('123')).toBe('123');
      expect(formatMobileNumber('')).toBe('');
      expect(formatMobileNumber('12345678901')).toBe('12345678901');
    });

    it('should handle partial input during typing', () => {
      expect(formatMobileNumber('1')).toBe('1');
      expect(formatMobileNumber('12')).toBe('12');
      expect(formatMobileNumber('123')).toBe('123');
      expect(formatMobileNumber('1234')).toBe('1234');
      expect(formatMobileNumber('12345')).toBe('12345');
      expect(formatMobileNumber('123456')).toBe('123456');
      expect(formatMobileNumber('1234567')).toBe('1234567');
    });

    it('should preserve formatting for already formatted numbers', () => {
      expect(formatMobileNumber('+47 123 45 678')).toBe('+47 123 45 678');
      expect(formatMobileNumber('+47 912 34 567')).toBe('+47 912 34 567');
    });
  });
});
