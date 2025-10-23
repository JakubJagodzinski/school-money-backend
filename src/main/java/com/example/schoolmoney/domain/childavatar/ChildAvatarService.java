package com.example.schoolmoney.domain.childavatar;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.AvatarMessages;
import com.example.schoolmoney.common.constants.messages.domain.ChildMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
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
public class ChildAvatarService {

    private final String bucketName = "child-avatar";

    private final ChildRepository childRepository;

    private final StorageService storageService;

    private final SecurityUtils securityUtils;

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
            storageService.deleteFile(child.getAvatarId().toString(), bucketName);
            log.debug("Old avatar deleted");
        }

        String avatarId = storageService.uploadFile(avatarFile, bucketName);

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
        return storageService.downloadFile(avatarId, bucketName);
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

        storageService.deleteFile(avatarId, bucketName);

        child.setAvatarId(null);
        childRepository.save(child);
        log.info("Avatar id set to null for child with childId={}", childId);

        log.debug("Exit deleteChildAvatar");
    }

}
