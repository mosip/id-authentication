/**
 * 
 */
package io.mosip.registration.processor.stages.packet.validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.packet.dto.BiometricSequence;
import io.mosip.registration.processor.core.packet.dto.DemographicSequence;
import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author M1022006
 *
 */
@Service
public class PacketValidatorStage extends MosipVerticleManager {

	private static Logger log = LoggerFactory.getLogger(PacketValidatorStage.class);

	private FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter = new FilesystemCephAdapterImpl();

	public static final String PACKET_META_INFO = "PacketMetaInfo";

	private static final String USER = "MOSIP_SYSTEM";

	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	@Autowired
	RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass());
		this.consumeAndSend(mosipEventBus, MessageBusAddress.STRUCTURE_BUS_IN, MessageBusAddress.STRUCTURE_BUS_OUT);
	}

	public boolean filesValidation(String registrationId, PacketInfo packetInfo) {
		boolean filesValidated = false;

		HashSequence hashSequence = packetInfo.getHashSequence();
		filesValidated = validateHashSequence(registrationId, hashSequence);

		return filesValidated;

	}

	private boolean validateHashSequence(String registrationId, HashSequence hashSequence) {
		boolean isHashSequenceValidated = false;

		if (validateBiometricSequence(registrationId, hashSequence.getBiometricSequence())
				&& validateDemographicSequence(registrationId, hashSequence.getDemographicSequence())) {
			isHashSequenceValidated = true;
		}

		return isHashSequenceValidated;
	}

	private boolean validateDemographicSequence(String registrationId, DemographicSequence demographicSequence) {
		boolean isDemographicSequenceValidated = false;
		for (String applicantFile : demographicSequence.getApplicant()) {
			String fileName = "";
			if (PacketFiles.APPLICANTPHOTO.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.APPLICANT.name()
						+ File.separator + PacketFiles.APPLICANTPHOTO.name();
			} else if (PacketFiles.REGISTRATIONACKNOWLDEGEMENT.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.APPLICANT.name()
						+ File.separator + PacketFiles.REGISTRATIONACKNOWLDEGEMENT.name();
			} else if (PacketFiles.DEMOGRAPHICINFO.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.DEMOGRAPHICINFO.name();
			} else if (PacketFiles.PROOFOFADDRESS.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.APPLICANT.name()
						+ File.separator + PacketFiles.PROOFOFADDRESS.name();
			} else if (PacketFiles.APPLICANTEXCEPTIONPHOTO.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.APPLICANT.name()
						+ File.separator + PacketFiles.APPLICANTEXCEPTIONPHOTO.name();
			}
			isDemographicSequenceValidated = adapter.checkFileExistence(registrationId, fileName);

			if (!isDemographicSequenceValidated) {
				break;
			}
		}

		return isDemographicSequenceValidated;
	}

	private boolean validateBiometricSequence(String registrationId, BiometricSequence biometricSequence) {

		boolean isBiometricSequenceValidated = false;

		if (validateBiometricApplicant(registrationId, biometricSequence.getApplicant())
				&& validateBiometricIntroducer(registrationId, biometricSequence.getIntroducer())) {
			isBiometricSequenceValidated = true;
		}

		return isBiometricSequenceValidated;
	}

	private boolean validateBiometricIntroducer(String registrationId, List<String> introducer) {
		boolean isIntroducerValidated = false;

		for (String applicantFile : introducer) {
			String fileName = "";
			if (PacketFiles.LEFTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.INTRODUCER.name()
						+ File.separator + PacketFiles.LEFTEYE.name();
			} else if (PacketFiles.RIGHTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.INTRODUCER.name()
						+ File.separator + PacketFiles.RIGHTEYE.name();
			} else if (PacketFiles.LEFTTHUMB.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.INTRODUCER.name()
						+ File.separator + PacketFiles.LEFTTHUMB.name();
			} else if (PacketFiles.RIGHTTHUMB.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.INTRODUCER.name()
						+ File.separator + PacketFiles.RIGHTTHUMB.name();
			}
			isIntroducerValidated = adapter.checkFileExistence(registrationId, fileName);

			if (!isIntroducerValidated) {
				break;
			}
		}
		return isIntroducerValidated;
	}

	private boolean validateBiometricApplicant(String registrationId, List<String> applicant) {
		boolean isApplicantValidated = false;

		for (String applicantFile : applicant) {
			String fileName = "";
			if (PacketFiles.LEFTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.APPLICANT.name() + File.separator
						+ PacketFiles.LEFTEYE.name();
			} else if (PacketFiles.RIGHTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.APPLICANT.name() + File.separator
						+ PacketFiles.RIGHTEYE.name();
			} else if (PacketFiles.LEFTPALM.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.APPLICANT.name() + File.separator
						+ PacketFiles.LEFTPALM.name();
			} else if (PacketFiles.RIGHTPALM.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.APPLICANT.name() + File.separator
						+ PacketFiles.RIGHTPALM.name();
			} else if (PacketFiles.BOTHTHUMBS.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.APPLICANT.name() + File.separator
						+ PacketFiles.BOTHTHUMBS.name();
			}
			isApplicantValidated = adapter.checkFileExistence(registrationId, fileName);

			if (!isApplicantValidated) {
				break;
			}
		}
		return isApplicantValidated;
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		object.setAddress(MessageBusAddress.STRUCTURE_BUS_IN);
		String registrationId = object.getRid();

		InputStream packetMetaInfoStream = adapter.getFile(registrationId, PACKET_META_INFO);
		try {

			JsonUtil jsonUtil = new JsonUtil();
			PacketInfo packetInfo = (PacketInfo) jsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
					PacketInfo.class);

			RegistrationStatusDto registrationStatusDto = registrationStatusService
					.getRegistrationStatus(registrationId);
			boolean isFilesValidated = filesValidation(registrationId, packetInfo);
			boolean isCheckSumValidated = false;
			if (isFilesValidated) {

				// To do call checksum validation and assign to isCheckSumValidated
				isCheckSumValidated = true;
				if (!isCheckSumValidated) {
					registrationStatusDto.setStatusComment("Packet checkSum validation failure");
				}

			} else {
				registrationStatusDto.setStatusComment("Packet files validation failure");

			}
			if (isFilesValidated && isCheckSumValidated) {
				object.setIsValid(Boolean.TRUE);
				registrationStatusDto
						.setStatusCode(RegistrationStatusCode.PACKET_STRUCTURAL_VALIDATION_SUCCESSFULL.toString());

			} else {
				object.setIsValid(Boolean.FALSE);
				if (registrationStatusDto.getRetryCount() == null) {
					registrationStatusDto.setRetryCount(0);
				} else {
					registrationStatusDto.setRetryCount(registrationStatusDto.getRetryCount() + 1);
				}

				registrationStatusDto
						.setStatusCode(RegistrationStatusCode.PACKET_STRUCTURAL_VALIDATION_FAILED.toString());

			}
			registrationStatusDto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);

		} catch (IOException e) {
			log.error("Structural validation Failed", e);
			object.setInternalError(Boolean.TRUE);

		} catch (Exception ex) {
			log.error("Structural validation Failed", ex);
			object.setInternalError(Boolean.TRUE);
		}

		return object;
	}

}
