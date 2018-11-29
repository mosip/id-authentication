package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_HANLDER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.INTERNAL_SERVER_ERROR;
import static io.mosip.registration.constants.RegistrationConstants.REGISTRATION_ID;
import static io.mosip.registration.constants.RegistrationExceptions.REG_PACKET_CREATION_ERROR_CODE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.PacketCreationService;
import io.mosip.registration.service.packet.PacketEncryptionService;
import io.mosip.registration.service.packet.PacketHandlerService;

/**
 * The class to handle the enrollment data and create packet out of it
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class PacketHandlerServiceImpl implements PacketHandlerService {

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
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(PacketHandlerServiceImpl.class);

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.packet.PacketHandlerService#handle(io.mosip.
	 * registration.dto.RegistrationDTO)
	 */
	@Override
	public ResponseDTO handle(RegistrationDTO registrationDTO) {
		LOGGER.debug(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID, "Registration Handler had been called");

		ResponseDTO responseDTO = new ResponseDTO();
		String rid = registrationDTO == null ? "RID" : registrationDTO.getRegistrationId();
		try {
			// 1. create packet
			byte[] inMemoryZipFile = packetCreationService.create(registrationDTO);

			// 2.encrypt packet
			if (inMemoryZipFile != null && inMemoryZipFile.length > 0) {
				LOGGER.debug(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
						"Registration Packet had been created successfully");

				responseDTO = packetEncryptionService.encrypt(registrationDTO, inMemoryZipFile);
			} else {
				ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
				errorResponseDTO.setCode(REG_PACKET_CREATION_ERROR_CODE.getErrorCode());
				errorResponseDTO.setMessage(REG_PACKET_CREATION_ERROR_CODE.getErrorMessage());
				List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
				errorResponseDTOs.add(errorResponseDTO);
				responseDTO.setErrorResponseDTOs(errorResponseDTOs);

				LOGGER.debug(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
						"Error in creating Registration Packet");
				auditFactory.audit(AuditEvent.PACKET_INTERNAL_ERROR, Components.PACKET_HANDLER, INTERNAL_SERVER_ERROR,
						REGISTRATION_ID, rid);
			}
		} catch (RegBaseCheckedException exception) {
			auditFactory.audit(AuditEvent.PACKET_INTERNAL_ERROR, Components.PACKET_HANDLER, INTERNAL_SERVER_ERROR,
					REGISTRATION_ID, rid);

			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			errorResponseDTO.setCode(RegistrationConstants.REG_FRAMEWORK_PACKET_HANDLING_EXCEPTION);
			errorResponseDTO.setMessage(exception.getErrorText());
			List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
			errorResponseDTOs.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		} catch (RegBaseUncheckedException uncheckedException) {
			auditFactory.audit(AuditEvent.PACKET_INTERNAL_ERROR, Components.PACKET_HANDLER, INTERNAL_SERVER_ERROR,
					REGISTRATION_ID, rid);

			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			errorResponseDTO.setCode(RegistrationConstants.REG_FRAMEWORK_PACKET_HANDLING_EXCEPTION);
			errorResponseDTO.setMessage(uncheckedException.getErrorText());
			List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
			errorResponseDTOs.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		}
		LOGGER.debug(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID, "Registration Handler had been ended");

		return responseDTO;
	}
}
