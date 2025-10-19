package com.example.schoolmoney.domain.fundoperation;

import com.example.schoolmoney.common.constants.messages.FundOperationMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Pay the child's contribution to the fund",
            description = """
                    Performs a payment operation where a parent pays a specified amount
                    towards their child's contribution to a particular fund.
                    Partial payments are not supported - parent has to pay full amount at once
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment performed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Parent is not authorized to pay for this fund or Child does not belong to the Parent",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Payment amount does not match the child's contribution amount or Contribution already paid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            )
    })
    @PostMapping("/funds/{fundId}/pay")
    public ResponseEntity<MessageResponseDto> performPayment(@PathVariable UUID fundId, @RequestParam UUID childId, @RequestParam long amountInCents) {
        fundOperationService.performPayment(fundId, childId, amountInCents);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(FundOperationMessages.FUND_PAYMENT_OPERATION_SUCCESSFUL));
    }

    @Operation(
            summary = "Withdraw funds from the fund",
            description = """
                    Performs a withdrawal operation where a treasurer withdraws a specified amount
                    from a particular fund.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Withdrawal operation performed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Parent is not a treasurer of the school class",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fund not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Insufficient funds available for withdrawal",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            )
    })
    @PostMapping("/funds/{fundId}/withdraw")
    public ResponseEntity<MessageResponseDto> withdrawFromFund(@PathVariable UUID fundId, @RequestParam long amountInCents) {
        fundOperationService.withdrawFromFund(fundId, amountInCents);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(FundOperationMessages.FUND_WITHDRAWAL_OPERATION_SUCCESSFUL));
    }

    @Operation(
            summary = "Deposit funds to the fund",
            description = """
                    Performs a deposit operation where a treasurer deposits a specified amount
                    to a particular fund.
                    Cannot deposit more than remaining withdrawn amount.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Deposit operation performed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Parent is not a treasurer of the school class",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fund not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Cannot deposit more than remaining withdrawn amount",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            )
    })
    @PostMapping("/funds/{fundId}/deposit")
    public ResponseEntity<MessageResponseDto> depositToFund(@PathVariable UUID fundId, @RequestParam long amountInCents) {
        fundOperationService.depositToFund(fundId, amountInCents);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(FundOperationMessages.FUND_DEPOSIT_OPERATION_SUCCESSFUL));
    }

}
