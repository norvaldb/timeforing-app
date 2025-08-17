Title: feat: Accessible ConfirmDialog + tests, use in Projects

Summary

This PR adds an accessible, keyboard-navigable `ConfirmDialog` component and replaces the previous `window.confirm` usage in the `Projects` page. It includes unit tests for the dialog and updates the Projects tests to interact with the dialog instead of mocking `window.confirm`.

What changed

- New component: `src/components/ui/ConfirmDialog.tsx`
  - Focus trap implementation (Tab/Shift+Tab cycling)
  - Escape key handling to cancel
  - ARIA attributes: `role="dialog"`, `aria-modal="true"`, `aria-labelledby` and `aria-describedby`
  - Restores previously focused element on close
  - Adds `data-testid` attributes for testing: `confirm-dialog`, `confirm-ok`, `confirm-cancel`

- Updated: `src/pages/Projects/Projects.tsx`
  - Replaced `window.confirm` with `ConfirmDialog` and dialog state
  - Added `confirmDelete` and `cancelDelete` handlers

- Tests:
  - `src/components/ui/__tests__/ConfirmDialog.test.tsx` — tests rendering, callbacks, and Escape handling
  - `src/pages/Projects/__tests__/Projects.test.tsx` — updated to click dialog confirm button instead of mocking `window.confirm`

Why

- Improves accessibility and UX by providing a consistent, keyboard-usable modal with proper ARIA semantics.
- Removes reliance on blocking browser confirm dialogs and enables consistent styling and behavior across the app.

How I tested

- Ran the full Vitest suite locally: 71 tests passed.

Notes / follow-ups

- Consider adding a focus-trap library (like `focus-trap`) for more robust focus management if the dialog becomes more complex.
- Consider adding animations and better backdrop handling for small screens.

Reviewer notes

- The branch is `feat/confirm-dialog-a11y` and the PR can be opened automatically at:
  https://github.com/norvaldb/timeforing-app/pull/new/feat/confirm-dialog-a11y

