package io.mosip.registration.processor.rest.client.regcentermachine.builder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.MachineHistoryResponseDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterResponseDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterUserMachineMappingHistoryResponseDto;


@Component
public class RegCenterMachineHistoryClientBuilder {
	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(RegCenterMachineHistoryClientBuilder.class);

	/** The registration processor rest service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;
	
	public MachineHistoryResponseDto getMachineHistoryIdLangEff(String machineId, String langCode,String effdatetimes) {
		MachineHistoryResponseDto dto=null;
		List<String> pathsegments=new ArrayList<>();
		pathsegments.add(machineId);
		pathsegments.add(langCode);
		pathsegments.add(effdatetimes);
		try {
			dto= (MachineHistoryResponseDto) registrationProcessorRestService.getApi(ApiName.MACHINEHISTORY,
					pathsegments,"","",MachineHistoryResponseDto.class);
		} catch (ApisResourceAccessException e) {
			logger.error(e.getMessage());
		}
		return dto;
	}
	
	public RegistrationCenterUserMachineMappingHistoryResponseDto getRegistrationCentersMachineUserMapping(
			 String effectiveTimestamp,String registrationCenterId,String machineId,String userId) {
		RegistrationCenterUserMachineMappingHistoryResponseDto dto=null;
		List<String> pathsegments=new ArrayList<>();
		pathsegments.add(effectiveTimestamp);
		pathsegments.add(registrationCenterId);
		pathsegments.add(machineId);
		pathsegments.add(userId);
		try {
			dto= (RegistrationCenterUserMachineMappingHistoryResponseDto) registrationProcessorRestService.
					getApi(ApiName.CENTERUSERMACHINEHISTORY,pathsegments,"","",RegistrationCenterUserMachineMappingHistoryResponseDto.class);
		} catch (ApisResourceAccessException e) {
			logger.error(e.getMessage());
		}
		return dto;
	}
	
	public RegistrationCenterResponseDto getRegistrationCentersHistory(String registrationCenterId,String langCode,
			String effectiveDate) {
		RegistrationCenterResponseDto dto=null;
		List<String> pathsegments=new ArrayList<>();
		pathsegments.add(registrationCenterId);
		pathsegments.add(langCode);
		pathsegments.add(effectiveDate);
		try {
			dto= (RegistrationCenterResponseDto) registrationProcessorRestService.
					getApi(ApiName.CENTERHISTORY,pathsegments,"","",RegistrationCenterResponseDto.class);
		} catch (ApisResourceAccessException e) {
			logger.error(e.getMessage());
		}
		return dto;
	}
}
