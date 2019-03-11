package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.poi.util.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationPacketSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.MoroccoIdentity;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.service.UserOnboardService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.sync.PacketSynchService;
/**
 * @author Leona Mary S
 *
 *Validating whether Packet Sync service is working as expected for invalid and valid inputs
 */
@SuppressWarnings("deprecation")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PacketSynchServiceTest extends BaseIntegrationTest{
	@Autowired
	private RegistrationRepository registrationRepository;
		@Autowired
		PacketSynchService PsyncService;
		@Autowired
		PacketHandlerService packetHandlerService;
		@Autowired 
		PacketHandlerService Phandlerservice;
		@Autowired
		private RidGenerator<String> ridGeneratorImpl;
		@Autowired
		private  GlobalParamService globalParamService;
		@Autowired
		UserOnboardService userOBservice;
		@Autowired
		RegistrationDAO regDAO;
		
		private static Properties prop = DBUtil.loadPropertiesFile();
		static List<String> a=new ArrayList<String>(100);
	
		@BeforeClass
		public static void getdbdata() {
			System.out.println("------- Before Class ----");
			DBUtil.createConnection();
			a=DBUtil.get_selectQuery(prop.getProperty("GET_SYNC_PACKETIDs"));
		}
	
		@Before
		public void SetUp()
		{		
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());
	
		}
		
		@Test
		public void validate_fetchPacketsToBeSynched_1()
		{
			
			System.out.println("Test case 1");
			List<String> actualres=a;
			List<String> expectedres=new ArrayList<String>(100);
 			//Fetching Data from database through JAVA API
			List<Registration> details=PsyncService.fetchPacketsToBeSynched();
			//System.out.println("==== "+details.size());
			for (int i = 0; i < details.size(); i++) {
				//System.out.println("==== "+details.get(i).getId());
				expectedres.add(details.get(i).getId());
			}
			 for (String i: actualres) {
		            if (expectedres.contains(i)) {
		                System.out.println("Packet ID fetched from Local database through API is equal");
		                break;
		            }
		
			 }}


		@Test
		public void validate_packetSync_2() {
			System.out.println("Test case 2");
			try {
				String res=PsyncService.packetSync("10031100110016920190225130805");
				if (res.isEmpty()) {
					System.out.println("Error while packet sync");
				}
				System.out.println("packetSync== "+res);
			} catch (RegBaseCheckedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		@Test public void validate_updateStatus_4() {
			System.out.println("Test case 4");
			Boolean expectedval=true;
			List<Registration> details = PsyncService.fetchPacketsToBeSynched();
			Boolean actualval=PsyncService.updateSyncStatus(details);
			System.out.println("validate_updateStatus== "+actualval);
			assertEquals(expectedval, actualval);
			
			}
			
		
		
			@Test
			public void validate_syncEODPackets_3()
			{
				System.out.println("Test case 3");
				List<String> regIds = null;
				try {
					regIds = PacketSynchServiceTest.gettestData("src/test/resources/testData/PacketSynchServiceData/PacketSynchService_syncEODPackets_regIds.json");
				} catch (IOException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					String res=PsyncService.syncEODPackets(regIds);
					if (res.isEmpty()) {
						System.out.println("Error while packet sync");
					}
					System.out.println("packetSync== "+res);
				} catch (RegBaseCheckedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
			
			@Test
			public void zvalidate_syncPacketsToServer_6()
				{
				System.out.println("Test case 6");
				RegistrationPacketSyncDTO dtoList=null;
				String expectedmsg="success";
				
				try {
					dtoList = (RegistrationPacketSyncDTO) testData("src/test/resources/testData/PacketSynchServiceData/PacketSyncService__syncPacketsToServer_syncDtoList_pos.json");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				try {
					Object response=PsyncService.syncPacketsToServer(dtoList);
					Map<String,Object> m1=(Map<String, Object>)response;
				//	Map<String,String>m2=(Map<String, String>) m1.get("error");
					String actualmsg=(String) m1.get("response");
					assertEquals(expectedmsg, actualmsg);
			
					
				} catch (RegBaseCheckedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
					
			}
		
		
			@Test
			public void zvalidate_syncPacketsToServer_5()
				{
				System.out.println("Test case 5");
				RegistrationPacketSyncDTO dtoList=null;
				String expectedmsg="Json Data Mapping Exception";
				
				try {
					dtoList = (RegistrationPacketSyncDTO) testData("src/test/resources/testData/PacketSynchServiceData/PacketSyncService__syncPacketsToServer_syncDtoList.json");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				try {
					Object response=PsyncService.syncPacketsToServer(dtoList);
					Map<String,Object> m1=(Map<String, Object>)response;
					Map<String,String>m2=(Map<String, String>) m1.get("error");
					String actualmsg=m2.get("message");
					assertEquals(expectedmsg, actualmsg);
			
					
				} catch (RegBaseCheckedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
					
			}
		
			
			
			
			public static List<SyncRegistrationDTO> testData(String Path) throws IOException, ParseException {
				
				ObjectMapper mapper = new ObjectMapper();
				JSONParser jsonParser = new JSONParser();
				FileReader reader = new FileReader(Path);
			    {
			        //Read JSON file
			        Object obj = jsonParser.parse(reader);

			        JSONArray jArray = (JSONArray) obj;
			      
			        String s=jArray.toString();
			        
			        List<SyncRegistrationDTO> SyncRegData = mapper.readValue(
			                s,mapper.getTypeFactory().constructCollectionType(
			                        List.class, SyncRegistrationDTO.class));
			     return SyncRegData;
			}}
			 
			    public static List<String> gettestData(String Path) throws IOException, ParseException {
					
					ObjectMapper mapper = new ObjectMapper();
					JSONParser jsonParser = new JSONParser();
					FileReader reader = new FileReader(Path);
				    {
				        //Read JSON file
				        Object obj = jsonParser.parse(reader);

				        JSONArray jArray = (JSONArray) obj;
				      
				        String s=jArray.toString();
				        
				        List<String> getIds = mapper.readValue(
				                s,mapper.getTypeFactory().constructCollectionType(
				                        List.class, String.class));
				     return getIds;
				}
			    
			}	
			    
			    
			    public String testHandelPacket(String Status_code) throws JsonParseException, JsonMappingException, IOException {
			    	ObjectMapper mapper = new ObjectMapper();
					mapper.registerModule(new JSR310Module());
					mapper.addMixInAnnotations(DemographicInfoDTO.class, DemographicInfoDTOMix.class);

					RegistrationDTO obj = mapper.readValue(new File("src/test/resources/testData/PacketHandlerServiceData/user.json"), RegistrationDTO.class);
					MoroccoIdentity identity = mapper.readValue(new File("src/test/resources/testData/PacketHandlerServiceData/identity.json"), MoroccoIdentity.class);
					
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
					String expectedCenterID=null;
					String expectedStatinID=null;
					Map<String,String> getres=userOBservice.getMachineCenterId();
					Set<Entry<String,String>> hashSet=getres.entrySet();
			        for(Entry entry:hashSet ) {

			        	if(entry.getKey().equals(IntegrationTestConstants.centerID))
			        	{
			        		expectedCenterID=entry.getValue().toString();
			        	}
			        	else {
							expectedStatinID=entry.getValue().toString();
						}
			    
			        	}
			String RandomID=ridGeneratorImpl.generateId(expectedCenterID,expectedStatinID);
			System.out.println(RandomID);
					obj.setRegistrationId(RandomID);
					

					ResponseDTO response = packetHandlerService.handle(obj);

					String jsonInString = mapper.writeValueAsString(response);
					System.out.println(jsonInString);
					Assert.assertEquals(response.getSuccessResponseDTO().getCode().toString(), "0000");
					Assert.assertEquals(response.getSuccessResponseDTO().getMessage().toString(), "Success");
					String response_msg=response.getSuccessResponseDTO().getMessage().toString();
					if (response_msg.contains("Success")) {
						Registration regi=regDAO.getRegistrationById(RegistrationClientStatusCode.CREATED.getCode(), RandomID);
						System.out.println("beFORE=== "+regi.getClientStatusCode());	
						
						regi.setClientStatusCode(Status_code);
						regDAO.updatePacketSyncStatus(regi);
						System.out.println("aFTER=== "+regi.getClientStatusCode());					
					}
					return RandomID;
					
					
				}

}
