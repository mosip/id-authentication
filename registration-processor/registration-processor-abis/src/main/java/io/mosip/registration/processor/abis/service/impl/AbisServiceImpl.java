package io.mosip.registration.processor.abis.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.abis.dto.AbisInsertRequestDto;
import io.mosip.registration.processor.abis.dto.AbisInsertResponseDto;
import io.mosip.registration.processor.abis.dto.CandidateListDto;
import io.mosip.registration.processor.abis.dto.CandidatesDto;
import io.mosip.registration.processor.abis.service.AbisService;
import io.mosip.registration.processor.abis.dto.AbisIdentifyRequestDto;
import io.mosip.registration.processor.abis.dto.AbisIdentifyResponseDto;
import io.mosip.registration.processor.core.bio.dedupe.dto.BioDedupeRegIdDto;
import io.mosip.registration.processor.core.bio.dedupe.dto.BioDedupeRequestDTO;
import io.mosip.registration.processor.core.bio.dedupe.dto.BioDedupeResponseDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

/**
 * The Class AbisServiceImpl.
 */
@Service
public class AbisServiceImpl implements AbisService {

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The Constant DUPLICATE. */
	private static final String DUPLICATE = "duplicate";

	/** The Constant INSERT. */
	private static final String INSERT = "insert";

	/** The Constant IDENTIFY. */
	private static final String IDENTIFY = "Identify";

	/** The Constant TESTFINGERPRINT. */
	@Value("${TESTFINGERPRINT}")
	private String testFingerPrint;

	/** The Constant TESTIRIS. */
	@Value("${TESTIRIS}")
	private String testIris;

	/** The Constant TESTFACE. */
	@Value("${TESTFACE}")
	private String testFace;

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(AbisServiceImpl.class);

	private static final String BIO_DEDUPE_SERVICE_ID = "mosip.packet.bio.dedupe";
	private static final String BIO_DEDUPE_APPLICATION_VERSION = "1.0";
	private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * Insert.
	 *
	 * @param abisInsertRequestDto
	 *            the abis insert request dto
	 * @return the abis insert responce dto
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 */
	public AbisInsertResponseDto insert(AbisInsertRequestDto abisInsertRequestDto)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException {

		boolean isPresent = false;
		AbisInsertResponseDto response = new AbisInsertResponseDto();
		String referenceId = abisInsertRequestDto.getReferenceId();
		try {

			Document doc = getCbeffDocument(referenceId);

			if (testFingerPrint == null || testIris == null || testFace == null) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), referenceId, "Test Tags are not present");
			}
			if(doc != null) {
				NodeList fingerNodeList = doc.getElementsByTagName(testFingerPrint);
				NodeList irisNodeList = doc.getElementsByTagName(testIris);
				NodeList faceNodeList = doc.getElementsByTagName(testFace);

				if (fingerNodeList.getLength() > 0 || irisNodeList.getLength() > 0 || faceNodeList.getLength() > 0) {
					isPresent = true;
				}
			}
			response.setId(INSERT);
			response.setRequestId(abisInsertRequestDto.getRequestId());
			response.setTimestamp(abisInsertRequestDto.getTimestamp());
			if (isPresent) {
				response.setReturnValue(1);
			} else {
				response.setReturnValue(2);
			}
		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					referenceId, "Test Tags are not present" + ExceptionUtils.getStackTrace(e));
		}

		return response;
	}

	/**
	 * Gets the cbeff document.
	 *
	 * @param referenceId
	 *            the reference id
	 * @return the cbeff document
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 */
	private Document getCbeffDocument(String referenceId)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException {

		String regId = packetInfoManager.getRidByReferenceId(referenceId).get(0);
		BioDedupeRequestDTO bioDedupeReuestObj=new BioDedupeRequestDTO();
		BioDedupeRegIdDto requestObj=new BioDedupeRegIdDto();
		
		requestObj.setRegId(regId);
		bioDedupeReuestObj.setRequest(requestObj);
		bioDedupeReuestObj.setId(BIO_DEDUPE_SERVICE_ID);
		bioDedupeReuestObj.setVersion(BIO_DEDUPE_APPLICATION_VERSION);
		bioDedupeReuestObj.setTimestamp(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN));
		
		BioDedupeResponseDTO bioReponseDto=(BioDedupeResponseDTO)restClientService.postApi(ApiName.BIODEDUPE,null, "", "",bioDedupeReuestObj, BioDedupeResponseDTO.class);
		byte[] bytefile=bioReponseDto.getFile().getBytes();
		
		if (bytefile == null) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					referenceId, "Byte file not found from BioDedupe api");
		}
		if (bytefile != null) {
			String byteFileStr = new String(bytefile);

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(byteFileStr));

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			return dBuilder.parse(is);
		}
		return null;
	}

	/**
	 * Perform dedupe.
	 *
	 * @param identifyRequest
	 *            the identity request
	 * @return the identity responce dto
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 */
	public AbisIdentifyResponseDto performDedupe(AbisIdentifyRequestDto identifyRequest)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException {
		boolean duplicate = false;

		int count = 0;
		AbisIdentifyResponseDto response = new AbisIdentifyResponseDto();
		String referenceId = identifyRequest.getReferenceId();

		try {
			Document doc = getCbeffDocument(referenceId);

			NodeList fingerNodeList = doc.getElementsByTagName(testFingerPrint);
			if(fingerNodeList != null) {
				duplicate = checkDuplicate(duplicate, fingerNodeList);
			}

			NodeList irisNodeList = doc.getElementsByTagName(testIris);
			if(irisNodeList != null) {
				duplicate = checkDuplicate(duplicate, irisNodeList);
			}
			NodeList faceNodeList = doc.getElementsByTagName(testFace);
			if(faceNodeList != null) {
				duplicate = checkDuplicate(duplicate, faceNodeList);
			}
			response.setId(IDENTIFY);
			response.setRequestId(identifyRequest.getRequestId());
			response.setTimestamp(identifyRequest.getTimestamp());
			response.setReturnValue(1);

			if (duplicate) {
				CandidateListDto cd = new CandidateListDto();
				CandidatesDto[] candidatesDto = new CandidatesDto[identifyRequest.getMaxResults() + 2];

				for (int i = 0; i <candidatesDto.length; i++) {
					candidatesDto[i] = new CandidatesDto();
					candidatesDto[i].setReferenceId(i + "1234567-89AB-CDEF-0123-456789ABCDEF");
					candidatesDto[i].setScaledScore(100 - i + "");
					count++;
				}
				cd.setCount(count + "");
				cd.setCandidates(candidatesDto);
				response.setCandidateList(cd);
			}
		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					referenceId, "Due to some internal error, abis failed" + ExceptionUtils.getStackTrace(e));
		}

		return response;
	}

	/**
	 * Check duplicate.
	 *
	 * @param duplicate
	 *            the duplicate
	 * @param nodeList
	 *            the node list
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

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.abis.service.impl.AbisService#delete()
	 */
	@Override
	public void delete() {
		// Delete should be implemented in future
	}
}
