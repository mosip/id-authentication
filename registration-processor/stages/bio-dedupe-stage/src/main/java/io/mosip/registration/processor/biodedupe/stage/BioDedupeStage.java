/**
 * 
 */
package io.mosip.registration.processor.biodedupe.stage;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.bio.dedupe.exception.ABISAbortException;
import io.mosip.registration.processor.bio.dedupe.exception.ABISInternalError;
import io.mosip.registration.processor.bio.dedupe.exception.UnableToServeRequestABISException;
import io.mosip.registration.processor.bio.dedupe.exception.UnexceptedError;
import io.mosip.registration.processor.biodedupe.stage.utils.StatusMessage;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.ResponseStatusCode;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class BioDedupeStage.
 *
 * @author M1022006
 */
@Service
public class BioDedupeStage extends MosipVerticleManager {
	
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BioDedupeStage.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The cluster manager url. */
	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The bio dedupe service. */
	@Autowired
	private BioDedupeService bioDedupeService;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	String description = "";

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.BIO_DEDUPE_BUS_IN, MessageBusAddress.BIO_DEDUPE_BUS_OUT);
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
		object.setMessageBusAddress(MessageBusAddress.BIO_DEDUPE_BUS_IN);
		object.setInternalError(Boolean.FALSE);

		boolean isTransactionSuccessful = false;

		String registrationId = object.getRid();
		
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "biodedupe::process()::entry");

		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);
		
		try {
			String insertionResult = bioDedupeService.insertBiometrics(registrationId);
			if (insertionResult.equalsIgnoreCase(ResponseStatusCode.SUCCESS.name())) {
				List<String> matchedRegIds = bioDedupeService.performDedupe(registrationId);
				checkBiometricPotentialMatch(matchedRegIds, registrationStatusDto, registrationId, object);
			} else {
				object.setIsValid(Boolean.FALSE);
				registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIOMETRIC_INSERTION_TO_ABIS);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.toString());
				description = registrationStatusDto.getStatusComment() + registrationId;
			}
			registrationStatusDto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			isTransactionSuccessful = true;

			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,description);
			
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, regProcLogger.getClass().getName() + "process()::exit");

		} catch (ABISInternalError e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			description = "ABIS Internal error occured while processing registration  id  : " + registrationId;

		}

		catch (ABISAbortException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			description = "ABIS Abort Exception occured while processing registration  id : " + registrationId;

		} catch (UnexceptedError e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			description = "Unexcepted Error occured while processing registration  id : " + registrationId;

		} catch (UnableToServeRequestABISException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			description = "Unable To Serve Request ABIS Exception occured while processing registration  id : "
					+ registrationId;

		} catch (DataAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			description = "Data voilation in reg packet : " + registrationId;

		}

		catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;

		} catch (Exception ex) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage() + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;
		} finally {
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);

		}
		return object;
	}

	private void checkBiometricPotentialMatch(List<String> matchedRegIds,
			InternalRegistrationStatusDto registrationStatusDto, String registrationId, MessageDTO object) {
		if (matchedRegIds != null && !matchedRegIds.isEmpty()) {
			object.setIsValid(Boolean.FALSE);
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIOMETRIC_POTENTIAL_MATCH);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_POTENTIAL_MATCH.toString());
			description = registrationStatusDto.getStatusComment() + registrationId;
			packetInfoManager.saveManualAdjudicationData(matchedRegIds, registrationId);
		} else {
			object.setIsValid(Boolean.TRUE);
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIODEDUPE_SUCCESS);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_SUCCESS.toString());
			description = registrationStatusDto.getStatusComment() + registrationId;
		}
	}

}
