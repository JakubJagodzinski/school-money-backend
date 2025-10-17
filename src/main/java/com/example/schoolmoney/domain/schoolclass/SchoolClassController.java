package com.example.schoolmoney.domain.schoolclass;

import com.example.schoolmoney.domain.child.dto.response.ChildResponseDto;
import com.example.schoolmoney.domain.schoolclass.dto.request.CreateSchoolClassRequestDto;
import com.example.schoolmoney.domain.schoolclass.dto.response.SchoolClassResponseDto;
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
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    @GetMapping("/school-classes")
    public ResponseEntity<Page<SchoolClassResponseDto>> getAllSchoolClasses(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<SchoolClassResponseDto> schoolClassResponseDtoPage = schoolClassService.getAllSchoolClasses(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(schoolClassResponseDtoPage);
    }

    @GetMapping("/school-classes/{schoolClassId}/children")
    public ResponseEntity<Page<ChildResponseDto>> getSchoolClassAllChildren(
            @PathVariable UUID schoolClassId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ChildResponseDto> childResponseDtoPage = schoolClassService.getSchoolClassAllChildren(schoolClassId, pageable);

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

}
