package io.mosip.registration.test.integrationtest;

import io.mosip.registration.dto.SyncRegistrationDTO;
import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationPacketSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.IndividualIdentity;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.sync.PacketSynchService;
import javafx.beans.property.BooleanProperty;

/**
 * @author Leona Mary S
 *
 *         Validating whether Packet Sync service is working as expected for
 *         invalid and valid inputs
 */
@SuppressWarnings("deprecation")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PacketSynchServiceTest extends BaseIntegrationTest {
	@Autowired
	private RegistrationRepository registrationRepository;
	@Autowired
	PacketSynchService PsyncService;

	@Autowired
	private RegistrationDAO registrationDAO;
	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	PacketHandlerService Phandlerservice;
	@Autowired
	private RidGenerator<String> ridGeneratorImpl;
	@Autowired
	private GlobalParamService globalParamService;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	RegistrationDAO regDAO;

	private static Properties prop = DBUtil.loadPropertiesFile();
	static List<String> a = new ArrayList<String>(100);
	static List<String> b = new ArrayList<String>(500);

	@BeforeClass
	public static void getdbdata() {
		System.out.println("------- Before Class ----");
		DBUtil.createConnection();
		a = DBUtil.get_selectQuery(prop.getProperty("GET_SYNC_PACKETIDs"));
		//b=DBUtil.get_selectQuery(prop.getProperty("GET_PACKETIDs"));
	}

	@Before
	public void SetUp() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());

	}

	@Test
	public void validate_fetchPacketsToBeSynched_1() {
		System.out.println("Test case 1");
		List<String> actualres = a;
		List<String> expectedres = new ArrayList<String>(100);
		// Fetching Data from database through JAVA API
		List<PacketStatusDTO> details = PsyncService.fetchPacketsToBeSynched();
		for (int i = 0; i < details.size(); i++) {
			expectedres.add(details.get(i).getFileName());
		}
		for (String i : actualres) {
			if (expectedres.contains(i)) {
				System.out.println("Packet ID fetched from Local database through API is equal");
				break;
			}
		}}

	@Test
	public void validate_packetSync_2() {
		System.out.println("Test case 2");
		try {
			String expectedmsg="success";
			String actualmsg=null;
			String RID = testHandelPacket(RegistrationClientStatusCode.APPROVED.getCode());
			String res = PsyncService.packetSync(RID);
			if (res.isEmpty()) {
				actualmsg="success";
				System.out.println("Packet is synched successfully");
				assertEquals(expectedmsg,actualmsg);
			} else {

				System.out.println("packetSync is failed due to " + res);

			}
		} catch (RegBaseCheckedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void validate_updateStatus_4() {
		System.out.println("Test case 4");
		Boolean expectedval = true;
		List<PacketStatusDTO> details = PsyncService.fetchPacketsToBeSynched();
		Boolean actualval = PsyncService.updateSyncStatus(details);
		assertEquals(expectedval, actualval);
	}

	@Test
	public void validate_syncEODPackets_3() {
		System.out.println("Test case 3");
		List<String> regIds = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			try {
				String RID = testHandelPacket(RegistrationClientStatusCode.APPROVED.getCode());
				regIds.add(RID);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("regIds== " + regIds.size());
		/*
		 * try { regIds = PacketSynchServiceTest.gettestData(
		 * "src/test/resources/testData/PacketSynchServiceData/PacketSynchService_syncEODPackets_regIds.json"
		 * ); } catch (IOException | ParseException e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); }
		 */
String expectedmsg="success";
String actualmsg=null;
		try {
			String res = PsyncService.syncEODPackets(regIds);
			if (res.isEmpty()) {
				actualmsg="success";
				assertEquals(expectedmsg,actualmsg);
				System.out.println("Packet is synched successfully");
			} else {
				System.out.println("packetSync is failed due to " + res);
			}
		} catch (RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Test
	public void zvalidate_syncPacketsToServer_6() {
		System.out.println("Test case 6");
		RegistrationPacketSyncDTO dtoList = new RegistrationPacketSyncDTO();
		ResponseDTO response = new ResponseDTO();
		String expectedmsg = "success";

		// try {
		dtoList = dtoList_negative();// (RegistrationPacketSyncDTO)
										// testData("src/test/resources/testData/PacketSynchServiceData/dtoList_negative.json");
		// } catch (IOException | ParseException e1) {
		// TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		try {
			response = PsyncService.syncPacketsToServer("System", "System");
			//System.out.println(response.getErrorResponseDTOs().get(0));
			// Map<String,String>m2=(Map<String, String>) m1.get("error");
			// String actualmsg=(String) m1.get("response");
			// assertEquals(expectedmsg, actualmsg);

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
	public void zvalidate_syncPacketsToServer_5() {
		System.out.println("Test case 5");
		RegistrationPacketSyncDTO dtoList = new RegistrationPacketSyncDTO();
		ResponseDTO response = new ResponseDTO();
		boolean expectedmsg = true;
		// static data used from Json
		// (RegistrationPacketSyncDTO)
		// testData("src/test/resources/testData/PacketSynchServiceData/PacketSyncService__syncPacketsToServer_syncDtoList.json");
		// Implemented dynamic test data creation
		dtoList = syncdatatoserver_Testdata();
		try {
			response = PsyncService.syncPacketsToServer("System", "System");
			Map<String, Object> m1 = response.getSuccessResponseDTO().getOtherAttributes();
			System.out.println(m1.size());
			boolean status = false;
			String key="";
			
			for(Map.Entry entry: m1.entrySet()){
	          //  if(value.equals(entry.getValue())){
	               key = (String) entry.getKey();
	               b.add(key); 
	               //    break; //breaking because its one to one map
	            //}
	        }
			System.out.println(b.size());
		for (int i = 0; i < m1.size(); i++) {
				if (m1.get(b.get(i)).equals("SUCCESS")) {
					status = true;
				}
				
			}
			System.out.println(status);
			assertEquals(expectedmsg, status);
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

	public RegistrationPacketSyncDTO syncdatatoserver_Testdata() {
		for (int i = 0; i < 3; i++) {
			try {
				String RID = testHandelPacket(RegistrationClientStatusCode.APPROVED.getCode());
				System.out.println(RID);
				a.add(RID);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<PacketStatusDTO> packetsToBeSynched =PsyncService.fetchPacketsToBeSynched();
				//registrationDAO
				//.getPacketsToBeSynched(RegistrationConstants.PACKET_STATUS);
		
	//	List<PacketStatusDTO> packetDto = new ArrayList<>();
		//List<PacketStatusDTO> synchedPackets = new ArrayList<>();
		//for (Registration reg : packetsToBeSynched) {
			//packetDto.add(packetStatusDtoPreperation(reg));
		//}
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		if (!packetsToBeSynched.isEmpty()) {
			for (PacketStatusDTO packetToBeSynch : packetsToBeSynched //packetDto
					
					) {
				SyncRegistrationDTO syncDto = new SyncRegistrationDTO();
				syncDto.setLangCode("ENG");
				syncDto.setRegistrationType(packetToBeSynch.getPacketClientStatus() + " " + "-" + " "
						+ packetToBeSynch.getClientStatusComments());
				syncDto.setRegistrationId(packetToBeSynch.getFileName());
				syncDtoList.add(syncDto);
			}
			registrationPacketSyncDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
			registrationPacketSyncDTO.setSyncRegistrationDTOs(syncDtoList);
			registrationPacketSyncDTO.setId(RegistrationConstants.PACKET_SYNC_STATUS_ID);
			registrationPacketSyncDTO.setVersion(RegistrationConstants.PACKET_SYNC_VERSION);
		}
		return registrationPacketSyncDTO;
	}

	public RegistrationPacketSyncDTO dtoList_negative() {
		
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		SyncRegistrationDTO syncDto = new SyncRegistrationDTO();
		syncDto.setLangCode("ENG");
		syncDto.setRegistrationType("APPROVED - null");
		syncDto.setRegistrationId("100111001100053201903190727");
		syncDtoList.add(syncDto);
		//registrationPacketSyncDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
		registrationPacketSyncDTO.setSyncRegistrationDTOs(syncDtoList);
		registrationPacketSyncDTO.setId(RegistrationConstants.PACKET_SYNC_STATUS_ID);
		registrationPacketSyncDTO.setVersion(RegistrationConstants.PACKET_SYNC_VERSION);

		return registrationPacketSyncDTO;

	}
	/**
	 * Convertion of Registration to Packet Status DTO
	 * @param registration
	 * @return
	 */
	public PacketStatusDTO packetStatusDtoPreperation(Registration registration) {
		PacketStatusDTO statusDTO = new PacketStatusDTO();
		statusDTO.setFileName(registration.getId());
		statusDTO.setPacketClientStatus(registration.getClientStatusCode());
		statusDTO.setPacketPath(registration.getAckFilename());
		statusDTO.setPacketServerStatus(registration.getServerStatusCode());
		statusDTO.setUploadStatus(registration.getFileUploadStatus());
		return statusDTO;
	}


	/*	public static List<SyncRegistrationDTO> testData(String Path) throws IOException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader(Path);
		{
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			// JSONArray jArray = (JSONArray) obj;

			String s = obj.toString();

			List<SyncRegistrationDTO> SyncRegData = mapper.readValue(s,
					mapper.getTypeFactory().constructCollectionType(List.class, SyncRegistrationDTO.class));
			return SyncRegData;
		}
	}

	public static List<String> gettestData(String Path) throws IOException, ParseException {

		ObjectMapper mapper = new ObjectMapper();
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader(Path);
		{
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONArray jArray = (JSONArray) obj;

			String s = jArray.toString();

			List<String> getIds = mapper.readValue(s,
					mapper.getTypeFactory().constructCollectionType(List.class, String.class));
			return getIds;
		}

	}*/

	public String testHandelPacket(String Status_code) throws JsonParseException, JsonMappingException, IOException {
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
		registrationCenter.setRegistrationCenterId("20916");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
		SessionContext.getInstance().getUserContext().setUserId("110011");
		SessionContext.getInstance().setMapObject(new HashMap<String, Object>());
		String expectedCenterID = null;
		String expectedStatinID = null;
		Map<String, String> getres = userOBservice.getMachineCenterId();
		Set<Entry<String, String>> hashSet = getres.entrySet();
		for (Entry entry : hashSet) {

			if (entry.getKey().equals(IntegrationTestConstants.centerID)) {
				expectedCenterID = entry.getValue().toString();
			} else {
				expectedStatinID = entry.getValue().toString();
			}

		}
		String RandomID = ridGeneratorImpl.generateId(expectedCenterID, expectedStatinID);
		System.out.println(RandomID);
		obj.setRegistrationId(RandomID);
		ResponseDTO response = packetHandlerService.handle(obj);
		String jsonInString = mapper.writeValueAsString(response);
		System.out.println(jsonInString);
		Assert.assertEquals(response.getSuccessResponseDTO().getCode().toString(), "0000");
		Assert.assertEquals(response.getSuccessResponseDTO().getMessage().toString(), "Success");
		String response_msg = response.getSuccessResponseDTO().getMessage().toString();
		if (response_msg.contains("Success")) {
			Registration regi = regDAO.getRegistrationById(RegistrationClientStatusCode.CREATED.getCode(), RandomID);
			System.out.println("beFORE=== " + regi.getClientStatusCode());

			regi.setClientStatusCode(Status_code);

			PacketStatusDTO packetStatusDTO = new PacketStatusDTO();

			/*
			 * private String sourcePath; private String fileName; private String
			 * packetClientStatus; private String packetServerStatus; private
			 * BooleanProperty status; private String packetPath;
			 */

			packetStatusDTO.setFileName(regi.getId());
			packetStatusDTO.setPacketClientStatus(Status_code);
			regDAO.updatePacketSyncStatus(packetStatusDTO);
			System.out.println("aFTER=== " + regi.getClientStatusCode());
		}
		return RandomID;
	}

}
