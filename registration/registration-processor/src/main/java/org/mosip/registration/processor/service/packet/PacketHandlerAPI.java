package org.mosip.registration.processor.service.packet;

import org.mosip.kernel.core.audit.builder.AuditRequestBuilder;
import org.mosip.kernel.core.audit.handler.AuditHandler;
import org.mosip.kernel.core.audit.request.AuditRequest;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.mosip.registration.processor.exception.RegBaseCheckedException;
import org.mosip.registration.processor.service.packet.creation.PacketCreationManager;
import org.mosip.registration.processor.service.packet.encryption.PacketEncryptionManager;
import org.mosip.registration.processor.response.Response;
import org.mosip.registration.processor.dto.EnrollmentDTO;
import org.mosip.registration.processor.consts.RegProcessorExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.mosip.registration.processor.consts.RegProcessorExceptionEnum.REG_PACKET_CREATION_ERROR_CODE;

import java.time.OffsetDateTime;

/**
 * The class to handle the enrollment data and create packet out of it
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class PacketHandlerAPI {

	/**
	 * Class to create the packet data
	 */
	@Autowired
	private PacketCreationManager packetCreationManager;

	/**
	 * Class to encrypt the packet data
	 */
	@Autowired
	private PacketEncryptionManager packetEncryptionManager;
	private static MosipLogger LOGGER;
	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}
	@Autowired
	@Qualifier("registrationAuditBuilder")
	private AuditRequestBuilder auditRequestBuilder;
	@Autowired
	private AuditHandler auditHandler;

	/**
	 * Method to create the packet data and encrypt the same
	 * 
	 * @param enrollmentDTO
	 * @return the {@link Response} object
	 */
	public Response handle(EnrollmentDTO enrollmentDTO) {
		LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", "EnrollmentId", "id", "Registration Handler had been called");
		Response response = null;
		try {
			// TODO: 1. Registration validation - To be implemented
			// packetValidationManager.validate(enrollmentDTO);

			// 2. create packet
			byte[] zipData = packetCreationManager.create(enrollmentDTO);

			// 3.encrypt packet
			if (zipData != null && zipData.length > 0) {
				LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", "EnrollmentId", "id", "Registration Packet had been created successfully");
				return packetEncryptionManager.encrypt(enrollmentDTO, zipData);
			} else {
				LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", "EnrollmentId", "id", "Error in creating Registration Packet");
				response = new Response();
				
				response.setCode(REG_PACKET_CREATION_ERROR_CODE.getErrorCode());
				response.setMessage(REG_PACKET_CREATION_ERROR_CODE.getErrorMessage());
			}
			auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now()).setApplicationId("applicationId")
			.setApplicationName("applicationName").setCreatedBy("createdBy").setDescription("description")
			.setEventId("eventId").setEventName("eventName").setEventType("eventType").setHostIp("hostIp")
			.setHostName("hostName").setId("id").setIdType("idType").setModuleId("moduleId")
			.setModuleName("moduleName").setSessionUserId("sessionUserId").setSessionUserName("sessionUserName");
			
			AuditRequest auditRequest = auditRequestBuilder.build();
			auditHandler.writeAudit(auditRequest);
		} catch (RegBaseCheckedException exception) {
			// TODO : Added the Log error for audit
			// TODO: Add for the general debug log
			response = new Response();
			
			response.setCode(RegProcessorExceptionCode.IDIS_FRAMEWORK_PACKET_HANDLING_EXCEPTION);
			response.setMessage(exception.getErrorText());
		}
		LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", "EnrollmentId", "id", "Registration Handler had been ended");
		return response;
	}
}
