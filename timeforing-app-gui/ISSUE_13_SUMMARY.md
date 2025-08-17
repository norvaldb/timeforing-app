# Issue #13 — Project CRUD (frontend) — Status Summary

Preamble
- I read the GitHub issue before starting implementation and created an initial frontend scaffold for Project CRUD.

What's done (high level)
- Created a minimal `projectService` that wraps API calls to `/api/projects`.
- Added a `Project` type definition for the frontend DTOs.
- Implemented a basic `Projects` page that fetches and lists projects on mount.
- Created a feature branch `feature/issue-13` and pushed the initial changes.

Files added / edited
- Added: `src/services/projectService.ts` — GET/POST/PUT/DELETE wrappers using the existing `apiClient`.
- Added: `src/types/project.ts` — Project DTO type definitions.
- Edited: `src/pages/Projects/Projects.tsx` — fetches projects and renders a simple list (placeholder for full CRUD UI).

How to run locally (frontend)
1. Ensure backend is running and `target/generated-openapi/api.json` is available if you rely on generated client contracts.
2. From repository root:

```bash
cd timeforing-app-gui
npm install
npm run dev
```

3. Open the app at http://localhost:5173 and navigate to the Projects page (route depends on app router configuration).

Validation steps
- The Projects page should fetch `/api/projects` and show a list of projects returned by the backend.
- If the backend is not running, the page will show an error or empty list — start the API (see repo README / `./scripts/start-api.sh`).

Pending work / next steps
- Implement create/edit modal UI and wire `projectService.create` and `projectService.update`.
- Add delete confirmation and wire `projectService.delete`.
- Add form validation and Norwegian localization for project fields.
- Add unit tests (Vitest) for `projectService` and basic component tests for `Projects` page.
- Create a PR from `feature/issue-13` and request review; attach `target/generated-openapi/api.json` if the work depends on API contract changes.

Notes
- The current implementation is intentionally minimal to provide a quick, testable integration point with the backend. It follows the project's guidance to use `target/generated-openapi/api.json` as the contract when implementing API calls.
- If you want, I can continue by implementing the full CRUD UI (create/edit/delete), add tests, and open a PR.

Status: In progress — initial scaffold completed and pushed to remote on branch `feature/issue-13`.
