package io.mosip.registration.test.integrationtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.util.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.MoroccoIdentity;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.PacketHandlerService;

@SuppressWarnings("deprecation")
public class PacketHandlerServiceTest extends BaseIntegrationTest {
	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	private GlobalParamService globalParamService;

	@Before
	public void setUp() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());

	}

	@Test
	public void testHandelPacket() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JSR310Module());
		mapper.addMixInAnnotations(DemographicInfoDTO.class, DemographicInfoDTOMix.class);

		RegistrationDTO obj = mapper.readValue(new File("src/test/resources/testData/PacketHandlerServiceData/user.json"), RegistrationDTO.class);
		MoroccoIdentity identity = mapper.readValue(new File("src/test/resources/testData/PacketHandlerServiceData/identity.json"), MoroccoIdentity.class);
		byte[] data = IOUtils.toByteArray(
				new FileInputStream(new File("src/test/resources/testData/PacketHandlerServiceData/PANStubbed.jpg")));
		DocumentDetailsDTO documentDetailsDTOIdentity = new DocumentDetailsDTO();
		documentDetailsDTOIdentity.setType("POI");
		identity.setProofOfIdentity(documentDetailsDTOIdentity);
		
		DocumentDetailsDTO documentDetailsDTOAddress = new DocumentDetailsDTO();
		documentDetailsDTOIdentity.setType("POA");
		identity.setProofOfIdentity(documentDetailsDTOIdentity);
		
		DocumentDetailsDTO documentDetailsDTORelationship = new DocumentDetailsDTO();
		documentDetailsDTOIdentity.setType("POR");
		identity.setProofOfIdentity(documentDetailsDTOIdentity);
		
		DocumentDetailsDTO documentDetailsDTODOB = new DocumentDetailsDTO();
		documentDetailsDTOIdentity.setType("PODOB");
		identity.setProofOfIdentity(documentDetailsDTOIdentity);
		identity.setProofOfAddress(documentDetailsDTOAddress);
		identity.setProofOfRelationship(documentDetailsDTORelationship);
		identity.setProofOfDateOfBirth(documentDetailsDTODOB);
		
		DocumentDetailsDTO documentDetailsDTO = identity.getProofOfIdentity();
		documentDetailsDTO.setDocument(data);
		documentDetailsDTO = identity.getProofOfAddress();
		documentDetailsDTO.setDocument(data);
		documentDetailsDTO = identity.getProofOfRelationship();
		documentDetailsDTO.setDocument(data);
		documentDetailsDTO = identity.getProofOfDateOfBirth();
		documentDetailsDTO.setDocument(data);
		RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
		registrationCenter.setRegistrationCenterId("20916");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().setMapObject(new HashMap<String, Object>());

		ResponseDTO response = packetHandlerService.handle(obj);

		String jsonInString = mapper.writeValueAsString(response);
		System.out.println(jsonInString);
		Assert.assertEquals(response.getSuccessResponseDTO().getCode().toString(), "0000");
		Assert.assertEquals(response.getSuccessResponseDTO().getMessage().toString(), "Success");
	}
}
