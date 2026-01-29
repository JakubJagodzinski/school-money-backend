package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.fundoperation.FundOperationService;
import com.example.schoolmoney.domain.fundoperation.FundOperationType;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentFinder;
import com.example.schoolmoney.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundProcessingService {

    private final FundRepository fundRepository;

    private final ChildRepository childRepository;

    private final EmailService emailService;

    private final FundOperationService fundOperationService;

    private final FundAccessService fundAccessService;

    private final SecurityUtils securityUtils;

    private final FundFinder fundFinder;

    private final FundOperationRepository fundOperationRepository;

    private final ParentFinder parentFinder;

    @Transactional
    public void cancelFund(UUID fundId) throws IllegalStateException {
        log.debug("Enter cancelFund(fundId={})", fundId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);
        fundAccessService.assertCanViewFund(parent, fund);
        fundAccessService.assertCanEditFund(parent, fund);
        fundAccessService.assertFundIsNotBlocked(fund);
        fundAccessService.assertFundIsNotFinishedAndNotCancelled(fund);

        List<FundOperation> fundOperations = fundOperationRepository.findAllByFund_FundId(fundId);

        if (!fundOperations.isEmpty()) {
            long fundTreasurerBalance = calculateFundTreasurerBalance(fundOperations);

            if (fundTreasurerBalance < 0) {
                log.warn(FundMessages.CANNOT_CANCEL_FUND_BECAUSE_OF_MISSING_FUNDS);
                throw new IllegalStateException(FundMessages.CANNOT_CANCEL_FUND_BECAUSE_OF_MISSING_FUNDS);
            }
            if (fundTreasurerBalance > 0) {
                log.warn(FundMessages.CANNOT_CANCEL_FUND_BECAUSE_OF_REMAINING_TREASURER_DEPOSITS);
                throw new IllegalStateException(FundMessages.CANNOT_CANCEL_FUND_BECAUSE_OF_REMAINING_TREASURER_DEPOSITS);
            }
        }

        fund.cancel();
        fundRepository.save(fund);
        log.info("Fund with id={} cancelled successfully", fund.getFundId());

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        sendFundCancelledEmailsToParents(fund);
                        fundOperationService.processParentRefunds(fundOperations);
                    }
                }
        );

        log.debug("Exit cancelFund(fundId={})", fundId);
    }

    private long calculateFundTreasurerBalance(List<FundOperation> fundOperations) {
        long fundTreasurerBalance = 0;

        for (FundOperation fundOperation : fundOperations) {
            FundOperationType fundOperationType = fundOperation.getOperationType();
            if (fundOperationType.equals(FundOperationType.FUND_DEPOSIT)) {
                fundTreasurerBalance += fundOperation.getAmountInCents();
            } else if (fundOperationType.equals(FundOperationType.FUND_WITHDRAWAL)) {
                fundTreasurerBalance -= fundOperation.getAmountInCents();
            }
        }

        return fundTreasurerBalance;
    }

    @Transactional
    public void markFundAsFinished(Fund fund) {
        log.debug("Enter markFundAsFinished(fundId={})", fund.getFundId());

        fund.finish();
        fundRepository.save(fund);
        log.info("Finished fund with fundId={}", fund.getFundId());

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        sendFundFinishedEmailsToParents(fund);
                    }
                }
        );

        log.debug("Exit markFundAsFinished(fundId={})", fund.getFundId());
    }

    @Transactional
    public void markFundAsActive(Fund fund) {
        log.debug("Enter markFundAsActive(fundId={}", fund.getFundId());

        fund.setFundStatus(FundStatus.ACTIVE);
        fundRepository.save(fund);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        sendFundStartedEmailsToParents(fund);
                    }
                }
        );

        log.debug("Exit markFundAsActive(fundId={})", fund.getFundId());
    }

    private void sendFundCancelledEmailsToParents(Fund fund) {
        log.debug("Enter sendFundCancelledEmailsToParents(fundId={})", fund.getFundId());

        List<Parent> participatinsParentsList = childRepository.findSchoolClassDistinctParents(fund.getSchoolClass().getSchoolClassId());

        for (Parent participatingParent : participatinsParentsList) {
            emailService.sendFundCancelledEmail(
                    participatingParent.getEmail(),
                    participatingParent.getFirstName(),
                    fund.getTitle(),
                    fund.getSchoolClass().getFullName(),
                    participatingParent.isNotificationsEnabled()
            );
        }

        log.debug("Exit sendFundCancelledEmailsToParents(fundId={})", fund.getFundId());
    }

    private void sendFundFinishedEmailsToParents(Fund fund) {
        log.debug("Enter sendFundFinishedEmailsToParents(fundId={})", fund.getFundId());

        List<Parent> parents = childRepository.findSchoolClassDistinctParents(fund.getSchoolClass().getSchoolClassId());

        for (Parent parent : parents) {
            emailService.sendFundFinishedEmail(
                    parent.getEmail(),
                    parent.getFirstName(),
                    fund.getTitle(),
                    fund.getSchoolClass().getFullName(),
                    parent.isNotificationsEnabled()
            );
        }

        log.debug("Exit sendFundFinishedEmailsToParents(fundId={})", fund.getFundId());
    }

    private void sendFundStartedEmailsToParents(Fund fund) {
        log.debug("Enter sendFundStartedEmailsToParents(fundId={})", fund.getFundId());

        List<Parent> parents = childRepository.findSchoolClassDistinctParents(fund.getSchoolClass().getSchoolClassId());

        for (Parent parent : parents) {
            emailService.sendFundStartedEmail(
                    parent.getEmail(),
                    parent.getFirstName(),
                    fund.getTitle(),
                    fund.getSchoolClass().getFullName(),
                    parent.isNotificationsEnabled()
            );
        }

        log.debug("Exit sendFundStartedEmailsToParents(fundId={})", fund.getFundId());
    }

}
