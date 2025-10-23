package com.example.schoolmoney.domain.avatar;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.AvatarMessages;
import com.example.schoolmoney.common.constants.messages.domain.ChildMessages;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.common.constants.messages.domain.ParentMessages;
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

        if (parent.getAvatarId() != null) {
            storageService.deleteFile(parent.getAvatarId().toString());
            log.debug("Old avatar deleted");
        }

        String avatarId = storageService.uploadFile(file);

        parent.setAvatarId(UUID.fromString(avatarId));
        parentRepository.save(parent);
        log.info("Avatar id saved for parent with parentId={}", userId);

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

        if (parent.getAvatarId() == null) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return null;
        }

        String avatarId = parent.getAvatarId().toString();

        log.debug("Exit getParentAvatar");
        return storageService.downloadFile(avatarId);
    }

    @Transactional
    public void deleteParentAvatar() {
        log.debug("Enter deleteParentAvatar");

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        if (parent.getAvatarId() == null) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return;
        }

        String avatarId = parent.getAvatarId().toString();

        storageService.deleteFile(avatarId);

        parent.setAvatarId(null);
        parentRepository.save(parent);
        log.info("Avatar id set to null for parent with parentId={}", userId);

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

        if (child.getAvatarId() != null) {
            storageService.deleteFile(child.getAvatarId().toString());
            log.debug("Old avatar deleted");
        }

        String avatarId = storageService.uploadFile(avatarFile);

        child.setAvatarId(UUID.fromString(avatarId));
        childRepository.save(child);
        log.info("Avatar id saved for child with childId={}", childId);

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

        if (child.getAvatarId() == null) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return null;
        }

        String avatarId = child.getAvatarId().toString();

        log.debug("Exit getChildAvatar");
        return storageService.downloadFile(avatarId);
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

        if (child.getAvatarId() == null) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return;
        }

        String avatarId = child.getAvatarId().toString();

        storageService.deleteFile(avatarId);

        child.setAvatarId(null);
        childRepository.save(child);
        log.info("Avatar id set to null for child with childId={}", childId);

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

        if (fund.getLogoId() != null) {
            storageService.deleteFile(fund.getLogoId().toString());
            log.debug("Old avatar deleted");
        }

        UUID avatarId = UUID.fromString(storageService.uploadFile(avatarFile));

        fund.setLogoId(avatarId);
        fundRepository.save(fund);
        log.info("Avatar id saved for fund with fundId={}", fundId);

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

        if (fund.getLogoId() == null) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return null;
        }

        String avatarId = fund.getLogoId().toString();

        log.debug("Exit getFundAvatar");
        return storageService.downloadFile(avatarId);
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

        if (fund.getLogoId() == null) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return;
        }

        String avatarId = fund.getLogoId().toString();

        storageService.deleteFile(avatarId);

        fund.setLogoId(null);
        fundRepository.save(fund);
        log.info("Avatar id set to null for fund with fundId={}", fund.getFundId());

        log.debug("Exit deleteFundAvatar");
    }

}
