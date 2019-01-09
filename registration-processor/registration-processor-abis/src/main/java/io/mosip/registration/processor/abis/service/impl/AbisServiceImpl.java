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
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.mosip.registration.processor.abis.dto.AbisInsertRequestDto;
import io.mosip.registration.processor.abis.dto.AbisInsertResponceDto;
import io.mosip.registration.processor.abis.dto.CandidateListDto;
import io.mosip.registration.processor.abis.dto.CandidatesDto;
import io.mosip.registration.processor.abis.dto.IdentityRequestDto;
import io.mosip.registration.processor.abis.dto.IdentityResponceDto;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

@Service
public class AbisServiceImpl {

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;
	
	private static final String DUPLICATE = "duplicate";

	public AbisInsertResponceDto insert(AbisInsertRequestDto abisInsertRequestDto) {
		AbisInsertResponceDto abisInsertResponceDto = new AbisInsertResponceDto();
		abisInsertResponceDto.setFailureReason(1);
		abisInsertResponceDto.setId("insert");
		abisInsertResponceDto.setTimestamp("1539777717");
		abisInsertResponceDto.setReturnValue("1");
		abisInsertResponceDto.setRequestId("01234567-89AB-CDEF-0123-456789ABCDEF");
		return abisInsertResponceDto;
	}

	public IdentityResponceDto performDedupe(IdentityRequestDto identityRequest) throws ApisResourceAccessException,
			IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
		boolean duplicate = false;
		int count = 0;
		String referenceId = identityRequest.getReferenceId();
	//	String regId = packetInfoManager.getRidByReferenceId(referenceId).get(0);
		List<String> pathSegments = new ArrayList<>();
	//	pathSegments.add(regId);

		//byte[] bytefile = (byte[]) restClientService.getApi(ApiName.BIODEDUPE, pathSegments, "", "",
				//byte[].class);

		ClassLoader classLoader = getClass().getClassLoader();
		File testCbeff = new File(classLoader.getResource("TestCbeff.xml").getFile());
		//File testCbeff = new File("TestCbeff.xml");
		//try (FileOutputStream fos = new FileOutputStream(testCbeff)) {
		//	fos.write(bytefile);
		//}

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(testCbeff);
		
		NodeList fingerNodeList = doc.getElementsByTagName("TestFingerPrint");
		for(int i = 0; i<fingerNodeList.getLength(); i++) {
			String value = fingerNodeList.item(i).getTextContent();
			if(value.equalsIgnoreCase(DUPLICATE)) {
				duplicate = true;
				break;
			}
		}
		
		NodeList irisNodeList = doc.getElementsByTagName("TestIRIS");
		for(int i = 0; i<irisNodeList.getLength(); i++) {
			String value = irisNodeList.item(i).getTextContent();
			if(value.equalsIgnoreCase(DUPLICATE)) {
				duplicate = true;
				break;
			}
		}
		
		NodeList faceNodeList = doc.getElementsByTagName("TestFace");
		for(int i = 0; i<faceNodeList.getLength(); i++) {
			String value = faceNodeList.item(i).getTextContent();
			if(value.equalsIgnoreCase(DUPLICATE)) {
				duplicate = true;
				break;
			}
		}
		
		IdentityResponceDto response = new IdentityResponceDto();
		response.setId("Identify");
		response.setRequestId(identityRequest.getRequestId());
		response.setTimestamp(identityRequest.getTimestamp());
		response.setReturnValue(1);
		
		if (duplicate) {
			CandidateListDto cd = new CandidateListDto();
			CandidatesDto[] candidatesDto = new CandidatesDto[10];
			for (int i = 1; i <= identityRequest.getMaxResults(); i++) {
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

}
