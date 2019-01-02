/**
 * 
 */
package io.mosip.registration.processor.stages.packet.validator;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.stages.utils.ApplicantDocumentValidation;
import io.mosip.registration.processor.stages.utils.CheckSumValidation;
import io.mosip.registration.processor.stages.utils.FilesValidation;
import io.mosip.registration.processor.stages.utils.StatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

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

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketValidatorStage.class);
	

	/** The adapter. */
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant APPLICANT_TYPE. */
	public static final String APPLICANT_TYPE = "applicantType";

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The core audit request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
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
			FilesValidation filesValidation = new FilesValidation(adapter, registrationStatusDto);
			boolean isFilesValidated = filesValidation.filesValidation(registrationId, packetMetaInfo.getIdentity());
			boolean isCheckSumValidated = false;
			boolean isApplicantDocumentValidation = false;
			if (isFilesValidated) {

				CheckSumValidation checkSumValidation = new CheckSumValidation(adapter, registrationStatusDto);
				isCheckSumValidated = checkSumValidation.checksumvalidation(registrationId,
						packetMetaInfo.getIdentity());

				if (isCheckSumValidated) {
					ApplicantDocumentValidation applicantDocumentValidation = new ApplicantDocumentValidation(
							registrationStatusDto);
					isApplicantDocumentValidation = applicantDocumentValidation
							.validateDocument(packetMetaInfo.getIdentity(), registrationId);

				}

			}

			if (isFilesValidated && isCheckSumValidated && isApplicantDocumentValidation) {
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

				int retryCount = registrationStatusDto.getRetryCount() != null
						? registrationStatusDto.getRetryCount() + 1
						: 1;
				description = registrationStatusDto.getStatusComment() + registrationId;
				registrationStatusDto.setRetryCount(retryCount);

				registrationStatusDto.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_FAILED.toString());

			}

			registrationStatusDto.setUpdatedBy(USER);

			setApplicant(packetMetaInfo.getIdentity(), registrationStatusDto);

			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			isTransactionSuccessful = true;
		} catch (DataAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),registrationId,PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage()+e.getMessage());
			object.setInternalError(Boolean.TRUE);
			description = "Data voilation in reg packet : " + registrationId;

		} catch (IOException exc) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),registrationId,PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage()+exc.getMessage());
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;

		} catch (Exception ex) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),registrationId,PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage()+ex.getMessage());
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;
		} finally {

			String eventId = "";
			String eventName = "";
			String eventType = "";

			if(isTransactionSuccessful) {
				description = "Packet uploaded to file system";
				eventId=EventId.RPR_402.toString();
				eventName = EventName.UPDATE.toString();
				eventType = EventType.BUSINESS.toString();
			}else {

				description = "Packet uploading to file system is unsuccessful";
				eventId=EventId.RPR_405.toString();
				eventName = EventName.EXCEPTION.toString();
				eventType = EventType.SYSTEM.toString();
			}
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);

		}

		return object;
	}

	/**
	 * Sets the applicant.
	 *
	 * @param identity the identity
	 * @param registrationStatusDto the registration status dto
	 */
	private void setApplicant(Identity identity, InternalRegistrationStatusDto registrationStatusDto) {
		IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
		String applicantType = identityIteratorUtil.getFieldValue(identity.getMetaData(), APPLICANT_TYPE);
		registrationStatusDto.setApplicantType(applicantType);

	}

}
