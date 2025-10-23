package com.example.schoolmoney.domain.child;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.common.constants.messages.domain.ChildMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.domain.child.dto.request.CreateChildRequestDto;
import com.example.schoolmoney.domain.child.dto.request.UpdateChildRequestDto;
import com.example.schoolmoney.domain.child.dto.response.ChildShortInfoResponseDto;
import com.example.schoolmoney.domain.child.dto.response.ChildWithSchoolClassInfoResponseDto;
import com.example.schoolmoney.user.Permission;
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
public class ChildController {

    private final ChildService childService;

    @CheckPermission(Permission.CHILD_CREATE)
    @PostMapping("/children")
    public ResponseEntity<ChildShortInfoResponseDto> createChild(@Valid @RequestBody CreateChildRequestDto createChildRequestDto) {
        ChildShortInfoResponseDto childShortInfoResponseDto = childService.createChild(createChildRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(childShortInfoResponseDto);
    }

    @CheckPermission(Permission.CHILD_CLASS_JOIN)
    @PostMapping("/children/{childId}/school-class")
    public ResponseEntity<MessageResponseDto> joinClass(@PathVariable UUID childId, @RequestParam String invitationCode) {
        childService.assignChildToSchoolClass(childId, invitationCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(ChildMessages.CHILD_ADDED_TO_SCHOOL_CLASS));
    }

    @CheckPermission(Permission.PARENT_CHILDREN_READ_ALL)
    @GetMapping("/children")
    public ResponseEntity<Page<ChildWithSchoolClassInfoResponseDto>> getParentAllChildren(
            @ParameterObject
            @PageableDefault(size = 20, sort = "birthDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ChildWithSchoolClassInfoResponseDto> childShortInfoResponseDtoPage = childService.getParentAllChildren(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(childShortInfoResponseDtoPage);
    }

    @CheckPermission(Permission.CHILD_UPDATE)
    @PatchMapping("/children/{childId}")
    public ResponseEntity<ChildShortInfoResponseDto> updateChild(@PathVariable UUID childId, @Valid @RequestBody UpdateChildRequestDto updateChildRequestDto) {
        ChildShortInfoResponseDto childShortInfoResponseDto = childService.updateChild(childId, updateChildRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(childShortInfoResponseDto);
    }

}
