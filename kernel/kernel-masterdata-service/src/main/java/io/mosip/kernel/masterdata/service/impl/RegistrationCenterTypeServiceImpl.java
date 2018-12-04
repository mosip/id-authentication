package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.RegistrationCenterTypeErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeRequestDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterTypeService;
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
	 * Autowired reference for {@link ModelMapper}
	 */
	

	/**
	 * Autowired reference for RegistrationCenterRepository.
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
	public PostResponseDto addRegistrationCenterType(
			RegistrationCenterTypeRequestDto registrationCenterTypeRequestDto) {
		List<RegistrationCenterType> entities = metaUtils.setCreateMetaData(
				registrationCenterTypeRequestDto.getRequest().getRegcentertypes(), RegistrationCenterType.class);
		List<RegistrationCenterType> regCenterTypes;
		try {
			regCenterTypes = registrationCenterTypeRepository.saveAll(entities);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_INSERT_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		List<CodeAndLanguageCodeID> codeLangCodeIds = new ArrayList<>();
		regCenterTypes.forEach(regCenterType -> {
			CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
			try {
				dataMapper.map(regCenterType, codeLangCodeId, true, null, null, true);
			} catch (DataMapperException e) {
				throw new MasterDataServiceException(
						RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_MAPPING_EXCEPTION.getErrorCode(),
						e.getMessage());
			}
			codeLangCodeIds.add(codeLangCodeId);
		});
		PostResponseDto postResponseDto = new PostResponseDto();
		postResponseDto.setResults(codeLangCodeIds);
		return postResponseDto;
	}
}
