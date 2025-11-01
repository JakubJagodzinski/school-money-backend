package com.example.schoolmoney.domain.fundlogo;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.FundLogoMessages;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundService;
import com.example.schoolmoney.domain.fund.FundStatus;
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
@Service
public class FundLogoService {

    private final String bucketName = "fund-logo";

    private final FundRepository fundRepository;

    private final StorageService storageService;

    private final SecurityUtils securityUtils;

    private final FundService fundService;

    @Transactional
    public void updateFundLogo(UUID fundId, MultipartFile logoFile) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter updateFundLogo(fundId={})", fundId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        if (!fundService.canParentAccessFund(userId, fundId)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        boolean isAuthor = fund.getAuthor().getUserId().equals(userId);
        boolean isTreasurer = fund.getSchoolClass().getTreasurer().getUserId().equals(userId);
        if (!isAuthor && !isTreasurer) {
            log.warn(FundMessages.NO_PERMISSION_TO_EDIT_FUND);
            throw new AccessDeniedException(FundMessages.NO_PERMISSION_TO_EDIT_FUND);
        }

        if (fund.getFundStatus() != FundStatus.ACTIVE) {
            log.warn(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        UUID newLogoId = UUID.fromString(storageService.uploadFile(logoFile, bucketName, FileCategory.AVATAR_OR_LOGO));

        if (fund.getLogoId() != null) {
            storageService.deleteFile(fund.getLogoId().toString(), bucketName);
            log.debug("Old logo deleted");
        }

        fund.setLogoId(newLogoId);
        fundRepository.save(fund);
        log.info("Logo id saved for fund with fundId={}", fundId);

        log.debug("Exit updateFundLogo");
    }

    @Transactional
    public InputStreamResource getFundLogo(UUID fundId) throws EntityNotFoundException {
        log.debug("Enter getFundLogo(fundId={})", fundId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        if (fund.getLogoId() == null) {
            log.warn(FundLogoMessages.FUND_LOGO_NOT_SET);
            return null;
        }

        String logoId = fund.getLogoId().toString();

        log.debug("Exit getFundLogo");
        return storageService.downloadFile(logoId, bucketName);
    }

    @Transactional
    public void deleteFundLogo(UUID fundId) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter deleteFundLogo(fundId={})", fundId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        if (!fundService.canParentAccessFund(userId, fundId)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        boolean isAuthor = fund.getAuthor().getUserId().equals(userId);
        boolean isTreasurer = fund.getSchoolClass().getTreasurer().getUserId().equals(userId);
        if (!isAuthor && !isTreasurer) {
            log.warn(FundMessages.NO_PERMISSION_TO_EDIT_FUND);
            throw new AccessDeniedException(FundMessages.NO_PERMISSION_TO_EDIT_FUND);
        }

        if (fund.getFundStatus() != FundStatus.ACTIVE) {
            log.warn(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        if (fund.getLogoId() == null) {
            log.warn(FundLogoMessages.FUND_LOGO_NOT_SET);
            return;
        }

        String logoId = fund.getLogoId().toString();

        storageService.deleteFile(logoId, bucketName);

        fund.setLogoId(null);
        fundRepository.save(fund);
        log.info("Logo id set to null for fund with fundId={}", fund.getFundId());

        log.debug("Exit deleteFundLogo");
    }

}
