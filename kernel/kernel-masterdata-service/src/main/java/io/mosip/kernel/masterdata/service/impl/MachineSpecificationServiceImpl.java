package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.MachineSpecificationErrorCode;
import io.mosip.kernel.masterdata.dto.MachineSpecificationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.masterdata.service.MachineSpecificationService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

@Service
public class MachineSpecificationServiceImpl implements MachineSpecificationService  {
	
	@Autowired
	private MetaDataUtils metaUtils;
	
	@Autowired
	MachineSpecificationRepository machineSpecificationRepository;
	
	@Autowired
	MapperUtils objectMapperUtil;

	@Override
	public IdResponseDto createMachineSpecification(
			RequestDto<MachineSpecificationDto> machineSpecification) {
		
		MachineSpecification renMachineSpecification = new MachineSpecification();

		MachineSpecification entity = metaUtils
				.setCreateMetaData(machineSpecification.getRequest(), MachineSpecification.class);
		try {
			 renMachineSpecification = machineSpecificationRepository.create(entity);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_INSERT_EXCEPTION.getErrorCode(),
					e.getErrorText()+ "  " + ExceptionUtils.parseException(e));
		}
		IdResponseDto idResponseDto = new IdResponseDto();
		objectMapperUtil.mapNew(renMachineSpecification, idResponseDto);
			
		return idResponseDto;	
	
	}

}
