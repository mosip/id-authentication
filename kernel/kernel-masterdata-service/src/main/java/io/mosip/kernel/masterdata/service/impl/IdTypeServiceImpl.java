package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.IdTypeErrorCode;
import io.mosip.kernel.masterdata.dto.IdTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.IdTypeResponseDto;
import io.mosip.kernel.masterdata.entity.IdType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.IdTypeRepository;
import io.mosip.kernel.masterdata.service.IdTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
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

	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	private DataMapper dataMapper;

	/**
	 * Reference to {@link ModelMapper}
	 */

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
			idList = idRepository.findByLangCode(languageCode);

		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new MasterDataServiceException(IdTypeErrorCode.ID_TYPE_FETCH_EXCEPTION.getErrorCode(),
					IdTypeErrorCode.ID_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}

		IdTypeResponseDto idTypeResponseDto = new IdTypeResponseDto();
		dataMapper.map(idList, idTypeResponseDto, true, null, null, true);
		idTypeResponseDto.setIdtypes(idList);
		return idTypeResponseDto;

		/*
		 * if (idList != null && !idList.isEmpty()) { idFetchList =
		 * objectMapperUtil.mapAll(idList, IdTypeDto.class); } else { throw new
		 * DataNotFoundException(IdTypeErrorCode.ID_TYPE_NOT_FOUND.getErrorCode(),
		 * IdTypeErrorCode.ID_TYPE_NOT_FOUND.getErrorMessage()); } return idFetchList;
		 */}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.IdTypeService#addIdType(io.mosip.kernel.
	 * masterdata.dto.IdTypeRequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createIdType(RequestDto<IdTypeDto> idTypeRequestDto) {
		IdType entity = metaUtils.setCreateMetaData(idTypeRequestDto.getRequest(), IdType.class);
		IdType idType;
		try {
			idType = idRepository.create(entity);
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new MasterDataServiceException(IdTypeErrorCode.ID_TYPE_INSERT_EXCEPTION.getErrorCode(),
					IdTypeErrorCode.ID_TYPE_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(dataAccessLayerException));
		}
		CodeAndLanguageCodeID codeAndLanguageCodeID = new CodeAndLanguageCodeID();
		dataMapper.map(idType, codeAndLanguageCodeID, true, null, null, true);
		return codeAndLanguageCodeID;
	}
}
