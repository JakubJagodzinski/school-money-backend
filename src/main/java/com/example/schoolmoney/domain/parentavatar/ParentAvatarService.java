package com.example.schoolmoney.domain.parentavatar;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.AvatarMessages;
import com.example.schoolmoney.common.constants.messages.domain.ParentMessages;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.files.FileCategory;
import com.example.schoolmoney.storage.StorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParentAvatarService {

    private final String bucketName = "parent-avatar";

    private final ParentRepository parentRepository;

    private final StorageService storageService;

    private final SecurityUtils securityUtils;

    @Transactional
    public void updateParentAvatar(MultipartFile file) throws IllegalStateException {
        log.debug("Enter uploadParentAvatar");

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        String newAvatarId = storageService.uploadFile(file, bucketName, FileCategory.AVATAR_OR_LOGO);

        if (parent.getAvatarId() != null) {
            storageService.deleteFile(parent.getAvatarId().toString(), bucketName);
            log.debug("Old avatar deleted");
        }

        parent.setAvatarId(UUID.fromString(newAvatarId));
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
        return storageService.downloadFile(avatarId, bucketName);
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

        storageService.deleteFile(avatarId, bucketName);

        parent.setAvatarId(null);
        parentRepository.save(parent);
        log.info("Avatar id set to null for parent with parentId={}", userId);

        log.debug("Exit deleteParentAvatar");
    }

}
