package org.mosip.registration.service.packet;

import org.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import org.mosip.kernel.core.spi.auditmanager.AuditHandler;
import org.mosip.kernel.auditmanager.request.AuditRequestDto;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.constants.RegProcessorExceptionCode;
import org.mosip.registration.dto.EnrollmentDTO;
import org.mosip.registration.dto.ResponseDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.service.PacketCreationService;
import org.mosip.registration.service.PacketEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_PACKET_CREATION_ERROR_CODE;

import java.time.OffsetDateTime;

/**
 * The class to handle the enrollment data and create packet out of it
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class PacketHandlerService {

	/**
	 * Class to create the packet data
	 */
	@Autowired
	private PacketCreationService packetCreationService;

	/**
	 * Class to encrypt the packet data
	 */
	@Autowired
	private PacketEncryptionService packetEncryptionService;
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
	 * @return the {@link ResponseDTO} object
	 */
	public ResponseDTO handle(EnrollmentDTO enrollmentDTO) {
		LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", "EnrollmentId", "id", "Registration Handler had been called");
		ResponseDTO responseDTO = null;
		try {
			// TODO: 1. Registration validation - To be implemented
			// packetValidationManager.validate(enrollmentDTO);

			// 2. create packet
			byte[] zipData = packetCreationService.create(enrollmentDTO);

			// 3.encrypt packet
			if (zipData != null && zipData.length > 0) {
				LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", "EnrollmentId", "id", "Registration Packet had been created successfully");
				return packetEncryptionService.encrypt(enrollmentDTO, zipData);
			} else {
				LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", "EnrollmentId", "id", "Error in creating Registration Packet");
				responseDTO = new ResponseDTO();
				
				responseDTO.setCode(REG_PACKET_CREATION_ERROR_CODE.getErrorCode());
				responseDTO.setMessage(REG_PACKET_CREATION_ERROR_CODE.getErrorMessage());
			}
			auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now()).setApplicationId("applicationId")
			.setApplicationName("applicationName").setCreatedBy("createdBy").setDescription("description")
			.setEventId("eventId").setEventName("eventName").setEventType("eventType").setHostIp("hostIp")
			.setHostName("hostName").setId("id").setIdType("idType").setModuleId("moduleId")
			.setModuleName("moduleName").setSessionUserId("sessionUserId").setSessionUserName("sessionUserName");
			
			AuditRequestDto auditRequest = auditRequestBuilder.build();
			auditHandler.writeAudit(auditRequest);
		} catch (RegBaseCheckedException exception) {
			// TODO : Added the Log error for audit
			// TODO: Add for the general debug log
			responseDTO = new ResponseDTO();
			
			responseDTO.setCode(RegProcessorExceptionCode.IDIS_FRAMEWORK_PACKET_HANDLING_EXCEPTION);
			responseDTO.setMessage(exception.getErrorText());
		}
		LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", "EnrollmentId", "id", "Registration Handler had been ended");
		return responseDTO;
	}
}
