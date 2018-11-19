package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.RegistrationCenterErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterHistory;
import io.mosip.kernel.masterdata.exception.RegistrationCenterFetchException;
import io.mosip.kernel.masterdata.exception.RegistrationCenterMappingException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterHistoryRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterHistoryService;

/**
 * Service class with function to fetch details of registration center history
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public class RegistrationCenterHistoryServiceImpl implements RegistrationCenterHistoryService {

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	private RegistrationCenterHistoryRepository registrationCenterHistoryRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterHistoryService#
	 * getRegistrationCenterHistory(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public RegistrationCenterResponseDto getRegistrationCenterHistory(String registrationCenterId, String langCode,
			String effectiveDate) {
		List<RegistrationCenterHistory> registrationCenter = null;
		RegistrationCenterResponseDto registrationCenterDto = new RegistrationCenterResponseDto();

		LocalDateTime localDateTime = LocalDateTime.parse(effectiveDate);
		try {
			registrationCenter = registrationCenterHistoryRepository
					.findByIdAndLanguageCodeAndEffectivetimesLessThanEqual(registrationCenterId, langCode,
							localDateTime);
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new RegistrationCenterFetchException(
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorMessage());
		}
		if (registrationCenter == null || registrationCenter.isEmpty()) {
			throw new RegistrationCenterMappingException(
					RegistrationCenterErrorCode.REGISTRATION_CENTER_NOT_FOUND.getErrorCode(),
					RegistrationCenterErrorCode.REGISTRATION_CENTER_NOT_FOUND.getErrorMessage());
		} else {
			try {
				registrationCenterDto.setRegistrationCenters(
						modelMapper.map(registrationCenter, new TypeToken<List<RegistrationCenterDto>>() {
						}.getType()));
			} catch (IllegalArgumentException | ConfigurationException | MappingException exception) {
				throw new RegistrationCenterMappingException(
						RegistrationCenterErrorCode.REGISTRATION_CENTER_MAPPING_EXCEPTION.getErrorCode(),
						RegistrationCenterErrorCode.REGISTRATION_CENTER_MAPPING_EXCEPTION.getErrorMessage());
			}
		}
		return registrationCenterDto;
	}
}
