package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DocumentCategoryErrorCode;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeRequestDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterTypeService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

@Service
public class RegistrationCenterTypeServiceImpl implements RegistrationCenterTypeService {
	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	DataMapper dataMapper;

	/**
	 * Reference to {@link ModelMapper}
	 */
	@Autowired
	ModelMapper modelMapper;

	/**
	 * Reference to RegistrationCenterRepository.
	 */
	@Autowired
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;

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
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_INSERT_EXCEPTION.getErrorCode(), e.getMessage());
		}
		List<CodeAndLanguageCodeId> codeLangCodeIds = new ArrayList<>();
		regCenterTypes.forEach(regCenterType -> {
			CodeAndLanguageCodeId codeLangCodeId = new CodeAndLanguageCodeId();
			try {
				dataMapper.map(regCenterType, codeLangCodeId, true, null, null, true);
			} catch (DataMapperException e) {
				throw new MasterDataServiceException(
						DocumentCategoryErrorCode.DOCUMENT_CATEGORY_MAPPING_EXCEPTION.getErrorCode(), e.getMessage());
			}
			codeLangCodeIds.add(codeLangCodeId);
		});
		PostResponseDto postResponseDto = new PostResponseDto();
		postResponseDto.setSuccessfully_created(codeLangCodeIds);
		return postResponseDto;
	}

}
