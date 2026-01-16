package com.example.schoolmoney.domain.schoolclassavatar;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.AvatarMessages;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentFinder;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.SchoolClassAccessService;
import com.example.schoolmoney.domain.schoolclass.SchoolClassFinder;
import com.example.schoolmoney.domain.schoolclass.SchoolClassRepository;
import com.example.schoolmoney.files.FileCategory;
import com.example.schoolmoney.storage.StorageService;
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
public class SchoolClassAvatarService {

    private final String bucketName = "school-class-avatar";

    private final SchoolClassRepository schoolClassRepository;

    private final StorageService storageService;

    private final SecurityUtils securityUtils;

    private final SchoolClassAccessService schoolClassAccessService;

    private final SchoolClassFinder schoolClassFinder;

    private final ParentFinder parentFinder;

    @Transactional
    public void updateSchoolClassAvatar(UUID schoolClassId, MultipartFile avatarFile) {
        log.debug("Enter updateSchoolClassAvatar(schoolClassId={})", schoolClassId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);
        schoolClassAccessService.assertCanViewSchoolClass(parent, schoolClass);
        schoolClassAccessService.assertCanEditSchoolClass(parent, schoolClass);

        String newAvatarId = storageService.uploadFile(
                avatarFile,
                bucketName,
                FileCategory.AVATAR_OR_LOGO
        );

        if (schoolClass.getAvatarId() != null) {
            storageService.deleteFile(schoolClass.getAvatarId().toString(), bucketName);
            log.debug("Old avatar deleted");
        }

        schoolClass.setAvatarId(UUID.fromString(newAvatarId));
        schoolClassRepository.save(schoolClass);
        log.info("Avatar id saved for school class with schoolClassId={}", schoolClassId);

        log.debug("Exit updateSchoolClassAvatar(schoolClassId={})", schoolClassId);
    }

    @Transactional(readOnly = true)
    public InputStreamResource getSchoolClassAvatar(UUID schoolClassId) {
        log.debug("Enter getSchoolClassAvatar(schoolClassId={})", schoolClassId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);
        schoolClassAccessService.assertCanViewSchoolClass(parent, schoolClass);

        if (schoolClass.getAvatarId() == null) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return null;
        }

        String avatarId = schoolClass.getAvatarId().toString();

        log.debug("Exit getSchoolClassAvatar(schoolClassId={})", schoolClassId);
        return storageService.downloadFile(avatarId, bucketName);
    }

    @Transactional
    public void deleteSchoolClassAvatar(UUID schoolClassId) {
        log.debug("Enter deleteSchoolClassAvatar(schoolClassId={})", schoolClassId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);
        schoolClassAccessService.assertCanViewSchoolClass(parent, schoolClass);
        schoolClassAccessService.assertCanEditSchoolClass(parent, schoolClass);

        if (schoolClass.getAvatarId() == null) {
            log.warn(AvatarMessages.AVATAR_NOT_SET);
            return;
        }

        String avatarId = schoolClass.getAvatarId().toString();

        storageService.deleteFile(avatarId, bucketName);

        schoolClass.setAvatarId(null);
        schoolClassRepository.save(schoolClass);
        log.info("Avatar id set to null for school class with schoolClassId={}", schoolClassId);

        log.debug("Exit deleteSchoolClassAvatar(schoolClassId={})", schoolClassId);
    }

}
