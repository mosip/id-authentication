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
	RegCenterMachineHistoryClientBuilder umcClient;

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
	private boolean validateRegistrationCenter(String registrationCenterId, String langCode, String effectiveDate,
			String latitude, String longitude) throws ApisResourceAccessException {
		boolean activeRegCenter = false;
		List<RegistrationCenterDto> dtos = umcClient
				.getRegistrationCentersHistory(registrationCenterId, langCode, effectiveDate).getRegistrationCenters();

		if (!dtos.isEmpty()) {

			for (RegistrationCenterDto dto : dtos) {

				if (dto.getLatitude() != null && dto.getLongitude() != null && dto.getLatitude().matches(latitude)
						&& dto.getLongitude().matches(longitude)) {

					activeRegCenter = dto.getIsActive();
					if (!activeRegCenter) {
						this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_NOT_ACTIVE);
					}
					break;
				} else {
					this.registrationStatusDto.setStatusComment(StatusMessage.GPS_DATA_NOT_PRESENT);
				}

			}
		} else {
			this.registrationStatusDto.setStatusComment(StatusMessage.CENTER_ID_NOT_FOUND);
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
	private boolean validateMachine(String machineId, String langCode, String effdatetimes)
			throws ApisResourceAccessException {

		boolean isActiveMachine = false;
		List<MachineHistoryDto> dtos = umcClient.getMachineHistoryIdLangEff(machineId, langCode, effdatetimes)
				.getMachineHistoryDetails();

		if (!dtos.isEmpty()) {
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

		} else {
			this.registrationStatusDto.setStatusComment(StatusMessage.MACHINE_ID_NOT_FOUND);
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
	private boolean validateUMCmapping(String effectiveTimestamp, String registrationCenterId, String machineId,
			String superviserId, String officerId) throws ApisResourceAccessException {
		boolean t = false;

		boolean supervisorActive = false;
		boolean officerActive = false;

		List<RegistrationCenterUserMachineMappingHistoryDto> supervisordtos = umcClient
				.getRegistrationCentersMachineUserMapping(effectiveTimestamp, registrationCenterId, machineId,
						superviserId)
				.getRegistrationCenters();
		List<RegistrationCenterUserMachineMappingHistoryDto> officerdtos = umcClient
				.getRegistrationCentersMachineUserMapping(effectiveTimestamp, registrationCenterId, machineId,
						officerId)
				.getRegistrationCenters();

		if (!supervisordtos.isEmpty() && supervisordtos.get(0).getIsActive() != null) {

			supervisorActive = supervisordtos.get(0).getIsActive();

		}
		if (!officerdtos.isEmpty() && officerdtos.get(0).getIsActive() !=null) {

			officerActive = officerdtos.get(0).getIsActive();

		}
		if (supervisordtos.isEmpty() && officerdtos.isEmpty()) {
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

		else if (validateRegistrationCenter(rcmDto.getRegcntrId(), primaryLanguagecode,
				rcmDto.getPacketCreationDate().toString(), rcmDto.getLatitude(), rcmDto.getLongitude())
				&& validateMachine(rcmDto.getMachineId(), primaryLanguagecode,
						rcmDto.getPacketCreationDate().toString())
				&& validateUMCmapping(rcmDto.getPacketCreationDate().toString(), rcmDto.getRegcntrId(),
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
