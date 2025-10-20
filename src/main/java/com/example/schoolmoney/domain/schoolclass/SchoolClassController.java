package com.example.schoolmoney.domain.schoolclass;

import com.example.schoolmoney.domain.child.dto.response.ChildWithParentInfoResponseDto;
import com.example.schoolmoney.domain.schoolclass.dto.request.CreateSchoolClassRequestDto;
import com.example.schoolmoney.domain.schoolclass.dto.response.SchoolClassInvitationCodeResponseDto;
import com.example.schoolmoney.domain.schoolclass.dto.response.SchoolClassResponseDto;
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
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    @GetMapping("/school-classes/all")
    public ResponseEntity<Page<SchoolClassResponseDto>> getAllSchoolClasses(
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<SchoolClassResponseDto> schoolClassResponseDtoPage = schoolClassService.getAllSchoolClasses(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(schoolClassResponseDtoPage);
    }

    @Operation(
            summary = "Get short info for each school class of parent children and where parent is treasurer",
            description = """
                    Returns paginated list of school classes that belong to the parent's children,
                    with summary details like number of children and number of active funds.
                    Classes where parent is a treasurer are also returned
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "School classes retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SchoolClassResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            )
    })
    @GetMapping("/school-classes")
    public ResponseEntity<Page<SchoolClassResponseDto>> getTreasurerAndParentChildrenSchoolClasses(
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<SchoolClassResponseDto> schoolClassResponseDtoPage = schoolClassService.getTreasurerAndParentChildrenSchoolClasses(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(schoolClassResponseDtoPage);
    }

    @Operation(
            summary = "Get all children from the school class",
            description = """
                    Returns paginated list of all children assigned to the specified school class.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "School class children retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChildWithParentInfoResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            )
    })
    @GetMapping("/school-classes/{schoolClassId}/children")
    public ResponseEntity<Page<ChildWithParentInfoResponseDto>> getSchoolClassAllChildren(
            @PathVariable UUID schoolClassId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "birthDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ChildWithParentInfoResponseDto> childResponseDtoPage = schoolClassService.getSchoolClassAllChildren(schoolClassId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(childResponseDtoPage);
    }

    @PostMapping("/school-classes")
    public ResponseEntity<SchoolClassResponseDto> createSchoolClass(@Valid @RequestBody CreateSchoolClassRequestDto createSchoolClassRequestDto) {
        SchoolClassResponseDto schoolClassResponseDto = schoolClassService.createSchoolClass(createSchoolClassRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(schoolClassResponseDto);
    }

    @PostMapping("/school-classes/{schoolClassId}/invitation-code")
    public ResponseEntity<SchoolClassInvitationCodeResponseDto> regenerateInvitationCode(@PathVariable UUID schoolClassId) {
        SchoolClassInvitationCodeResponseDto schoolClassInvitationCodeResponseDto = schoolClassService.regenerateInvitationCode(schoolClassId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(schoolClassInvitationCodeResponseDto);
    }

}
