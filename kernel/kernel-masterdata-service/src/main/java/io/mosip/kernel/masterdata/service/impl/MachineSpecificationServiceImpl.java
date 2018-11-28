package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DeviceSpecificationErrorCode;
import io.mosip.kernel.masterdata.dto.MachineSpecificationRequestDto;
import io.mosip.kernel.masterdata.dto.MachineTypeCodeAndLanguageCodeAndId;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.masterdata.service.MachineSpecificationService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

@Service
public class MachineSpecificationServiceImpl implements MachineSpecificationService  {
	
	@Autowired
	private MetaDataUtils metaUtils;
	
	@Autowired
	MachineSpecificationRepository machineSpecificationRepository;
	
	@Autowired
	private DataMapper dataMapper;

	@Override
	public MachineTypeCodeAndLanguageCodeAndId saveMachineSpecification(
			MachineSpecificationRequestDto machineSpecification) {
		
		MachineSpecification renMachineSpecification = new MachineSpecification();

		MachineSpecification entity = metaUtils
				.setCreateMetaData(machineSpecification.getRequest().getMachineSpecificationDto(), MachineSpecification.class);
		try {
			 renMachineSpecification = machineSpecificationRepository.create(entity);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_INSERT_EXCEPTION.getErrorCode(),
					e.getErrorText(), e);
		}
		MachineTypeCodeAndLanguageCodeAndId machineTypeCodeAndLanguageCodeAndId = new MachineTypeCodeAndLanguageCodeAndId();
				dataMapper.map(renMachineSpecification, machineTypeCodeAndLanguageCodeAndId, true, null, null, true);
			
		return machineTypeCodeAndLanguageCodeAndId;	
	
	}

}
