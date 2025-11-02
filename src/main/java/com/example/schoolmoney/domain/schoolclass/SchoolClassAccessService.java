package com.example.schoolmoney.domain.schoolclass;

import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.parent.Parent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SchoolClassAccessService {

    private final ChildRepository childRepository;

    public boolean canViewSchoolClass(Parent parent, SchoolClass schoolClass) {
        boolean isTreasurer = schoolClass.getTreasurer().getUserId().equals(parent.getUserId());
        boolean hasAnyChildrenInSchoolClass = childRepository.existsByParent_UserIdAndSchoolClass_SchoolClassId(parent.getUserId(), schoolClass.getSchoolClassId());

        return hasAnyChildrenInSchoolClass || isTreasurer;
    }

    public boolean canEditSchoolClass(Parent parent, SchoolClass schoolClass) {
        return schoolClass.getTreasurer().getUserId().equals(parent.getUserId());
    }

    public boolean canCreateFund(Parent parent, SchoolClass schoolClass) {
        return schoolClass.getTreasurer().getUserId().equals(parent.getUserId());
    }

}
