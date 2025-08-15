import { z } from 'zod';

// Norwegian mobile number validation regex
// Accepts: +47 12345678, 47 12345678, 12345678, +47 123 45 678
const norwegianMobileRegex = /^(\+47|0047|47)?[\s]?[4-9]\d{7}$/;

// Norwegian error messages
const errorMessages = {
  navn: {
    required: 'Navn er påkrevd',
    min: 'Navn må være minst 2 tegn',
    max: 'Navn kan ikke være lenger enn 50 tegn',
  },
  mobil: {
    required: 'Mobilnummer er påkrevd',
    invalid: 'Ugyldig norsk mobilnummer',
  },
  epost: {
    required: 'Epost er påkrevd',
    invalid: 'Ugyldig epost adresse',
  },
} as const;

// User form validation schema
export const userFormSchema = z.object({
  navn: z
    .string({ required_error: errorMessages.navn.required })
    .min(1, errorMessages.navn.required)
    .min(2, errorMessages.navn.min)
    .max(50, errorMessages.navn.max)
    .trim(),
  
  mobil: z
    .string({ required_error: errorMessages.mobil.required })
    .min(1, errorMessages.mobil.required)
    .regex(norwegianMobileRegex, errorMessages.mobil.invalid)
    .transform((val) => {
      // Normalize mobile number format
      const cleaned = val.replace(/\s/g, '');
      if (cleaned.startsWith('+47')) return cleaned;
      if (cleaned.startsWith('47')) return `+${cleaned}`;
      if (cleaned.startsWith('0047')) return `+${cleaned.slice(2)}`;
      return `+47${cleaned}`;
    }),
  
  epost: z
    .string({ required_error: errorMessages.epost.required })
    .min(1, errorMessages.epost.required)
    .email(errorMessages.epost.invalid)
    .toLowerCase()
    .trim(),
});

// Profile update schema (all fields optional)
export const profileUpdateSchema = z.object({
  navn: z
    .string()
    .min(2, errorMessages.navn.min)
    .max(50, errorMessages.navn.max)
    .trim()
    .optional(),
  
  mobil: z
    .string()
    .regex(norwegianMobileRegex, errorMessages.mobil.invalid)
    .transform((val) => {
      if (!val) return undefined;
      const cleaned = val.replace(/\s/g, '');
      if (cleaned.startsWith('+47')) return cleaned;
      if (cleaned.startsWith('47')) return `+${cleaned}`;
      if (cleaned.startsWith('0047')) return `+${cleaned.slice(2)}`;
      return `+47${cleaned}`;
    })
    .optional(),
  
  epost: z
    .string()
    .email(errorMessages.epost.invalid)
    .toLowerCase()
    .trim()
    .optional(),
});

// Export types
export type UserFormData = z.infer<typeof userFormSchema>;
export type ProfileUpdateData = z.infer<typeof profileUpdateSchema>;

// Export error messages for use in components
export { errorMessages };

// Utility function to validate Norwegian mobile number
export const isValidNorwegianMobile = (mobile: string): boolean => {
  return norwegianMobileRegex.test(mobile);
};

// Utility function to format mobile number for display
export const formatMobileNumber = (mobile: string): string => {
  const cleaned = mobile.replace(/\s/g, '');
  
  if (cleaned.startsWith('+47')) {
    const number = cleaned.slice(3);
    return `+47 ${number.slice(0, 3)} ${number.slice(3, 5)} ${number.slice(5)}`;
  }
  
  if (cleaned.length === 8) {
    return `${cleaned.slice(0, 3)} ${cleaned.slice(3, 5)} ${cleaned.slice(5)}`;
  }
  
  return mobile;
};
