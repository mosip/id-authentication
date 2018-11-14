package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.PacketRejectionReasonErrorCode;
import io.mosip.kernel.masterdata.dto.ReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonResponseDto;
import io.mosip.kernel.masterdata.entity.ReasonCategory;
import io.mosip.kernel.masterdata.exception.ReasonsFetchException;
import io.mosip.kernel.masterdata.repository.ReasonRepository;
import io.mosip.kernel.masterdata.service.ReasonService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@Service
public class ReasonServiceImpl implements ReasonService {

	@Autowired
	ReasonRepository reasonRepository;

	@Autowired
	ObjectMapperUtil ObjectMapperUtil;

	@Override
	public ReasonResponseDto getAllReasons() {
		List<ReasonCategory> reasonCategories = null;
		List<ReasonCategoryDto> reasonCategoryDtos = null;
		ReasonResponseDto reasonResponseDto = new ReasonResponseDto();

		try {
			reasonCategories = reasonRepository.findAll();
		} catch (DataAccessException e) {
			throw new ReasonsFetchException(
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorCode(),
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorMessage());
		}
		if (reasonCategories != null && !reasonCategories.isEmpty()) {
			try {
				reasonCategoryDtos = ObjectMapperUtil.reasonConverter(reasonCategories);
			} catch (IllegalArgumentException | ConfigurationException | MappingException exception) {
				throw new ReasonsFetchException(
						PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_MAPPING_EXCEPTION.getErrorCode(),
						PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_MAPPING_EXCEPTION.getErrorMessage());
			}
		} else {
			throw new ReasonsFetchException(
					PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorCode(),
					PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorMessage());
		}
		reasonResponseDto.setReasonCategories(reasonCategoryDtos);

		return reasonResponseDto;
	}

	@Override
	public ReasonResponseDto getReasonsBasedOnLangCodeAndCategoryCode(String categoryCode, String langCode) {

		List<ReasonCategory> reasonCategories = null;
		List<ReasonCategoryDto> reasonCategoryDtos = null;
		ReasonResponseDto reasonResponseDto = new ReasonResponseDto();
        if(categoryCode!=null && langCode!=null && !categoryCode.isEmpty() && !langCode.isEmpty()) {
		try {
			reasonCategories = reasonRepository.findReasonCategoryByCodeAndLanguageCode(categoryCode, langCode);
		} catch (DataAccessException e) {
			throw new ReasonsFetchException(
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorCode(),
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_FETCH_EXCEPTION.getErrorMessage());
		}
		if (reasonCategories != null && !reasonCategories.isEmpty()) {
			try {
				reasonCategoryDtos = ObjectMapperUtil.reasonConverter(reasonCategories);
			} catch (IllegalArgumentException | ConfigurationException | MappingException exception) {
				throw new ReasonsFetchException(
						PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_MAPPING_EXCEPTION.getErrorCode(),
						PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_MAPPING_EXCEPTION.getErrorMessage());

			}
		} else {
			throw new ReasonsFetchException(
					PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorCode(),
					PacketRejectionReasonErrorCode.NO_PACKET_REJECTION_REASONS_FOUND.getErrorMessage());
		}
		reasonResponseDto.setReasonCategories(reasonCategoryDtos);
        }
        else
        {
        	throw new ReasonsFetchException(
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_ARGUMENT_NOT_FOUND_EXCEPTION.getErrorCode(),
					PacketRejectionReasonErrorCode.PACKET_REJECTION_REASONS_ARGUMENT_NOT_FOUND_EXCEPTION.getErrorMessage());	
        }
		return reasonResponseDto;
	}

}
