# Issue: Frontend Logging and Error Handling Plan

## Objective
Implement robust error handling and user-facing error reporting for the React TypeScript frontend, and ensure that all API errors are logged and surfaced appropriately for debugging and support.

---

## 1. Error Handling
- Use React error boundaries to catch and display UI errors.
- Centralize API error handling (e.g., in a custom fetch/axios wrapper).
- Show user-friendly, Norwegian error messages for all API and validation errors.
- Ensure all error dialogs/toasts are accessible and dismissible.
- Map backend error DTOs (e.g., `ApiError`) to frontend error displays.

## 2. Logging (Frontend)
- Log all caught errors to the browser console at `error` level (never log sensitive data).
- Optionally, integrate with a frontend error tracking service (e.g., Sentry, LogRocket) for production.
- Include correlationId from backend error responses (if present) in logs and error dialogs for support traceability.

## 3. Implementation Steps
1. Implement or update React error boundaries for global UI error capture.
2. Refactor API client to handle and surface backend errors consistently.
3. Display Norwegian error messages from backend or validation in all forms and dialogs.
4. Add logging to console and (optionally) integrate with error tracking service.
5. Ensure all error dialogs/toasts are accessible and tested.
6. Document error handling and logging conventions in frontend developer docs.

---

## Acceptance Criteria
- All API and UI errors are handled and displayed in Norwegian.
- Errors are logged to the console (and optionally to a tracking service).
- CorrelationId is shown in error dialogs if available.
- Error handling and logging are documented for frontend developers.
