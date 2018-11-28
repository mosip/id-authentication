package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DeviceTypeErrorCode;
import io.mosip.kernel.masterdata.dto.MachineTypeRequestDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.entity.MachineType;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.MachineTypeRepository;
import io.mosip.kernel.masterdata.service.MachineTypeService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

@Service
public class MachineTypeServiceImpl implements MachineTypeService {
	
	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	MachineTypeRepository machineTypeRepository;

	@Autowired
	private DataMapper dataMapper;

	@Override
	public CodeAndLanguageCodeId saveMachineType(MachineTypeRequestDto machineType) {
		MachineType renMachineType= null;

		MachineType entity = metaUtils.setCreateMetaData(machineType.getRequest().getMachineTypeDto(),
				MachineType.class);

		try {
			renMachineType = machineTypeRepository.create(entity);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceTypeErrorCode.DEVICE_TYPE_INSERT_EXCEPTION.getErrorCode(),
					e.getErrorText());
		}
		
		CodeAndLanguageCodeId codeLangCodeId = new CodeAndLanguageCodeId();
		dataMapper.map(renMachineType, codeLangCodeId, true, null, null, true);
		return codeLangCodeId;
	}

	
}
