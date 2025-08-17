# Mock JWT Authentication System: Usage Guide (Claims-Driven Model)

This guide explains how to use the mock JWT authentication system for local development and integration testing in a stateless, claims-driven backend.
# Mock JWT Authentication System: Usage Guide

This guide explains how to use the mock JWT authentication system for local development and integration testing.

## Overview
- The mock JWT system is enabled in `dev`, `local`, and `test` Spring profiles.
- It provides a REST endpoint to issue JWT tokens for any user/roles, valid for 12 hours.
- No external OAuth2/JWT provider is required for local development or CI.
- **All user identification and roles are provided as JWT claims. No user table or user CRUD exists in the backend.**

## Endpoint
- **URL:** `POST /api/mock-auth/token`
- **Profiles:** Only available in `dev`, `local`, or `test` profiles

### Request Body
```json
{
  "sub": "user-uuid-123",
  "email": "devuser@example.com",
  "roles": ["USER", "ADMIN"]
}
```
- `sub`: The unique user identifier for the token (string, required)
- `email`: The user's email (optional, for profile display)
- `roles`: List of roles to include in the token (optional, defaults to `["USER"]`)

### Example cURL
```
curl -X POST http://localhost:8080/api/mock-auth/token \
  -H "Content-Type: application/json" \
  -d '{"sub": "user-uuid-123", "email": "devuser@example.com", "roles": ["USER", "ADMIN"]}'
```

### Response
```json
{
  "token": "<JWT_TOKEN_STRING>"
}
```
- The `token` is a signed JWT valid for 12 hours from issuance.

## Using the Token
- Add the JWT as a Bearer token in the `Authorization` header for API requests:
```
Authorization: Bearer <JWT_TOKEN_STRING>
```

## Configuration
- The signing secret is generated in code and is not configurable via `application.yml` or environment variables.
- The secret is unique to each application instance and is suitable for local development and testing only.

## Integration Testing
- Integration tests can POST to `/api/mock-auth/token` to obtain a valid JWT for any test user/role.
- Use the token in test requests to authenticate as needed.

## Security Note
- The mock JWT system is **not** enabled in production.
- Do not use the mock secret or endpoint in production environments.

## Troubleshooting
- If the endpoint is not available, ensure you are running with the correct Spring profile (`dev`, `local`, or `test`).
- The mock JWT secret is not configurable; if you need a custom secret, modify the code in `MockJwtService`.

---
For questions or issues, see the implementation in `feature/mockauth/` or contact the project maintainer.
