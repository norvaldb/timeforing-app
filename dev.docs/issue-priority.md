# Issue Implementation Priority

## 1. Implement stateless, claim-driven authentication model (JWT-only, no user table)
**Issue #23**
- Refactor backend to remove user table and use JWT claims for all user profile data and roles.
- Update mock token generator, documentation, and Copilot instructions.
- This is foundational for all future backend and API work.

## 2. Implementer omfattende logging og feilhåndtering
**Issue #16**
- Add structured logging, error handling, and monitoring across backend and frontend.
- Improves maintainability, debugging, and production readiness.

## 3. API: Implementer timeføring med validering og aggregering
**Issue #9**
- Core time tracking API with validation, aggregation, and business rules.
- Needed for all time entry and reporting features.

## 4. API: Implementer Excel eksport for timeføring rapporter
**Issue #10**
- Enables export of time tracking data to Excel with Norwegian formatting.
- Supports business and reporting needs.

## 5. GUI: Implementer prosjekt håndtering interface
**Issue #13**
- Frontend for project CRUD (create, read, update, delete) operations.
- Key for user/project management.

## 6. GUI: Implementer ukesvisning med hurtigstart/stopp funksjonalitet
**Issue #14**
- Weekly time tracking view with quick start/stop.
- Improves user experience for time entry.

## 7. GUI: Implementer Excel eksport og rapporter side
**Issue #15**
- Frontend for report export and download.
- Complements backend Excel export.

## 8. GUI: Implementer brukerregistrering og profil håndtering
**Issue #12**
- User registration and profile management in frontend.
- Lower priority if using stateless, claim-driven model, but may be needed for onboarding.

## 9. Identify and add tests for missing code areas (controllers, repositories, config, etc.)
**Issue #20**
- Code coverage improvements for long-term quality.
- Can be addressed incrementally after core features are stable.

---

**Recommended order:**
1. Stateless authentication (23)
2. Logging/error handling (16)
3. Core time tracking API (9)
4. Excel export API (10)
5. Project management GUI (13)
6. Weekly time tracking GUI (14)
7. Report export GUI (15)
8. User registration/profile GUI (12)
9. Test coverage (20)

> Review priorities regularly as dependencies and business needs evolve.
