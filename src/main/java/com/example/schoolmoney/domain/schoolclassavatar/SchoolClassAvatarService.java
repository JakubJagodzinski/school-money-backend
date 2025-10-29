package com.example.schoolmoney.domain.schoolclassavatar;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.AvatarMessages;
import com.example.schoolmoney.common.constants.messages.domain.SchoolClassMessages;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.SchoolClassRepository;
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
public class SchoolClassAvatarService {

    private final String bucketName = "school-class-avatar";

    private final SchoolClassRepository schoolClassRepository;

    private final StorageService storageService;

    private final SecurityUtils securityUtils;

    @Transactional
    public void updateSchoolClassAvatar(UUID schoolClassId, MultipartFile avatarFile) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Enter updateSchoolClassAvatar(schoolClassId={})", schoolClassId);

        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> {
                    log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        if (!schoolClass.getTreasurer().getUserId().equals(userId)) {
            log.warn(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        String newAvatarId = storageService.uploadFile(avatarFile, bucketName, FileCategory.AVATAR_OR_LOGO);

        if (schoolClass.getAvatarId() != null) {
            storageService.deleteFile(schoolClass.getAvatarId().toString(), bucketName);
            log.debug("Old avatar deleted");
        }

        schoolClass.setAvatarId(UUID.fromString(newAvatarId));
        schoolClassRepository.save(schoolClass);
        log.info("Avatar id saved for school class with schoolClassId={}", schoolClassId);

        log.debug("Exit updateSchoolClassAvatar");
    }

    @Transactional
    public InputStreamResource getSchoolClassAvatar(UUID schoolClassId) throws EntityNotFoundException {
        log.debug("Enter getSchoolClassAvatar(schoolClassId={})", schoolClassId);

        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> {
                    log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        if (schoolClass.getAvatarId() == null) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return null;
        }

        String avatarId = schoolClass.getAvatarId().toString();

        log.debug("Exit getSchoolClassAvatar");
        return storageService.downloadFile(avatarId, bucketName);
    }

    @Transactional
    public void deleteSchoolClassAvatar(UUID schoolClassId) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter deleteSchoolClassAvatar(schoolClassId={})", schoolClassId);

        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> {
                    log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        if (!schoolClass.getTreasurer().getUserId().equals(userId)) {
            log.warn(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        if (schoolClass.getAvatarId() == null) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return;
        }

        String avatarId = schoolClass.getAvatarId().toString();

        storageService.deleteFile(avatarId, bucketName);

        schoolClass.setAvatarId(null);
        schoolClassRepository.save(schoolClass);
        log.info("Avatar id set to null for school class with schoolClassId={}", schoolClassId);

        log.debug("Exit deleteSchoolClassAvatar");
    }

}
