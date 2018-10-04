package io.mosip.registration.service.packet;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegProcessorExceptionEnum.REG_PACKET_CREATION_ERROR_CODE;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.util.ArrayList;
import java.util.List;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.PacketCreationService;
import io.mosip.registration.service.PacketEncryptionService;

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
	private PacketCreationService packetCreationManager;

	/**
	 * Class to encrypt the packet data
	 */
	@Autowired
	private PacketEncryptionService packetEncryptionManager;
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/**
	 * Method to create the packet data and encrypt the same
	 * 
	 * @param enrollmentDTO
	 * @return the {@link ResponseDTO} object
	 */
	public ResponseDTO handle(RegistrationDTO registrationDTO) {
		LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Registration Handler had been called");
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			// TODO: 1. Registration validation - To be implemented
			// packetValidationManager.validate(enrollmentDTO);

			// 2. create packet
			byte[] inMemoryZipFile = packetCreationManager.create(registrationDTO);

			// 3.encrypt packet
			if (inMemoryZipFile != null && inMemoryZipFile.length > 0) {
				LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "Registration Packet had been created successfully");
				responseDTO = packetEncryptionManager.encrypt(registrationDTO, inMemoryZipFile);
			} else {
				ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
				errorResponseDTO.setCode(REG_PACKET_CREATION_ERROR_CODE.getErrorCode());
				errorResponseDTO.setMessage(REG_PACKET_CREATION_ERROR_CODE.getErrorMessage());
				List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
				errorResponseDTOs.add(errorResponseDTO);
				responseDTO.setErrorResponseDTOs(errorResponseDTOs);
				LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "Error in creating Registration Packet");
				auditFactory.audit(AuditEventEnum.PACKET_INTERNAL_ERROR, AppModuleEnum.PACKET_HANDLER,
						"Internal error while creating packet", "registration reference id", "123456");
			}
		} catch (RegBaseCheckedException exception) {
			auditFactory.audit(AuditEventEnum.PACKET_INTERNAL_ERROR, AppModuleEnum.PACKET_HANDLER,
					"Internal error while creating packet", "registration reference id", "123456");
			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			errorResponseDTO.setCode(RegProcessorExceptionCode.REG_FRAMEWORK_PACKET_HANDLING_EXCEPTION);
			errorResponseDTO.setMessage(exception.getErrorText());
			List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
			errorResponseDTOs.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		} catch (RegBaseUncheckedException uncheckedException) {
			auditFactory.audit(AuditEventEnum.PACKET_INTERNAL_ERROR, AppModuleEnum.PACKET_HANDLER,
					"Internal error while creating packet", "registration reference id", "123456");
			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			errorResponseDTO.setCode(RegProcessorExceptionCode.REG_FRAMEWORK_PACKET_HANDLING_EXCEPTION);
			errorResponseDTO.setMessage(uncheckedException.getErrorText());
			List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
			errorResponseDTOs.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		} 
		LOGGER.debug("REGISTRATION - PACKET_HANDLER - CREATE", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Registration Handler had been ended");
		return responseDTO;
	}
}
