package com.example.schoolmoney.domain.fundmedia;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundAccessService;
import com.example.schoolmoney.domain.fund.FundFinder;
import com.example.schoolmoney.domain.fundmedia.dto.FundMediaMapper;
import com.example.schoolmoney.domain.fundmedia.dto.internal.FileWithMetadata;
import com.example.schoolmoney.domain.fundmedia.dto.request.UpdateFundMediaFileMetadataRequestDto;
import com.example.schoolmoney.domain.fundmedia.dto.response.FundMediaResponseDto;
import com.example.schoolmoney.domain.fundmediaoperation.FundMediaOperationService;
import com.example.schoolmoney.domain.fundmediaoperation.FundMediaOperationType;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentFinder;
import com.example.schoolmoney.files.FileCategory;
import com.example.schoolmoney.files.FileTypeDetector;
import com.example.schoolmoney.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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

    private final StorageService storageService;

    private final SecurityUtils securityUtils;

    private final FundMediaOperationService fundMediaOperationService;

    private final FundAccessService fundAccessService;

    private final FundFinder fundFinder;

    private final FundMediaFinder fundMediaFinder;

    private final ParentFinder parentFinder;

    @Transactional
    public FundMediaResponseDto uploadFundMediaFile(UUID fundId, MultipartFile file) {
        log.debug("Enter uploadFundMedia(fundId={})", fundId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);
        fundAccessService.assertCanViewFund(parent, fund);
        fundAccessService.assertCanEditFund(parent, fund);

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

        log.debug("Exit uploadFundMedia(fundId={})", fundId);
        return fundMediaMapper.toDto(fundMedia);
    }

    @Transactional(readOnly = true)
    public Page<FundMediaResponseDto> getFundMediaMetadataPage(UUID fundId, Pageable pageable) {
        log.debug("Enter getFundMediaPage(fundId={}, pageable={})", fundId, pageable);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);
        fundAccessService.assertCanViewFund(parent, fund);

        Page<FundMedia> fundMediaPage = fundMediaRepository.findAllByFund_FundId(fundId, pageable);

        log.debug("Exit getFundMediaPage(fundId={}, pageable={})", fundId, pageable);
        return fundMediaPage.map(fundMediaMapper::toDto);
    }

    @Transactional(readOnly = true)
    public FileWithMetadata getFundMediaFileWithMetadata(UUID fundId, UUID fundMediaId) {
        log.debug("Enter getFundMediaFileWithMetadata(fundMediaId={})", fundMediaId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);
        fundAccessService.assertCanViewFund(parent, fund);

        FundMedia fundMedia = fundMediaFinder.getByFundIdAndFundMediaIdOrThrow(fund.getFundId(), fundMediaId);

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
    public FundMediaResponseDto updateFundMediaFileMetadata(UUID fundId, UUID fundMediaId, UpdateFundMediaFileMetadataRequestDto updateFundMediaFileMetadataRequestDto) {
        log.debug("Enter updateFundMediaFileMetadata(fundId={}, fundMediaId={})", fundId, fundMediaId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);
        fundAccessService.assertCanViewFund(parent, fund);
        fundAccessService.assertCanEditFund(parent, fund);

        FundMedia fundMedia = fundMediaFinder.getByFundIdAndFundMediaIdOrThrow(fund.getFundId(), fundMediaId);

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

        log.debug("Exit updateFundMediaFileMetadata(fundId={}, fundMediaId={})", fundId, fundMediaId);
        return fundMediaMapper.toDto(fundMedia);
    }

    @Transactional
    public void deleteFundMediaFile(UUID fundId, UUID fundMediaId) {
        log.debug("Enter deleteFundMediaFile(fundId={}, fundMediaId={})", fundId, fundMediaId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);
        fundAccessService.assertCanViewFund(parent, fund);
        fundAccessService.assertCanEditFund(parent, fund);
        fundAccessService.assertFundIsActive(fund);

        FundMedia fundMedia = fundMediaFinder.getByFundIdAndFundMediaIdOrThrow(fund.getFundId(), fundMediaId);

        String fileId = fundMedia.getFileId().toString();

        storageService.deleteFile(fileId, bucketName);

        fundMediaRepository.deleteById(fundMediaId);
        log.info("Fund media with id={} deleted", fundMedia.getFundMediaId());

        fundMediaOperationService.saveFundMediaOperation(
                parent,
                fundMedia.getFundMediaId(),
                fundMedia.getFilename(),
                fundMedia.getMediaType(),
                fundMedia.getFund().getFundId(),
                FundMediaOperationType.DELETE
        );

        log.debug("Exit deleteFundMediaFile(fundId={}, fundMediaId={})", fundId, fundMediaId);
    }

}
