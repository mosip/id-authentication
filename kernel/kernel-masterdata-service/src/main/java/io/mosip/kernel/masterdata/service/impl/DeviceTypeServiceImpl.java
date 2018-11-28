package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DeviceTypeErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
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
	public CodeAndLanguageCodeId saveDeviceTypes(DeviceTypeRequestDto deviceType) {
		DeviceType renDeviceType = null;

		DeviceType entity = metaUtils.setCreateMetaData(deviceType.getRequest().getDeviceTypeDto(),
				DeviceType.class);

		try {
			renDeviceType = deviceTypeRepository.create(entity);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceTypeErrorCode.DEVICE_TYPE_INSERT_EXCEPTION.getErrorCode(),
					e.getErrorText());
		}
		
		CodeAndLanguageCodeId codeLangCodeId = new CodeAndLanguageCodeId();
		dataMapper.map(renDeviceType, codeLangCodeId, true, null, null, true);
		return codeLangCodeId;
	}

}
