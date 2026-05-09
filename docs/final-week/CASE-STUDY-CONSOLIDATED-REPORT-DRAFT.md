# Charity-AID Management System
## Final Case Study Consolidated Report Draft (AIT725)

**Student:** [Add Name]  
**Course:** AIT725  
**Submission Date:** May 9, 2026  
**Project:** Charity-AID Management System

---

## 1. Executive Summary

This case study documents the end-to-end design, implementation, validation, and final delivery of the Charity-AID Management System for AIT725. The origin problem was clear: charity operations are often fragmented across spreadsheets, email, and disconnected tools, which creates delays in aid approvals, weak accountability, inconsistent donor communication, and limited visibility for leadership reporting. The opportunity in this project was to transform those disconnected workflows into one integrated platform that supports secure, role-based operations for administrators, staff, case managers, donors, and leadership.

The proposed solution was a full-stack web application centered on operational reliability, traceability, and decision-quality reporting. The system supports the complete lifecycle of campaign management, beneficiary management, aid requests, donations (including recurring donations), inventory updates, notifications, and compliance auditing. From a software delivery perspective, the implementation used a layered Spring Boot architecture with role-based security, test-driven hardening, and artifact-based validation to ensure that every major requirement could be traced to evidence.

From a project management standpoint, the work was executed in iterative sprint cycles with measurable quality checkpoints. Sprint outputs included requirement implementation, integration of major modules, test scaffolding expansion, defect triage and remediation, and final quality stabilization. This cadence improved planning discipline by forcing each increment to produce both code and evidence (traceability mapping, endpoint matrix, test results, and defect logs). Risk was managed through early mocking of external services (payment, email provider, SMS), which reduced integration uncertainty while still allowing complete functional validation of internal workflows.

From a developer standpoint, the project emphasized maintainable service design, secure endpoint boundaries, and high-confidence testing. The final automated suite achieved 135 passing tests (72 unit, 44 integration, 19 system smoke) with a 100% pass rate. Controller coverage reached all 10 controllers and 70 mapped endpoints in system/integration validation. Quality outcomes were reinforced by defect analysis: five minor defects were identified and resolved before final acceptance, resulting in zero open critical, major, or minor issues at submission time.

From an analyst standpoint, the case study demonstrates practical control over requirement completeness, verification evidence, and delivery readiness. The FR traceability artifact confirms implementation status for FR-01 through FR-94. The endpoint matrix and test artifacts support verification of both functional and access-control behavior. UAT scenario execution across five user roles and 12 critical workflows produced a 100% pass outcome, indicating that business processes are executable from the perspective of real user journeys rather than only developer test paths.

Overall, the project achieved the proposal intent by converting a broad problem statement into an implemented and validated system with measurable outcomes. The case study shows successful alignment between planning, engineering, quality assurance, and stakeholder validation. The final outcome is not only a working application, but also a complete set of delivery artifacts that make the project auditable, repeatable, and defensible in an academic and professional context.

---

## 2. Project Context and Problem Statement

### 2.1 Origin Problem

The initiating problem for this case study was the operational inefficiency and risk created by manual charity workflows. Typical pain points included:
- Slow aid request processing due to manual handoffs.
- Limited accountability for role-based decisions.
- Inconsistent communication with donors and beneficiaries.
- Weak reporting visibility for leadership and compliance checks.

### 2.2 Opportunity

The opportunity was to create an integrated platform that:
- Standardizes core charity workflows end to end.
- Enforces role-based controls and auditability.
- Improves trust through timely notifications and receipt generation.
- Enables data-driven leadership decisions through reporting and KPI dashboards.

### 2.3 Solution Scope

The delivered scope included:
- Authentication and authorization with JWT and MFA support.
- User administration and account lifecycle management.
- Campaign, beneficiary, aid request, donation, and inventory modules.
- Notification templates and communication workflows.
- Audit logging and compliance reporting.
- Reporting APIs including summary metrics and CSV exports.

---

## 3. Execution: Full Project Delivery Narrative

### 3.1 Project Management Approach and Sprint Cadence

This project was executed as a structured, sprint-based case study rather than as ad hoc coding. Each sprint had a planning objective, a delivery objective, and an evidence objective.

Sprint pattern used:
- Sprint planning: define scope, prioritize features, identify dependencies and risk.
- Sprint execution: implement prioritized modules and complete integration checkpoints.
- Sprint review: verify against requirements and stakeholder use cases.
- Sprint closure: document outcomes, unresolved risks, and next-sprint adjustments.

This approach made progress measurable at both technical and management levels, which is critical for a case study that must demonstrate process quality in addition to software output.

### 3.2 Business and Functional Delivery Across the Whole Project

The project delivered a complete charity operations platform, not only a test package. Major business capabilities implemented include:
- Onboarding and access control for administrators, staff, case managers, donors, and leadership.
- Campaign lifecycle management from creation to closeout.
- Beneficiary onboarding and aid request processing through multiple status transitions.
- Donation processing for one-time and recurring contributions, including receipt generation.
- Inventory visibility and low-stock monitoring to support fulfillment operations.
- Leadership reporting through KPI and exportable data views.
- Audit and compliance logging for governance and accountability.

By finalization, FR-01 through FR-94 were implemented and traceable to code and endpoint evidence, demonstrating full functional completion against the planned requirements baseline.

### 3.3 Technical Architecture and Engineering Decisions

Core engineering decisions were made to balance speed, maintainability, and control:
- Backend framework: Java 17 with Spring Boot 3.5.13 for rapid, modular API delivery.
- Security model: JWT-based authentication with MFA support and role-constrained endpoint access.
- Layered design: controller, service, repository separation to improve clarity and maintainability.
- Data and persistence: JPA-based model with auto-migration support in development.
- External dependency strategy: payment, SMS, and email provider integration points were mocked or fallback-enabled during case-study execution to reduce instability risk.

These decisions enabled the team to continue delivering core business logic while containing infrastructure uncertainty.

### 3.4 Key Deliverables Produced During the Entire Project

The final package includes both implementation and management evidence. Key deliverables include:
- **CASE-STUDY-CONSOLIDATED-REPORT-DRAFT.md**: full narrative of problem, approach, outcomes, and reflection.
- **fr-01-to-fr-94-traceability.csv**: requirement-to-evidence mapping for all functional requirements.
- **mockmvc-endpoint-matrix.csv**: API inventory with roles, FR linkage, and execution references.
- **defect-log.csv**: issue lifecycle with severity, root cause, and remediation closure.
- **TEST-PLAN-COMPREHENSIVE.md**: testing strategy and critical test inventory.
- **UNIT-TEST-COVERAGE-SUMMARY.md**: service-level quality and test coverage summary.
- **SYSTEM-INTEGRATION-TESTING-REPORT.md**: integration/system verification outcomes.
- **UAT-EXECUTION-REPORT.md** and **uat-simulated-script.md**: role-based acceptance evidence.
- **service-unit-test-checklist.md** and **test-plan-test-cases.csv**: execution planning artifacts.

In addition, the source package was finalized for handoff and reproducibility as part of the submission bundle.

### 3.5 Challenges Encountered and How They Were Managed

The case study surfaced practical project challenges beyond coding:
- Scope control challenge: many requirements and endpoints increased drift risk.
  - Mitigation: active FR traceability and endpoint matrix tracking per sprint.
- Quality governance challenge: balancing delivery speed with reliability.
  - Mitigation: layered verification and formal defect closure discipline.
- Integration stability challenge: third-party services can derail schedule.
  - Mitigation: mocked dependencies and fallback paths to keep internal workflows testable.
- Communication challenge: translating technical progress for non-developer readers.
  - Mitigation: consolidated reporting and structured PM/developer/analyst reflections.

---

## 4. Analysis: Why the Project Was Successful Beyond Test Cases

### 4.1 PM Analysis

From a project management perspective, success came from linking every sprint to evidence. Progress was not measured only by features completed, but by whether each increment was documented, traceable, and reviewable. This reduced last-minute uncertainty and improved submission readiness.

### 4.2 Developer Analysis

From a development perspective, modular service design and explicit role boundaries reduced rework and made defects easier to isolate. The architecture supported parallel development of core modules, while traceability and endpoint mapping kept implementation aligned with requirement intent.

### 4.3 Analyst Analysis

From an analyst perspective, this project produced a clear line of sight from business need to technical outcome. Requirements were mapped, workflows were validated, and governance evidence was preserved. This allows objective claims about completeness, not subjective claims based on code volume.

### 4.4 Outcome Indicators Across the Full Project

Project-wide indicators at finalization:
- Requirements implemented and mapped: 94 functional requirements.
- Platform scope delivered: full role-based operational workflow coverage.
- Quality evidence: 135/135 automated tests passed and 12/12 UAT scenarios passed.
- Defect governance: all logged defects resolved before closure.
- Submission readiness: consolidated artifact set prepared for final PDF packaging.

The central point is that testing evidence supported the project outcome; it did not define the project outcome by itself.

---

## 5. Lessons Learned and Learning Objective Achievement

### 5.1 Lessons Learned from Managing the Whole Project

1. A case study must tell a delivery story, not only list technical outputs.
The strongest reports connect problem, approach, execution, and measurable impact in one narrative.

2. Traceability is a management tool, not a documentation afterthought.
Maintaining requirement and endpoint evidence continuously prevents drift and strengthens sprint planning.

3. Architecture decisions are project decisions.
Security model, module boundaries, and integration strategy directly influence schedule risk and quality risk.

4. Stakeholder perspective improves implementation quality.
Designing around role workflows (admin, staff, case manager, donor, leadership) improved practical usability and acceptance confidence.

5. Professional delivery requires both product and evidence.
A completed system without organized deliverables is hard to defend; a complete evidence package improves credibility and grading readiness.

### 5.2 Learning Objectives Achievement Mapping

Use this section to align wording exactly with your proposal while keeping evidence-based statements.

**Learning Objective 1: Apply project management principles in software delivery.**  
Achievement: Met through sprint planning, controlled scope execution, risk mitigation, and structured deliverable governance.

**Learning Objective 2: Build a solution that addresses a real operational problem.**  
Achievement: Met by delivering a full charity operations platform that supports user roles, requests, campaigns, donations, reporting, and compliance workflows.

**Learning Objective 3: Apply software engineering best practices.**  
Achievement: Met through layered architecture, secure access design, maintainable service boundaries, and controlled integration strategy.

**Learning Objective 4: Validate and analyze outcomes with evidence.**  
Achievement: Met through FR traceability, endpoint mapping, defect lifecycle control, automated tests, and role-based acceptance workflows.

**Learning Objective 5: Communicate a professional case-study outcome.**  
Achievement: Met by producing a consolidated report and appendix-ready artifact suite suitable for academic and professional review.

### 5.3 Reflection by Role (PM, Developer, Analyst)

- PM Reflection: Planning discipline and artifact checkpoints converted a complex project into manageable delivery increments.
- Developer Reflection: Modular design and clear security boundaries reduced regression risk while enabling faster feature completion.
- Analyst Reflection: Traceability and measurable indicators enabled defensible conclusions about readiness and objective achievement.

---

## 6. Final Outcome and Proposal Alignment

This case study delivered more than test execution. It produced a complete, role-based charity management platform and an auditable project record from problem identification through planning, implementation, risk management, verification, and final reporting. The project met its proposal intent by demonstrating technical execution, project control, and analytical rigor in a single integrated outcome. Based on implementation scope, requirement coverage, quality evidence, and final documentation readiness, this case study is aligned with AIT725 expectations and ready for final submission.

---

## 7. Class-by-Class Implementation Map

This section documents what was implemented across the full codebase so the project can be explained class by class, not only through test artifacts.

### 7.1 Application Bootstrap Classes

- CharityAidApplication: bootstraps the Spring Boot application and starts all configured modules.
- ServletInitializer: enables WAR-style deployment initialization for servlet container environments.

### 7.2 Controller Layer Implementation

- AidRequestController: implemented aid request retrieval, filtered retrieval, create, update, approve, deny, fulfill, escalate, status history retrieval, comments add/view, batch processing, and supporting document upload endpoints.
- AuditController: implemented paginated audit log retrieval endpoint with filtering.
- AuthController: implemented register, login, setup-needed check, first-admin setup, forgot password, reset password, logout, MFA setup, MFA confirm, MFA verify, and MFA disable endpoints.
- BeneficiaryController: implemented beneficiary list/search, get by id, register, update, deactivate, and beneficiary history endpoints.
- CampaignController: implemented active/all campaign listing, get by id, create, update, close, delete, bulk update notifications, leaderboard, share link, and impact metrics endpoints.
- DonationController: implemented submit donation, in-kind options, recurring donation schedule/list/cancel, donor donation history, get donation by id, donation search, donor profile, donor summary, matching-apply flow, and receipt download endpoints.
- InventoryController: implemented inventory list, low-stock list, get by id, create, update, delete, category list, inventory movement record/list, and inventory report endpoints.
- NotificationTemplateController: implemented list templates, get template, and update template endpoints.
- ReportController: implemented donation summary, campaign progress, pending requests, fulfilled requests, KPI dashboard, donation CSV export, and compliance report endpoints.
- SystemController: implemented health check, system configuration read/update, role-permission read/update endpoints.
- UserController: implemented current-user profile read/update, password change, data export, admin user listing, get user by id, create user, role change, deactivate, reactivate, suspend, and unlock endpoints.

### 7.3 Service Layer Implementation

- AidRequestService: implemented request lifecycle logic including create, update, review decisioning, fulfillment, escalation, comments, status history, batch operations, document upload, SLA breach marking, and reminder scheduling support.
- AppConfigService: implemented database-backed system configuration retrieval/update with startup default seeding, plus role-permission mapping retrieval/update.
- AuditService: implemented audit event recording, paginated audit retrieval, and compliance report generation over time windows.
- AuthService: implemented registration, setup-needed check, first-admin creation, login, logout recording, MFA lifecycle management, forgot-password token issue, and password reset logic.
- BeneficiaryService: implemented beneficiary CRUD-style lifecycle operations and beneficiary aid history retrieval.
- CampaignService: implemented campaign lifecycle operations, category filtering, campaign closure automation, milestone processing, donor bulk updates, leaderboard generation, share link generation, and campaign impact metrics.
- DonationService: implemented donation submission, recurring donation schedule/cancel/process, donor history/search, donor profile/summary, donation matching flow, and receipt byte generation orchestration.
- InventoryService: implemented inventory CRUD, low-stock retrieval, category retrieval, movement recording/history, inventory reporting, and automated low-stock checks.
- MfaService: implemented secret generation, OTP URL generation, and TOTP verification.
- NotificationService: implemented domain notifications for donation confirmations, receipt delivery, campaign alerts, aid request decisions, escalation alerts, reset links, reminders, bulk updates, high-value alerts, and generic system notifications.
- NotificationTemplateService: implemented template retrieval/update plus subject/body variable rendering.
- ReceiptService: implemented PDF receipt generation for donation transactions.
- ReportService: implemented donation summary, campaign progress, pending/fulfilled aid reports, KPI dashboard metrics, CSV export generation, and automated report export task.
- UserService: implemented user administration and account lifecycle including list/get/create, role change, deactivate/reactivate/suspend/unlock, profile update, password change, and data export.

### 7.4 Security and Platform Implementation

- JwtAuthFilter: implemented request authentication filtering for JWT token-based authorization flow.
- JwtUtil: implemented token generation (auth and MFA challenge), claim extraction, MFA token classification, and token validation.
- RateLimitFilter: implemented request throttling filter for API protection against high-frequency abuse.
- SecurityConfig: implemented security filter chain, password encoder bean, and authentication manager configuration.
- UserDetailsServiceImpl: implemented Spring Security user lookup by email for authentication.
- GlobalExceptionHandler: implemented centralized handling for validation errors, status exceptions, access denied, method not allowed, and generic server errors.

### 7.5 Repository Layer Implementation

- AidRequestCommentRepository: implemented ordered comment retrieval by aid request id.
- AidRequestRepository: implemented status, type, status-and-type, beneficiary-based, reviewer-based, duplicate-request check, status counts, old pending requests, and beneficiary history queries.
- AidRequestStatusHistoryRepository: implemented ordered status transition history retrieval.
- AuditLogRepository: implemented entity and performer filters with compliance-report date-range query.
- BeneficiaryRepository: implemented case-insensitive name search and duplicate beneficiary existence checks.
- CampaignRepository: implemented status-based and category-based campaign retrieval plus status counts.
- DonationRepository: implemented donor history filters, donor-name search, campaign aggregation totals, donor and campaign-level counts, distinct donor retrieval by campaign, and monetary total metrics.
- EmailTemplateRepository: implemented template lookup by template key.
- InventoryMovementRepository: implemented item movement history retrieval in descending time order.
- InventoryRepository: implemented low-stock query, ordered inventory retrieval, category filtering, and duplicate item-name checks.
- PasswordResetTokenRepository: implemented token lookup and bulk token invalidation updates for user reset flows.
- RecurringDonationRepository: implemented donor recurring list retrieval and due recurring job retrieval.
- UserRepository: implemented user lookup by email, existence checks for email and role, role-based retrieval, role-and-status retrieval, and account status counts.

### 7.6 Domain Entity Implementation

- User: implemented core identity, authentication, role, and account-status model used across authorization and ownership relations.
- Beneficiary: implemented beneficiary profile and needs record used in aid request workflows.
- AidRequest: implemented aid request core model including category/type, priority, status, assignment, and lifecycle timestamps.
- AidRequestComment: implemented comment thread model attached to aid requests.
- AidRequestStatusHistory: implemented status transition audit trail per request.
- Campaign: implemented campaign model including goal progress, status, category, and timeline attributes.
- Donation: implemented donation transaction model for monetary and in-kind contributions.
- RecurringDonation: implemented recurring donation schedule, activation state, and next-run tracking.
- Inventory: implemented stock model with thresholds, categorization, and quantity controls.
- InventoryMovement: implemented inventory movement log for stock in/out adjustments.
- EmailTemplate: implemented editable notification template store with subject/body placeholders.
- AuditLog: implemented cross-cutting action logging model for compliance and traceability.
- PasswordResetToken: implemented one-time token model for password reset lifecycle.

### 7.7 Enum and Value-Type Implementation

- UserRole: role set for authorization boundaries.
- AccountStatus: user account lifecycle states.
- AidType: aid request type classification.
- RequestStatus: aid request lifecycle states.
- RequestPriority: aid request priority levels.
- BeneficiaryStatus: beneficiary lifecycle states.
- CampaignStatus: campaign lifecycle states.
- CampaignCategory: campaign grouping classifications.
- DonationType: donation mode classification.
- PaymentMethod: donation payment channel classification.
- RecurringDonationFrequency: recurring schedule frequencies.
- ItemCategory: inventory categorization values.
- UnitOfMeasure: inventory unit standardization values.
- InventoryMovementType: stock movement direction/type values.
- NeedsCategory: beneficiary needs grouping values.

### 7.8 DTO Layer Implementation Catalog

Authentication and user DTOs:
- RegisterRequest: input contract for donor/admin registration.
- LoginRequest: input contract for login credentials.
- AuthResponse: auth output including token and role details.
- ForgotPasswordRequest: input contract for password reset initiation.
- ResetPasswordRequest: input contract for password reset completion.
- MfaSetupResponse: output contract for MFA setup secret and URI.
- MfaConfirmRequest: input contract for MFA confirmation code.
- MfaVerifyRequest: input contract for MFA challenge verification.
- ChangePasswordRequest: input contract for in-session password change.
- UpdateProfileRequest: input contract for user profile update.
- CreateUserRequest: input contract for admin user provisioning.
- UserResponse: output contract for user profile and admin views.

Aid request and beneficiary DTOs:
- BeneficiaryRequest: input contract for beneficiary create/update.
- BeneficiaryResponse: output contract for beneficiary data.
- AidRequestRequest: input contract for aid request creation/update.
- AidRequestResponse: output contract for aid request details.
- AidRequestStatusHistoryResponse: output contract for status timeline events.
- AidRequestCommentRequest: input contract for aid request comments.
- AidRequestCommentResponse: output contract for request comments.
- AidRequestBatchProcessRequest: input contract for multi-request batch actions.

Campaign and donation DTOs:
- CampaignRequest: input contract for campaign create/update.
- CampaignResponse: output contract for campaign views.
- BulkCampaignUpdateRequest: input contract for donor broadcast updates.
- DonationRequest: input contract for donation submission.
- DonationResponse: output contract for donation details.
- RecurringDonationRequest: input contract for recurring donation setup.
- RecurringDonationResponse: output contract for recurring donation details.
- DonorSummaryResponse: output contract for donor-level contribution summary.

Inventory DTOs:
- InventoryRequest: input contract for inventory create/update.
- InventoryResponse: output contract for inventory item details.
- InventoryMovementRequest: input contract for stock movement operations.
- InventoryMovementResponse: output contract for stock movement history entries.

Reporting and audit DTOs:
- DonationSummaryReport: output contract for donation aggregate analytics.
- CampaignProgressReport: output contract for campaign progress analytics.
- AidRequestReport: output contract for aid request report views.
- KpiDashboardReport: output contract for KPI dashboard metrics.
- AuditLogResponse: output contract for audit log records.
- ComplianceReport: output contract for compliance report payload.

Notification and template DTOs:
- EmailTemplateRequest: input contract for template updates.
- EmailTemplateResponse: output contract for template retrieval.

Shared response DTO:
- ApiResponse: standardized API wrapper for success, message, and payload consistency across all controllers.

---

## 8. Final Submission Format (PDF Consolidation Runbook)

Prepare one merged PDF package in the order below.

### Step 1: Export Main Case Study Report PDF

Export this file first after peer review and final edits:
- CASE-STUDY-CONSOLIDATED-REPORT-DRAFT.md -> PDF

Suggested output name:
- AIT725-CharityAID-CaseStudy-Report.pdf

### Step 2: Export Each Deliverable as Individual PDF

Export each major deliverable with readable formatting:
- TEST-PLAN-COMPREHENSIVE.md -> PDF
- UNIT-TEST-COVERAGE-SUMMARY.md -> PDF
- SYSTEM-INTEGRATION-TESTING-REPORT.md -> PDF
- UAT-EXECUTION-REPORT.md -> PDF
- uat-simulated-script.md -> PDF
- service-unit-test-checklist.md -> PDF
- fr-01-to-fr-94-traceability.csv -> PDF
- mockmvc-endpoint-matrix.csv -> PDF
- test-plan-test-cases.csv -> PDF
- defect-log.csv -> PDF

Formatting note:
- Ensure spreadsheet exports use landscape orientation where necessary and maintain full column visibility.

### Step 3: Create Appendix Cover Sheets as Separate PDFs

Create a one-page cover PDF before each appendix section, for example:
- Appendix A Cover: System Request
- Appendix B Cover: FR Traceability
- Appendix C Cover: Endpoint Matrix
- Appendix D Cover: Test Plan
- Appendix E Cover: Unit Test Coverage
- Appendix F Cover: System & Integration Testing
- Appendix G Cover: UAT Artifacts
- Appendix H Cover: Defect Log

### Step 4: Merge All PDFs into One Final Submission

Merge order:
1. Main Case Study Report PDF
2. Appendix cover sheet PDFs and corresponding deliverable PDFs in sequence

Submission notes:
- Appendix page numbers are not required.
- Verify the merged file opens correctly and all pages are legible before submission.

Suggested final file name:
- AIT725-CharityAID-Final-CaseStudy-Submission.pdf

---

## 9. Final Quality Check Before Submission

- Confirm objective wording in Section 5.2 exactly matches the proposal.
- Replace placeholders for student name and any pending details.
- Add selected screenshots if required by instructor preference.
- Run a final spelling and consistency check.
- Re-export PDF and perform one final visual review.

---

**End of Draft**
