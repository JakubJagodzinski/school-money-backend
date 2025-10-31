package com.example.schoolmoney.domain.fundmediaoperation;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.domain.fundmediaoperation.dto.response.FundMediaOperationResponseDto;
import com.example.schoolmoney.user.Permission;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class FundMediaOperationController {

    private final FundMediaOperationService fundMediaOperationService;

    @CheckPermission(Permission.FUND_MEDIA_OPERATION_READ_ALL)
    @GetMapping("/funds/{fundId}/media/operations")
    public ResponseEntity<Page<FundMediaOperationResponseDto>> getFundMediaOperations(
            @PathVariable UUID fundId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "processedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FundMediaOperationResponseDto> fundMediaOperationPage = fundMediaOperationService.getFundMediaOperations(fundId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fundMediaOperationPage);
    }

}
