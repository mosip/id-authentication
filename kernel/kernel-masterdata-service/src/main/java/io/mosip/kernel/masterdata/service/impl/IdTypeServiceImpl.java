package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.IdTypeErrorCode;
import io.mosip.kernel.masterdata.dto.IdTypeDto;
import io.mosip.kernel.masterdata.dto.IdTypeResponseDto;
import io.mosip.kernel.masterdata.entity.IdType;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.IdTypeRepository;
import io.mosip.kernel.masterdata.service.IdTypeService;

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
	 * Reference to {@link ModelMapper}
	 */
	@Autowired
	ModelMapper modelMapper;

	/**
	 * Reference to RegistrationCenterRepository.
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
	public IdTypeResponseDto getIdTypeByLanguageCode(String languageCode) {
		List<IdType> idList = null;
		try {
			idList = idRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse(languageCode);

		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new MasterDataServiceException(IdTypeErrorCode.ID_TYPE_FETCH_EXCEPTION.getErrorCode(),
					IdTypeErrorCode.ID_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (idList.isEmpty()) {
			throw new DataNotFoundException(IdTypeErrorCode.ID_TYPE_NOT_FOUND.getErrorCode(),
					IdTypeErrorCode.ID_TYPE_NOT_FOUND.getErrorMessage());
		}
		List<IdTypeDto> idDtoList = modelMapper.map(idList, new TypeToken<List<IdTypeDto>>() {
		}.getType());
		IdTypeResponseDto idResponseDto = new IdTypeResponseDto();
		idResponseDto.setIdtypes(idDtoList);
		return idResponseDto;
	}
}
