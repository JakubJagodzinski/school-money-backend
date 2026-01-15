package com.example.schoolmoney.domain.fundlogo;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.FundLogoMessages;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundAccessService;
import com.example.schoolmoney.domain.fund.FundFinder;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.files.FileCategory;
import com.example.schoolmoney.storage.StorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FundLogoService {

    private final String bucketName = "fund-logo";

    private final FundRepository fundRepository;

    private final StorageService storageService;

    private final SecurityUtils securityUtils;

    private final FundAccessService fundAccessService;

    private final ParentRepository parentRepository;

    private final FundFinder fundFinder;

    @Transactional
    public void updateFundLogo(UUID fundId, MultipartFile logoFile) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter updateFundLogo(fundId={})", fundId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        fundAccessService.assertCanViewFund(parent, fund);
        fundAccessService.assertCanEditFund(parent, fund);
        fundAccessService.assertFundIsNotBlocked(fund);
        fundAccessService.assertFundIsActive(fund);

        UUID newLogoId = UUID.fromString(storageService.uploadFile(logoFile, bucketName, FileCategory.AVATAR_OR_LOGO));

        if (fund.getLogoId() != null) {
            storageService.deleteFile(fund.getLogoId().toString(), bucketName);
            log.debug("Old logo deleted");
        }

        fund.setLogoId(newLogoId);
        fundRepository.save(fund);
        log.info("Logo id saved for fund with fundId={}", fundId);

        log.debug("Exit updateFundLogo(fundId={})", fundId);
    }

    public InputStreamResource getFundLogo(UUID fundId) throws EntityNotFoundException {
        log.debug("Enter getFundLogo(fundId={})", fundId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);

        fundAccessService.assertCanViewFund(parent, fund);

        if (fund.getLogoId() == null) {
            log.warn(FundLogoMessages.FUND_LOGO_NOT_SET);
            return null;
        }

        String logoId = fund.getLogoId().toString();

        log.debug("Exit getFundLogo(fundId={})", fundId);
        return storageService.downloadFile(logoId, bucketName);
    }

    @Transactional
    public void deleteFundLogo(UUID fundId) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter deleteFundLogo(fundId={})", fundId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);

        fundAccessService.assertCanViewFund(parent, fund);
        fundAccessService.assertCanEditFund(parent, fund);
        fundAccessService.assertFundIsNotBlocked(fund);
        fundAccessService.assertFundIsActive(fund);

        if (fund.getLogoId() == null) {
            log.warn(FundLogoMessages.FUND_LOGO_NOT_SET);
            return;
        }

        String logoId = fund.getLogoId().toString();

        storageService.deleteFile(logoId, bucketName);

        fund.setLogoId(null);
        fundRepository.save(fund);
        log.info("Logo id set to null for fund with fundId={}", fund.getFundId());

        log.debug("Exit deleteFundLogo(fundId={})", fundId);
    }

}
