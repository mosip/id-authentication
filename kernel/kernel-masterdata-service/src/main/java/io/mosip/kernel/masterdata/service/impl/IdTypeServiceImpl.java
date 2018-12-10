package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.dto.IdTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.IdTypeResponseDto;
import io.mosip.kernel.masterdata.entity.IdType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.IdTypeRepository;
import io.mosip.kernel.masterdata.service.IdTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Implementation class for {@link IdTypeService}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Service
public class IdTypeServiceImpl implements IdTypeService {

	/**
	 * Autowired reference for {@link IdTypeRepository}
	 */
	@Autowired
	private IdTypeRepository idRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.IdTypeService#getIdTypeByLanguageCode(java
	 * .lang.String)
	 */
	@Override
	public IdTypeResponseDto getIdTypesByLanguageCode(String languageCode) {
		IdTypeResponseDto idTypeResponseDto = new IdTypeResponseDto();
		List<IdTypeDto> idDtoList = null;
		List<IdType> idList = null;
		try {
			idList = idRepository.findByLangCode(languageCode);
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(dataAccessLayerException));
		}
		if (idList != null && !idList.isEmpty()) {
			idDtoList = MapperUtils.mapAll(idList, IdTypeDto.class);
		} else {
			throw new DataNotFoundException(ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		idTypeResponseDto.setIdtypes(idDtoList);
		return idTypeResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.IdTypeService#addIdType(io.mosip.kernel.
	 * masterdata.dto.IdTypeRequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createIdType(RequestDto<IdTypeDto> idTypeRequestDto) {
		IdType entity = MetaDataUtils.setCreateMetaData(idTypeRequestDto.getRequest(), IdType.class);
		IdType idType;
		try {
			idType = idRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		CodeAndLanguageCodeID codeAndLanguageCodeID = new CodeAndLanguageCodeID();
		MapperUtils.map(idType, codeAndLanguageCodeID);
		return codeAndLanguageCodeID;
	}
}
