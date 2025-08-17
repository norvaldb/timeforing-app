# User Identification and JWT Best Practices for Timeforing App

## Industry-Standard Approach

### 1. JWT Subject (`sub`) Should Be the User’s Unique ID
- The JWT `sub` claim must be the user’s unique, immutable identifier (e.g., database user ID, like `123`).
- The user’s email (`epost`) should be included as a custom claim (e.g., `email`).
- **Reason:** This is the OAuth2/JWT standard. `sub` is the subject (unique user ID), not the email. This allows email changes without breaking authentication.

### 2. Spring Security Principal Should Use the User ID
- The backend extracts the user ID from the JWT `sub` claim and uses it as the principal’s username.
- When you call `principal.username` in controllers, it will be the user ID (as expected by the current code).

### 3. Mock Token Generation
- When generating a mock token, provide both the user ID (as `sub`) and the email (as `email` claim).
- Example JWT payload:
  ```json
  {
    "sub": "123",
    "email": "ola@nordmann.no",
    "roles": ["USER"]
  }
  ```

### 4. User Creation and Test Scenario
**Recommended Test Scenario:**
1. Create a user with `epost = ola@nordmann.no` (user gets ID, e.g., `123`).
2. Generate a mock token with `sub = 123`, `email = ola@nordmann.no`.
3. Use this token to create a project.
4. The backend will associate the project with user ID `123`.

### 5. Code Changes Needed
- **MockJwtService**: Allow setting both `sub` (user ID) and `email` claim.
- **Controllers**: No change needed if you always use user ID for identification.
- **UserController**: If you want to fetch the profile by email, use the `email` claim from the JWT.

### 6. Industry Standard Summary
- `sub`: Unique user ID (never email)
- `email`: Custom claim for user’s email
- `roles`: Custom claim for authorities/roles

---

**This approach ensures robust, future-proof user identification and aligns with OAuth2/JWT best practices.**
