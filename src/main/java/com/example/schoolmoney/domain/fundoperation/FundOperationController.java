package com.example.schoolmoney.domain.fundoperation;

import com.example.schoolmoney.common.constants.messages.FundOperationMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FundOperationController {

    private final FundOperationService fundOperationService;

    @PostMapping("/funds/{fundId}/pay")
    public ResponseEntity<MessageResponseDto> performPayment(@PathVariable UUID fundId, @RequestParam UUID childId, @RequestParam long amountInCents) {
        fundOperationService.performPayment(fundId, childId, amountInCents);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(FundOperationMessages.FUND_OPERATION_SUCCESSFUL));
    }

}
