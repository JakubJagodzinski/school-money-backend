package com.example.schoolmoney.domain.parent;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.domain.parent.dto.request.UpdateParentRequestDto;
import com.example.schoolmoney.domain.parent.dto.response.ParentResponseDto;
import com.example.schoolmoney.user.Permission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ParentController {

    private final ParentService parentService;

    @Operation(
            summary = "Get parent information",
            description = """
                    Retrieves detailed information about the parent, including personal details.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Parent information retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ParentResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            )
    })
    @CheckPermission(Permission.PARENT_READ)
    @GetMapping("/parents")
    public ResponseEntity<ParentResponseDto> getParent() {
        ParentResponseDto parentResponseDto = parentService.getParent();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(parentResponseDto);
    }

    @Operation(
            summary = "Update parent information",
            description = """
                    Updates the parent's personal information such as first name, last name.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Parent information updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ParentResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            )
    })
    @CheckPermission(Permission.PARENT_UPDATE)
    @PatchMapping("/parents")
    public ResponseEntity<ParentResponseDto> updateParent(@Valid @RequestBody UpdateParentRequestDto updateParentRequestDto) {
        ParentResponseDto parentResponseDto = parentService.updateParent(updateParentRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(parentResponseDto);
    }

}
