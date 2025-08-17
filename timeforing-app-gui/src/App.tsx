import { Routes, Route } from 'react-router-dom';
import { useTheme } from '@/hooks/useTheme';

// Layout components
import { Layout } from '@/components/layout/Layout';
import { NotificationProvider } from '@/components/notifications/NotificationToast';

// Pages
import { Dashboard } from '@/pages/Dashboard/Dashboard';
import { Register } from '@/pages/Register/Register';
import { Profile } from '@/pages/Profile/Profile';
import { Projects } from '@/pages/Projects/Projects';
import { TimeEntries } from '@/pages/TimeEntries/TimeEntries';
import { Reports } from '@/pages/Reports/Reports';
import { NotFound } from '@/pages/NotFound/NotFound';

function App() {
  useTheme(); // Initialize theme system

  return (
    <NotificationProvider>
      <Layout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/register" element={<Register />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/projects" element={<Projects />} />
        <Route path="/time-entries" element={<TimeEntries />} />
        <Route path="/reports" element={<Reports />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </Layout>
    </NotificationProvider>
  );
}

export default App;
