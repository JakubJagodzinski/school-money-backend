package com.example.schoolmoney.domain.schoolclass;

import com.example.schoolmoney.common.constants.messages.domain.SchoolClassMessages;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.parent.Parent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SchoolClassAccessService {

    private final ChildRepository childRepository;

    public void assertCanViewSchoolClass(Parent parent, SchoolClass schoolClass) {
        boolean isTreasurer = schoolClass.getTreasurer().getUserId().equals(parent.getUserId());
        boolean hasAnyChildrenInSchoolClass = childRepository.existsByParent_UserIdAndSchoolClass_SchoolClassId(parent.getUserId(), schoolClass.getSchoolClassId());

        if (!isTreasurer && !hasAnyChildrenInSchoolClass) {
            log.warn("User {} doesn't have access to school class with id {}", parent.getUserId(), schoolClass.getSchoolClassId());
            throw new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
        }
    }

    public void assertCanEditSchoolClass(Parent parent, SchoolClass schoolClass) {
        boolean isTreasurer = schoolClass.getTreasurer().getUserId().equals(parent.getUserId());

        if (!isTreasurer) {
            log.warn("User {} doesn't have permission to edit school class with id {}", parent.getUserId(), schoolClass.getSchoolClassId());
            throw new AccessDeniedException(SchoolClassMessages.NO_PERMISSION_TO_EDIT_THIS_SCHOOL_CLASS);
        }
    }

}
