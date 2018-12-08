package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Implementation class for {@link RegistrationCenterTypeService}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Service
public class RegistrationCenterTypeServiceImpl implements RegistrationCenterTypeService {
	/**
	 * Autowired reference for {@link MetaDataUtils}
	 */
	@Autowired
	private MetaDataUtils metaUtils;

	/**
	 * Autowired reference for {@link DataMapper}
	 */
	@Autowired
	DataMapper dataMapper;

	/**
	 * Autowired reference for {@link RegistrationCenterTypeRepository}.
	 */
	@Autowired
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterTypeService#
	 * addRegistrationCenterType(io.mosip.kernel.masterdata.dto.
	 * RegistrationCenterTypeRequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createRegistrationCenterType(
			RequestDto<RegistrationCenterTypeDto> registrationCenterTypeRequestDto) {
		RegistrationCenterType entity = metaUtils.setCreateMetaData(registrationCenterTypeRequestDto.getRequest(),
				RegistrationCenterType.class);
		RegistrationCenterType registrationCenterType;
		try {
			registrationCenterType = registrationCenterTypeRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		CodeAndLanguageCodeID codeAndLanguageCodeID = new CodeAndLanguageCodeID();
		dataMapper.map(registrationCenterType, codeAndLanguageCodeID, true, null, null, true);
		return codeAndLanguageCodeID;
	}
}
