# Feature-Based Refactor Plan

## Goal
Refactor the codebase to organize by feature (user, project, auth, etc.) instead of by technical layer (controller, facade, repository, etc.), for both production and test code.

---

## 1. Directory Structure

**Production code:**
- `src/main/kotlin/com/example/basespringbootapikotlin/feature/<feature>/`
  - `controller/`
  - `facade/`
  - `repository/`
  - `dto/`
  - `model/`
  - (other as needed)

**Test code:**
- `src/test/kotlin/com/example/basespringbootapikotlin/feature/<feature>/`
  - (mirror production structure)

**Common/config/exception/service**
- Place in `feature/common`, `feature/config`, `feature/exception`, `feature/service` as appropriate.

---

## 2. Migration Steps

### A. User Feature
- Move:
  - `controller/UserController.kt` → `feature/user/controller/UserController.kt`
  - `facade/UserFacade.kt` → `feature/user/facade/UserFacade.kt`
  - `repository/UserRepository.kt` → `feature/user/repository/UserRepository.kt`
  - `model/User.kt` → `feature/user/model/User.kt`
  - `dto/UserDto.kt`, `dto/UserProfileDtos.kt`, `dto/RegisterUserRequest.kt` → `feature/user/dto/`
- Update all imports and references.
- Move tests:
  - `facade/UserFacadeTest.kt` → `feature/user/facade/UserFacadeTest.kt`

### B. Project Feature
- Move:
  - `controller/ProjectController.kt` → `feature/project/controller/ProjectController.kt`
  - `facade/ProjectFacade.kt`, `facade/ProjectFacadeImpl.kt` → `feature/project/facade/`
  - `repository/ProjectRepository.kt` → `feature/project/repository/ProjectRepository.kt`
  - `model/Project.kt` → `feature/project/model/Project.kt`
  - `dto/ProjectDtos.kt` → `feature/project/dto/ProjectDtos.kt`
- Update all imports and references.
- Move tests:
  - `controller/ProjectControllerIT.kt` → `feature/project/controller/ProjectControllerIT.kt`
  - `facade/ProjectFacadeTest.kt` → `feature/project/facade/ProjectFacadeTest.kt`

### C. Auth Feature (if applicable)
- Move any future or existing auth-related code to `feature/auth/`

### D. Common/Config/Exception/Service
- Move:
  - `config/*` → `feature/config/`
  - `exception/*` → `feature/exception/`
  - `service/*` → `feature/service/`
- Update all imports and references.
- Move related tests.

### E. Test Utilities
- Move test configs (e.g., `controller/OracleTestContainerConfig.kt`) to the relevant feature or a shared test util location.

---

## 3. Update Imports and References
- Update all package declarations and imports in moved files.
- Update references in other files to new package locations.

---

## 4. Validation
- Run `mvn clean verify` to ensure all tests pass.
- Manually test API endpoints.
- Update documentation if needed.

---

## 5. Commit & Push
- Commit in logical steps (e.g., user feature, then project, then common, etc.)
- Push to remote and verify CI passes.

---

## 6. Communication
- Update issue #21 with progress and checklist.
- Announce refactor completion and any breaking changes to the team.

---

## Checklist
- [ ] User feature migrated
- [ ] Project feature migrated
- [ ] Common/config/exception/service migrated
- [ ] Tests migrated
- [ ] Imports/references updated
- [ ] All tests pass
- [ ] Documentation updated
- [ ] Issue #21 updated

---

*This plan ensures a safe, incremental, and traceable migration to a feature-based structure.*
