package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineService;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Service
public class RegistrationCenterMachineServiceImpl implements RegistrationCenterMachineService {

	@Autowired
	private RegistrationCenterMachineRepository registrationCenterMachineRepository;

	@Autowired
	private MetaDataUtils metadataUtils;

	@Autowired
	private MapperUtils mapperUtils;

	@Override
	public ResponseRrgistrationCenterMachineDto mapRegistrationCenterAndMachine(
			RequestDto<RegistrationCenterMachineDto> requestDto) {
		ResponseRrgistrationCenterMachineDto responseRrgistrationCenterMachineDto = null;

		try {
			RegistrationCenterMachine registrationCenterMachine = metadataUtils
					.setCreateMetaData(requestDto.getRequest(), RegistrationCenterMachine.class);

			RegistrationCenterMachine savedRegistrationCenterMachine = registrationCenterMachineRepository
					.create(registrationCenterMachine);

			responseRrgistrationCenterMachineDto = mapperUtils.map(savedRegistrationCenterMachine,
					ResponseRrgistrationCenterMachineDto.class);
		} catch (Exception e) {
			new MasterDataServiceException("", "", e);
		}

		return responseRrgistrationCenterMachineDto;
	}

}
