# Timeforing App - Frontend GUI

A modern, responsive web frontend for the Timeforing time tracking application. Built with React, TypeScript, and Tailwind CSS to provide an intuitive and efficient time tracking experience with full Norwegian localization.

## 🚀 Project Overview

The **Timeforing App Frontend** is the user interface component of the comprehensive time tracking solution. It provides a clean, modern interface for tracking work hours, managing projects, and generating insightful reports. The frontend communicates with the Kotlin Spring Boot API backend for all data operations and features complete Norwegian language support.

## 🏗️ Architecture

### Frontend Stack
- **React 18+** - Modern React with concurrent features
- **TypeScript** - Type-safe development with strict mode
- **Vite** - Fast build tool and development server
- **Tailwind CSS** - Utility-first CSS framework
- **React Router** - Client-side routing
- **Zustand** - Lightweight state management
- **React Query** - Server state management and caching
- **React Hook Form** - Performant form handling
- **Zod** - Schema validation

### Project Structure
```
timeforing-app-gui/
├── public/                   # Static assets
├── src/
│   ├── components/           # Reusable UI components
│   │   ├── ui/              # Base components (Button, Input, etc.)
│   │   ├── forms/           # Form components (UserForm, ProjectForm)
│   │   ├── layout/          # Layout components (Header, Sidebar)
│   │   ├── calendar/        # Weekly calendar components
│   │   └── notifications/   # Toast notification system
│   ├── pages/               # Route-level page components
│   │   ├── Dashboard/       # Main dashboard with timer widget
│   │   ├── Register/        # User registration (Norwegian)
│   │   ├── Profile/         # User profile management
│   │   ├── Projects/        # Project CRUD interface
│   │   ├── TimeEntries/     # Weekly time tracking view
│   │   ├── Reports/         # Excel export and analytics
│   │   └── Settings/        # User settings and preferences
│   ├── hooks/               # Custom React hooks
│   ├── services/            # API service layer
│   ├── stores/              # Zustand stores
│   ├── types/               # TypeScript type definitions
│   ├── utils/               # Utility functions (Norwegian formatting)
│   ├── assets/              # Images, icons, etc.
│   └── styles/              # Global styles
├── .env.example             # Environment variables template
├── package.json             # Dependencies and scripts
├── tailwind.config.js       # Tailwind CSS configuration
├── vite.config.ts           # Vite configuration
└── tsconfig.json            # TypeScript configuration
```

## 🎯 Features

### Core Time Tracking (Norwegian Interface)
- **⏱️ Timer Widget** - "Start dagen" / "Slutt dagen" timer with real-time tracking
- **📝 Manual Entry** - Add time entries with Norwegian date formats
- **✏️ Quick Edit** - Inline editing of time entries
- **🏷️ Project Assignment** - Assign time entries to projects
- **📊 Live Statistics** - Real-time daily/weekly hour tracking

### User Management (Norwegian)
- **👤 User Registration** - Registration form with navn, mobil, epost
- **📱 Norwegian Validation** - Mobile number validation (+47 format)
- **⚙️ Profile Management** - User profile settings and preferences
- **🔐 Authentication** - OAuth2 integration with backend

### Project Management
- **📁 Project CRUD** - Create, read, update, delete projects
- **🎨 Color Coding** - Visual project identification
- **📈 Project Analytics** - Time spent per project analysis
- **🔍 Search & Filter** - Quick project search and filtering

### Weekly Calendar View (Norwegian)
- **� Norwegian Calendar** - Mandag-Søndag week view
- **📆 Date Formatting** - "Man 15. jan" Norwegian date format
- **⏰ Hour Input** - Dropdown with 0.5, 1.0, 1.5... 8.0 timer options
- **� Week Navigation** - "Forrige uke" / "Neste uke" controls

### Reports & Export
- **� Reports Dashboard** - Daily, weekly, monthly time reports
- **� Excel Export** - Generate and download .xlsx files
- **📋 File Naming** - "Timeforing_01.01.2025-31.01.2025.xlsx" format
- **📱 Responsive Design** - Mobile-first, tablet-friendly interface

### User Experience
- **🔄 Real-time Sync** - Live updates across sessions
- **💾 Offline Support** - Basic functionality without internet
- **🎯 Keyboard Shortcuts** - Power user efficiency
- **♿ Accessibility** - WCAG 2.1 AA compliant
- **🚀 Fast Loading** - Optimized performance with code splitting
- **🌙 Dark Mode** - System preference and manual toggle
- **🇳🇴 Norwegian Localization** - Complete Norwegian interface and error messages

## 🛠️ Technology Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| Framework | React 18+ | UI library with concurrent features |
| Language | TypeScript | Type-safe development |
| Build Tool | Vite | Fast development and building |
| Styling | Tailwind CSS | Utility-first styling |
| Routing | React Router | Client-side navigation |
| State | Zustand | Global state management |
| Server State | React Query | API data fetching and caching |
| Forms | React Hook Form | Performant form handling |
| Validation | Zod | Schema validation |
| Charts | Recharts | Data visualization |
| Icons | Lucide React | Consistent icon set |
| Testing | Vitest + Testing Library | Unit and integration testing |
| Error Handling | Error Boundaries + Toast | Norwegian error messages |
| Export | SheetJS/ExcelJS | Excel file generation |

## 🚦 Getting Started

### Prerequisites

- **Node.js 18+**
- **npm or yarn**
- **Backend API** (Timeforing Spring Boot API running)

### 1. Installation

```bash
cd timeforing-app-gui

# Install dependencies
npm install

# Copy environment variables
cp .env.example .env.local
```

### 2. Environment Configuration

Update `.env.local` with your backend API configuration:

```bash
# Backend API Configuration
VITE_API_BASE_URL=http://localhost:8080/api
VITE_AUTH_BASE_URL=http://localhost:8080/oauth2

# OAuth2 Configuration (Norwegian interface)
VITE_OAUTH2_CLIENT_ID=timeforing-client
VITE_OAUTH2_REDIRECT_URI=http://localhost:3000/auth/callback

# App Configuration
VITE_APP_TITLE=Timeforing
VITE_APP_VERSION=1.0.0
VITE_APP_LOCALE=nb-NO
```

### 3. Development

```bash
# Start development server
npm run dev

# Open browser
# http://localhost:3000
```

### 4. Building for Production

```bash
# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint

# Run tests
npm run test
```

## 🧪 Testing

### Test Configuration

```bash
# Run all tests
npm run test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage

# Run e2e tests (Playwright)
npm run test:e2e
```

### Test Structure
- **Unit Tests**: Component and hook testing
- **Integration Tests**: API integration and user flows
- **E2E Tests**: Full application workflows
- **Visual Regression**: Component visual testing

## 🎨 Design System

### Color Palette
```css
/* Primary Colors */
--color-primary-50: #eff6ff;
--color-primary-500: #3b82f6;
--color-primary-900: #1e3a8a;

/* Semantic Colors */
--color-success: #10b981;
--color-warning: #f59e0b;
--color-error: #ef4444;
--color-info: #06b6d4;
```

### Component Library
- **Buttons**: Primary, secondary, outline, ghost variants
- **Inputs**: Text, number, date, time, select components
- **Cards**: Project cards, time entry cards, stat cards
- **Navigation**: Sidebar, breadcrumbs, tabs
- **Feedback**: Toast notifications, loading states, error boundaries
- **Norwegian Forms**: Validation with Norwegian error messages

## 🔧 Development Guidelines

### Code Standards
- **TypeScript Strict Mode**: Enable all strict type checking
- **ESLint + Prettier**: Consistent code formatting
- **Component Naming**: PascalCase for components, camelCase for functions
- **File Organization**: Group by feature, not by file type
- **Custom Hooks**: Extract reusable logic, prefix with `use`

### Performance Optimization
- **Code Splitting**: Route-based and component-based splitting
- **Lazy Loading**: Images and non-critical components
- **Memoization**: React.memo, useMemo, useCallback for expensive operations
- **Bundle Analysis**: Regular bundle size monitoring
- **Caching**: Aggressive caching with React Query

### Accessibility
- **Semantic HTML**: Use proper HTML elements
- **ARIA Labels**: Comprehensive screen reader support
- **Keyboard Navigation**: Full keyboard accessibility
- **Focus Management**: Proper focus handling
- **Color Contrast**: WCAG 2.1 AA compliance

## 🔌 API Integration

### Authentication Flow
```typescript
// OAuth2 integration with backend
const useAuth = () => {
  const login = async () => {
    window.location.href = `${VITE_AUTH_BASE_URL}/authorize?client_id=${VITE_OAUTH2_CLIENT_ID}&redirect_uri=${VITE_OAUTH2_REDIRECT_URI}`;
  };

  const logout = async () => {
    await apiClient.post('/auth/logout');
    // Clear local state
  };
};
```

### API Service Layer
```typescript
// Type-safe API client
class ApiClient {
  private baseURL = process.env.VITE_API_BASE_URL;
  
  async get<T>(url: string): Promise<T> {
    // Implementation with error handling
  }
  
  async post<T>(url: string, data: any): Promise<T> {
    // Implementation with error handling
  }
}
```

## 📱 Mobile Support

### Responsive Breakpoints
- **Mobile**: < 768px
- **Tablet**: 768px - 1024px
- **Desktop**: > 1024px

### Mobile Features
- **Touch Gestures**: Swipe actions for time entries
- **Mobile Navigation**: Bottom tab bar for mobile
- **Offline Mode**: Service worker for basic offline functionality
- **PWA Ready**: Installable progressive web app

## 🚀 Deployment

### Build Configuration

```bash
# Production build
npm run build

# Environment-specific builds
npm run build:staging
npm run build:production
```

### Deployment Options
- **Vercel**: Zero-config deployment with Git integration
- **Netlify**: JAMstack hosting with form handling
- **AWS S3 + CloudFront**: Scalable static hosting
- **Docker**: Containerized deployment

### CI/CD Pipeline
```yaml
# .github/workflows/frontend-ci.yml
name: Frontend CI/CD
on:
  push:
    branches: [main, develop]
    paths: ['timeforing-app-gui/**']

jobs:
  test-and-build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '18'
          cache: 'npm'
      - run: npm ci
      - run: npm run test
      - run: npm run build
```

## 🔗 Integration with Backend

### API Endpoints
- **Authentication**: `/api/auth/*`
- **Time Entries**: `/api/time-entries/*`
- **Projects**: `/api/projects/*`
- **Users**: `/api/users/*` (with Norwegian validation)
- **Reports**: `/api/reports/*` (Excel export)

### Real-time Features
```typescript
// WebSocket integration for live timer updates
const useRealtimeTimer = () => {
  useEffect(() => {
    const ws = new WebSocket('ws://localhost:8080/ws/timer');
    
    ws.onmessage = (event) => {
      const update = JSON.parse(event.data);
      // Update timer state
    };
    
    return () => ws.close();
  }, []);
};
```

### Norwegian Error Messages
```typescript
const norwegianErrorMessages = {
  network: 'Kunne ikke kontakte server',
  duplicate: 'Epost addressen er allerede registrert',
  validation: 'Vennligst rett opp feilene',
  generic: 'Noe gikk galt, prøv igjen',
  success: 'Lagret!',
  deleted: 'Slettet!',
  created: 'Bruker opprettet!',
  profileUpdated: 'Profil oppdatert!',
} as const;
```

## 📋 Development Roadmap

### Phase 1 - Core Features ✅
- [ ] Project setup and configuration (Issue #11)
- [ ] Authentication integration
- [ ] Basic time tracking (start/stop timer)
- [ ] User registration and profile (Issue #12)
- [ ] Project management CRUD (Issue #13)
- [ ] Weekly calendar view (Issue #14)

### Phase 2 - Enhanced UX
- [ ] Excel export and reports (Issue #15)
- [ ] Advanced error handling (Issue #16)
- [ ] Comprehensive logging and notifications
- [ ] Mobile optimization and gestures
- [ ] Keyboard shortcuts and accessibility

### Phase 3 - Advanced Features
- [ ] Team collaboration features
- [ ] Advanced analytics and charts
- [ ] Integrations (Google Calendar, etc.)
- [ ] PWA functionality
- [ ] Advanced project features

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Follow the coding standards in `.github/copilot-instructions.md`
4. Add tests for new functionality
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

### Development Setup
```bash
# Clone the repository
git clone https://github.com/norvaldb/timeforing-app.git
cd timeforing-app/timeforing-app-gui

# Install dependencies
npm install

# Start development server
npm run dev

# Ensure backend API is running on localhost:8080
# See parent README.md for backend setup with Podman/Oracle
```

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](../LICENSE) file for details.

## 👨‍💻 Author

**Norvald Bjarte Algrøy**
- GitHub: [@norvaldb](https://github.com/norvaldb)
- Email: norvald@example.com

## 🔗 Links

- **Main Repository**: https://github.com/norvaldb/timeforing-app
- **Backend API Documentation**: http://localhost:8080/swagger-ui.html
- **Frontend Demo**: https://timeforing-app.vercel.app (when deployed)

---

**Happy coding! 🚀**

Built with ❤️ using React, TypeScript, and modern web technologies.
