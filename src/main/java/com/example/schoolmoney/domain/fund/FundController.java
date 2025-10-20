package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.common.constants.messages.FundMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.domain.fund.dto.request.CreateFundRequestDto;
import com.example.schoolmoney.domain.fund.dto.request.UpdateFundRequestDto;
import com.example.schoolmoney.domain.fund.dto.response.FundResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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

    @Operation(
            summary = "Create a new fund for a school class",
            description = """
                    Creates a new fund associated with a specific school class you are treasurer of.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Fund created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FundResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            )
    })
    @PostMapping("/funds")
    public ResponseEntity<FundResponseDto> createFund(@Valid @RequestBody CreateFundRequestDto createFundRequestDto) {
        FundResponseDto fundResponseDto = fundService.createFund(createFundRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fundResponseDto);
    }

    @Operation(
            summary = "Cancel an existing fund",
            description = """
                    Cancels an existing fund identified by its ID.
                    You need to be a treasurer of the class.
                    Fund can be cancelled only when whole sum withdrew by treasurer is returned.
                    After cancellation all children who contributed to the fund will be refunded.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Fund cancelled successfully",
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
                    description = "Forbidden - You are not a treasurer of the class",
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
                    description = "Conflict - Fund cannot be cancelled because not all funds were returned by the treasurer",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
    })
    @PostMapping("/funds/{fundId}/cancel")
    public ResponseEntity<MessageResponseDto> cancelFund(@PathVariable UUID fundId) {
        fundService.cancelFund(fundId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(FundMessages.FUND_CANCELLED_SUCCESSFULLY));
    }

    @Operation(
            summary = "Get all funds created by the treasurer",
            description = """
                    Returns paginated list of all funds created by the treasurer.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Created funds retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            )
    })
    @GetMapping("/funds/created")
    public ResponseEntity<Page<FundResponseDto>> getCreatedFunds(
            @ParameterObject
            @PageableDefault(size = 20, sort = "startsAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FundResponseDto> fundResponseDtoPage = fundService.getCreatedFunds(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fundResponseDtoPage);
    }

    @Operation(
            summary = "Update an existing fund",
            description = """
                    Updates an existing fund identified by its ID.
                    You need to be a treasurer of the class.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Fund updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FundResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Fund is not active and cannot be updated",
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
                    description = "Forbidden - You are not a treasurer of the class",
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
    })
    @PatchMapping("/funds/{fundId}")
    public ResponseEntity<FundResponseDto> updateFund(@PathVariable UUID fundId, @Valid @RequestBody UpdateFundRequestDto updateFundRequestDto) {
        FundResponseDto fundResponseDto = fundService.updateFund(fundId, updateFundRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fundResponseDto);
    }

    @Operation(
            summary = "Get all funds of a school class",
            description = """
                    Returns paginated list of all funds of a specific school class.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "School class funds retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FundResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - You do not have access to this school class",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "School class not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
    })
    @GetMapping("/school-class/{schoolClassId}/funds")
    public ResponseEntity<Page<FundResponseDto>> getSchoolClassAllFunds(
            @PathVariable UUID schoolClassId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "startsAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FundResponseDto> fundResponseDtoPage = fundService.getSchoolClassAllFunds(schoolClassId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fundResponseDtoPage);
    }

}
