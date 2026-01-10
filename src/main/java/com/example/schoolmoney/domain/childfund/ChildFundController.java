package com.example.schoolmoney.domain.childfund;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.domain.childfund.dto.response.ParentChildHistoryFundResponseDto;
import com.example.schoolmoney.domain.childfund.dto.response.ParentChildUnpaidFundResponseDto;
import com.example.schoolmoney.user.Permission;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ChildFundController {

    private final ChildFundService childFundService;

    @CheckPermission(Permission.PARENT_CHILDREN_FUND_HISTORY_READ_ALL)
    @GetMapping("/school-classes/funds/history")
    public ResponseEntity<Page<ParentChildHistoryFundResponseDto>> getParentChildrenFundsHistory(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ParentChildHistoryFundResponseDto> responseDto = childFundService.getParentChildrenFundsHistory(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    @CheckPermission(Permission.PARENT_CHILDREN_SCHOOL_CLASS_UNPAID_FUND_READ_ALL)
    @GetMapping("/school-classes/{schoolClassId}/funds/unpaid")
    public ResponseEntity<Page<ParentChildUnpaidFundResponseDto>> getParentChildrenSchoolClassUnpaidFunds(
            @PathVariable UUID schoolClassId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ParentChildUnpaidFundResponseDto> responseDto = childFundService.getParentChildrenSchoolClassUnpaidFunds(schoolClassId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    @CheckPermission(Permission.PARENT_CHILDREN_UNPAID_FUND_READ_ALL)
    @GetMapping("/school-classes/funds/unpaid")
    public ResponseEntity<Page<ParentChildUnpaidFundResponseDto>> getParentChildrenUnpaidFunds(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ParentChildUnpaidFundResponseDto> responseDto = childFundService.getParentChildrenSchoolClassUnpaidFunds(null, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

}
