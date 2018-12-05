package io.mosip.registration.processor.stages.osivalidator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.regcentermachine.builder.RegCenterMachineHistoryClientBuilder;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.MachineHistoryDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterUserMachineMappingHistoryDto;
import io.mosip.registration.processor.stages.osivalidator.utils.StatusMessage;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

@Service
public class UMCValidator {
	@Autowired
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	RegCenterMachineHistoryClientBuilder umcClient;

	/** The registration status dto. */
	private InternalRegistrationStatusDto registrationStatusDto;

	@Value("${primary.language}")
	private String primaryLanguagecode;

	private boolean validateRegistrationCenter(String registrationCenterId, String langCode, String effectiveDate,
			String latitude, String longitude)throws ApisResourceAccessException {
		boolean activeRegCenter=false;
		List<RegistrationCenterDto> dtos= umcClient.getRegistrationCentersHistory(registrationCenterId, langCode, effectiveDate)
				.getRegistrationCenters();
		
		if( latitude==null || longitude==null) {
			this.registrationStatusDto.setStatusComment(StatusMessage.GPS_DATA_NOT_PRESENT);
		}
		else if(latitude.trim().isEmpty() || longitude.trim().isEmpty()) {
			this.registrationStatusDto.setStatusComment(StatusMessage.GPS_DATA_NOT_PRESENT);
		}
		else {
		if(!dtos.isEmpty()) {
			
				for(RegistrationCenterDto dto: dtos) {
					if(registrationCenterId==dto.getId()) {
						if(dto.getLatitude()==null || dto.getLongitude()==null) {
							this.registrationStatusDto.setStatusComment(StatusMessage.GPS_DATA_NOT_PRESENT);
						}
						else if(dto.getLatitude().trim().isEmpty() || dto.getLongitude().trim().isEmpty()) {
							this.registrationStatusDto.setStatusComment(StatusMessage.GPS_DATA_NOT_PRESENT);
						}else {
							if(dto.getLatitude().matches(latitude) && dto.getLongitude().matches(longitude)) {
				
								activeRegCenter=dto.getIsActive();
								if(!activeRegCenter)this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_NOT_ACTIVE);
								break;
							}
							else {
								this.registrationStatusDto.setStatusComment(StatusMessage.GPS_DATA_NOT_PRESENT);
							}
						}
					}
					else {
						this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_ID_NOT_FOUND);
					}
				}
			}
		else {
			this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_ID_NOT_FOUND);
		}
		}
		
		return activeRegCenter;

	}

	private boolean validateMachine(String machineId, String langCode, String effdatetimes)
			throws ApisResourceAccessException {
		
		boolean isActiveMachine=false;
		List<MachineHistoryDto> dtos=umcClient.getMachineHistoryIdLangEff(machineId, langCode, effdatetimes).getMachineHistoryDetails();
		
		if(!dtos.isEmpty()) {
			for(MachineHistoryDto dto:dtos) {
				if(dto.getId()==machineId) {
					isActiveMachine=dto.getIsActive();
					if(!isActiveMachine)this.registrationStatusDto.setStatusComment(StatusMessage.MACHINE_NOT_ACTIVE);
					break;
				}
				else {
					this.registrationStatusDto.setStatusComment(StatusMessage.MACHINE_ID_NOT_FOUND);
				}
			}
			
		}else {
			this.registrationStatusDto.setStatusComment(StatusMessage.MACHINE_ID_NOT_FOUND);
		}
		
		return isActiveMachine;

	}

	private boolean validateUMCmapping(String effectiveTimestamp, String registrationCenterId, String machineId,
			String superviserId, String officerId) throws ApisResourceAccessException {
		boolean t = false;
		boolean supervisorActive=false;
		boolean officerActive =false;
		
		List<RegistrationCenterUserMachineMappingHistoryDto> supervisordtos=umcClient.getRegistrationCentersMachineUserMapping(effectiveTimestamp,
				registrationCenterId, machineId, superviserId).getRegistrationCenters();
		List<RegistrationCenterUserMachineMappingHistoryDto> officerdtos=umcClient.getRegistrationCentersMachineUserMapping(effectiveTimestamp,
				registrationCenterId, machineId, officerId).getRegistrationCenters();
		
		if(!supervisordtos.isEmpty()) {
			for(RegistrationCenterUserMachineMappingHistoryDto dto:supervisordtos) {
				if(dto.getCntrId()==registrationCenterId && dto.getMachineId()==machineId && dto.getUsrId()==superviserId) {
					supervisorActive = supervisordtos.get(0).getIsActive();
					break;
				}
				else {
					this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_MACHINE_USER_MAPPING_NOT_FOUND);
				}
		} 
		}
		if(!officerdtos.isEmpty()) {
			for(RegistrationCenterUserMachineMappingHistoryDto dto: officerdtos) {
				if(dto.getCntrId()==registrationCenterId && dto.getMachineId()==machineId && dto.getUsrId()==officerId) {
					officerActive = officerdtos.get(0).getIsActive();
					break;
				}
				else {
					this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_MACHINE_USER_MAPPING_NOT_FOUND);
				}
		}
		}
		if(supervisordtos.isEmpty() && officerdtos.isEmpty()) {
			this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_MACHINE_USER_MAPPING_NOT_FOUND);
		}
		
		
		if (supervisorActive || officerActive) {
			t = true;
			this.registrationStatusDto.setStatusComment(null);
		}
		return t;
	}

	public boolean isValidUMC(String registrationId) throws ApisResourceAccessException {
		RegistrationCenterMachineDto rcmDto = packetInfoManager.getRegistrationCenterMachine(registrationId);

		RegOsiDto regOsi = packetInfoManager.getOsi(registrationId);
		boolean umc = false;
		if (validateRegistrationCenter(rcmDto.getRegcntrId(), primaryLanguagecode,
				rcmDto.getPacketCreationDate().toString(),rcmDto.getLatitude(),rcmDto.getLongitude())
				&& validateMachine(rcmDto.getMachineId(), primaryLanguagecode,
						rcmDto.getPacketCreationDate().toString())
				&& validateUMCmapping(rcmDto.getPacketCreationDate().toString(), rcmDto.getRegcntrId(),
						rcmDto.getMachineId(), regOsi.getSupervisorId(), regOsi.getOfficerId())) {
			umc = true;
		}
		return umc;
	}

	public InternalRegistrationStatusDto getRegistrationStatusDto() {
		return this.registrationStatusDto;
	}

	public void setRegistrationStatusDto(InternalRegistrationStatusDto registrationStatusDto) {
		this.registrationStatusDto = registrationStatusDto;
	}
	
}
