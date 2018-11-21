package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DeviceTypeErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;
import io.mosip.kernel.masterdata.service.DeviceTypeService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Service class have methods to save a DeviceType Details to the Database table
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Service
public class DeviceTypeServiceImpl implements DeviceTypeService {

	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	DeviceTypeRepository deviceTypeRepository;
	
	@Autowired
	private DataMapper dataMapper;

	@Override
	public PostResponseDto addDeviceTypes(DeviceTypeRequestDto deviceTypes) {
		PostResponseDto postResponseDto = new PostResponseDto();
		List<DeviceType> renDeviceTypeList = null;
		
		List<DeviceType> entities = metaUtils.setCreateMetaData(deviceTypes.getRequest().getDeviceTypeDtos(),
				DeviceType.class);

		try {
			renDeviceTypeList = deviceTypeRepository.saveAll(entities);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(DeviceTypeErrorCode.DEVICE_TYPE_INSERT_EXCEPTION.getErrorCode(),
					DeviceTypeErrorCode.DEVICE_TYPE_INSERT_EXCEPTION.getErrorMessage());
		}
		List<CodeAndLanguageCodeId> codeLangCodeIds = new ArrayList<>();
		renDeviceTypeList.forEach(renDeviceType -> {
			CodeAndLanguageCodeId codeLangCodeId = new CodeAndLanguageCodeId();
			try {
				dataMapper.map(renDeviceType, codeLangCodeId, true, null, null, true);
			} catch (DataMapperException e) {
				throw new MasterDataServiceException(
						DeviceTypeErrorCode.DEVICE_TYPE_MAPPING_EXCEPTION.getErrorCode(), e.getMessage());
			}
			codeLangCodeIds.add(codeLangCodeId);
		});
		postResponseDto.setSuccessfully_created(codeLangCodeIds);
		return postResponseDto;
	}

}
