# Testing Guide for Timeforing App Frontend

## 🧪 Testing Framework & Setup

### Stack
- **Vitest** - Fast unit test runner (Vite-native)
- **React Testing Library** - Component testing with user-centric approach
- **Jest DOM** - Custom matchers for DOM testing  
- **User Event** - Realistic user interaction simulation
- **Coverage reporting** - Built-in with Vitest

### Commands
```bash
npm test              # Run all tests
npm run test:watch    # Run tests in watch mode
npm run test:coverage # Generate coverage report
```

## 📁 Test File Structure

```
src/
├── components/
│   ├── ui/
│   │   ├── Button.tsx
│   │   └── __tests__/
│   │       └── Button.test.tsx
│   ├── forms/
│   │   ├── UserForm.tsx
│   │   └── __tests__/
│   │       └── UserForm.test.tsx
│   └── layout/
│       ├── Header.tsx
│       └── __tests__/
│           └── Header.test.tsx
├── pages/
│   ├── Dashboard/
│   │   ├── Dashboard.tsx
│   │   └── __tests__/
│   │       └── Dashboard.test.tsx
├── hooks/
│   ├── useTheme.ts
│   └── __tests__/
│       └── useTheme.test.ts
├── services/
│   ├── userService.ts
│   └── __tests__/
│       └── userService.test.ts
├── utils/
│   ├── norwegianDate.ts
│   └── __tests__/
│       └── norwegianDate.test.ts
└── test/
    ├── setup.ts      # Test environment setup
    └── utils.tsx     # Norwegian testing utilities
```

## 🎯 Coverage Targets

- **Overall coverage**: Minimum 80%
- **Critical components**: 90%+ (forms, authentication, time tracking)
- **Utility functions**: 95%+ (date formatting, validation)
- **API services**: 85%+ (all service methods)

## 🇳🇴 Norwegian Testing Patterns

### 1. Norwegian Text Testing
```typescript
import { render, screen } from '@testing-library/react';
import { norwegianText } from '@/test/utils';

describe('UserForm', () => {
  it('should display Norwegian form labels', () => {
    render(<UserForm />);
    
    expect(screen.getByLabelText('Navn *')).toBeInTheDocument();
    expect(screen.getByLabelText('Mobilnummer *')).toBeInTheDocument();
    expect(screen.getByLabelText('Epost *')).toBeInTheDocument();
  });

  it('should show Norwegian error messages', async () => {
    render(<UserForm />);
    
    // Submit empty form
    fireEvent.click(screen.getByRole('button', { name: norwegianText.lagre }));
    
    await waitFor(() => {
      expect(screen.getByText(norwegianText.error.navnMåVæreMinst2Tegn)).toBeInTheDocument();
    });
  });
});
```

### 2. Norwegian Mobile Number Validation
```typescript
import { isValidNorwegianMobile } from '@/test/utils';

describe('Norwegian mobile validation', () => {
  it('should validate Norwegian mobile numbers', () => {
    expect(isValidNorwegianMobile('+47 12345678')).toBe(true);
    expect(isValidNorwegianMobile('47 12345678')).toBe(true);
    expect(isValidNorwegianMobile('12345678')).toBe(true);
    expect(isValidNorwegianMobile('123456789')).toBe(false);
    expect(isValidNorwegianMobile('+46 12345678')).toBe(false);
  });
});
```

### 3. Norwegian Date Formatting
```typescript
import { formatNorwegianDate } from '@/test/utils';

describe('Norwegian date formatting', () => {
  it('should format dates in Norwegian', () => {
    const date = new Date('2025-01-15');
    expect(formatNorwegianDate(date)).toBe('Ons 15. Jan');
  });

  it('should display Norwegian weekdays', () => {
    render(<WeeklyCalendar />);
    
    expect(screen.getByText('Mandag')).toBeInTheDocument();
    expect(screen.getByText('Tirsdag')).toBeInTheDocument();
    // ... etc for all weekdays
  });
});
```

## 🔧 Component Testing Patterns

### 1. Form Testing
```typescript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { UserForm } from '../UserForm';
import { generateTestUser } from '@/test/utils';

describe('UserForm', () => {
  const mockOnSubmit = vi.fn();
  
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should submit valid form data', async () => {
    const user = userEvent.setup();
    const testUser = generateTestUser();
    
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    await user.type(screen.getByLabelText('Navn *'), testUser.navn);
    await user.type(screen.getByLabelText('Mobilnummer *'), testUser.mobil);
    await user.type(screen.getByLabelText('Epost *'), testUser.epost);
    
    await user.click(screen.getByRole('button', { name: 'Lagre' }));
    
    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledWith(testUser);
    });
  });

  it('should validate required fields', async () => {
    const user = userEvent.setup();
    
    render(<UserForm onSubmit={mockOnSubmit} />);
    
    await user.click(screen.getByRole('button', { name: 'Lagre' }));
    
    await waitFor(() => {
      expect(screen.getByText('Navn må være minst 2 tegn')).toBeInTheDocument();
    });
  });
});
```

### 2. Hook Testing
```typescript
import { renderHook, act } from '@testing-library/react';
import { useTheme } from '../useTheme';

describe('useTheme', () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.className = '';
  });

  it('should toggle theme and persist preference', () => {
    const { result } = renderHook(() => useTheme());
    
    expect(result.current.theme).toBe('system');
    
    act(() => {
      result.current.setTheme('dark');
    });
    
    expect(result.current.theme).toBe('dark');
    expect(document.documentElement.classList.contains('dark')).toBe(true);
    expect(localStorage.getItem('theme')).toBe('dark');
  });
});
```

### 3. API Service Testing
```typescript
import { userService } from '../userService';
import { apiClient } from '../apiClient';
import { mockApiResponses, generateTestUser } from '@/test/utils';

vi.mock('../apiClient');
const mockedApiClient = vi.mocked(apiClient);

describe('UserService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should create user successfully', async () => {
    const userData = generateTestUser();
    const expectedResponse = mockApiResponses.user.success;
    
    mockedApiClient.post.mockResolvedValueOnce({ data: expectedResponse });
    
    const result = await userService.create(userData);
    
    expect(mockedApiClient.post).toHaveBeenCalledWith('/api/users', userData);
    expect(result).toEqual(expectedResponse);
  });

  it('should handle duplicate email error', async () => {
    const userData = generateTestUser();
    
    mockedApiClient.post.mockRejectedValueOnce(mockApiResponses.user.duplicateEmail);
    
    await expect(userService.create(userData)).rejects.toThrow();
  });
});
```

### 4. Page Integration Testing
```typescript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Register } from '../Register';
import { mockApiResponses } from '@/test/utils';

// Mock the service
vi.mock('@/services/userService', () => ({
  userService: {
    create: vi.fn(),
  },
}));

describe('Register Page', () => {
  it('should complete registration flow', async () => {
    const mockCreate = vi.mocked(userService.create);
    mockCreate.mockResolvedValueOnce(mockApiResponses.user.success);
    
    const user = userEvent.setup();
    
    render(<Register />);
    
    // Fill form
    await user.type(screen.getByLabelText('Navn *'), 'Test Bruker');
    await user.type(screen.getByLabelText('Mobilnummer *'), '+47 12345678');
    await user.type(screen.getByLabelText('Epost *'), 'test@example.com');
    
    // Submit
    await user.click(screen.getByRole('button', { name: 'Lagre' }));
    
    // Verify success
    await waitFor(() => {
      expect(screen.getByText('Bruker opprettet!')).toBeInTheDocument();
    });
    
    expect(mockCreate).toHaveBeenCalledWith({
      navn: 'Test Bruker',
      mobil: '+47 12345678',
      epost: 'test@example.com'
    });
  });
});
```

## 📱 Mobile & Responsive Testing

### Touch Interactions
```typescript
import { fireEvent } from '@testing-library/react';

describe('Mobile interactions', () => {
  it('should handle swipe gestures on project cards', () => {
    render(<ProjectCard project={mockProject} />);
    
    const card = screen.getByTestId('project-card');
    
    // Simulate swipe left
    fireEvent.touchStart(card, { touches: [{ clientX: 100, clientY: 0 }] });
    fireEvent.touchMove(card, { touches: [{ clientX: 50, clientY: 0 }] });
    fireEvent.touchEnd(card);
    
    expect(screen.getByText('Slett')).toBeInTheDocument();
  });
});
```

### Responsive Design
```typescript
describe('Responsive design', () => {
  it('should display mobile navigation on small screens', () => {
    // Mock mobile viewport
    Object.defineProperty(window, 'innerWidth', { value: 500 });
    
    render(<Header />);
    
    expect(screen.getByRole('button', { name: 'Open menu' })).toBeInTheDocument();
    expect(screen.queryByRole('navigation')).not.toBeInTheDocument();
  });
});
```

## ♿ Accessibility Testing

### Screen Reader Support
```typescript
describe('Accessibility', () => {
  it('should have proper ARIA labels', () => {
    render(<TimerWidget />);
    
    expect(screen.getByRole('button', { name: 'Start timer' })).toBeInTheDocument();
    expect(screen.getByLabelText('Current project')).toBeInTheDocument();
  });

  it('should support keyboard navigation', async () => {
    const user = userEvent.setup();
    
    render(<ProjectList />);
    
    // Tab through project cards
    await user.tab();
    expect(screen.getByText('Frontend Utvikling')).toHaveFocus();
    
    // Enter should open project
    await user.keyboard('{Enter}');
    expect(mockOnEdit).toHaveBeenCalled();
  });
});
```

## 🚀 Performance Testing

### Large Dataset Handling
```typescript
describe('Performance', () => {
  it('should handle large time entry lists efficiently', () => {
    const largeTimeEntryList = Array.from({ length: 1000 }, (_, i) => 
      generateTestTimeEntry({ id: i.toString() })
    );
    
    const startTime = performance.now();
    render(<TimeEntryList entries={largeTimeEntryList} />);
    const endTime = performance.now();
    
    expect(endTime - startTime).toBeLessThan(100); // Should render in <100ms
  });
});
```

## 🔍 Test Debugging

### Debug Failing Tests
```typescript
import { screen, logRoles } from '@testing-library/react';

describe('Debug example', () => {
  it('should debug component structure', () => {
    render(<ComplexComponent />);
    
    // Log all roles for debugging
    logRoles(screen.getByTestId('container'));
    
    // Take snapshot for visual debugging
    expect(screen.getByTestId('container')).toMatchSnapshot();
  });
});
```

### Common Test Utilities
```typescript
// Wait for async operations
await waitFor(() => {
  expect(screen.getByText('Loading...')).not.toBeInTheDocument();
});

// Find elements by Norwegian text
const saveButton = screen.getByRole('button', { name: /lagre/i });
const nameField = screen.getByLabelText(/navn/i);

// Check for Norwegian error messages
expect(screen.getByText(/ugyldig.*mobilnummer/i)).toBeInTheDocument();
```

## 📋 Testing Checklist

For each component/feature, ensure:

- [ ] **Rendering**: Component renders without crashing
- [ ] **Props**: All props are handled correctly
- [ ] **User interactions**: Click, type, submit work as expected
- [ ] **Norwegian text**: All UI text is in Norwegian
- [ ] **Validation**: Form validation shows Norwegian error messages
- [ ] **API calls**: Service calls are mocked and tested
- [ ] **Loading states**: Loading spinners and disabled states work
- [ ] **Error states**: Error handling displays Norwegian messages
- [ ] **Accessibility**: ARIA labels and keyboard navigation
- [ ] **Mobile**: Touch interactions and responsive design
- [ ] **Theme**: Light/dark mode compatibility

## 🎯 Coverage Reporting

Generate coverage reports:
```bash
npm run test:coverage
```

View coverage in browser:
```bash
open coverage/index.html
```

Critical files requiring high coverage:
- Form validation logic (95%+)
- API service classes (90%+)
- Norwegian date/time utilities (95%+)
- Authentication flows (90%+)
- Timer functionality (90%+)
