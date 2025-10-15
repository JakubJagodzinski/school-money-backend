package com.example.schoolmoney.domain.childignoredfund;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildIgnoredFundService {

    private final ChildIgnoredFundRepository childIgnoredFundRepository;

}
