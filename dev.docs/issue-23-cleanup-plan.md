# Issue 23 Cleanup Plan: Remove User Feature and Artifacts

## Objective
Fully remove all user-related code, DTOs, repositories, and tests from the codebase to complete the stateless, claim-driven backend migration.

## Steps

### 1. Delete User Feature Code
- Remove all files in `src/main/kotlin/com/example/basespringbootapikotlin/feature/user/`:
  - `User.kt`, `UserDto.kt`, `UserProfileDtos.kt`, `RegisterUserRequest.kt`, `UserRepository.kt`, etc.

### 2. Delete User Feature Tests
- Remove all files in `src/test/kotlin/com/example/basespringbootapikotlin/feature/user/`:
  - `UserFacadeTest.kt`, and any other user-related test files.

### 3. Refactor Project and Time Entry Tests
- Update all tests in `feature/project/` and `feature/timeentry/` to:
  - Remove references to `userId`, `users` table, and user-related setup.
  - Use JWT `sub` claim (e.g., `userSub: String`) for all user identification.

### 4. Remove User References in Project/TimeEntry Code
- Ensure all code in `feature/project/` and `feature/timeentry/` uses only stateless JWT claims for user context.
- Remove any remaining user-related logic or comments.

### 5. Validate and Test
- Run `mvn clean verify` to ensure all tests pass and no user-related code remains.

---

## Status (Before Cleanup)
- [ ] User feature code present
- [ ] User feature tests present
- [ ] Project/timeentry tests reference userId and users table
- [ ] Some project/timeentry code may reference userId
- [x] Migrations are stateless and user-free

---

## Status (After Cleanup)
- [ ] All user feature code and tests deleted
- [ ] All project/timeentry code and tests refactored to use JWT claims only
- [ ] No userId or users table references remain
- [ ] All tests pass

---

## Next: Proceeding with cleanup now. This plan will be updated as each step is completed.
