package com.example.schoolmoney.domain.parent;

import com.example.schoolmoney.domain.parent.dto.response.ParentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
