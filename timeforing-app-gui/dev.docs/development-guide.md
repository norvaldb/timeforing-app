# Development Guide

## 🛠️ Development Workflow

### Prerequisites
- Node.js 18+
- npm or yarn
- VS Code (recommended)
- Git

### Initial Setup
```bash
# Clone repository
git clone https://github.com/norvaldb/timeforing-app.git
cd timeforing-app/timeforing-app-gui

# Install dependencies
npm install

# Copy environment configuration
cp .env.example .env.local

# Start development server
npm run dev
```

### Available Scripts

#### Development
```bash
npm run dev          # Start development server (localhost:3000)
npm run preview      # Preview production build
```

#### Building
```bash
npm run build        # TypeScript compilation + Vite build
npm run type-check   # TypeScript type checking only
```

#### Code Quality
```bash
npm run lint         # ESLint code analysis
npm run lint:fix     # Auto-fix ESLint issues
```

#### Testing
```bash
npm run test         # Run all tests
npm run test:watch   # Run tests in watch mode
npm run test:coverage # Run tests with coverage report
```

### Development Server
- **URL**: http://localhost:3000
- **Hot Reload**: Automatic on file changes
- **Theme Toggle**: Available in header
- **Navigation**: Sidebar with all routes

## 🏗️ Architecture Overview

### Technology Stack
- **Frontend Framework**: React 18+ with TypeScript
- **Build Tool**: Vite (fast development and building)
- **Styling**: Tailwind CSS with custom design system
- **Routing**: React Router v6
- **State Management**: Zustand (global state)
- **Server State**: React Query (planned)
- **Forms**: React Hook Form + Zod validation
- **Testing**: Vitest + Testing Library

### Code Organization
```
src/
├── components/        # Reusable UI components
│   ├── ui/           # Base components (Button, Input, etc.)
│   ├── forms/        # Form-specific components
│   ├── layout/       # Layout components (Header, Sidebar)
│   ├── calendar/     # Calendar-related components (planned)
│   └── notifications/# Toast notifications (planned)
├── pages/            # Route-level page components
│   ├── Dashboard/    # Main dashboard
│   ├── Register/     # User registration (Norwegian)
│   ├── Profile/      # User profile management
│   ├── Projects/     # Project CRUD interface
│   ├── TimeEntries/  # Weekly calendar view
│   └── Reports/      # Excel export and reports
├── hooks/            # Custom React hooks
├── services/         # API service layer (planned)
├── stores/           # Zustand global stores
├── types/            # TypeScript type definitions
├── utils/            # Utility functions
├── assets/           # Static assets
└── styles/           # Global styles and CSS
```

## 🎨 Design System

### Color Palette
The application uses CSS custom properties for theming:

```css
/* Light Mode */
--primary: 221.2 83.2% 53.3%;        /* Blue #3b82f6 */
--secondary: 210 40% 96%;            /* Light gray */
--background: 0 0% 100%;             /* White */
--foreground: 222.2 84% 4.9%;        /* Dark text */

/* Dark Mode */
--primary: 217.2 91.2% 59.8%;        /* Lighter blue */
--background: 222.2 84% 4.9%;        /* Dark background */
--foreground: 210 40% 98%;           /* Light text */
```

### Responsive Breakpoints
- **Mobile**: < 768px
- **Tablet**: 768px - 1024px  
- **Desktop**: > 1024px

### Component Patterns
```typescript
// Example component structure
interface ComponentProps {
  // Props with proper TypeScript types
}

export const Component: React.FC<ComponentProps> = ({ ...props }) => {
  // Component implementation
  return (
    <div className="responsive-classes dark:dark-classes">
      {/* Content */}
    </div>
  );
};
```

## 🇳🇴 Norwegian Localization

### UI Text Guidelines
- All user-facing text in Norwegian
- Form labels and error messages in Norwegian
- Navigation items in Norwegian
- Date/time formatting using Norwegian locale

### Examples
```typescript
// Navigation
const navigation = [
  { name: 'Dashboard', href: '/' },
  { name: 'Timeføring', href: '/time-entries' },
  { name: 'Prosjekter', href: '/projects' },
  { name: 'Rapporter', href: '/reports' },
  { name: 'Profil', href: '/profile' },
];

// Error messages
const errorMessages = {
  network: 'Kunne ikke kontakte server',
  validation: 'Vennligst rett opp feilene',
  success: 'Lagret!',
};
```

## 🧪 Testing Strategy

### Testing Philosophy
- Unit tests for utilities and hooks
- Component tests for UI components
- Integration tests for user workflows
- E2E tests for critical paths (planned)

### Test Structure
```
src/
├── __tests__/        # General tests
├── components/
│   └── __tests__/    # Component-specific tests
└── test/
    └── setup.ts      # Test configuration
```

### Writing Tests
```typescript
import { render, screen } from '@testing-library/react';
import { vi } from 'vitest';
import Component from './Component';

// Mock dependencies
vi.mock('@/stores/store', () => ({
  useStore: () => mockState,
}));

describe('Component', () => {
  it('renders correctly', () => {
    render(<Component />);
    expect(screen.getByText('Expected Text')).toBeInTheDocument();
  });
});
```

## 🔧 Configuration Files

### TypeScript Configuration
- `tsconfig.json` - Main TypeScript config with strict mode
- `tsconfig.node.json` - Node.js specific config for build tools

### Build Configuration
- `vite.config.ts` - Vite development and build configuration
- `vitest.config.ts` - Testing configuration

### Code Quality
- `.eslintrc.cjs` - ESLint rules for code quality
- `.prettierrc` - Prettier formatting rules

### Styling
- `tailwind.config.js` - Tailwind CSS configuration
- `postcss.config.js` - PostCSS plugins configuration

## 🚀 Deployment

### Production Build
```bash
npm run build
```

This creates an optimized build in the `dist/` folder:
- Minified JavaScript and CSS
- Source maps for debugging
- Static assets with cache-friendly names

### Build Output
```
dist/
├── index.html          # Main HTML file
├── assets/
│   ├── index-[hash].js # Main JavaScript bundle
│   └── index-[hash].css # Main CSS bundle
└── [other assets]      # Images, icons, etc.
```

### Environment Variables
```bash
# .env.local (not committed)
VITE_API_BASE_URL=http://localhost:8080/api
VITE_AUTH_BASE_URL=http://localhost:8080/oauth2
VITE_OAUTH2_CLIENT_ID=timeforing-client
VITE_APP_LOCALE=nb-NO
```

## 🔍 Debugging

### Development Tools
- **React DevTools**: Browser extension for React debugging
- **Vite DevTools**: Built-in development server features
- **VS Code Debugger**: Configured for TypeScript debugging

### Common Issues
1. **CSS not loading**: Check PostCSS configuration
2. **TypeScript errors**: Verify path aliases in tsconfig.json
3. **Hot reload not working**: Restart development server
4. **Build failures**: Check for TypeScript strict mode issues

## 📦 Dependencies Management

### Adding Dependencies
```bash
# Production dependency
npm install package-name

# Development dependency  
npm install --save-dev package-name
```

### Key Dependencies
- **React**: UI library
- **React Router**: Navigation
- **Tailwind CSS**: Styling
- **Zustand**: State management
- **React Hook Form**: Form handling
- **Zod**: Schema validation
- **Lucide React**: Icons

## 🔄 Git Workflow

### Branch Strategy
- `main` - Production ready code
- `develop` - Development integration
- `feature/[issue-number]-description` - Feature branches

### Commit Messages
```
feat: add user registration form (issue #12)
fix: resolve theme toggle bug
docs: update development guide
style: format code with prettier
```

### Pull Request Process
1. Create feature branch from `develop`
2. Implement feature following documentation
3. Add/update tests
4. Update documentation if needed
5. Create pull request to `develop`
