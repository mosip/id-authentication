package io.mosip.registration.processor.abis.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.mosip.registration.processor.abis.dto.AbisInsertRequestDto;
import io.mosip.registration.processor.abis.dto.AbisInsertResponseDto;
import io.mosip.registration.processor.abis.dto.CandidateListDto;
import io.mosip.registration.processor.abis.dto.CandidatesDto;
import io.mosip.registration.processor.abis.dto.IdentifyRequestDto;
import io.mosip.registration.processor.abis.dto.IdentifyResponseDto;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

/**
 * The Class AbisServiceImpl.
 */
@Service
public class AbisServiceImpl {

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The env. */
	@Autowired
	private static Environment env;

	/** The Constant DUPLICATE. */
	private static final String DUPLICATE = "duplicate";
	
	/** The Constant INSERT. */
	private static final String INSERT = "insert";
	
	/** The Constant IDENTIFY. */
	private static final String IDENTIFY = "Identify";
	
	/** The Constant TESTFINGERPRINT. */
	private static final String TESTFINGERPRINT = env.getProperty("TESTFINGERPRINT");
	
	/** The Constant TESTIRIS. */
	private static final String TESTIRIS = env.getProperty("TESTIRIS");
	
	/** The Constant TESTFACE. */
	private static final String TESTFACE = env.getProperty("TESTFACE");

	/**
	 * Insert.
	 *
	 * @param abisInsertRequestDto the abis insert request dto
	 * @return the abis insert responce dto
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	public AbisInsertResponseDto insert(AbisInsertRequestDto abisInsertRequestDto)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException {
		boolean isPresent = false;

		String referenceId = abisInsertRequestDto.getReferenceId();
		Document doc = getCbeffDocument(referenceId);

		NodeList fingerNodeList = doc.getElementsByTagName(TESTFINGERPRINT);
		NodeList irisNodeList = doc.getElementsByTagName(TESTIRIS);
		NodeList faceNodeList = doc.getElementsByTagName(TESTFACE);

		if (fingerNodeList.getLength() > 0 || irisNodeList.getLength() > 0 || faceNodeList.getLength() > 0) {
			isPresent = true;
		}

		AbisInsertResponseDto response = new AbisInsertResponseDto();
		response.setId(INSERT);
		response.setRequestId(abisInsertRequestDto.getRequestId());
		response.setTimestamp(abisInsertRequestDto.getTimestamp());
		if (isPresent) {
			response.setReturnValue(1);
		} else {
			response.setReturnValue(2);
		}

		return response;
	}

	/**
	 * Gets the cbeff document.
	 *
	 * @param referenceId the reference id
	 * @return the cbeff document
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private Document getCbeffDocument(String referenceId)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException {

		String regId = packetInfoManager.getRidByReferenceId(referenceId).get(0);
		List<String> pathSegments = new ArrayList<>();
		pathSegments.add(regId);

		byte[] bytefile = (byte[]) restClientService.getApi(ApiName.BIODEDUPE, pathSegments, "", "", byte[].class);

		File testCbeff = new File("TestCbeff.xml");
		try (FileOutputStream fos = new FileOutputStream(testCbeff)) {
			fos.write(bytefile);
		}

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		return dBuilder.parse(testCbeff);
	}

	/**
	 * Perform dedupe.
	 *
	 * @param identifyRequest the identity request
	 * @return the identity responce dto
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	public IdentifyResponseDto performDedupe(IdentifyRequestDto identifyRequest)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException {
		boolean duplicate = false;

		int count = 0;
		String referenceId = identifyRequest.getReferenceId();
		Document doc = getCbeffDocument(referenceId);

		NodeList fingerNodeList = doc.getElementsByTagName(TESTFINGERPRINT);
		duplicate = checkDuplicate(duplicate, fingerNodeList);

		NodeList irisNodeList = doc.getElementsByTagName(TESTIRIS);
		duplicate = checkDuplicate(duplicate, irisNodeList);

		NodeList faceNodeList = doc.getElementsByTagName(TESTFACE);
		duplicate = checkDuplicate(duplicate, faceNodeList);

		IdentifyResponseDto response = new IdentifyResponseDto();
		response.setId(IDENTIFY);
		response.setRequestId(identifyRequest.getRequestId());
		response.setTimestamp(identifyRequest.getTimestamp());
		response.setReturnValue(1);

		if (duplicate) {
			CandidateListDto cd = new CandidateListDto();
			CandidatesDto[] candidatesDto = new CandidatesDto[10];
			for (int i = 1; i <= identifyRequest.getMaxResults(); i++) {
				candidatesDto[i] = new CandidatesDto();
				candidatesDto[i].setReferenceId(i + "1234567-89AB-CDEF-0123-456789ABCDEF");
				candidatesDto[i].setScaledScore(100 - i + "");
				count++;
			}
			cd.setCount(count + "");
			cd.setCandidates(candidatesDto);
			response.setCandidateList(cd);
		}

		return response;
	}

	/**
	 * Check duplicate.
	 *
	 * @param duplicate the duplicate
	 * @param nodeList the node list
	 * @return true, if successful
	 */
	private boolean checkDuplicate(boolean duplicate, NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			String value = nodeList.item(i).getTextContent();
			if (value.equalsIgnoreCase(DUPLICATE)) {
				duplicate = true;
				break;
			}
		}
		return duplicate;
	}
}
