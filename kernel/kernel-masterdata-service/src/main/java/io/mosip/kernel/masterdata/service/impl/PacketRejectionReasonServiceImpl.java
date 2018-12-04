package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.PacketRejectionReasonErrorCode;
import io.mosip.kernel.masterdata.dto.ReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.PacketRejectionReasonResponseDto;
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
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * 
 * @author Srinivasan 
 * 
 * This class implements PacketRejectionReasonService has all
 *         the logics to store and retrieve data from database
 *         {@link ReasonCategoryRepository} and {@link ReasonListRepository}
 *
 */
@Service
public class PacketRejectionReasonServiceImpl implements PacketRejectionReasonService {

	/** 
	 * reason repository instance
	 */
	@Autowired
	ReasonCategoryRepository reasonRepository;
 
	/**
	 * reason list repository instance
	 */
	@Autowired
	ReasonListRepository reasonListRepository;

	/**
	 * objetMapperUtil instance
	 */
	@Autowired
	MapperUtils objectMapperUtil;

	/**
	 * metadataUtil instance
	 */
	@Autowired
	MetaDataUtils metaDataUtils;

	@Autowired
	DataMapper dataMapper;

	/**
	 * Method fetches all the reasons from Database irrespective of code or
	 * languagecode {@inheritDoc}
	 */
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
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
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

	/**
	 * Method fetchs reason based on reasonCategorycode and langCode {@inheritDoc}
	 */
	@Override
	public PacketRejectionReasonResponseDto getReasonsBasedOnLangCodeAndCategoryCode(String categoryCode,
			String langCode) {

		List<ReasonCategory> reasonCategories = null;
		List<ReasonCategoryDto> reasonCategoryDtos = null;
		PacketRejectionReasonResponseDto reasonResponseDto = new PacketRejectionReasonResponseDto();

		try {
			reasonCategories = reasonRepository.findReasonCategoryByCodeAndLangCode(categoryCode, langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorCode(),
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
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

	/**
	 * Method creates Reason Category data based on the request sent. {@inheritDoc}
	 */
	@Override
	public CodeAndLanguageCodeID createReasonCategories(RequestDto<ReasonCategoryDto> reasonRequestDto) {
		ReasonCategory reasonCategories = metaDataUtils.setCreateMetaData(reasonRequestDto.getRequest(),
				ReasonCategory.class);

		CodeAndLanguageCodeID reasonCategoryId = new CodeAndLanguageCodeID();
		ReasonCategory resultantReasonCategory = null;

		try {

			resultantReasonCategory = reasonRepository.create(reasonCategories);

		} catch (DataAccessLayerException e) {

			throw new MasterDataServiceException(
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_INSERT_EXCEPTION.getErrorCode(),
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}

		dataMapper.map(resultantReasonCategory, reasonCategoryId, true, null, null, true);

		return reasonCategoryId;

	}

	/**
	 * Method creates ReasonList with the parameter that is sent in Request.
	 * {@inheritDoc}
	 */
	@Override
	public CodeLangCodeAndRsnCatCodeID createReasonList(RequestDto<ReasonListDto> reasonRequestDto) {
		ReasonList reasonList = metaDataUtils.setCreateMetaData(reasonRequestDto.getRequest(), ReasonList.class);

		CodeLangCodeAndRsnCatCodeID reasonListId = new CodeLangCodeAndRsnCatCodeID();
		ReasonList resultantReasonList;

		try {

			resultantReasonList = reasonListRepository.create(reasonList);

		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorCode(),
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}

		dataMapper.map(resultantReasonList, reasonListId, true, null, null, true);

		return reasonListId;

	}

}
