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

@Service
public interface AbisService {
	public AbisInsertResponseDto insert(AbisInsertRequestDto abisInsertRequestDto)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException;
	public IdentifyResponseDto performDedupe(IdentifyRequestDto identifyRequest)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException;
}
