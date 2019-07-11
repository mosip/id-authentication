package io.mosip.registration.processor.packet.service;

import java.io.IOException;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;

/**
 * Class for creating the Resident Registration
 * 
 * @author Sowmya
 * 
 *
 */
public interface PacketCreationService {

	/**
	 * Creates the packet
	 * 
	 * @param registrationDTO
	 *            the enrollment data for which packet has to be created
	 * @throws RegBaseCheckedException
	 * @throws ParseException 
	 * @throws IdObjectIOException 
	 * @throws IdObjectValidationFailedException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	byte[] create(RegistrationDTO registrationDTO) throws RegBaseCheckedException, ParseException, IdObjectValidationFailedException, IdObjectIOException, JsonParseException, JsonMappingException, IOException;

	String getCreationTime();
}
