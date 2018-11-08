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
import io.mosip.registration.processor.core.builder.CoreAuditRequestBuilder;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.packet.dto.DemographicInfo;
import io.mosip.registration.processor.core.packet.dto.MetaData;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.stages.exception.utils.ExceptionMessages;
import io.mosip.registration.processor.stages.utils.CheckSumValidation;
import io.mosip.registration.processor.stages.utils.FilesValidation;
import io.mosip.registration.processor.stages.utils.StatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * The Class PacketValidatorStage.
 *
 * @author M1022006
 */

@RefreshScope
@Service
public class PacketValidatorStage extends MosipVerticleManager {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(PacketValidatorStage.class);

	@Autowired
	FilesystemCephAdapterImpl adapter;

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The file manager. */
	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<PacketInfo, Demographic, MetaData,ApplicantInfoDto> packetInfoManager;

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	/** The event id. */
	private String eventId = "";

	/** The event name. */
	private String eventName = "";

	/** The event type. */
	private String eventType = "";

	/** The core audit request builder. */
	@Autowired
	CoreAuditRequestBuilder coreAuditRequestBuilder;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.STRUCTURE_BUS_IN, MessageBusAddress.STRUCTURE_BUS_OUT);
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		object.setMessageBusAddress(MessageBusAddress.STRUCTURE_BUS_IN);
		object.setIsValid(Boolean.FALSE);
		object.setInternalError(Boolean.FALSE);
		String registrationId = object.getRid();
		String description = "";
		InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKETMETAINFO.name());
		try {

			PacketInfo packetInfo = (PacketInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
					PacketInfo.class);

			InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
					.getRegistrationStatus(registrationId);
			FilesValidation filesValidation = new FilesValidation(adapter);
			boolean isFilesValidated = filesValidation.filesValidation(registrationId, packetInfo);
			boolean isCheckSumValidated = false;
			if (isFilesValidated) {

				CheckSumValidation checkSumValidation = new CheckSumValidation(adapter);
				isCheckSumValidated = checkSumValidation.checksumvalidation(registrationId, packetInfo);
				if (!isCheckSumValidated) {
					registrationStatusDto.setStatusComment(StatusMessage.PACKET_CHECKSUM_VALIDATION_FAILURE);
				}
			} else {
				registrationStatusDto.setStatusComment(StatusMessage.PACKET_FILES_VALIDATION_FAILURE);
			}
			if (isFilesValidated && isCheckSumValidated) {
				object.setIsValid(Boolean.TRUE);
				registrationStatusDto.setStatusComment(StatusMessage.PACKET_STRUCTURAL_VALIDATION_SUCCESS);
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
			if (!isFilesValidated) {
				description = "File validation Failed for registration id : " + registrationId;
			} else if (!isCheckSumValidated) {
				description = "Checksum validation failed for registration id : " + registrationId;
			} else {
				description = "Packet validation successful for registration id : " + registrationId;
			}

			eventId = EventId.RPR_402.toString();
			eventName = EventName.UPDATE.toString();
			eventType = EventType.BUSINESS.toString();
			registrationStatusDto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);

		} catch (IOException e) {
			log.error(ExceptionMessages.STRUCTURAL_VALIDATION_FAILED.name(), e);
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.SYSTEM.toString();

		} catch (Exception ex) {
			log.error(ExceptionMessages.STRUCTURAL_VALIDATION_FAILED.name(), ex);
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.SYSTEM.toString();
		} finally {

			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);

		}

		return object;
	}

}
