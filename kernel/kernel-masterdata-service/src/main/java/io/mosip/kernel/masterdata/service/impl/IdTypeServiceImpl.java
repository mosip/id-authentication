package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.IdTypeErrorCode;
import io.mosip.kernel.masterdata.dto.IdTypeDto;
import io.mosip.kernel.masterdata.dto.IdTypeRequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.IdTypeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostResponseDto;
import io.mosip.kernel.masterdata.entity.IdType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.IdTypeRepository;
import io.mosip.kernel.masterdata.service.IdTypeService;
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

	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	private DataMapper dataMapper;

	@Autowired
	private MapperUtils objectMapperUtil;

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
			idList = idRepository.findByLangCodeAndIsDeletedFalse(languageCode);

		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new MasterDataServiceException(IdTypeErrorCode.ID_TYPE_FETCH_EXCEPTION.getErrorCode(),
					IdTypeErrorCode.ID_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (idList.isEmpty()) {
			throw new DataNotFoundException(IdTypeErrorCode.ID_TYPE_NOT_FOUND.getErrorCode(),
					IdTypeErrorCode.ID_TYPE_NOT_FOUND.getErrorMessage());
		}
		List<IdTypeDto> idDtoList = objectMapperUtil.mapAll(idList, IdTypeDto.class);
		IdTypeResponseDto idResponseDto = new IdTypeResponseDto();
		idResponseDto.setIdtypes(idDtoList);
		return idResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.IdTypeService#addIdType(io.mosip.kernel.
	 * masterdata.dto.IdTypeRequestDto)
	 */
	@Override
	public PostResponseDto addIdType(IdTypeRequestDto idTypeRequestDto) {
		List<IdType> entities = metaUtils.setCreateMetaData(idTypeRequestDto.getRequest().getIdtypes(), IdType.class);
		List<IdType> idTypes;
		try {
			idTypes = idRepository.saveAll(entities);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(IdTypeErrorCode.ID_TYPE_INSERT_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		List<CodeAndLanguageCodeID> codeLangCodeIds = new ArrayList<>();
		idTypes.forEach(idType -> {
			CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
			dataMapper.map(idType, codeLangCodeId, true, null, null, true);
			codeLangCodeIds.add(codeLangCodeId);
		});
		PostResponseDto postResponseDto = new PostResponseDto();
		postResponseDto.setResults(codeLangCodeIds);
		return postResponseDto;
	}
}
