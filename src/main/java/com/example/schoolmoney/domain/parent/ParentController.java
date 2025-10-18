package com.example.schoolmoney.domain.parent;

import com.example.schoolmoney.domain.parent.dto.request.UpdateParentRequestDto;
import com.example.schoolmoney.domain.parent.dto.response.ParentResponseDto;
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

    @GetMapping("/parents")
    public ResponseEntity<ParentResponseDto> getParent() {
        ParentResponseDto parentResponseDto = parentService.getParent();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(parentResponseDto);
    }

    @PatchMapping("/parents")
    public ResponseEntity<ParentResponseDto> updateParent(@Valid @RequestBody UpdateParentRequestDto updateParentRequestDto) {
        ParentResponseDto parentResponseDto = parentService.updateParent(updateParentRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(parentResponseDto);
    }

}
