import '@testing-library/jest-dom';
import { vi, afterEach, beforeAll, afterAll, beforeEach } from 'vitest';
import React from 'react';
import { createRoot } from 'react-dom/client';
import ConfirmProvider from '@/components/confirm/ConfirmProvider';

// Mock APIs that may not be available in test environment
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(), // deprecated
    removeListener: vi.fn(), // deprecated
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});

// Mock IntersectionObserver
global.IntersectionObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn(),
}));

// Mock ResizeObserver
global.ResizeObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn(),
}));

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
  length: 0,
  key: vi.fn(),
} as unknown as Storage;
Object.defineProperty(window, 'localStorage', { value: localStorageMock });

// Mock sessionStorage
const sessionStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
  length: 0,
  key: vi.fn(),
} as unknown as Storage;
Object.defineProperty(window, 'sessionStorage', { value: sessionStorageMock });

// Mock navigator.clipboard
Object.assign(navigator, {
  clipboard: {
    writeText: vi.fn(),
    readText: vi.fn(),
  },
});

// Mock window.location methods safely
const mockLocation = {
  ...window.location,
  assign: vi.fn(),
  replace: vi.fn(),
  reload: vi.fn(),
};
Object.defineProperty(window, 'location', {
  value: mockLocation,
  writable: true,
});

// Clean up after each test
afterEach(() => {
  vi.clearAllMocks();
  localStorageMock.clear();
  sessionStorageMock.clear();
});

// Global test utilities
global.scrollTo = vi.fn();

// Mock fetch for API calls
global.fetch = vi.fn();

// Console error suppression for known issues
const originalError = console.error;
beforeAll(() => {
  console.error = (...args: any[]) => {
    if (
      typeof args[0] === 'string' &&
      args[0].includes('Warning: ReactDOM.render is deprecated')
    ) {
      return;
    }
    originalError.call(console, ...args);
  };
});

afterAll(() => {
  console.error = originalError;
});

// Mount a global ConfirmProvider so components using useConfirm() in tests
// have a provider available without wrapping every single render. The
// provider renders a ConfirmDialog into the DOM which tests can interact
// with via Testing Library.
// Mount/unmount a ConfirmProvider for each test so dialog state is isolated
// between tests. We try to use React 18's createRoot when available.
let __test_root_container: HTMLDivElement | null = null;
let __test_root: any = null;

beforeEach(() => {
  __test_root_container = document.createElement('div');
  __test_root_container.id = 'test-root';
  document.body.appendChild(__test_root_container);
  try {
    __test_root = createRoot(__test_root_container);
    __test_root.render(React.createElement(ConfirmProvider, null, React.createElement('div')));
  } catch (e) {
    __test_root = null;
  }
});

afterEach(() => {
  // Unmount the provider and remove container to clean up any open dialogs
  try {
    if (__test_root && typeof __test_root.unmount === 'function') {
      __test_root.unmount();
    }
  } catch (e) {
    // ignore
  }
  if (__test_root_container && __test_root_container.parentNode) {
    __test_root_container.parentNode.removeChild(__test_root_container);
  }
  __test_root = null;
  __test_root_container = null;
});
