package io.mosip.registration.processor.packet.service.impl;

import java.io.File;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.registration.processor.packet.service.PacketCreationService;
import io.mosip.registration.processor.packet.service.PacketGeneratorService;
import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
import io.mosip.registration.processor.packet.service.dto.RegistrationMetaDataDTO;
import io.mosip.registration.processor.packet.service.dto.demographic.DemographicDTO;
import io.mosip.registration.processor.packet.service.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.processor.packet.service.dto.demographic.MoroccoIdentity;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.packet.service.external.StorageService;
import io.mosip.registration.processor.packet.upload.service.SyncUploadEncryptionService;

@Service
public class PacketGeneratorServiceImpl implements PacketGeneratorService {

	@Autowired
	private RidGenerator<String> ridGeneratorImpl;

	@Autowired
	private PacketCreationService packetCreationService;

	@Autowired
	private StorageService storageService;

	@Autowired
	SyncUploadEncryptionService syncUploadEncryptionService;

	@Override
	public String createPacket(String uin, String registrationType, String applicantType, String reason) {
		RegistrationDTO registrationDTO = createRegistrationDTOObject(uin, registrationType, applicantType);
		byte[] packetZipBytes = null;
		try {
			packetZipBytes = packetCreationService.create(registrationDTO);
			String filePath = storageService.storeToDisk(registrationDTO.getRegistrationId(), packetZipBytes, false);
			// encrypte the packet
			// sync the packet and upload and return the status
			File decryptedFile = new File(filePath);
			syncUploadEncryptionService.uploadUinPacket(decryptedFile, registrationDTO.getRegistrationId());
		} catch (RegBaseCheckedException e) {

			e.printStackTrace();
		}
		return null;
	}

	private RegistrationDTO createRegistrationDTOObject(String uin, String registrationType, String applicantType) {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setDemographicDTO(getDemographicDTO(uin));
		RegistrationMetaDataDTO registrationMetaDataDTO = getRegistrationMetaDataDTO(registrationType, applicantType,
				uin);
		String registrationId = ridGeneratorImpl.generateId(registrationMetaDataDTO.getCenterId(),
				registrationMetaDataDTO.getMachineId());
		registrationDTO.setRegistrationId(registrationId);
		registrationDTO.setRegistrationMetaDataDTO(registrationMetaDataDTO);
		return registrationDTO;

	}

	private DemographicDTO getDemographicDTO(String uin) {
		DemographicDTO demographicDTO = new DemographicDTO();
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		MoroccoIdentity identity = new MoroccoIdentity();
		identity.setIdSchemaVersion(1.0);
		identity.setUin(new BigInteger(uin));
		demographicInfoDTO.setIdentity(identity);
		demographicDTO.setDemographicInfoDTO(demographicInfoDTO);
		return demographicDTO;
	}

	private RegistrationMetaDataDTO getRegistrationMetaDataDTO(String registrationType, String applicantType,
			String uin) {
		RegistrationMetaDataDTO registrationMetaDataDTO = new RegistrationMetaDataDTO();

		registrationMetaDataDTO.setApplicationType(applicantType);
		registrationMetaDataDTO.setCenterId("10031");
		registrationMetaDataDTO.setMachineId("10011");
		registrationMetaDataDTO.setRegistrationCategory(registrationType);
		registrationMetaDataDTO.setUin(uin);
		return registrationMetaDataDTO;

	}
}
