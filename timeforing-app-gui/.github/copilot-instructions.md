# Copilot Instructions: Frontend GUI for Timeforing App

> **📝 Guidelines for Updates**: Keep these instructions concise and focused on frontend development patterns. Prioritize modern web development practices and actionable guidance. Maximum target: ~150 lines total.

## API contract
- All frontend API calls must adhere to the OpenAPI contract at `/home/norvald/git/timeforing-app/target/generated-openapi/api.json`.
- Prefer using the generated client (if available) or mirror its DTOs/paths when implementing service methods to ensure compatibility with the backend. Keep the contract in sync with backend changes and include the generated JSON in PRs when API changes are required.

## Stack & Architecture
- **Stack**: React 18+ + TypeScript + Vite + Tailwind CSS + React Router
- **State Management**: Zustand for global state, React Query for server state
- **Testing**: Vitest + React Testing Library + Jest DOM + User Event
- **Authentication**: OAuth2/JWT integration with backend API
- **Pattern**: Component-based architecture with custom hooks
- **Localization**: Norwegian UI text and date/time formatting
- **Export**: Excel export functionality with SheetJS/ExcelJS

## Key Standards

### Project Structure
```
src/
├── components/         # Reusable UI components
│   ├── ui/            # Base UI components (Button, Input, etc.)
│   │   └── __tests__/ # Component unit tests
│   ├── forms/         # Form components (UserForm, ProjectForm, TimeEntryForm)
│   │   └── __tests__/ # Form testing with validation
│   ├── layout/        # Layout components (Header, Sidebar, etc.)
│   │   └── __tests__/ # Layout component tests
│   ├── calendar/      # Weekly calendar components
│   │   └── __tests__/ # Calendar functionality tests
│   └── notifications/ # Toast notification components
│       └── __tests__/ # Notification testing
├── pages/             # Route-level page components
│   ├── Dashboard/     # Main dashboard with timer widget
│   │   └── __tests__/ # Page integration tests
│   ├── Register/      # User registration page
│   │   └── __tests__/ # Registration flow tests
│   ├── Profile/       # User profile management
│   │   └── __tests__/ # Profile management tests
│   ├── Projects/      # Project CRUD interface
│   │   └── __tests__/ # Project CRUD tests
│   ├── TimeEntries/   # Time tracking weekly view
│   │   └── __tests__/ # Time tracking tests
│   └── Reports/       # Excel export and reports
│       └── __tests__/ # Reports and export tests
├── hooks/             # Custom React hooks
│   └── __tests__/     # Hook testing with renderHook
├── services/          # API service layer
│   └── __tests__/     # Service testing with mocks
├── stores/            # Zustand stores
│   └── __tests__/     # Store testing and state management
├── types/             # TypeScript type definitions
├── utils/             # Utility functions (Norwegian date formatting)
│   └── __tests__/     # Utility function tests
├── assets/            # Static assets
└── styles/            # Global styles and Tailwind config
```

### TypeScript & React Conventions
- **Naming**: PascalCase for components, camelCase for functions/variables
- **Components**: Function components with TypeScript, prefer composition
- **Props**: Define interfaces for all component props
- **Hooks**: Custom hooks start with `use`, follow hooks rules
- **Types**: Export interfaces from dedicated files, use strict mode
- **Localization**: Norwegian text for UI, Norwegian date/time formats
- **Testing**: Test files in `__tests__` folders, `.test.tsx` extension

### Testing Standards
- **Coverage**: Minimum 80% code coverage for all components
- **Framework**: Vitest + React Testing Library + Jest DOM + User Event
- **Norwegian Testing**: All UI text, error messages, and dates in Norwegian
- **Accessibility**: Screen reader compatibility and keyboard navigation
- **Mobile Testing**: Touch interactions and responsive design validation
- **API Mocking**: Mock all backend service calls with realistic data

### Styling & UI
- **Tailwind CSS**: Utility-first approach, custom design system
- **Components**: Headless UI for accessibility, shadcn/ui patterns
- **Responsive**: Mobile-first design, consistent spacing scale
- **Dark Mode**: System preference detection and toggle
- **Norwegian UX**: Error messages and notifications in Norwegian

## Essential Patterns

### Component Example
```typescript
interface TimeEntryCardProps {
  entry: TimeEntry;
  onEdit: (id: string) => void;
  onDelete: (id: string) => void;
}

export const TimeEntryCard: React.FC<TimeEntryCardProps> = ({
  entry,
  onEdit,
  onDelete
}) => {
  const formatDuration = useFormatDuration();
  
  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-sm border p-4">
      <div className="flex items-center justify-between">
        <div>
          <h3 className="font-medium text-gray-900 dark:text-white">
            {entry.project.name}
          </h3>
          <p className="text-sm text-gray-600 dark:text-gray-400">
            {entry.description}
          </p>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-sm font-medium">
            {formatDuration(entry.duration)}
          </span>
          <Button
            variant="outline"
            size="sm"
            onClick={() => onEdit(entry.id)}
          >
            Edit
          </Button>
        </div>
      </div>
    </div>
  );
};
```

### Custom Hook Example
```typescript
export const useTimeEntries = () => {
  const [timeEntries] = useTimeEntriesStore();
  
  const {
    data: entries,
    isLoading,
    error,
    refetch
  } = useQuery({
    queryKey: ['timeEntries'],
    queryFn: () => timeEntriesApi.getAll(),
    staleTime: 5 * 60 * 1000, // 5 minutes
  });

  const createEntry = useMutation({
    mutationFn: timeEntriesApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['timeEntries'] });
    },
  });

  return {
    entries: entries ?? [],
    isLoading,
    error,
    refetch,
    createEntry: createEntry.mutate,
    isCreating: createEntry.isPending,
  };
};
```

### API Service Example
```typescript
export class TimeEntriesApi {
  private baseUrl = '/api/time-entries';

  async getAll(): Promise<TimeEntry[]> {
    const response = await apiClient.get(this.baseUrl);
    return response.data;
  }

  async create(entry: CreateTimeEntryRequest): Promise<TimeEntry> {
    const response = await apiClient.post(this.baseUrl, entry);
    return response.data;
  }

  async update(id: string, entry: UpdateTimeEntryRequest): Promise<TimeEntry> {
    const response = await apiClient.put(`${this.baseUrl}/${id}`, entry);
    return response.data;
  }

  async delete(id: string): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/${id}`);
  }
}

export const timeEntriesApi = new TimeEntriesApi();
```

### Zustand Store Example
```typescript
interface AuthStore {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (token: string) => void;
  logout: () => void;
  setUser: (user: User) => void;
}

export const useAuthStore = create<AuthStore>((set, get) => ({
  user: null,
  token: localStorage.getItem('auth_token'),
  isAuthenticated: !!localStorage.getItem('auth_token'),
  
  login: (token: string) => {
    localStorage.setItem('auth_token', token);
    set({ token, isAuthenticated: true });
  },
  
  logout: () => {
    localStorage.removeItem('auth_token');
    set({ token: null, user: null, isAuthenticated: false });
  },
  
  setUser: (user: User) => set({ user }),
}));
```

### Form with Validation
```typescript
const userSchema = z.object({
  navn: z.string().min(2, 'Navn må være minst 2 tegn'),
  mobil: z.string().regex(/^(\+47|0047|47)?[4-9]\d{7}$/, 'Ugyldig norsk mobilnummer'),
  epost: z.string().email('Ugyldig epost adresse'),
});

type UserFormData = z.infer<typeof userSchema>;

export const UserForm: React.FC<UserFormProps> = ({ onSubmit }) => {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<UserFormData>({
    resolver: zodResolver(userSchema),
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label className="block text-sm font-medium mb-1">
          Navn *
        </label>
        <Input
          {...register('navn')}
          placeholder="Ditt navn"
          error={errors.navn?.message}
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium mb-1">
          Mobilnummer *
        </label>
        <Input
          {...register('mobil')}
          placeholder="+47 123 45 678"
          error={errors.mobil?.message}
        />
      </div>
      
      <Button
        type="submit"
        loading={isSubmitting}
        disabled={isSubmitting}
        className="w-full"
      >
        {isSubmitting ? 'Lagrer...' : 'Lagre'}
      </Button>
    </form>
  );
};
```

### Norwegian Error Handling
```typescript
const errorMessages = {
  network: 'Kunne ikke kontakte server',
  duplicate: 'Epost addressen er allerede registrert',
  validation: 'Vennligst rett opp feilene',
  generic: 'Noe gikk galt, prøv igjen',
  success: 'Lagret!',
  deleted: 'Slettet!',
  created: 'Bruker opprettet!',
} as const;

export const useErrorHandler = () => {
  const toast = useToast();
  
  const handleError = (error: ApiError) => {
    const message = errorMessages[error.code] || errorMessages.generic;
    toast.error(message);
  };
  
  const showSuccess = (type: keyof typeof errorMessages) => {
    toast.success(errorMessages[type]);
  };
  
  return { handleError, showSuccess };
};
```

### Component Testing Example
```typescript
// UserForm.test.tsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { UserForm } from '../UserForm';

describe('UserForm', () => {
  const mockOnSubmit = vi.fn();
  
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render Norwegian form labels', () => {
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    expect(screen.getByLabelText('Navn *')).toBeInTheDocument();
    expect(screen.getByLabelText('Mobilnummer *')).toBeInTheDocument();
    expect(screen.getByLabelText('Epost *')).toBeInTheDocument();
  });

  it('should validate Norwegian mobile number format', async () => {
    const user = userEvent.setup();
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    const mobilInput = screen.getByPlaceholderText('+47 123 45 678');
    await user.type(mobilInput, '12345');
    
    await waitFor(() => {
      expect(screen.getByText('Ugyldig norsk mobilnummer')).toBeInTheDocument();
    });
  });

  it('should submit form with valid data', async () => {
    const user = userEvent.setup();
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    await user.type(screen.getByLabelText('Navn *'), 'Test Bruker');
    await user.type(screen.getByLabelText('Mobilnummer *'), '+47 12345678');
    await user.type(screen.getByLabelText('Epost *'), 'test@example.com');
    
    await user.click(screen.getByRole('button', { name: 'Lagre' }));
    
    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledWith({
        navn: 'Test Bruker',
        mobil: '+47 12345678',
        epost: 'test@example.com'
      });
    });
  });
});
```

### Hook Testing Example
```typescript
// useTheme.test.ts
import { renderHook, act } from '@testing-library/react';
import { useTheme } from '../useTheme';

describe('useTheme', () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.className = '';
  });

  it('should initialize with system theme', () => {
    const { result } = renderHook(() => useTheme());
    
    expect(result.current.theme).toBe('system');
  });

  it('should toggle to dark mode', () => {
    const { result } = renderHook(() => useTheme());
    
    act(() => {
      result.current.setTheme('dark');
    });
    
    expect(result.current.theme).toBe('dark');
    expect(document.documentElement.classList.contains('dark')).toBe(true);
    expect(localStorage.getItem('theme')).toBe('dark');
  });
});
```

### Service Testing Example
```typescript
// userService.test.ts
import { userService } from '../userService';
import { apiClient } from '../apiClient';

vi.mock('../apiClient');
const mockedApiClient = vi.mocked(apiClient);

describe('UserService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should create user successfully', async () => {
    const userData = { navn: 'Test', mobil: '+47 12345678', epost: 'test@example.com' };
    const expectedResponse = { id: '1', ...userData };
    
    mockedApiClient.post.mockResolvedValueOnce({ data: expectedResponse });
    
    const result = await userService.create(userData);
    
    expect(mockedApiClient.post).toHaveBeenCalledWith('/api/users', userData);
    expect(result).toEqual(expectedResponse);
  });

  it('should handle duplicate email error', async () => {
    const userData = { navn: 'Test', mobil: '+47 12345678', epost: 'test@example.com' };
    
    mockedApiClient.post.mockRejectedValueOnce({
      response: { status: 409, data: { message: 'Email already exists' } }
    });
    
    await expect(userService.create(userData)).rejects.toThrow();
  });
});
```

## Key Features to Implement

### Core Features
- **Time Tracking**: Start/stop timer, manual entry, time editing
- **Project Management**: Create projects, assign time entries
- **Dashboard**: Overview of today's work, recent entries, statistics
- **Reports**: Daily/weekly/monthly reports, export functionality
- **User Profile**: Settings, preferences, authentication

### UI Components
- **Timer Widget**: Prominent start/stop timer with current project
- **Time Entry List**: Sortable, filterable list with inline editing
- **Project Selector**: Dropdown with search and quick creation
- **Date Range Picker**: For reports and filtering
- **Charts**: Time distribution, productivity trends

### Technical Requirements
- **Responsive Design**: Mobile-first, tablet-friendly
- **Offline Support**: Service worker for basic functionality
- **Real-time Updates**: WebSocket for timer synchronization
- **Accessibility**: WCAG 2.1 AA compliance
- **Performance**: Code splitting, lazy loading, optimistic updates

## Priority Features (Based on GitHub Issues)

### Issue #11: Basic React Setup
- **Vite + TypeScript**: Modern build setup with hot reload
- **Tailwind CSS**: Utility-first styling framework
- **Dark Mode**: System preference detection and toggle
- **Mobile-first**: Responsive breakpoints (mobile <768px, tablet 768-1024px, desktop >1024px)

### Issue #12: User Registration & Profile (Norwegian UI)
- **Registration Page**: `/register` with navn, mobil, epost fields
- **Profile Management**: `/profile` for user settings
- **Norwegian Validation**: Norsk mobilnummer (+47), norske feilmeldinger
- **Toast Notifications**: "Bruker opprettet!", "Profil oppdatert!"

### Issue #13: Project Management CRUD
- **Project List**: `/projects` with search, filter, sort functionality
- **CRUD Operations**: Create, read, update, delete projects
- **Mobile Gestures**: Swipe actions for edit/delete on mobile
- **Confirmation Dialogs**: "Er du sikker på at du vil slette [prosjektnavn]?"

### Issue #14: Weekly Calendar View (Norwegian)
- **Norwegian Week**: Mandag-Søndag with "Man 15. jan" format
- **Quick Timer**: "Start dagen" / "Slutt dagen" functionality
- **Hour Input**: Dropdown with 0.5, 1.0, 1.5... 8.0 timer options
- **Week Navigation**: "Forrige uke" / "Neste uke" controls

### Issue #15: Excel Export & Reports
- **Reports Page**: `/reports` with period and project filters
- **Excel Download**: Generate and download .xlsx files
- **File Naming**: "Timeforing_01.01.2025-31.01.2025.xlsx" format
- **Progress Indication**: Loading states during report generation
