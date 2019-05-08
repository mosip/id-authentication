package io.mosip.registration.processor.abis.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.abis.service.AbisService;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisPingRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisPingResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.CandidateListDto;
import io.mosip.registration.processor.core.packet.dto.abis.CandidatesDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

/**
 * The Class AbisServiceImpl.
 * @author M1048860 Kiran Raj
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
	private static final String ABIS_INSERT = "mosip.abis.insert";

	/** The Constant IDENTIFY. */
	private static final String ABIS_IDENTIFY = "mosip.abis.identify";

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

	public AbisInsertResponseDto insert(AbisInsertRequestDto abisInsertRequestDto)
	{
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "AbisServiceImpl::insert()::entry");

		boolean isPresent = false;
		AbisInsertResponseDto response = new AbisInsertResponseDto();
		String referenceId = abisInsertRequestDto.getReferenceId();

		response.setId(ABIS_INSERT);
		response.setRequestId(abisInsertRequestDto.getRequestId());
		response.setTimestamp(abisInsertRequestDto.getTimestamp());

		Document doc;
		try {
			doc = getCbeffDocument(referenceId);


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
				if (isPresent) {
					response.setReturnValue(1);
				} else {
					response.setReturnValue(2);
				}
			}else {
				response.setReturnValue(2);
				response.setFailureReason(7);
			}

		}  catch (ApisResourceAccessException | ParserConfigurationException | SAXException | IOException e) {
			response.setReturnValue(2);
			response.setFailureReason(7);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					referenceId, "ApisResourceAccessException : Unable to acces getting cbef url." + ExceptionUtils.getStackTrace(e));

		}catch(Exception e) {
			response.setReturnValue(2);
			response.setFailureReason(3);

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					referenceId, "Due to some internal error, abis failed" + ExceptionUtils.getStackTrace(e));

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "AbisServiceImpl::insert()::exit");

		return response;
	}

	private Document getCbeffDocument(String referenceId) throws ApisResourceAccessException, ParserConfigurationException, SAXException, IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "AbisServiceImpl::getCbeffDocument()::entry");
		List<String> regId = null;
		if(referenceId != null) {
			regId = packetInfoManager.getRidByReferenceId(referenceId);
			List<String> pathSegments = new ArrayList<>();
			if(regId != null && regId.size() > 0) {
				pathSegments.add(regId.get(0));

				byte[] bytefile = (byte[]) restClientService.getApi(ApiName.BIODEDUPE, pathSegments, "", "", byte[].class);
				if (bytefile == null) {
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
							referenceId, "Byte file not found from BioDedupe api");
				}

				if (bytefile != null) {
					String byteFileStr = new String(bytefile);

					InputSource is = new InputSource();
					is.setCharacterStream(new StringReader(byteFileStr));

					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					return dBuilder.parse(is);
				}
			}else {
				//bio request url send no data
			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "AbisServiceImpl::getCbeffDocument()::exit");

		return null;
	}

	public AbisIdentifyResponseDto performDedupe(AbisIdentifyRequestDto identifyRequest){
		boolean duplicate = false;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "AbisServiceImpl::performDedupe()::entry");

		int count = 0;
		AbisIdentifyResponseDto response = new AbisIdentifyResponseDto();
		String referenceId = identifyRequest.getReferenceId();


		response.setId(ABIS_IDENTIFY);
		response.setRequestId(identifyRequest.getRequestId());
		response.setTimestamp(identifyRequest.getTimestamp());

		Document doc;
		try {
			doc = getCbeffDocument(referenceId);

			if(doc != null) {
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
				response.setReturnValue(1);

				if (duplicate) {
					CandidateListDto cd = new CandidateListDto();
					CandidatesDto[] candidatesDto;
					if(!identifyRequest.getGallery().getReferenceIds().isEmpty()) {
						candidatesDto = new CandidatesDto[identifyRequest.getGallery().getReferenceIds().size() ];
						for (int i = 0; i <candidatesDto.length; i++) {
							candidatesDto[i] = new CandidatesDto();
							candidatesDto[i].setReferenceId(identifyRequest.getGallery().getReferenceIds().get(i).getReferenceId());
							candidatesDto[i].setScaledScore(100 - i + "");
							count++;
						}							
					}
					else{
						candidatesDto = new CandidatesDto[identifyRequest.getMaxResults() + 2];
						for (int i = 0; i <candidatesDto.length; i++) {
							candidatesDto[i] = new CandidatesDto();
							candidatesDto[i].setReferenceId(i + "1234567-89AB-CDEF-0123-456789ABCDEF");
							candidatesDto[i].setScaledScore(100 - i + "");
							count++;
						}
					}
					cd.setCount(count + "");
					cd.setCandidates(candidatesDto);
					response.setCandidateList(cd);
				}
			}else {
				response.setReturnValue(2);

			}

		}  catch (ApisResourceAccessException | ParserConfigurationException | SAXException | IOException e) {
			response.setReturnValue(2);
			response.setFailureReason(7);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					referenceId, "ApisResourceAccessException : Unable to acces getting cbef url." + ExceptionUtils.getStackTrace(e));

		}catch(Exception e) {
			response.setReturnValue(2);
			response.setFailureReason(3);

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					referenceId, "Due to some internal error, abis failed" + ExceptionUtils.getStackTrace(e));

		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "AbisServiceImpl::performDedupe()::exit");
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

	@Override
	public AbisPingResponseDto ping(AbisPingRequestDto abisPingRequestDto) {
		// Ping should be implemented in future
		return null;
	}
}
