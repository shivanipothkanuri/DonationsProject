package org.charityaid.charity_aid.service;

import org.charityaid.charity_aid.dto.BeneficiaryRequest;
import org.charityaid.charity_aid.dto.BeneficiaryResponse;
import org.charityaid.charity_aid.entity.Beneficiary;
import org.charityaid.charity_aid.entity.BeneficiaryStatus;
import org.charityaid.charity_aid.entity.User;
import org.charityaid.charity_aid.repository.BeneficiaryRepository;
import org.charityaid.charity_aid.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public Page<BeneficiaryResponse> getAllBeneficiaries(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return beneficiaryRepository.findByFullNameContainingIgnoreCase(search, pageable)
                    .map(BeneficiaryResponse::from);
        }
        return beneficiaryRepository.findAll(pageable).map(BeneficiaryResponse::from);
    }

    public BeneficiaryResponse getBeneficiaryById(Integer id) {
        return BeneficiaryResponse.from(findOrThrow(id));
    }

    // FR-41 + FR-46: Register a beneficiary, reject duplicates (same name + phone)
    @Transactional
    public BeneficiaryResponse registerBeneficiary(BeneficiaryRequest request, String staffEmail) {
        User staff = userRepository.findByEmailAddress(staffEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (beneficiaryRepository.existsByFullNameIgnoreCaseAndPhoneNumber(
                request.getFullName(), request.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A beneficiary with the same name and phone number already exists");
        }

        Beneficiary beneficiary = Beneficiary.builder()
                .fullName(request.getFullName())
                .emailAddress(request.getEmailAddress())
                .phoneNumber(request.getPhoneNumber())
                .residentialAddress(request.getResidentialAddress())
                .beneficiaryStatus(BeneficiaryStatus.ACTIVE)
                .registeredBy(staff)
                .build();

        BeneficiaryResponse response = BeneficiaryResponse.from(beneficiaryRepository.save(beneficiary));
        auditService.record("BENEFICIARY", response.getBeneficiaryId(), "CREATE", staffEmail,
                "Beneficiary registered: " + request.getFullName());
        return response;
    }

    // FR-20: Update contact info
    @Transactional
    public BeneficiaryResponse updateBeneficiary(Integer id, BeneficiaryRequest request) {
        Beneficiary b = findOrThrow(id);

        b.setFullName(request.getFullName());
        b.setEmailAddress(request.getEmailAddress());
        b.setPhoneNumber(request.getPhoneNumber());
        b.setResidentialAddress(request.getResidentialAddress());

        BeneficiaryResponse response = BeneficiaryResponse.from(beneficiaryRepository.save(b));
        auditService.record("BENEFICIARY", id, "UPDATE", null, "Beneficiary contact info updated");
        return response;
    }

    @Transactional
    public BeneficiaryResponse deactivateBeneficiary(Integer id) {
        Beneficiary b = findOrThrow(id);
        b.setBeneficiaryStatus(BeneficiaryStatus.INACTIVE);
        BeneficiaryResponse response = BeneficiaryResponse.from(beneficiaryRepository.save(b));
        auditService.record("BENEFICIARY", id, "UPDATE", null, "Beneficiary deactivated");
        return response;
    }

    private Beneficiary findOrThrow(Integer id) {
        return beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Beneficiary not found"));
    }
}
