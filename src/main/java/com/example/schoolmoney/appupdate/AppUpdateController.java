package com.example.schoolmoney.appupdate;

import com.example.schoolmoney.appupdate.dto.request.CreateAppUpdateRequestDto;
import com.example.schoolmoney.appupdate.dto.response.CurrentAppVersionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AppUpdateController {

    private final AppUpdateService appUpdateService;

    @PostMapping("/app/update")
    public ResponseEntity<Void> updateApp(
            @RequestHeader("Update-Secret-Key") String secretKey,
            @RequestBody CreateAppUpdateRequestDto requestDto
    ) {
        appUpdateService.saveAppUpdate(secretKey, requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/app/version")
    public ResponseEntity<CurrentAppVersionResponseDto> getCurrentAppVersion() {
        CurrentAppVersionResponseDto responseDto = appUpdateService.getCurrentAppVersion();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

}
