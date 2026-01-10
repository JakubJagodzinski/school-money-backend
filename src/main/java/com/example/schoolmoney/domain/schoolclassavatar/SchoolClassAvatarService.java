package com.example.schoolmoney.domain.schoolclassavatar;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.AvatarMessages;
import com.example.schoolmoney.common.constants.messages.domain.SchoolClassMessages;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.SchoolClassAccessService;
import com.example.schoolmoney.domain.schoolclass.SchoolClassFinder;
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
@Transactional(readOnly = true)
@Service
public class SchoolClassAvatarService {

    private final String bucketName = "school-class-avatar";

    private final SchoolClassRepository schoolClassRepository;

    private final StorageService storageService;

    private final SecurityUtils securityUtils;

    private final SchoolClassAccessService schoolClassAccessService;

    private final ParentRepository parentRepository;

    private final SchoolClassFinder schoolClassFinder;

    @Transactional
    public void updateSchoolClassAvatar(UUID schoolClassId, MultipartFile avatarFile) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Enter updateSchoolClassAvatar(schoolClassId={})", schoolClassId);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        if (!schoolClassAccessService.canViewSchoolClass(parent, schoolClass)) {
            log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
            throw new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
        }

        if (!schoolClassAccessService.canEditSchoolClass(parent, schoolClass)) {
            log.warn(SchoolClassMessages.NO_PERMISSION_TO_EDIT_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.NO_PERMISSION_TO_EDIT_THIS_SCHOOL_CLASS);
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

    public InputStreamResource getSchoolClassAvatar(UUID schoolClassId) throws EntityNotFoundException {
        log.debug("Enter getSchoolClassAvatar(schoolClassId={})", schoolClassId);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        if (!schoolClassAccessService.canViewSchoolClass(parent, schoolClass)) {
            log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
            throw new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
        }

        if (schoolClass.getAvatarId() == null) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return null;
        }

        String avatarId = schoolClass.getAvatarId().toString();

        log.debug("Exit getSchoolClassAvatar");
        return storageService.downloadFile(avatarId, bucketName);
    }

    @Transactional
    public void deleteSchoolClassAvatar(UUID schoolClassId) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Enter deleteSchoolClassAvatar(schoolClassId={})", schoolClassId);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        if (!schoolClassAccessService.canViewSchoolClass(parent, schoolClass)) {
            log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
            throw new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
        }

        if (!schoolClassAccessService.canEditSchoolClass(parent, schoolClass)) {
            log.warn(SchoolClassMessages.NO_PERMISSION_TO_EDIT_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.NO_PERMISSION_TO_EDIT_THIS_SCHOOL_CLASS);
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
