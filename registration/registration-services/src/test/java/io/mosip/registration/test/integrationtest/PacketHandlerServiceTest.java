package io.mosip.registration.test.integrationtest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.util.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.MoroccoIdentity;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.operator.impl.UserOnboardServiceImpl;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

@SuppressWarnings("deprecation")
public class PacketHandlerServiceTest extends BaseIntegrationTest {
	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	private GlobalParamService globalParamService;
	@Autowired
	private RidGenerator<String> ridGeneratorImpl;
	@Autowired
	UserOnboardService userOBservice;
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

		RegistrationDTO obj = mapper.readValue(
				new File("src/test/resources/testData/PacketHandlerServiceData/user.json"), RegistrationDTO.class);
		MoroccoIdentity identity = mapper.readValue(
				new File("src/test/resources/testData/PacketHandlerServiceData/identity.json"), MoroccoIdentity.class);

		
		byte[] data = IOUtils.toByteArray(
				new FileInputStream(new File("src/test/resources/testData/PacketHandlerServiceData/PANStubbed.jpg")));
		
		DocumentDetailsDTO documentDetailsDTOIdentity = new DocumentDetailsDTO();
		documentDetailsDTOIdentity.setType("POI");
		documentDetailsDTOIdentity.setFormat("format");
		documentDetailsDTOIdentity.setOwner("owner");
		documentDetailsDTOIdentity.setValue("ProofOfIdentity");

		DocumentDetailsDTO documentDetailsDTOAddress = new DocumentDetailsDTO();
		documentDetailsDTOAddress.setType("POA");
		documentDetailsDTOAddress.setFormat("format");
		documentDetailsDTOAddress.setOwner("owner");
		documentDetailsDTOAddress.setValue("ProofOfAddress");
		
		DocumentDetailsDTO documentDetailsDTORelationship = new DocumentDetailsDTO();
		documentDetailsDTORelationship.setType("POR");
		documentDetailsDTORelationship.setFormat("format");
		documentDetailsDTORelationship.setOwner("owner");
		documentDetailsDTORelationship.setValue("ProofOfRelationship");
		
		DocumentDetailsDTO documentDetailsDTODOB = new DocumentDetailsDTO();
		documentDetailsDTODOB.setType("POB");
		documentDetailsDTODOB.setFormat("format");
		documentDetailsDTODOB.setOwner("owner");
		documentDetailsDTODOB.setValue("DateOfBirthProof");
		
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
		registrationCenter.setRegistrationCenterId("10011");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
		SessionContext.getInstance().getUserContext().setUserId("110011");
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
		RegistrationMetaDataDTO dto=new RegistrationMetaDataDTO();
		dto.setCenterId(CenterID);
		dto.setMachineId(StatinID);
		obj.setRegistrationMetaDataDTO(dto);
		
		
		try {
			String regDTO=mapper.writeValueAsString(obj.getRegistrationMetaDataDTO());
			File f= new File("registration.json");
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(regDTO);
			bw.flush();
			bw.close();
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO: handle exception
		}	
		

		ResponseDTO response = packetHandlerService.handle(obj);

		String jsonInString = mapper.writeValueAsString(response);
		
		System.out.println(jsonInString);
		Assert.assertEquals(response.getSuccessResponseDTO().getCode().toString(), "0000");
		Assert.assertEquals(response.getSuccessResponseDTO().getMessage().toString(), "Success");
	}
}