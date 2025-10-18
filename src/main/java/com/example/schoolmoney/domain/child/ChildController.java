package com.example.schoolmoney.domain.child;

import com.example.schoolmoney.common.constants.messages.ChildMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.domain.child.dto.request.CreateChildRequestDto;
import com.example.schoolmoney.domain.child.dto.response.ChildShortInfoResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ChildController {

    private final ChildService childService;

    @PostMapping("/children")
    public ResponseEntity<ChildShortInfoResponseDto> createChild(@Valid @RequestBody CreateChildRequestDto createChildRequestDto) {
        ChildShortInfoResponseDto childShortInfoResponseDto = childService.createChild(createChildRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(childShortInfoResponseDto);
    }

    @PostMapping("/children/{childId}/school-class")
    public ResponseEntity<MessageResponseDto> joinClass(@PathVariable UUID childId, @RequestParam String invitationCode) {
        childService.assignChildToSchoolClass(childId, invitationCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(ChildMessages.CHILD_ADDED_TO_SCHOOL_CLASS));
    }

    @GetMapping("/children")
    public ResponseEntity<Page<ChildShortInfoResponseDto>> getParentAllChildren(
            @ParameterObject
            @PageableDefault(size = 20, sort = "birthDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ChildShortInfoResponseDto> childShortInfoResponseDtoPage = childService.getParentAllChildren(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(childShortInfoResponseDtoPage);
    }

}
