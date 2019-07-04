package io.mosip.registration.processor.abis.service;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisPingRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisPingResponseDto;

/**
 * The Interface AbisService.
 *  @author M1048860 Kiran Raj
 */
@Service
public interface AbisService {


	/**
	 * Insert.
	 *
	 * @param abisInsertRequestDto the abis insert request dto
	 * @return the abis insert response dto
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	public AbisInsertResponseDto insert(AbisInsertRequestDto abisInsertRequestDto);

	/**
	 * Identify.
	 *
	 * @param identifyRequest the identify request
	 * @return the abis identify response dto
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	public AbisIdentifyResponseDto identify(AbisIdentifyRequestDto identifyRequest);
	/**
	 * Delete.
	 */
	public void delete();

	/**
	 * Ping.
	 *
	 * @param abisPingRequestDto the abis ping request dto
	 * @return the abis ping response dto
	 */
	public AbisPingResponseDto ping(AbisPingRequestDto abisPingRequestDto);
}
