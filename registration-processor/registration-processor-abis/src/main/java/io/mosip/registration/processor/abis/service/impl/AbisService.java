package io.mosip.registration.processor.abis.service.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import io.mosip.registration.processor.abis.dto.AbisInsertRequestDto;
import io.mosip.registration.processor.abis.dto.AbisInsertResponseDto;
import io.mosip.registration.processor.abis.dto.IdentifyRequestDto;
import io.mosip.registration.processor.abis.dto.IdentifyResponseDto;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;

/**
 * The Interface AbisService.
 * @Author Kiran
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
	public AbisInsertResponseDto insert(AbisInsertRequestDto abisInsertRequestDto)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException;

	/**
	 * Perform dedupe.
	 *
	 * @param identifyRequest the identify request
	 * @return the identify response dto
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	public IdentifyResponseDto performDedupe(IdentifyRequestDto identifyRequest)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException;
}
