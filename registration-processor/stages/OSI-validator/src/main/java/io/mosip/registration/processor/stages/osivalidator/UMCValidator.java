package io.mosip.registration.processor.stages.osivalidator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.MachineHistoryDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.MachineHistoryResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterUserMachineMappingHistoryDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.registration.processor.stages.osivalidator.utils.StatusMessage;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

/**
 * The Class UMCValidator.
 * 
 * @author Jyothi
 */
@Service
public class UMCValidator {

	/** The packet info manager. */
	@Autowired
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The umc client. */
	
	@Autowired
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	/** The registration status dto. */
	private InternalRegistrationStatusDto registrationStatusDto;

	/** The primary languagecode. */
	@Value("${primary.language}")
	private String primaryLanguagecode;

	/**
	 * Validate registration center.
	 *
	 * @param registrationCenterId
	 *            the registration center id
	 * @param langCode
	 *            the lang code
	 * @param effectiveDate
	 *            the effective date
	 * @param latitude
	 *            the latitude
	 * @param longitude
	 *            the longitude
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isValidRegistrationCenter(String registrationCenterId, String langCode, String effectiveDate,
			String latitude, String longitude) throws ApisResourceAccessException {
		boolean activeRegCenter = false;
		List<String> pathsegments=new ArrayList<>();
		pathsegments.add(registrationCenterId);
		pathsegments.add(langCode);
		pathsegments.add(effectiveDate);
		
		RegistrationCenterResponseDto rcpdto = (RegistrationCenterResponseDto) registrationProcessorRestService.
				getApi(ApiName.CENTERHISTORY,pathsegments,"","",RegistrationCenterResponseDto.class);

		List<RegistrationCenterDto> dtos=new ArrayList<>();
		if(rcpdto !=null)dtos=rcpdto.getRegistrationCenters();
		
		if (dtos ==null  ||dtos.isEmpty()) {
			this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_ID_NOT_FOUND);
		}
		else {
			for (RegistrationCenterDto dto : dtos) {

				if (dto.getLatitude() != null && dto.getLongitude() != null && dto.getLatitude().matches(latitude)
						&& dto.getLongitude().matches(longitude) ) {

					activeRegCenter = dto.getIsActive();
					if (!activeRegCenter) {
						this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_NOT_ACTIVE);
					}
					break;
				} else {
					this.registrationStatusDto.setStatusComment(StatusMessage.GPS_DATA_NOT_PRESENT);
				}

			}
		} 

		return activeRegCenter;

	}

	/**
	 * Validate machine.
	 *
	 * @param machineId
	 *            the machine id
	 * @param langCode
	 *            the lang code
	 * @param effdatetimes
	 *            the effdatetimes
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isValidMachine(String machineId, String langCode, String effdatetimes)
			throws ApisResourceAccessException {

		boolean isActiveMachine = false;
		
		List<String> pathsegments=new ArrayList<>();
		pathsegments.add(machineId);
		pathsegments.add(langCode);
		pathsegments.add(effdatetimes);
		MachineHistoryResponseDto mhrdto = (MachineHistoryResponseDto) registrationProcessorRestService.getApi(ApiName.MACHINEHISTORY,
				pathsegments,"","",MachineHistoryResponseDto.class);

		List<MachineHistoryDto> dtos=new ArrayList<>();
		if(mhrdto !=null)dtos=mhrdto.getMachineHistoryDetails();
		if (dtos ==null || dtos.isEmpty()  ) {
			this.registrationStatusDto.setStatusComment(StatusMessage.MACHINE_ID_NOT_FOUND);
		}else {
			for (MachineHistoryDto dto : dtos) {
				if (dto.getId() != null &&dto.getId().matches(machineId)  ) {
					isActiveMachine = dto.getIsActive();
					if (!isActiveMachine) {
						this.registrationStatusDto.setStatusComment(StatusMessage.MACHINE_NOT_ACTIVE);
					}
					break;
				} else {
					this.registrationStatusDto.setStatusComment(StatusMessage.MACHINE_ID_NOT_FOUND);
				}
			}

		} 

		return isActiveMachine;

	}

	/**
	 * Validate UM cmapping.
	 *
	 * @param effectiveTimestamp
	 *            the effective timestamp
	 * @param registrationCenterId
	 *            the registration center id
	 * @param machineId
	 *            the machine id
	 * @param superviserId
	 *            the superviser id
	 * @param officerId
	 *            the officer id
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isValidUMCmapping(String effectiveTimestamp, String registrationCenterId, String machineId,
			String superviserId, String officerId) throws ApisResourceAccessException {
		boolean t = false;

		boolean supervisorActive = false;
		boolean officerActive = false;
		List<String> pathsegments=new ArrayList<>();
		pathsegments.add(effectiveTimestamp);
		pathsegments.add(registrationCenterId);
		pathsegments.add(machineId);
		pathsegments.add(superviserId);
		
		RegistrationCenterUserMachineMappingHistoryResponseDto supervisordto = (RegistrationCenterUserMachineMappingHistoryResponseDto) registrationProcessorRestService.
				getApi(ApiName.CENTERUSERMACHINEHISTORY,pathsegments,"","",RegistrationCenterUserMachineMappingHistoryResponseDto.class);
		List<String> officerpathsegments=new ArrayList<>();
		officerpathsegments.add(effectiveTimestamp);
		officerpathsegments.add(registrationCenterId);
		officerpathsegments.add(machineId);
		officerpathsegments.add(officerId);
		RegistrationCenterUserMachineMappingHistoryResponseDto officerdto = (RegistrationCenterUserMachineMappingHistoryResponseDto) registrationProcessorRestService.
				getApi(ApiName.CENTERUSERMACHINEHISTORY,officerpathsegments,"","",RegistrationCenterUserMachineMappingHistoryResponseDto.class);

		List<RegistrationCenterUserMachineMappingHistoryDto> supervisordtos= new ArrayList<>();
		if(supervisordto !=null)supervisordtos=supervisordto.getRegistrationCenters();
		List<RegistrationCenterUserMachineMappingHistoryDto> officerdtos= new ArrayList<>();
		if(officerdto !=null)officerdtos=officerdto.getRegistrationCenters();
		
		if (supervisordtos != null  && !supervisordtos.isEmpty()  ) {

			supervisorActive = supervisordtos.get(0).getIsActive();
			
		}
		if(officerdtos !=null && !officerdtos.isEmpty()) {
			officerActive = officerdtos.get(0).getIsActive();
		}
		if((supervisordtos == null || supervisordtos.isEmpty()  ) && (officerdtos ==null ||officerdtos.isEmpty())) {
			this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_MACHINE_USER_MAPPING_NOT_FOUND);
		}

		if (supervisorActive || officerActive) {
			t = true;
			
		}
		return t;
	}

	/**
	 * Checks if is valid UMC.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return true, if is valid UMC
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	public boolean isValidUMC(String registrationId) throws ApisResourceAccessException {
		RegistrationCenterMachineDto rcmDto = packetInfoManager.getRegistrationCenterMachine(registrationId);

		RegOsiDto regOsi = packetInfoManager.getOsi(registrationId);
		boolean umc = false;
		if (rcmDto.getLatitude() == null || rcmDto.getLongitude() == null || rcmDto.getLatitude().trim().isEmpty()
				|| rcmDto.getLongitude().trim().isEmpty()) {
			this.registrationStatusDto.setStatusComment(StatusMessage.GPS_DATA_NOT_PRESENT);
		}

		else if (isValidRegistrationCenter(rcmDto.getRegcntrId(), primaryLanguagecode,
				rcmDto.getPacketCreationDate().toString(), rcmDto.getLatitude(), rcmDto.getLongitude())
				&& isValidMachine(rcmDto.getMachineId(), primaryLanguagecode,
						rcmDto.getPacketCreationDate().toString())
				&& isValidUMCmapping(rcmDto.getPacketCreationDate().toString(), rcmDto.getRegcntrId(),
						rcmDto.getMachineId(), regOsi.getSupervisorId(), regOsi.getOfficerId())) {
			umc = true;
		}
		return umc;
	}

	/**
	 * Gets the registration status dto.
	 *
	 * @return the registration status dto
	 */
	public InternalRegistrationStatusDto getRegistrationStatusDto() {
		return this.registrationStatusDto;
	}

	/**
	 * Sets the registration status dto.
	 *
	 * @param registrationStatusDto
	 *            the new registration status dto
	 */
	public void setRegistrationStatusDto(InternalRegistrationStatusDto registrationStatusDto) {
		this.registrationStatusDto = registrationStatusDto;
	}

}
