package com.example.schoolmoney.domain.child;

import com.example.schoolmoney.domain.parent.Parent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChildAccessService {

    public boolean canAccessChild(Parent parent, Child child) {
        return child.getParent().getUserId().equals(parent.getUserId());
    }

}
