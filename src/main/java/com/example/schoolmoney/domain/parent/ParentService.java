package com.example.schoolmoney.domain.parent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParentService {

    private final ParentRepository parentRepository;

}
