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
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
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

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	/** The core audit request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.STRUCTURE_BUS_IN, MessageBusAddress.STRUCTURE_BUS_OUT);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		object.setMessageBusAddress(MessageBusAddress.STRUCTURE_BUS_IN);
		object.setIsValid(Boolean.FALSE);
		object.setInternalError(Boolean.FALSE);
		String registrationId = object.getRid();
		String description = "";
		boolean isTransactionSuccessful = false;
		InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKETMETAINFO.name());
		try {

			PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
					PacketMetaInfo.class);

			InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
					.getRegistrationStatus(registrationId);
			FilesValidation filesValidation = new FilesValidation(adapter);
			boolean isFilesValidated = filesValidation.filesValidation(registrationId, packetMetaInfo.getIdentity());
			boolean isCheckSumValidated = false;
			if (isFilesValidated) {

				CheckSumValidation checkSumValidation = new CheckSumValidation(adapter);
				isCheckSumValidated = checkSumValidation.checksumvalidation(registrationId,
						packetMetaInfo.getIdentity());

				if (!isCheckSumValidated) {
					registrationStatusDto.setStatusComment(StatusMessage.PACKET_CHECKSUM_VALIDATION_FAILURE);
				}
			} else {
				registrationStatusDto.setStatusComment(StatusMessage.PACKET_FILES_VALIDATION_FAILURE);
			}
			if (isFilesValidated && isCheckSumValidated) {
				object.setIsValid(Boolean.TRUE);
				registrationStatusDto.setStatusComment(StatusMessage.PACKET_STRUCTURAL_VALIDATION_SUCCESS);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_SUCCESS.toString());
				packetInfoManager.savePacketData(packetMetaInfo.getIdentity());
				InputStream demographicInfoStream = adapter.getFile(registrationId,
						PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name());
				packetInfoManager.saveDemographicInfoJson(demographicInfoStream,
						packetMetaInfo.getIdentity().getMetaData());

			} else {
				object.setIsValid(Boolean.FALSE);
				if (registrationStatusDto.getRetryCount() == null) {
					registrationStatusDto.setRetryCount(0);
				} else {
					registrationStatusDto.setRetryCount(registrationStatusDto.getRetryCount() + 1);
				}

				registrationStatusDto.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_FAILED.toString());

			}
			if (!isFilesValidated) {
				description = "File validation Failed for registration id : " + registrationId;
			} else if (!isCheckSumValidated) {
				description = "Checksum validation failed for registration id : " + registrationId;
			} else {
				description = "Packet validation successful for registration id : " + registrationId;
			}
			registrationStatusDto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			isTransactionSuccessful = true;
		} catch (IOException e) {
			log.error(PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage(), e);
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;

		} catch (Exception ex) {
			log.error(PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage(), ex);
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;
		} finally {

			String eventId = "";
			String eventName = "";
			String eventType = "";
			description = isTransactionSuccessful ? "Packet uploaded to file system"
					: "Packet uploading to file system is unsuccessful";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);

		}

		return object;
	}

}
