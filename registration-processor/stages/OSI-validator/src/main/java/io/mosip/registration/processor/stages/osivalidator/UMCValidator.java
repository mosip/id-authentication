package io.mosip.registration.processor.stages.osivalidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.regcentermachine.builder.RegCenterMachineHistoryClientBuilder;

@Service
public class UMCValidator {
	@Autowired
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	RegCenterMachineHistoryClientBuilder umcClient;

	@Value("${primary.language}")
	private String primaryLanguagecode;

	private boolean validateRegistrationCenter(String registrationCenterId, String langCode, String effectiveDate) {
		return umcClient.getRegistrationCentersHistory(registrationCenterId, langCode, effectiveDate)
				.getRegistrationCenters().get(0).getIsActive();
	}

	private boolean validateMachine(String machineId, String langCode, String effdatetimes) {
		return umcClient.getMachineHistoryIdLangEff(machineId, langCode, effdatetimes).getMachineHistoryDetails().get(0)
				.getIsActive();
	}

	private boolean validateUMCmapping(String effectiveTimestamp, String registrationCenterId, String machineId,
			String superviserId, String officerId) {
		boolean t = false;
		boolean supervisorActive = umcClient.getRegistrationCentersMachineUserMapping(effectiveTimestamp,
				registrationCenterId, machineId, superviserId).getRegistrationCenters().get(0).getIsActive();
		boolean officerActive = umcClient.getRegistrationCentersMachineUserMapping(effectiveTimestamp,
				registrationCenterId, machineId, officerId).getRegistrationCenters().get(0).getIsActive();
		if (supervisorActive || officerActive) {
			t = true;
		}
		return t;
	}

	public boolean isValidUMC(String registrationId) {
		RegistrationCenterMachineDto rcmDto = packetInfoManager.getRegistrationCenterMachine(registrationId);
		RegOsiDto regOsi = packetInfoManager.getOsi(registrationId);
		boolean umc = false;
		if (validateRegistrationCenter(rcmDto.getRegcntrId(), primaryLanguagecode,
				rcmDto.getPacketCreationDate().toString())
				&& validateMachine(rcmDto.getMachineId(), primaryLanguagecode,
						rcmDto.getPacketCreationDate().toString())
				&& validateUMCmapping(rcmDto.getPacketCreationDate().toString(), rcmDto.getRegcntrId(),
						rcmDto.getMachineId(), regOsi.getSupervisorId(), regOsi.getOfficerId())) {
			umc = true;
		}
		return umc;
	}
}
