# User Identification and JWT Best Practices for Timeforing App

## Claims-Driven, Stateless Model (2025-08-17)

### 1. JWT Subject (`sub`) Is the User’s Unique ID
- The JWT `sub` claim is the user’s unique, immutable identifier (e.g., UUID or external ID).
- The user’s email (`email`) and other profile data are included as custom claims.
- **No user table or user CRUD exists in the backend.**

### 2. All User Data and Roles Are Extracted from JWT Claims
- The backend extracts the user ID from the JWT `sub` claim and uses it for all user-specific data.
- All profile data (name, email, phone, etc.) and roles/authorities are provided as claims.

### 3. Mock Token Generation
- When generating a mock token, provide all required claims: `sub`, `email`, `roles`, etc.
- Example JWT payload:
  ```json
  {
    "sub": "user-uuid-123",
    "email": "ola@nordmann.no",
    "roles": ["USER"]
  }
  ```

### 4. Test Scenario
- Use a mock token with the correct claims to test all endpoints.
- The backend will associate all data with the `sub` claim from the token.

### 5. Industry Standard Summary
- `sub`: Unique user ID (never email)
- `email`: Custom claim for user’s email
- `roles`: Custom claim for authorities/roles

**All user identification and authorization is claims-driven and stateless. No user table or user CRUD remains in the backend.**

---

**This approach ensures robust, future-proof user identification and aligns with OAuth2/JWT best practices.**
