import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import App from '../App';
import '@testing-library/jest-dom';

// Mock the theme store
vi.mock('@/stores/themeStore', () => ({
  useThemeStore: () => ({
    theme: 'light',
    setTheme: vi.fn(),
  }),
}));

// Mock the useTheme hook
vi.mock('@/hooks/useTheme', () => ({
  useTheme: () => ({
    theme: 'light',
    setTheme: vi.fn(),
  }),
}));

describe('App', () => {
  it('renders without crashing', () => {
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Check for app title
    expect(screen.getByText('Timeforing')).toBeInTheDocument();
    
    // Check for navigation dashboard link (more specific)
    expect(screen.getByRole('link', { name: /dashboard/i })).toBeInTheDocument();
    
    // Check for main dashboard heading
    expect(screen.getByRole('heading', { level: 1, name: /dashboard/i })).toBeInTheDocument();
    
    // Check for Norwegian navigation items
    expect(screen.getByText('Timeføring')).toBeInTheDocument();
    expect(screen.getByText('Prosjekter')).toBeInTheDocument();
    expect(screen.getByText('Rapporter')).toBeInTheDocument();
    expect(screen.getByText('Profil')).toBeInTheDocument();
  });
});
