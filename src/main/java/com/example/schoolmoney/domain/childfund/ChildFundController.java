package com.example.schoolmoney.domain.childfund;

import com.example.schoolmoney.domain.childfund.dto.response.ParentChildHistoryFundResponseDto;
import com.example.schoolmoney.domain.childfund.dto.response.ParentChildUnpaidFundResponseDto;
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

    @GetMapping("/school-classes/{schoolClassId}/funds/history")
    public ResponseEntity<Page<ParentChildHistoryFundResponseDto>> getSchoolClassParentChildrenFundsHistory(
            @PathVariable UUID schoolClassId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ParentChildHistoryFundResponseDto> responseDto = childFundService.getSchoolClassParentChildrenFundsHistory(schoolClassId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    @GetMapping("/school-classes/{schoolClassId}/funds/unpaid")
    public ResponseEntity<Page<ParentChildUnpaidFundResponseDto>> getSchoolClassParentChildrenUnpaidFunds(
            @PathVariable UUID schoolClassId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ParentChildUnpaidFundResponseDto> responseDto = childFundService.getSchoolClassParentChildrenUnpaidFunds(schoolClassId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

}
