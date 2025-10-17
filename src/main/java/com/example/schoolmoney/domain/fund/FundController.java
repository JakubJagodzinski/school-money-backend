package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.common.constants.messages.FundMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.domain.fund.dto.request.CreateFundRequestDto;
import com.example.schoolmoney.domain.fund.dto.response.FundResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FundController {

    private final FundService fundService;

    @PostMapping("/funds")
    public ResponseEntity<FundResponseDto> createFund(@Valid @RequestBody CreateFundRequestDto createFundRequestDto) {
        FundResponseDto fundResponseDto = fundService.createFund(createFundRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fundResponseDto);
    }

    @PostMapping("/funds/{fundId}/cancel")
    public ResponseEntity<MessageResponseDto> cancelFund(@PathVariable UUID fundId) {
        fundService.cancelFund(fundId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(FundMessages.FUND_CANCELLED_SUCCESSFULLY));
    }

    @GetMapping("/funds/created")
    public ResponseEntity<Page<FundResponseDto>> getCreatedFunds(
            @PageableDefault(size = 20, sort = "startsAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FundResponseDto> fundResponseDtoPage = fundService.getCreatedFunds(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fundResponseDtoPage);
    }

}
