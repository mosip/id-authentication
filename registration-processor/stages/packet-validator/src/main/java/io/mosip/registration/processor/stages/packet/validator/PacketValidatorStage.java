/**
 * 
 */
package io.mosip.registration.processor.stages.packet.validator;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.packet.dto.Demographic;
import io.mosip.registration.processor.core.packet.dto.MetaData;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.stages.exception.utils.ExceptionMessages;
import io.mosip.registration.processor.stages.utils.CheckSumValidation;
import io.mosip.registration.processor.stages.utils.FilesValidation;
import io.mosip.registration.processor.stages.utils.StatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author M1022006
 *
 */

@RefreshScope
@Service
public class PacketValidatorStage extends MosipVerticleManager {

	public static final String FILE_SEPARATOR = "\\";

	private static Logger log = LoggerFactory.getLogger(PacketValidatorStage.class);

	private FileSystemAdapter<InputStream, Boolean> adapter = new FilesystemCephAdapterImpl();

	private static final String USER = "MOSIP_SYSTEM";

	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	@Autowired
	RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

	@Autowired
	private PacketInfoManager<PacketInfo, Demographic, MetaData> packetInfoManager;

	@Value("${vertx.cluster.address}")
	private String clusterAddress;

	@Value("${vertx.localhost}")
	private String localhost;

	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.STRUCTURE_BUS_IN, MessageBusAddress.STRUCTURE_BUS_OUT);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		object.setMessageBusAddress(MessageBusAddress.STRUCTURE_BUS_IN);
		object.setIsValid(Boolean.FALSE);
		object.setInternalError(Boolean.FALSE);
		String registrationId = object.getRid();

		InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKETMETAINFO.name());
		try {

			PacketInfo packetInfo = (PacketInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
					PacketInfo.class);

			RegistrationStatusDto registrationStatusDto = registrationStatusService
					.getRegistrationStatus(registrationId);
			FilesValidation filesValidation = new FilesValidation(adapter);
			boolean isFilesValidated = filesValidation.filesValidation(registrationId, packetInfo);
			boolean isCheckSumValidated = false;
			if (isFilesValidated) {

				CheckSumValidation checkSumValidation = new CheckSumValidation(adapter);
				isCheckSumValidated = checkSumValidation.checksumvalidation(registrationId, packetInfo);
				if (!isCheckSumValidated) {
					registrationStatusDto.setStatusComment(StatusMessage.PACKET_CHECKSUM_VALIDATION);
				}

			} else {
				registrationStatusDto.setStatusComment(StatusMessage.PACKET_FILES_VALIDATION);

			}
			if (isFilesValidated && isCheckSumValidated) {
				object.setIsValid(Boolean.TRUE);
				registrationStatusDto.setStatusComment(StatusMessage.PACKET_STRUCTURAL_VALIDATION);
				registrationStatusDto
						.setStatusCode(RegistrationStatusCode.PACKET_STRUCTURAL_VALIDATION_SUCCESSFULL.toString());
				packetInfoManager.savePacketData(packetInfo);
				InputStream demographicInfoStream = adapter.getFile(registrationId,
						PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name());
				Demographic demographicData = (Demographic) JsonUtil.inputStreamtoJavaObject(demographicInfoStream,
						Demographic.class);
				packetInfoManager.saveDemographicData(demographicData, packetInfo.getMetaData());

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
			log.error(ExceptionMessages.STRUCTURAL_VALIDATION_FAILED.name(), e);
			object.setInternalError(Boolean.TRUE);

		} catch (Exception ex) {
			log.error(ExceptionMessages.STRUCTURAL_VALIDATION_FAILED.name(), ex);
			object.setInternalError(Boolean.TRUE);
		}

		return object;
	}

}
