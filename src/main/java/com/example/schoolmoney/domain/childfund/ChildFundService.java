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

    public Page<ParentChildHistoryFundResponseDto> getSchoolClassParentChildrenFundsHistory(UUID schoolClassId, Pageable pageable) {
        log.debug("Enter getSchoolClassParentChildrenFundsHistory(schoolClassId={})", schoolClassId);

        UUID parentId = securityUtils.getCurrentUserId();

        Page<ChildFundView> childFundViewPage = childFundRepository.findSchoolClassParentChildrenFundsHistory(parentId, schoolClassId, pageable);

        Map<UUID, Child> childMap = getChildMap(childFundViewPage);

        Map<UUID, Fund> fundMap = getFundMap(childFundViewPage);

        Map<UUID, Double> fundProgressMap = getFundProgressMap(fundMap);

        Page<ParentChildHistoryFundResponseDto> childFundDtoPage = childFundViewPage.map(view -> new ParentChildHistoryFundResponseDto(
                childMapper.toShortInfoDto(childMap.get(UUID.fromString(view.getChildId()))),
                view.getChildStatus(),
                fundMapper.toDto(fundMap.get(UUID.fromString(view.getFundId())))
        ));

        for (ParentChildHistoryFundResponseDto parentChildHistoryFundResponseDto : childFundDtoPage.getContent()) {
            UUID fundId = parentChildHistoryFundResponseDto.getFund().getFundId();
            parentChildHistoryFundResponseDto.getFund().setFundProgress(fundProgressMap.get(fundId));
        }

        log.debug("Exit getSchoolClassParentChildrenFundsHistory(schoolClassId={})", schoolClassId);
        return childFundDtoPage;
    }

    public Page<ParentChildUnpaidFundResponseDto> getSchoolClassParentChildrenUnpaidFunds(UUID schoolClassId, Pageable pageable) {
        log.debug("Enter getSchoolClassParentChildrenUnpaidFunds(schoolClassId={}, pageable={})", schoolClassId, pageable);

        UUID parentId = securityUtils.getCurrentUserId();

        Page<ChildFundView> childFundViewPage = childFundRepository.findSchoolClassParentChildrenUnpaidFunds(parentId, schoolClassId, pageable);

        Map<UUID, Child> childMap = getChildMap(childFundViewPage);

        Map<UUID, Fund> fundMap = getFundMap(childFundViewPage);

        Map<UUID, Double> fundProgressMap = getFundProgressMap(fundMap);

        Page<ParentChildUnpaidFundResponseDto> childFundDtoPage = childFundViewPage.map(view -> new ParentChildUnpaidFundResponseDto(
                childMapper.toShortInfoDto(childMap.get(UUID.fromString(view.getChildId()))),
                fundMapper.toDto(fundMap.get(UUID.fromString(view.getFundId())))
        ));

        for (ParentChildUnpaidFundResponseDto parentChildUnpaidFundResponseDto : childFundDtoPage.getContent()) {
            UUID fundId = parentChildUnpaidFundResponseDto.getFund().getFundId();
            parentChildUnpaidFundResponseDto.getFund().setFundProgress(fundProgressMap.get(fundId));
        }

        log.debug("Exit getSchoolClassParentChildrenUnpaidFunds(schoolClassId={}, pageable={})", schoolClassId, pageable);
        return childFundDtoPage;
    }

    private Map<UUID, Child> getChildMap(Page<ChildFundView> childFundViewPage) {
        return childRepository.findAllById(
                        childFundViewPage.stream()
                                .map(ChildFundView::getChildId)
                                .map(UUID::fromString)
                                .collect(Collectors.toSet())
                ).stream()
                .collect(Collectors.toMap(Child::getChildId, c -> c));
    }

    private Map<UUID, Fund> getFundMap(Page<ChildFundView> childFundViewPage) {
        return fundRepository.findAllById(
                        childFundViewPage.stream()
                                .map(ChildFundView::getFundId)
                                .map(UUID::fromString)
                                .collect(Collectors.toSet())
                ).stream()
                .collect(Collectors.toMap(Fund::getFundId, f -> f));
    }

    private Map<UUID, Double> getFundProgressMap(Map<UUID, Fund> fundMap) {
        Map<UUID, Double> fundProgressMap = new HashMap<>();
        for (Fund fund : fundMap.values()) {
            fundProgressMap.put(fund.getFundId(), fundService.countFundProgress(fund.getFundId()));
        }
        return fundProgressMap;
    }

}
