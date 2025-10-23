package com.example.schoolmoney.domain.avatar;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.AvatarMessages;
import com.example.schoolmoney.common.constants.messages.ChildMessages;
import com.example.schoolmoney.common.constants.messages.FundMessages;
import com.example.schoolmoney.common.constants.messages.ParentMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
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
public class AvatarService {

    private final ParentRepository parentRepository;

    private final ChildRepository childRepository;

    private final FundRepository fundRepository;

    private final SecurityUtils securityUtils;

    private final StorageService storageService;

    @Transactional
    public void updateParentAvatar(MultipartFile file) throws IllegalStateException {
        log.debug("Enter uploadParentAvatar");

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        if (parent.getAvatarUrl() != null) {
            storageService.deleteFile(parent.getAvatarUrl());
            log.debug("Old avatar deleted");
        }

        String avatarUrl = storageService.uploadFile(file);

        parent.setAvatarUrl(avatarUrl);
        parentRepository.save(parent);
        log.info("Avatar url saved for parent with parentId={}", userId);

        log.debug("Exit uploadParentAvatar");
    }

    @Transactional
    public InputStreamResource getParentAvatar(UUID parentId) throws EntityNotFoundException {
        log.debug("Enter getParentAvatar(parentId={})", parentId);

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> {
                    log.warn(ParentMessages.PARENT_NOT_FOUND);
                    return new EntityNotFoundException(ParentMessages.PARENT_NOT_FOUND);
                });

        String avatarUrl = parent.getAvatarUrl();
        if (avatarUrl == null || avatarUrl.isBlank()) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return null;
        }

        log.debug("Exit getParentAvatar");
        return storageService.downloadFile(avatarUrl);
    }

    @Transactional
    public void deleteParentAvatar() {
        log.debug("Enter deleteParentAvatar");

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        String avatarUrl = parent.getAvatarUrl();

        if (avatarUrl == null || avatarUrl.isBlank()) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return;
        }

        storageService.deleteFile(avatarUrl);

        parent.setAvatarUrl(null);
        parentRepository.save(parent);
        log.info("Avatar url set to null for parent with parentId={}", userId);

        log.debug("Exit deleteParentAvatar");
    }

    @Transactional
    public void updateChildAvatar(UUID childId, MultipartFile avatarFile) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Enter uploadChildAvatar(childId={})", childId);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.warn(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        if (!child.getParent().getUserId().equals(userId)) {
            log.warn(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
            throw new AccessDeniedException(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
        }

        if (child.getAvatarUrl() != null) {
            storageService.deleteFile(child.getAvatarUrl());
            log.debug("Old avatar deleted");
        }

        String avatarUrl = storageService.uploadFile(avatarFile);

        child.setAvatarUrl(avatarUrl);
        childRepository.save(child);
        log.info("Avatar url saved for child with childId={}", childId);

        log.debug("Exit uploadChildAvatar");
    }

    @Transactional
    public InputStreamResource getChildAvatar(UUID childId) throws EntityNotFoundException {
        log.debug("Enter getChildAvatar(childId={})", childId);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.warn(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        String avatarUrl = child.getAvatarUrl();
        if (avatarUrl == null || avatarUrl.isBlank()) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return null;
        }

        log.debug("Exit getChildAvatar");
        return storageService.downloadFile(avatarUrl);
    }

    @Transactional
    public void deleteChildAvatar(UUID childId) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter deleteChildAvatar(childId={})", childId);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.warn(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        if (!child.getParent().getUserId().equals(userId)) {
            log.warn(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
            throw new AccessDeniedException(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
        }

        String avatarUrl = child.getAvatarUrl();
        if (avatarUrl == null || avatarUrl.isBlank()) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return;
        }

        storageService.deleteFile(avatarUrl);

        child.setAvatarUrl(null);
        childRepository.save(child);
        log.info("Avatar url set to null for child with childId={}", childId);

        log.debug("Exit deleteChildAvatar");
    }

    @Transactional
    public void updateFundLogo(UUID fundId, MultipartFile avatarFile) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter uploadFundAvatar(fundId={})", fundId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();

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

        if (fund.getLogoUrl() != null) {
            storageService.deleteFile(fund.getLogoUrl());
            log.debug("Old avatar deleted");
        }

        String avatarUrl = storageService.uploadFile(avatarFile);

        fund.setLogoUrl(avatarUrl);
        fundRepository.save(fund);
        log.info("Avatar url saved for fund with fundId={}", fundId);

        log.debug("Exit uploadFundAvatar");
    }

    @Transactional
    public InputStreamResource getFundLogo(UUID fundId) throws EntityNotFoundException {
        log.debug("Enter getFundAvatar(fundId={})", fundId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        String avatarUrl = fund.getLogoUrl();
        if (avatarUrl == null || avatarUrl.isBlank()) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return null;
        }

        log.debug("Exit getFundAvatar");
        return storageService.downloadFile(avatarUrl);
    }

    @Transactional
    public void deleteFundLogo(UUID fundId) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter deleteFundAvatar(fundId={})", fundId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();

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

        String avatarUrl = fund.getLogoUrl();
        if (avatarUrl == null || avatarUrl.isBlank()) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return;
        }

        storageService.deleteFile(avatarUrl);

        fund.setLogoUrl(null);
        fundRepository.save(fund);
        log.info("Avatar url set to null for fund with fundId={}", fund.getFundId());

        log.debug("Exit deleteFundAvatar");
    }

}
