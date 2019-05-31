package io.mosip.registration.test.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.poi.util.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.IndividualIdentity;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.packet.PacketUploadService;


@SuppressWarnings("deprecation")
public class IntegrationScenario02_NewRegistrationFlow extends BaseIntegrationTest
{
	
	@Autowired
	LoginService loginService;
	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	private GlobalParamService globalParamService;
	@Autowired
	private RidGenerator<String> ridGeneratorImpl;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	PacketUploadService PacketUploadservice;
	
	//////////////////////////////////////////login
	public void login() {
		// Get user Details
		UserDTO userDetail = loginService.getUserDetail("mosip");
		
		
				// Password check for login Check if Password is same
				String hashPassword = null;
				String password = "mosip";
				byte[] bytePassword = password.getBytes();
				hashPassword = HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytePassword));

				AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
				authenticationValidatorDTO.setUserId("mosip");
				authenticationValidatorDTO.setPassword(hashPassword);

//			     userDetail = loginService.getUserDetail(authenticationValidatorDTO.getUserId());
			     String passwordCheck="";
				if (userDetail.getUserPassword().getPwd().equals(authenticationValidatorDTO.getPassword())) {
					passwordCheck=RegistrationConstants.PWD_MATCH;
					
				} else {
					passwordCheck=RegistrationConstants.PWD_MISMATCH;
				}
				assertEquals(RegistrationConstants.PWD_MATCH, passwordCheck);
				System.out.println("login completed successfully");
	}
	
	////////////////////////////////////////////////////PacketHandler
	@Before
	public void setUp() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());

	}

	public String testHandelPacket() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JSR310Module());
		mapper.addMixInAnnotations(DemographicInfoDTO.class, DemographicInfoDTOMix.class);

		RegistrationDTO obj = mapper.readValue(
				new File("src/test/resources/testData/PacketHandlerServiceData/user.json"), RegistrationDTO.class);
		IndividualIdentity identity = mapper.readValue(
				new File("src/test/resources/testData/PacketHandlerServiceData/identity.json"), IndividualIdentity.class);

		byte[] data = IOUtils.toByteArray(
				new FileInputStream(new File("src/test/resources/testData/PacketHandlerServiceData/PANStubbed.jpg")));
		DocumentDetailsDTO documentDetailsDTOIdentity = new DocumentDetailsDTO();
		documentDetailsDTOIdentity.setType("POI");
		documentDetailsDTOIdentity.setFormat("format");
		documentDetailsDTOIdentity.setOwner("owner");

		DocumentDetailsDTO documentDetailsDTOAddress = new DocumentDetailsDTO();
		documentDetailsDTOAddress.setType("POA");
		documentDetailsDTOAddress.setFormat("format");
		documentDetailsDTOAddress.setOwner("owner");

		DocumentDetailsDTO documentDetailsDTORelationship = new DocumentDetailsDTO();
		documentDetailsDTORelationship.setType("POR");
		documentDetailsDTORelationship.setFormat("format");
		documentDetailsDTORelationship.setOwner("owner");

		DocumentDetailsDTO documentDetailsDTODOB = new DocumentDetailsDTO();
		documentDetailsDTODOB.setType("PODOB");
		documentDetailsDTODOB.setFormat("format");
		documentDetailsDTODOB.setOwner("owner");
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
		obj.getDemographicDTO().getDemographicInfoDTO().setIdentity(identity);
		RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
		registrationCenter.setRegistrationCenterId("20916");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().setMapObject(new HashMap<String, Object>());
		String CenterID=null;
		String StatinID=null;
		Map<String,String> getres=userOBservice.getMachineCenterId();
		Set<Entry<String,String>> hashSet=getres.entrySet();
        for(Entry entry:hashSet ) {

        	if(entry.getKey().equals(IntegrationTestConstants.centerID))
        	{
        		CenterID=entry.getValue().toString();
        	}
        	else {
				StatinID=entry.getValue().toString();
			}
    
        	}
        String RandomID=ridGeneratorImpl.generateId(CenterID,StatinID);
		obj.setRegistrationId(RandomID);
		ResponseDTO response = packetHandlerService.handle(obj);

		String jsonInString = mapper.writeValueAsString(response);
		System.out.println(jsonInString);
		Assert.assertEquals(response.getSuccessResponseDTO().getCode().toString(), "0000");
		Assert.assertEquals(response.getSuccessResponseDTO().getMessage().toString(), "Success");
		System.out.println("packet created successfully");
		return RandomID;
	}

	
	
	
	
	
	@Test
	public void testNewRegistrationFlow() throws InterruptedException, ParseException, JsonParseException, JsonMappingException, IOException {
		login();
		String packetId=testHandelPacket();
		System.out.println(packetId);
		//PacketUploadservice.uploadPacket(packetId);
		
	}
}
