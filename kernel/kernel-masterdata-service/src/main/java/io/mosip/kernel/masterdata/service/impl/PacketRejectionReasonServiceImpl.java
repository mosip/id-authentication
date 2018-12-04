package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.PacketRejectionReasonErrorCode;
import io.mosip.kernel.masterdata.dto.ReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonCategoryRequestDto;
import io.mosip.kernel.masterdata.dto.ReasonListRequestDto;
import io.mosip.kernel.masterdata.dto.ReasonListResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PacketRejectionReasonResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostResponseDto;
import io.mosip.kernel.masterdata.entity.ReasonCategory;
import io.mosip.kernel.masterdata.entity.ReasonList;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.entity.id.CodeLangCodeAndRsnCatCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.ReasonCategoryRepository;
import io.mosip.kernel.masterdata.repository.ReasonListRepository;
import io.mosip.kernel.masterdata.service.PacketRejectionReasonService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;

@Service
public class PacketRejectionReasonServiceImpl implements PacketRejectionReasonService {

	@Autowired
	ReasonCategoryRepository reasonRepository;

	@Autowired
	ReasonListRepository reasonListRepository;

	@Autowired
	MapperUtils objectMapperUtil;

	@Autowired
	MetaDataUtils metaDataUtils;

	@Autowired
	DataMapper dataMapper;

	@Override
	public PacketRejectionReasonResponseDto getAllReasons() {
		List<ReasonCategory> reasonCategories = null;
		List<ReasonCategoryDto> reasonCategoryDtos = null;
		PacketRejectionReasonResponseDto reasonResponseDto = new PacketRejectionReasonResponseDto();

		try {
			reasonCategories = reasonRepository.findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorCode(),
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorMessage());
		}
		if (reasonCategories != null && !reasonCategories.isEmpty()) {
			reasonCategoryDtos = objectMapperUtil.reasonConverter(reasonCategories);

		} else {
			throw new DataNotFoundException(
					PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorCode(),
					PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorMessage());
		}
		reasonResponseDto.setReasonCategories(reasonCategoryDtos);

		return reasonResponseDto;
	}

	@Override
	public PacketRejectionReasonResponseDto getReasonsBasedOnLangCodeAndCategoryCode(String categoryCode,
			String langCode) {

		List<ReasonCategory> reasonCategories = null;
		List<ReasonCategoryDto> reasonCategoryDtos = null;
		PacketRejectionReasonResponseDto reasonResponseDto = new PacketRejectionReasonResponseDto();

		try {
			reasonCategories = reasonRepository
					.findReasonCategoryByCodeAndLangCode(categoryCode, langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorCode(),
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorMessage());
		}
		if (reasonCategories != null && !reasonCategories.isEmpty()) {

			reasonCategoryDtos = objectMapperUtil.reasonConverter(reasonCategories);

		} else {
			throw new RequestException(PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorCode(),
					PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorMessage());
		}
		reasonResponseDto.setReasonCategories(reasonCategoryDtos);

		return reasonResponseDto;
	}

	@Override
	public PostResponseDto saveReasonCategories(ReasonCategoryRequestDto reasonRequestDto) {
		List<ReasonCategory> reasonCategories = metaDataUtils.setCreateMetaData(reasonRequestDto.getReasonCategories(),
				ReasonCategory.class);
		List<CodeAndLanguageCodeID> reasonCategoryIds = new ArrayList<>();
		PostResponseDto reasonResponseDto = new PostResponseDto();
		CodeAndLanguageCodeID reasonCategoryId = new CodeAndLanguageCodeID();
		List<ReasonCategory> resultantReasonCategory = null;
		if (!reasonCategories.isEmpty()) {
			try {

				resultantReasonCategory = reasonRepository.saveAll(reasonCategories);

			} catch (DataAccessException e) {

				
				throw new MasterDataServiceException(
						PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorCode(),
						PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorMessage());
			}
			if (!resultantReasonCategory.isEmpty()) {
				resultantReasonCategory.parallelStream().forEach(reasonCategory -> {

					dataMapper.map(reasonCategory, reasonCategoryId, true, null, null, true);

					reasonCategoryIds.add(reasonCategoryId);
				});
			} else {
				throw new DataNotFoundException(
						PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorCode(),
						PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorMessage());
			}
			reasonResponseDto.setResults(reasonCategoryIds);
		} else {
			throw new DataNotFoundException(
					PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorCode(),
					PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorMessage());
		}
		return reasonResponseDto;

	}

	@Override
	public ReasonListResponseDto saveReasonList(ReasonListRequestDto reasonRequestDto) {
		List<ReasonList> reasonList = metaDataUtils.setCreateMetaData(reasonRequestDto.getReasonList(),
				ReasonList.class);
		List<CodeLangCodeAndRsnCatCodeID> reasonListIds = new ArrayList<>();
		ReasonListResponseDto reasonResponseDto = new ReasonListResponseDto();
		CodeLangCodeAndRsnCatCodeID reasonListId = new CodeLangCodeAndRsnCatCodeID();
		List<ReasonList> resultantReasonList = null;
		if (!reasonList.isEmpty()) {
			try {

				resultantReasonList = reasonListRepository.saveAll(reasonList);

			} catch (DataAccessException e) {
				throw new MasterDataServiceException(
						PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorCode(),
						PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorMessage());
			}
			if (!resultantReasonList.isEmpty()) {
				resultantReasonList.parallelStream().forEach(reasonListObj -> {

					dataMapper.map(reasonListObj, reasonListId, true, null, null, true);

					reasonListIds.add(reasonListId);
				});
			} else {
				throw new DataNotFoundException(
						PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorCode(),
						PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorMessage());
			}
		} else {
			throw new DataNotFoundException(
					PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorCode(),
					PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorMessage());
		}
		reasonResponseDto.setReasonList(reasonListIds);
		return reasonResponseDto;

	}

}
