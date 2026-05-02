# Simulated UAT Script (5 Roles)

## Roles

- Administrator
- Staff
- Case Manager
- Donor
- Leadership

## UAT Execution Table

| Role | Scenario | Steps | Expected Result | Actual | Defect ID |
|---|---|---|---|---|---|
| Administrator | Create user and assign role | Login, open Users, create staff user | User created and visible in list |  |  |
| Administrator | Deactivate and reactivate user | Deactivate then reactivate same user | Status changes correctly and audit log recorded |  |  |
| Staff | Create campaign | Open Campaigns, submit create form | Campaign created and listed |  |  |
| Staff | Register beneficiary | Open Beneficiaries, add new beneficiary | Beneficiary saved and retrievable |  |  |
| Staff | Submit aid request | Create request for existing beneficiary | Request status starts as PENDING |  |  |
| Case Manager | Approve aid request | Open pending request, approve | Request status becomes APPROVED |  |  |
| Case Manager | Escalate request | Escalate pending request to admin | Escalation event logged |  |  |
| Donor | Register and login | Register account then login | JWT issued and profile accessible |  |  |
| Donor | Donate to campaign | Submit donation for active campaign | Donation recorded and receipt available |  |  |
| Donor | Manage recurring donation | Create recurring donation then cancel | Recurring item appears then is removed |  |  |
| Leadership | View KPI dashboard | Open Reports KPI endpoint/UI | KPI data visible without errors |  |  |
| Leadership | Export donation CSV | Download report CSV | File download succeeds with rows |  |  |

## Sign-off

- UAT Start Date:
- UAT End Date:
- Pass Rate:
- Critical Defects Open:
- Approved By:
