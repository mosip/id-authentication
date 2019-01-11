package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.ApplicationResponseDto;
import io.mosip.kernel.masterdata.entity.Application;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.ApplicationRepository;
import io.mosip.kernel.masterdata.service.ApplicationService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Service API implementaion class for Application
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

	@Autowired
	private ApplicationRepository applicationRepository;
	

    MapperFactory mapperFactory = null;
	MapperFacade mapper = null;

	@PostConstruct
	private void postConsApplicationServiceImpl() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap(Application.class, ApplicationDto.class);
		mapper = mapperFactory.getMapperFacade();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.ApplicationService#getAllApplication()
	 */
	@Override
	public ApplicationResponseDto getAllApplication() {
		List<ApplicationDto> applicationDtoList = new ArrayList<>();
		List<Application> applicationList;
		try {
			applicationList = applicationRepository.findAllByIsDeletedFalseOrIsDeletedNull(Application.class);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}

		if (!(applicationList.isEmpty())) {
			applicationList.forEach(application -> 
				applicationDtoList.add(mapper.map(application,ApplicationDto.class))
			);
		} else {
			throw new DataNotFoundException(ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		ApplicationResponseDto applicationResponseDto = new ApplicationResponseDto();
		applicationResponseDto.setApplicationtypes(applicationDtoList);
		return applicationResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.ApplicationService#
	 * getAllApplicationByLanguageCode(java.lang.String)
	 */
	@Override
	public ApplicationResponseDto getAllApplicationByLanguageCode(String languageCode) {
		List<ApplicationDto> applicationDtoList = new ArrayList<>();
		List<Application> applicationList;
		try {
			applicationList = applicationRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(languageCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		if (!(applicationList.isEmpty())) {
			applicationList.forEach(application -> 
				applicationDtoList.add(mapper.map(application,ApplicationDto.class))
			);
		} else {
			throw new DataNotFoundException(ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		ApplicationResponseDto applicationResponseDto = new ApplicationResponseDto();
		applicationResponseDto.setApplicationtypes(applicationDtoList);
		return applicationResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.ApplicationService#
	 * getApplicationByCodeAndLanguageCode(java.lang.String, java.lang.String)
	 */
	@Override
	public ApplicationResponseDto getApplicationByCodeAndLanguageCode(String code, String languageCode) {
		Application application;
		List<ApplicationDto> applicationDtoList = new ArrayList<>();
		try {
			application = applicationRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(code,
					languageCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		if (application != null) {
	     applicationDtoList.add(mapper.map(application,ApplicationDto.class));
		} else {
			throw new DataNotFoundException(ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		ApplicationResponseDto applicationResponseDto = new ApplicationResponseDto();
		applicationResponseDto.setApplicationtypes(applicationDtoList);
		return applicationResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.ApplicationService#createApplication(io.
	 * mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createApplication(RequestDto<ApplicationDto> applicationRequestDto) {
		Application entity = MetaDataUtils.setCreateMetaData(applicationRequestDto.getRequest(), Application.class);
		Application application;
		try {
			application = applicationRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		return mapper.map(application,CodeAndLanguageCodeID.class);
	}
}
