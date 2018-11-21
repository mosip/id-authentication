package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.dto.ApplicationRequestDto;
import io.mosip.kernel.masterdata.dto.ApplicationResponseDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.entity.Application;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
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
			applicationList = applicationRepository.findAllByIsActiveTrueAndIsDeletedFalse(Application.class);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (!(applicationList.isEmpty())) {
			applicationList.forEach(application -> {
				ApplicationDto applicationDto = new ApplicationDto();
				try {
					dataMapper.map(application, applicationDto, true, null, null, true);
				} catch (DataMapperException e) {
					throw new MasterDataServiceException(
							ApplicationErrorCode.APPLICATION_MAPPING_EXCEPTION.getErrorCode(), e.getMessage());
				}
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
			applicationList = applicationRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(languageCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorMessage());
		}
		if (!(applicationList.isEmpty())) {
			applicationList.forEach(application -> {
				ApplicationDto applicationDto = new ApplicationDto();
				try {
					dataMapper.map(application, applicationDto, true, null, null, true);
				} catch (DataMapperException e) {
					throw new MasterDataServiceException(
							ApplicationErrorCode.APPLICATION_MAPPING_EXCEPTION.getErrorCode(), e.getMessage());
				}
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
			application = applicationRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(code,
					languageCode);
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
	public PostResponseDto addApplicationData(ApplicationRequestDto application) {
		List<Application> entities = metaUtils.setCreateMetaData(application.getRequest().getApplicationtypes(),
				Application.class);
		List<Application> applications;
		try {
			applications = applicationRepository.saveAll(entities);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		List<CodeAndLanguageCodeId> codeLangCodeIds = new ArrayList<>();
		applications.forEach(app -> {
			CodeAndLanguageCodeId codeLangCodeId = new CodeAndLanguageCodeId();
			try {
				dataMapper.map(app, codeLangCodeId, true, null, null, true);
			} catch (DataMapperException e) {
				throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_MAPPING_EXCEPTION.getErrorCode(),
						e.getMessage());
			}
			codeLangCodeIds.add(codeLangCodeId);
		});
		PostResponseDto postResponseDto = new PostResponseDto();
		postResponseDto.setResults(codeLangCodeIds);
		return postResponseDto;
	}
}
