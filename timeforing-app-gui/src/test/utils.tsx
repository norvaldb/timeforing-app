import { render, RenderOptions } from '@testing-library/react';
import { ReactElement } from 'react';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

// Custom render function with providers
const AllTheProviders = ({ children }: { children: React.ReactNode }) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
      mutations: {
        retry: false,
      },
    },
  });

  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        {children}
      </BrowserRouter>
    </QueryClientProvider>
  );
};

const customRender = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'>,
) => render(ui, { wrapper: AllTheProviders, ...options });

// Norwegian test utilities
export const norwegianText = {
  // Form labels
  navn: 'Navn',
  mobil: 'Mobilnummer',
  epost: 'Epost',
  
  // Buttons
  lagre: 'Lagre',
  avbryt: 'Avbryt',
  slett: 'Slett',
  rediger: 'Rediger',
  
  // Navigation
  dashboard: 'Dashboard',
  timeføring: 'Timeføring',
  prosjekter: 'Prosjekter',
  rapporter: 'Rapporter',
  profil: 'Profil',
  
  // Messages
  success: {
    brukerOpprettet: 'Bruker opprettet!',
    profilOppdatert: 'Profil oppdatert!',
    lagret: 'Lagret!',
    slettet: 'Slettet!',
  },
  
  error: {
    kunneIkkeKontakteServer: 'Kunne ikke kontakte server',
    epostAlleredeRegistrert: 'Epost addressen er allerede registrert',
    ugyldigNorskMobilnummer: 'Ugyldig norsk mobilnummer',
    noeGikkGalt: 'Noe gikk galt, prøv igjen',
    navnMåVæreMinst2Tegn: 'Navn må være minst 2 tegn',
    ugyldigEpostAdresse: 'Ugyldig epost adresse',
  },
  
  // Time tracking
  timer: {
    startDagen: 'Start dagen',
    sluttDagen: 'Slutt dagen',
    forrigeUke: 'Forrige uke',
    nesteUke: 'Neste uke',
  },
  
  // Date formats
  weekdays: ['Mandag', 'Tirsdag', 'Onsdag', 'Torsdag', 'Fredag', 'Lørdag', 'Søndag'],
  weekdaysShort: ['Man', 'Tir', 'Ons', 'Tor', 'Fre', 'Lør', 'Søn'],
  months: [
    'Januar', 'Februar', 'Mars', 'April', 'Mai', 'Juni',
    'Juli', 'August', 'September', 'Oktober', 'November', 'Desember'
  ],
  monthsShort: [
    'Jan', 'Feb', 'Mar', 'Apr', 'Mai', 'Jun',
    'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Des'
  ],
} as const;

// Mock API responses for testing
export const mockApiResponses = {
  user: {
    success: {
      id: '1',
      navn: 'Test Bruker',
      mobil: '+47 12345678',
      epost: 'test@example.com',
      createdAt: '2025-01-01T00:00:00Z',
      updatedAt: '2025-01-01T00:00:00Z',
    },
    duplicateEmail: {
      response: {
        status: 409,
        data: { message: 'Email already exists' }
      }
    },
    networkError: {
      message: 'Network Error',
      code: 'NETWORK_ERROR'
    }
  },
  
  project: {
    success: {
      id: '1',
      name: 'Test Prosjekt',
      description: 'Et test prosjekt',
      color: '#3b82f6',
      userId: '1',
      createdAt: '2025-01-01T00:00:00Z',
      updatedAt: '2025-01-01T00:00:00Z',
    },
    list: [
      {
        id: '1',
        name: 'Frontend Utvikling',
        description: 'React og TypeScript utvikling',
        color: '#3b82f6',
        userId: '1',
        createdAt: '2025-01-01T00:00:00Z',
        updatedAt: '2025-01-01T00:00:00Z',
      },
      {
        id: '2',
        name: 'Backend API',
        description: 'Node.js API utvikling',
        color: '#ef4444',
        userId: '1',
        createdAt: '2025-01-01T00:00:00Z',
        updatedAt: '2025-01-01T00:00:00Z',
      }
    ]
  },
  
  timeEntry: {
    success: {
      id: '1',
      projectId: '1',
      description: 'Implementert brukerregistrering',
      startTime: '2025-01-01T08:00:00Z',
      endTime: '2025-01-01T12:00:00Z',
      duration: 240, // minutes
      userId: '1',
      createdAt: '2025-01-01T00:00:00Z',
      updatedAt: '2025-01-01T00:00:00Z',
    }
  }
};

// Norwegian date formatting helpers for testing
export const formatNorwegianDate = (date: Date): string => {
  const dayName = norwegianText.weekdaysShort[date.getDay()];
  const day = date.getDate();
  const monthName = norwegianText.monthsShort[date.getMonth()];
  return `${dayName} ${day}. ${monthName}`;
};

// Validation helpers for Norwegian mobile numbers
export const isValidNorwegianMobile = (mobile: string): boolean => {
  const pattern = /^(\+47|0047|47)?[4-9]\d{7}$/;
  return pattern.test(mobile.replace(/\s/g, ''));
};

// Test data generators
export const generateTestUser = (overrides = {}) => ({
  navn: 'Test Bruker',
  mobil: '+47 12345678',
  epost: 'test@example.com',
  ...overrides,
});

export const generateTestProject = (overrides = {}) => ({
  name: 'Test Prosjekt',
  description: 'Et test prosjekt for testing',
  color: '#3b82f6',
  ...overrides,
});

export const generateTestTimeEntry = (overrides = {}) => ({
  projectId: '1',
  description: 'Test arbeid',
  startTime: new Date().toISOString(),
  endTime: new Date(Date.now() + 4 * 60 * 60 * 1000).toISOString(), // 4 hours later
  ...overrides,
});

// Wait utilities for async testing
export const waitForElement = async (getElement: () => HTMLElement | null): Promise<HTMLElement> => {
  let element = null;
  let attempts = 0;
  const maxAttempts = 50;

  while (!element && attempts < maxAttempts) {
    element = getElement();
    if (!element) {
      await new Promise(resolve => setTimeout(resolve, 100));
      attempts++;
    }
  }

  if (!element) {
    throw new Error('Element not found after waiting');
  }

  return element;
};

// Re-export everything from testing-library
export * from '@testing-library/react';
export { customRender as render };
