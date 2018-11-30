package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.dto.ApplicationData;
import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.dto.ApplicationResponseDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.Application;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.ApplicationRepository;
import io.mosip.kernel.masterdata.service.ApplicationService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

@Service
public class ApplicationServiceImpl implements ApplicationService {

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	private DataMapper dataMapper;

	private List<Application> applicationList;

	/**
	 * Get All Applications
	 * 
	 * @return {@link List<ApplicationDto>}
	 */
	@Override
	public ApplicationResponseDto getAllApplication() {
		List<ApplicationDto> applicationDtoList = new ArrayList<>();
		try {
			applicationList = applicationRepository.findAllByIsDeletedFalse(Application.class);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (!(applicationList.isEmpty())) {
			applicationList.forEach(application -> {
				ApplicationDto applicationDto = new ApplicationDto();
				dataMapper.map(application, applicationDto, true, null, null, true);
				applicationDtoList.add(applicationDto);
			});
		} else {
			throw new DataNotFoundException(ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		ApplicationResponseDto applicationResponseDto = new ApplicationResponseDto();
		applicationResponseDto.setApplicationtypes(applicationDtoList);
		return applicationResponseDto;
	}

	@Override
	public ApplicationResponseDto getAllApplicationByLanguageCode(String languageCode) {
		List<ApplicationDto> applicationDtoList = new ArrayList<>();
		try {
			applicationList = applicationRepository.findAllByLangCodeAndIsDeletedFalse(languageCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorMessage());
		}
		if (!(applicationList.isEmpty())) {
			applicationList.forEach(application -> {
				ApplicationDto applicationDto = new ApplicationDto();
				dataMapper.map(application, applicationDto, true, null, null, true);
				applicationDtoList.add(applicationDto);
			});
		} else {
			throw new DataNotFoundException(ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		ApplicationResponseDto applicationResponseDto = new ApplicationResponseDto();
		applicationResponseDto.setApplicationtypes(applicationDtoList);
		return applicationResponseDto;
	}

	@Override
	public ApplicationResponseDto getApplicationByCodeAndLanguageCode(String code, String languageCode) {
		Application application;
		ApplicationDto applicationDto = new ApplicationDto();
		List<ApplicationDto> applicationDtoList = new ArrayList<>();
		try {
			application = applicationRepository.findByCodeAndLangCodeAndIsDeletedFalse(code, languageCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorMessage());
		}
		if (application != null) {
			dataMapper.map(application, applicationDto, true, null, null, true);
			applicationDtoList.add(applicationDto);
		} else {
			throw new DataNotFoundException(ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		ApplicationResponseDto applicationResponseDto = new ApplicationResponseDto();
		applicationResponseDto.setApplicationtypes(applicationDtoList);
		return applicationResponseDto;
	}

	@Override
	public CodeAndLanguageCodeID addApplicationData(RequestDto<ApplicationData> applicationRequestDto) {
		Application entity = metaUtils.setCreateMetaData(applicationRequestDto.getRequest().getApplicationtype(),
				Application.class);
		Application application;
		try {
			application = applicationRepository.create(entity);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		dataMapper.map(application, codeLangCodeId, true, null, null, true);
		return codeLangCodeId;
	}
}
