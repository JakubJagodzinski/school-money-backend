package com.example.schoolmoney.domain.child;

import com.example.schoolmoney.common.constants.messages.domain.ChildMessages;
import com.example.schoolmoney.domain.parent.Parent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildAccessService {

    public void assertCanAccessChild(Parent parent, Child child) {
        boolean isChildOfParent = child.getParent().getUserId().equals(parent.getUserId());

        if (!isChildOfParent) {
            log.warn("Child with id={} is not parent with id={} child", child.getChildId(), parent.getUserId());
            throw new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
        }
    }

}
