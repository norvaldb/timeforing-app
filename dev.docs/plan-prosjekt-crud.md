# Implementation Plan: Prosjekt CRUD API (Issue #8)

## 1. Data Model & Database
- [ ] Define `Project` entity (fields: id, userId, navn, beskrivelse, aktiv, opprettetDato, endretDato)
- [ ] Flyway migration for `projects` table (with soft delete and user FK)
- [ ] Add relation to user (user_id)
- [ ] Ensure DB constraints for validation (name length, description max)

## 2. DTOs & Contracts
- [ ] Create DTOs:
    - `CreateProjectRequest`
    - `UpdateProjectRequest`
    - `ProjectDto`
    - `ProjectListResponse` (with pagination info)
- [ ] Add validation annotations (min/max, required)

## 3. Repository Layer
- [ ] `ProjectRepository` with methods:
    - save, findById, findAllByUser, update, softDelete, existsWithTimeregistrering
    - Support pagination & sorting

## 4. Facade/Service Layer
- [ ] `ProjectFacade` interface & implementation
    - Business logic for all CRUD operations
    - Enforce user ownership, validation, and business rules (cannot delete with hours)
    - Logging for all operations

## 5. Controller Layer
- [ ] `ProjectController` with endpoints:
    - `POST /api/projects`
    - `GET /api/projects`
    - `GET /api/projects/{id}`
    - `PUT /api/projects/{id}`
    - `DELETE /api/projects/{id}`
- [ ] Use `@PreAuthorize` for security (user can only access own projects)
- [ ] Add OpenAPI annotations for all endpoints

## 6. Error Handling
- [ ] Return 404 if project not found
- [ ] Return 400 for validation errors (with Norwegian messages)
- [ ] Return 409 if trying to delete project with hours
- [ ] Return 403 for unauthorized access
- [ ] Use `@ControllerAdvice` for consistent error responses

## 7. Testing
- [ ] Unit tests for facade/service (all business rules)
- [ ] Integration tests for controller + DB (using TestContainers)
- [ ] Test all error/edge cases (validation, forbidden, conflict)

## 8. Documentation
- [ ] Ensure OpenAPI/Swagger docs are complete
- [ ] Add endpoint examples and error responses

## 9. Review & Finalize
- [ ] Code review and refactor
- [ ] Ensure logging for all operations
- [ ] Verify coverage and run all tests

---

**Note:**
- Follow same architecture and conventions as user management.
- Use Norwegian for all validation and error messages.
- Prioritize security: users can only access their own projects.
- Use paginering and sorting for project list endpoint.
