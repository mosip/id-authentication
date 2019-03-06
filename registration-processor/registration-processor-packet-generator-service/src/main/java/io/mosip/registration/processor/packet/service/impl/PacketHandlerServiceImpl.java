package io.mosip.registration.processor.packet.service.impl;

import static io.mosip.registration.processor.packet.service.constants.LoggerConstants.LOG_PKT_HANLDER;
import static io.mosip.registration.processor.packet.service.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.processor.packet.service.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.processor.packet.service.exception.RegistrationExceptionConstants.REG_PACKET_CREATION_ERROR_CODE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.packet.service.PacketCreationService;
import io.mosip.registration.processor.packet.service.PacketHandlerService;
import io.mosip.registration.processor.packet.service.constants.Components;
import io.mosip.registration.processor.packet.service.constants.RegistrationConstants;
import io.mosip.registration.processor.packet.service.dto.ErrorResponseDTO;
import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
import io.mosip.registration.processor.packet.service.dto.ResponseDTO;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.packet.service.exception.RegBaseUncheckedException;

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
	 * Instance of {@code AuditFactory}
	 */
	//@Autowired
	//private AuditFactory auditFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.packet.PacketHandlerService#handle(io.mosip.
	 * registration.dto.RegistrationDTO)
	 */
	@Override
	public ResponseDTO handle(RegistrationDTO registrationDTO) {
		
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JSR310Module());
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		try {
			writer.writeValue(new File("user.json"), registrationDTO);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		//LOGGER.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID, "Registration Handler had been called");

		ResponseDTO responseDTO = new ResponseDTO();
		String rid = registrationDTO == null ? "RID" : registrationDTO.getRegistrationId();
		try {
			// 1. create packet
			byte[] inMemoryZipFile = packetCreationService.create(registrationDTO);

			// 2.encrypt packet
			if (inMemoryZipFile != null && inMemoryZipFile.length > 0) {
				//LOGGER.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					//	"Registration Packet had been created successfully");

				//responseDTO = packetEncryptionService.encrypt(registrationDTO, inMemoryZipFile);
			} else {
				ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
				errorResponseDTO.setCode(REG_PACKET_CREATION_ERROR_CODE.getErrorCode());
				errorResponseDTO.setMessage(REG_PACKET_CREATION_ERROR_CODE.getErrorMessage());
				List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
				errorResponseDTOs.add(errorResponseDTO);
				responseDTO.setErrorResponseDTOs(errorResponseDTOs);

				//LOGGER.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
						//"Error in creating Registration Packet");
				//auditFactory.audit(AuditEvent.PACKET_INTERNAL_ERROR, Components.PACKET_HANDLER, rid,
						//AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());
			}
		} catch (RegBaseCheckedException exception) {
			//LOGGER.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					//ExceptionUtils.getStackTrace(exception));

			//auditFactory.audit(AuditEvent.PACKET_INTERNAL_ERROR, Components.PACKET_HANDLER, rid,
				//	AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			errorResponseDTO.setCode(RegistrationConstants.REG_FRAMEWORK_PACKET_HANDLING_EXCEPTION);
			errorResponseDTO.setMessage(exception.getErrorText());
			List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
			errorResponseDTOs.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		} catch (RegBaseUncheckedException uncheckedException) {
			//LOGGER.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					//ExceptionUtils.getStackTrace(uncheckedException));
	
			//auditFactory.audit(AuditEvent.PACKET_INTERNAL_ERROR, Components.PACKET_HANDLER, rid,
					//AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			errorResponseDTO.setCode(RegistrationConstants.REG_FRAMEWORK_PACKET_HANDLING_EXCEPTION);
			errorResponseDTO.setMessage(uncheckedException.getErrorText());
			List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
			errorResponseDTOs.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		}
		//LOGGER.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID, "Registration Handler had been ended");

		return responseDTO;
	}
}
