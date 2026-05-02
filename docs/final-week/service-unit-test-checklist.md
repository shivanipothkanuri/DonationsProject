# Service Unit Test Checklist

Generated from detected public method names in service classes.

## AidRequestService.java

- [ ] getRequestsByStatus
- [ ] getAllRequests
- [ ] getRequestsByBeneficiary
- [ ] getRequestById
- [ ] submitRequest
- [ ] updateRequest
- [ ] reviewRequest
- [ ] fulfillRequest
- [ ] escalateRequest
- [ ] sendPendingApprovalReminders
- [ ] getStatusHistory

## AuditService.java

- [ ] record
- [ ] getAuditLog
- [ ] getComplianceReport

## AuthService.java

- [ ] register
- [ ] isSetupNeeded
- [ ] setupFirstAdmin
- [ ] login
- [ ] recordLogout
- [ ] setupMfa
- [ ] confirmMfa
- [ ] verifyMfa
- [ ] disableMfa
- [ ] forgotPassword
- [ ] resetPassword

## BeneficiaryService.java

- [ ] getAllBeneficiaries
- [ ] getBeneficiaryById
- [ ] registerBeneficiary
- [ ] updateBeneficiary
- [ ] deactivateBeneficiary

## CampaignService.java

- [ ] getActiveCampaigns
- [ ] getAllCampaigns
- [ ] getCampaignById
- [ ] createCampaign
- [ ] updateCampaign
- [ ] closeCampaign
- [ ] deleteCampaign
- [ ] sendBulkCampaignUpdate

## DonationService.java

- [ ] submitDonation
- [ ] getInKindDonationOptions
- [ ] scheduleRecurringDonation
- [ ] getMyRecurringDonations
- [ ] cancelRecurringDonation
- [ ] processRecurringDonations
- [ ] getDonorHistory
- [ ] getDonationById
- [ ] generateReceiptBytes

## InventoryService.java

- [ ] getAllItems
- [ ] getItemById
- [ ] getLowStockItems
- [ ] addItem
- [ ] updateItem
- [ ] deleteItem

## MfaService.java

- [ ] generateSecret
- [ ] buildOtpauthUrl
- [ ] verifyCode

## NotificationService.java

- [ ] sendDonationConfirmation
- [ ] sendDonationConfirmationWithReceipt
- [ ] sendCampaignNinetyPercentAlert
- [ ] sendAidRequestApproved
- [ ] sendAidRequestDenied
- [ ] sendEscalationAlert
- [ ] sendPasswordResetLink
- [ ] sendFulfillmentNoticeToCaseManager
- [ ] sendPendingApprovalReminder
- [ ] sendBulkCampaignUpdate
- [ ] sendHighValueDonationAlert

## NotificationTemplateService.java

- [ ] getAllTemplates
- [ ] getTemplate
- [ ] updateTemplate
- [ ] renderSubject
- [ ] renderBody

## ReceiptService.java

- [ ] generateReceipt

## ReportService.java

- [ ] donationSummary
- [ ] campaignProgress
- [ ] pendingRequestsReport
- [ ] fulfilledRequestsReport
- [ ] kpiDashboard
- [ ] donationSummaryCsv

## UserService.java

- [ ] getAllUsers
- [ ] getUserById
- [ ] getMyProfile
- [ ] updateMyProfile
- [ ] createUser
- [ ] changeRole
- [ ] deactivateUser
- [ ] reactivateUser
- [ ] suspendUser
- [ ] unlockUser
- [ ] changePassword

