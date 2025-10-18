package com.example.schoolmoney.domain.parent;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.domain.parent.dto.ParentMapper;
import com.example.schoolmoney.domain.parent.dto.request.UpdateParentRequestDto;
import com.example.schoolmoney.domain.parent.dto.response.ParentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParentService {

    private final ParentMapper parentMapper;

    private final ParentRepository parentRepository;

    private final SecurityUtils securityUtils;

    @Transactional
    public ParentResponseDto getParent() {
        log.debug("enter getParent");

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        log.debug("Exit getParent");
        return parentMapper.toDto(parent);
    }

    @Transactional
    public ParentResponseDto updateParent(UpdateParentRequestDto updateParentRequestDto) {
        log.debug("enter updateParent {}", updateParentRequestDto);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        parentMapper.updateEntityFromDto(updateParentRequestDto, parent);

        parentRepository.save(parent);
        log.debug("Exit updateParent");
        return parentMapper.toDto(parent);
    }

}
