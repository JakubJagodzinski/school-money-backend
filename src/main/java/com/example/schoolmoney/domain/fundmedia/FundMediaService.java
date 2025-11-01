package com.example.schoolmoney.domain.fundmedia;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.FundMediaMessages;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.fundmedia.dto.FundMediaMapper;
import com.example.schoolmoney.domain.fundmedia.dto.internal.FileWithMetadata;
import com.example.schoolmoney.domain.fundmedia.dto.request.UpdateFundMediaFileMetadataRequestDto;
import com.example.schoolmoney.domain.fundmedia.dto.response.FundMediaResponseDto;
import com.example.schoolmoney.domain.fundmediaoperation.FundMediaOperationService;
import com.example.schoolmoney.domain.fundmediaoperation.FundMediaOperationType;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.files.FileCategory;
import com.example.schoolmoney.files.FileTypeDetector;
import com.example.schoolmoney.storage.StorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundMediaService {

    private final String bucketName = "fund-media";

    private final FundMediaMapper fundMediaMapper;

    private final FundMediaRepository fundMediaRepository;

    private final FundRepository fundRepository;

    private final ParentRepository parentRepository;

    private final StorageService storageService;

    private final SecurityUtils securityUtils;

    private final FundMediaOperationService fundMediaOperationService;

    @Transactional
    public FundMediaResponseDto uploadFundMediaFile(UUID fundId, MultipartFile file) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Enter uploadFundMedia(fundId={})", fundId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        boolean isAuthor = fund.getAuthor().getUserId().equals(userId);
        boolean isTreasurer = fund.getSchoolClass().getTreasurer().getUserId().equals(userId);
        if (!isAuthor && !isTreasurer) {
            log.warn(FundMessages.NO_PERMISSION_TO_EDIT_FUND);
            throw new AccessDeniedException(FundMessages.NO_PERMISSION_TO_EDIT_FUND);
        }

        String fileId = storageService.uploadFile(file, bucketName, FileCategory.FUND_MEDIA);

        FundMedia fundMedia = FundMedia.builder()
                .fund(fund)
                .uploadedBy(parent)
                .fileId(UUID.fromString(fileId))
                .filename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .mediaType(FileTypeDetector.determineFileType(file.getContentType()))
                .build();

        fundMediaRepository.save(fundMedia);
        log.info("Fund media saved {}", fundMedia);

        fundMediaOperationService.saveFundMediaOperation(
                parent,
                fundMedia.getFundMediaId(),
                fundMedia.getFilename(),
                fundMedia.getMediaType(),
                fundMedia.getFund().getFundId(),
                FundMediaOperationType.UPLOAD
        );

        log.debug("Exit uploadFundMedia");
        return fundMediaMapper.toDto(fundMedia);
    }

    @Transactional
    public Page<FundMediaResponseDto> getFundMediaMetadataPage(UUID fundId, Pageable pageable) throws EntityNotFoundException {
        log.debug("Enter getFundMediaPage(fundId={}, pageable={})", fundId, pageable);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        // TODO add check if parent has access to this fund

        Page<FundMedia> fundMediaPage = fundMediaRepository.findAllByFund_FundId(fundId, pageable);

        return fundMediaPage.map(fundMediaMapper::toDto);
    }

    @Transactional
    public FileWithMetadata getFundMediaFileWithMetadata(UUID fundId, UUID fundMediaId) throws EntityNotFoundException {
        log.debug("Enter getFundMediaFileWithMetadata(fundMediaId={})", fundMediaId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();

        // TODO check access to the fund

        FundMedia fundMedia = fundMediaRepository.findById(fundMediaId)
                .orElseThrow(() -> {
                    log.warn(FundMediaMessages.FUND_MEDIA_NOT_FOUND);
                    return new EntityNotFoundException(FundMediaMessages.FUND_MEDIA_NOT_FOUND);
                });

        String fileId = fundMedia.getFileId().toString();

        InputStreamResource inputStreamResource = storageService.downloadFile(fileId, bucketName);

        FileWithMetadata fileWithMetadata = FileWithMetadata
                .builder()
                .resource(inputStreamResource)
                .contentType(MediaType.parseMediaType(fundMedia.getContentType()))
                .filename(fundMedia.getFilename())
                .build();

        log.debug("Exit getFundMediaFileWithMetadata");
        return fileWithMetadata;
    }

    @Transactional
    public FundMediaResponseDto updateFundMediaFileMetadata(
            UUID fundId,
            UUID fundMediaId,
            UpdateFundMediaFileMetadataRequestDto updateFundMediaFileMetadataRequestDto
    ) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter updateFundMediaFileMetadata(fundId={}, fundMediaId={})", fundId, fundMediaId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        boolean isAuthor = fund.getAuthor().getUserId().equals(userId);
        boolean isTreasurer = fund.getSchoolClass().getTreasurer().getUserId().equals(userId);
        if (!isAuthor && !isTreasurer) {
            log.warn(FundMessages.NO_PERMISSION_TO_EDIT_FUND);
            throw new AccessDeniedException(FundMessages.NO_PERMISSION_TO_EDIT_FUND);
        }

        if (fund.getFundStatus() != FundStatus.ACTIVE) {
            log.warn(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        FundMedia fundMedia = fundMediaRepository.findById(fundMediaId)
                .orElseThrow(() -> {
                    log.warn(FundMediaMessages.FUND_MEDIA_NOT_FOUND);
                    return new EntityNotFoundException(FundMediaMessages.FUND_MEDIA_NOT_FOUND);
                });

        fundMediaMapper.updateEntityFromDto(updateFundMediaFileMetadataRequestDto, fundMedia);
        fundMediaRepository.save(fundMedia);
        log.info("Fund media updated {}", fundMedia);

        fundMediaOperationService.saveFundMediaOperation(
                parent,
                fundMedia.getFundMediaId(),
                fundMedia.getFilename(),
                fundMedia.getMediaType(),
                fundMedia.getFund().getFundId(),
                FundMediaOperationType.UPDATE
        );

        log.debug("Exit updateFundMediaFileMetadata");
        return fundMediaMapper.toDto(fundMedia);
    }

    @Transactional
    public void deleteFundMediaFile(UUID fundId, UUID fundMediaId) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter deleteFundMediaFile(fundMediaId={})", fundMediaId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        boolean isAuthor = fund.getAuthor().getUserId().equals(userId);
        boolean isTreasurer = fund.getSchoolClass().getTreasurer().getUserId().equals(userId);
        if (!isAuthor && !isTreasurer) {
            log.warn(FundMessages.NO_PERMISSION_TO_EDIT_FUND);
            throw new AccessDeniedException(FundMessages.NO_PERMISSION_TO_EDIT_FUND);
        }

        if (fund.getFundStatus() != FundStatus.ACTIVE) {
            log.warn(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        FundMedia fundMedia = fundMediaRepository.findById(fundMediaId)
                .orElseThrow(() -> {
                    log.warn(FundMediaMessages.FUND_MEDIA_NOT_FOUND);
                    return new EntityNotFoundException(FundMediaMessages.FUND_MEDIA_NOT_FOUND);
                });

        String fileId = fundMedia.getFileId().toString();

        storageService.deleteFile(fileId, bucketName);

        fundMediaRepository.deleteById(fundMediaId);
        log.info("Fund media deleted {}", fundMedia);

        fundMediaOperationService.saveFundMediaOperation(
                parent,
                fundMedia.getFundMediaId(),
                fundMedia.getFilename(),
                fundMedia.getMediaType(),
                fundMedia.getFund().getFundId(),
                FundMediaOperationType.DELETE
        );

        log.debug("Exit deleteFundMediaFile");
    }

}
