package com.example.schoolmoney.domain.fundmedia;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FundMediaController {

    private final FundMediaService fundMediaService;

}
