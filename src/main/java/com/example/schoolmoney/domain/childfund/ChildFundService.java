package com.example.schoolmoney.domain.childfund;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.child.dto.ChildMapper;
import com.example.schoolmoney.domain.childfund.dto.response.ParentChildHistoryFundResponseDto;
import com.example.schoolmoney.domain.childfund.dto.response.ParentChildUnpaidFundResponseDto;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundService;
import com.example.schoolmoney.domain.fund.dto.FundMapper;
import com.example.schoolmoney.domain.fund.dto.response.FundProgressResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildFundService {

    private final ChildFundRepository childFundRepository;

    private final SecurityUtils securityUtils;

    private final ChildRepository childRepository;

    private final FundRepository fundRepository;

    private final FundMapper fundMapper;

    private final ChildMapper childMapper;

    private final FundService fundService;

    public Page<ParentChildHistoryFundResponseDto> getParentChildrenFundsHistory(Pageable pageable) {
        log.debug("Enter getParentChildrenFundsHistory(pageable={})", pageable);

        UUID parentId = securityUtils.getCurrentUserId();

        Page<ChildFundView> childFundViewPage = childFundRepository.findParentChildrenFundsHistory(parentId, pageable);

        Map<UUID, Child> childMap = getChildMap(childFundViewPage);

        Map<UUID, Fund> fundMap = getFundMap(childFundViewPage);

        Map<UUID, FundProgressResponseDto> fundProgressMap = getFundProgressMap(fundMap);

        Page<ParentChildHistoryFundResponseDto> childFundDtoPage = childFundViewPage.map(view -> ParentChildHistoryFundResponseDto.builder()
                .fund(fundMapper.toDto(fundMap.get(view.getFundId())))
                .child(childMapper.toShortInfoDto(childMap.get(view.getChildId())))
                .childStatus(view.getChildStatus())
                .timestamp(view.getTimestamp())
                .build()
        );

        for (ParentChildHistoryFundResponseDto parentChildHistoryFundResponseDto : childFundDtoPage.getContent()) {
            UUID fundId = parentChildHistoryFundResponseDto.getFund().getFundId();
            parentChildHistoryFundResponseDto.getFund().setFundProgress(fundProgressMap.get(fundId));
        }

        log.debug("Exit getParentChildrenFundsHistory(pageable={})", pageable);
        return childFundDtoPage;
    }

    public Page<ParentChildUnpaidFundResponseDto> getParentChildrenSchoolClassUnpaidFunds(UUID schoolClassId, Pageable pageable) {
        log.debug("Enter getParentChildrenSchoolClassUnpaidFunds(schoolClassId={}, pageable={})", schoolClassId, pageable);

        if (schoolClassId == null) {
            log.debug("No school class id provided, getting all unpaid funds for current parent");
        }

        UUID parentId = securityUtils.getCurrentUserId();

        Page<ChildFundView> childFundViewPage = childFundRepository.findParentChildrenUnpaidFunds(parentId, schoolClassId, pageable);

        Map<UUID, Child> childMap = getChildMap(childFundViewPage);

        Map<UUID, Fund> fundMap = getFundMap(childFundViewPage);

        Map<UUID, FundProgressResponseDto> fundProgressMap = getFundProgressMap(fundMap);

        Page<ParentChildUnpaidFundResponseDto> childFundDtoPage = childFundViewPage.map(view -> ParentChildUnpaidFundResponseDto.builder()
                .fund(fundMapper.toDto(fundMap.get(view.getFundId())))
                .childStatus(view.getChildStatus())
                .child(childMapper.toShortInfoDto(childMap.get(view.getChildId())))
                .timestamp(view.getTimestamp())
                .build()
        );

        for (ParentChildUnpaidFundResponseDto parentChildUnpaidFundResponseDto : childFundDtoPage.getContent()) {
            UUID fundId = parentChildUnpaidFundResponseDto.getFund().getFundId();
            parentChildUnpaidFundResponseDto.getFund().setFundProgress(fundProgressMap.get(fundId));
        }

        log.debug("Exit getParentChildrenSchoolClassUnpaidFunds(schoolClassId={}, pageable={})", schoolClassId, pageable);
        return childFundDtoPage;
    }

    private Map<UUID, Child> getChildMap(Page<ChildFundView> childFundViewPage) {
        return childRepository.findAllById(
                        childFundViewPage.stream()
                                .map(ChildFundView::getChildId)
                                .collect(Collectors.toSet())
                ).stream()
                .collect(Collectors.toMap(Child::getChildId, c -> c));
    }

    private Map<UUID, Fund> getFundMap(Page<ChildFundView> childFundViewPage) {
        return fundRepository.findAllById(
                        childFundViewPage.stream()
                                .map(ChildFundView::getFundId)
                                .collect(Collectors.toSet())
                ).stream()
                .collect(Collectors.toMap(Fund::getFundId, f -> f));
    }

    private Map<UUID, FundProgressResponseDto> getFundProgressMap(Map<UUID, Fund> fundMap) {
        Map<UUID, FundProgressResponseDto> fundProgressMap = new HashMap<>();
        for (Fund fund : fundMap.values()) {
            fundProgressMap.put(fund.getFundId(), fundService.countFundProgress(fund.getFundId()));
        }
        return fundProgressMap;
    }

}
