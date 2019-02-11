package io.mosip.registration.test.integrationtest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.util.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.PacketHandlerService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {AppConfig.class,DaoConfig.class})
public class PacketHandlerServiceTest {
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
		ObjectMapper mapper= new ObjectMapper();
		mapper.registerModule(new JSR310Module());
		RegistrationDTO obj = mapper.readValue(new File("user.json"), RegistrationDTO.class);
        byte[] data=IOUtils.toByteArray(new FileInputStream(new File("src/test/resources/testData/PacketHandlerServiceData/PANStubbed.jpg")));
        DemographicDTO documentDetails=obj.getDemographicDTO();
        DocumentDetailsDTO documentDetailsDTO = documentDetails.getDemographicInfoDTO().getIdentity().getProofOfIdentity();
        documentDetailsDTO.setDocument(data);
        documentDetailsDTO = documentDetails.getDemographicInfoDTO().getIdentity().getProofOfAddress();
        documentDetailsDTO.setDocument(data);
        documentDetailsDTO = documentDetails.getDemographicInfoDTO().getIdentity().getProofOfRelationship();
        documentDetailsDTO.setDocument(data);
        documentDetailsDTO = documentDetails.getDemographicInfoDTO().getIdentity().getProofOfDateOfBirth();
        documentDetailsDTO.setDocument(data);
        RegistrationCenterDetailDTO registrationCenter= new RegistrationCenterDetailDTO();
        registrationCenter.setRegistrationCenterId("20916");
        SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
        SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().setMapObject(new HashMap<String,Object>());
		
		ResponseDTO response=packetHandlerService.handle(obj);
		
		
		String jsonInString = mapper.writeValueAsString(response);
		System.out.println(jsonInString);
		Assert.assertEquals(response.getSuccessResponseDTO().getCode().toString(),"0000");
		Assert.assertEquals(response.getSuccessResponseDTO().getMessage().toString(), "Success");
	}
}
