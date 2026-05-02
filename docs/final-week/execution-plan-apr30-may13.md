# Charity-AID Final Sprint Execution Plan (Apr 30 - May 13, 2026)

## Situation Snapshot

- Functional coverage appears implemented (repo memory indicates 94/94 FR complete), but evidence artifacts are incomplete.
- Automated testing is currently near-zero in repository test source:
  - Existing test classes in `src/test/java`: 1
  - Existing service unit test classes: 0
  - Existing MockMvc endpoint test classes: 0
- Current API surface detected from controller mappings: 70 mapped endpoints across 10 controllers.
- Advisor checkpoint is fixed: May 2, 2026.

## Critical Path (What must happen first)

1. Build traceability matrix (FR-01 to FR-94) with code and endpoint evidence.
2. Build baseline tests quickly for high-risk business logic and authentication/security flows.
3. Run role-based UAT and capture defects with reproducible evidence.
4. Remediate defects and re-run affected tests before advisor review.

## Day-by-Day Crash Plan

### Apr 30 (Today)

- Produce FR traceability matrix skeleton and fill all 94 rows with:
  - Status: Implemented / Partial / Missing
  - File(s)
  - Endpoint(s)
  - Test evidence column (to be filled as tests are completed)
- Set up test scaffolding:
  - JUnit 5 + Mockito base for each service
  - MockMvc base test class with auth helpers for all roles
- Complete tests for security-critical flows first:
  - AuthService, UserService, AuthController, UserController

Definition of done today:
- 100% FR rows created with at least initial mapping
- 2 service test classes complete
- 2 controller integration test classes complete

### May 1

- Complete unit tests for remaining core services:
  - DonationService, AidRequestService, CampaignService, BeneficiaryService
- Start and complete integration tests for donor + staff workflows
- Run full test suite and create first defect list

Definition of done:
- At least 60% of service methods covered by unit tests
- At least 40% of endpoints covered by MockMvc tests
- Defect log initialized with severity tags (Critical/Major/Minor)

### May 2 (Advisor Meeting)

- Present evidence, not just status:
  - FR traceability matrix (complete draft)
  - Test execution summary (pass/fail counts)
  - Open defects and remediation ETA
  - Public deployment URL status

Meeting output required:
- Advisor feedback list converted into explicit action items and owners

### May 3 - May 6

- Complete all remaining service-layer unit tests
- Complete all remaining endpoint integration tests
- Execute 5-role UAT scripts and capture defects
- Start defect remediation cycle 1

Definition of done:
- 100% service methods tested (or explicitly justified)
- 100% required endpoints tested (your target set; verify if 35 or all 70)
- UAT evidence captured per role

### May 7 - May 9

- Defect remediation cycle 2 and regression retest
- Finalize API documentation and deployment documentation
- Prepare final test plan/test cases workbook and export evidence

Definition of done:
- No open critical defects
- No open major defects without waiver/approval
- Documentation drafts complete

### May 10 - May 13

- Finalize case study report and presentation pack
- Record walkthrough v1.2 and presentation video
- Package all final deliverables in final submission format
- Final quality gate and submission dry run

Definition of done:
- All listed final deliverables present and validated
- Submission checklist signed off

## Deliverables Quality Gate (Final Format)

For each item, verify: owner, version, location, format, and review date.

- Database ER diagram -> PDF (must be in SRS package as noted)
- Test plan and test cases -> Excel
- Public solution URL -> HTTPS live link (Railway/EC2)
- Source code package -> tagged snapshot/export
- Case study report -> PDF
- Presentation -> PPT + MP4
- Walkthrough video V1.2 -> MP4
- Exit survey -> completed proof

## Risk Register (Current)

1. Scope mismatch risk: requirement states 35 endpoints, code currently shows 70 mappings.
   - Mitigation: define official "required endpoint set" before finishing MockMvc plan.
2. Test debt risk: almost no automated tests currently checked in.
   - Mitigation: parallelize by controller and service modules; prioritize auth, aid, donation first.
3. Defect compression risk: UAT late start leaves little remediation time.
   - Mitigation: begin UAT once 60% endpoint coverage is ready, not after 100%.

## Execution Rules for Next 72 Hours

- Every code change linked to FR ID(s).
- Every FR row must have at least one verifiable artifact (code location, endpoint, or test).
- No documentation-only progress claims without repository evidence.
- End each day with:
  - Test pass/fail snapshot
  - Defect delta
  - Updated ETA to completion
