/**
 * 
 */
package io.mosip.registration.processor.stages.packet.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
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
		this.consume(mosipEventBus, MessageBusAddress.STRUCTURE_BUS_IN);
	}

	public boolean filesValidation(String registrationId, PacketInfo packetInfo) {
		boolean filesValidated = false;

		HashSequence hashSequence = packetInfo.getHashSequence();
		/*
		 * if (validateApplicant(registrationId, hashSequence.getApplicant()) &&
		 * validateHOF(registrationId, hashSequence.getHof()) &&
		 * validateIntroducer(registrationId, hashSequence.getIntroducer()) &&
		 * validateApplicantPhoto(registrationId, packetInfo)) { filesValidated = true;
		 * }
		 */

		return filesValidated;

	}

	public boolean validateApplicantPhoto(String registrationId, PacketInfo packetInfo) {
		boolean isPhotovalidated = false;
		isPhotovalidated = adapter.checkFileExistence(registrationId, PacketFiles.DEMOGRAPHIC.name()
				+ PacketFiles.APPLICANT.name() + packetInfo.getPhotograph().getPhotographName());

		return isPhotovalidated;
	}

	public boolean validateApplicant(String registrationId, List<String> applicant) {
		boolean isApplicantValidated = false;
		/*for (String applicantFile : applicant) {
			String fileName = "";
			if (PacketFiles.LEFTEYE.name().equals(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + PacketFiles.APPLICANT.name() + PacketFiles.LEFTEYE.name();
			} else if (PacketFiles.LEFTTHUMB.name().equals(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + PacketFiles.APPLICANT.name() + PacketFiles.LEFTTHUMB.name();
			} else if (PacketFiles.RIGHTEYE.name().equals(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + PacketFiles.APPLICANT.name() + PacketFiles.RIGHTEYE.name();
			} else if (PacketFiles.RIGHTTHUMB.name().equals(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + PacketFiles.APPLICANT.name() + PacketFiles.RIGHTTHUMB.name();
			} else if (PacketFiles.DEMOGRAPHICINFO.name().equals(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + PacketFiles.DEMOGRAPHICINFO.name();
			}
			// To do for residence copy
			isApplicantValidated = adapter.checkFileExistence(registrationId, fileName);

		}*/
		return isApplicantValidated;
	}

	public boolean validateIntroducer(String registrationId, List<String> introducer) {
		boolean isIntroducerValidated = false;
		/*for (String introducerFile : introducer) {
			String fileName = "";
			if (PacketFiles.LEFTEYE.name().equals(introducerFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + PacketFiles.APPLICANT.name() + PacketFiles.LEFTEYE.name();
			} else if (PacketFiles.LEFTTHUMB.name().equals(introducerFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + PacketFiles.APPLICANT.name() + PacketFiles.LEFTTHUMB.name();
			} else if (PacketFiles.RIGHTEYE.name().equals(introducerFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + PacketFiles.APPLICANT.name() + PacketFiles.RIGHTEYE.name();
			} else if (PacketFiles.RIGHTTHUMB.name().equals(introducerFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + PacketFiles.APPLICANT.name() + PacketFiles.RIGHTTHUMB.name();
			}
			isIntroducerValidated = adapter.checkFileExistence(registrationId, fileName);

		}*/
		return isIntroducerValidated;
	}

	@Override
	public MessageDTO process(MessageDTO object) {

		String registrationId = object.getRid();

		InputStream packetMetaInfoStream = adapter.getFile(registrationId, PACKET_META_INFO);
		try {
			fileManager.put(PACKET_META_INFO, packetMetaInfoStream, DirectoryPathDto.TEMP);
			PacketInfo packetInfo = (PacketInfo) JsonUtils.jsonFileToJavaObject(PacketInfo.class,
					DirectoryPathDto.TEMP.toString());
			fileManager.cleanUpFile(DirectoryPathDto.TEMP, DirectoryPathDto.TEMP, registrationId);
			boolean isFilesValidated = filesValidation(registrationId, packetInfo);
			RegistrationStatusDto registrationStatusDto = registrationStatusService
					.getRegistrationStatus(registrationId);
			if (isFilesValidated) {
				registrationStatusDto.setStatusCode(RegistrationStatusCode.FILE_VALIDATION_SUCCESSFULL.toString());
				registrationStatusDto.setStatusComment("packet is in status File Validation Successful");

			} else {
				registrationStatusDto.setRetryCount(registrationStatusDto.getRetryCount() + 1);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.FILE_VALIDATION_FAILED.toString());
				registrationStatusDto.setStatusComment("packet is in status");

			}
			registrationStatusDto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			if (isFilesValidated) {

				boolean isCheckSumValidated = false;
				// To do checksum validation and update the status
				object.setIsValid(isCheckSumValidated);

			} else {
				object.setIsValid(isFilesValidated);
			}

		} catch (MosipJsonParseException | MosipJsonMappingException | MosipIOException | IOException e) {
			log.error("Structural validation Failed", e);
			object.setRetry(object.getRetry() + 1);
			// To do set values for DTO retry
		} catch (Exception ex) {
			log.error("Structural validation Failed", ex);
			object.setRetry(object.getRetry() + 1);
			// To do set values for DTO retry
		}

		return object;
	}

}
