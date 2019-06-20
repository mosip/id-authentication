package io.mosip.registration.test.integrationtest;

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
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.poi.util.IOUtils;
import org.eclipse.jdt.internal.core.UserLibrary;
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
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.IndividualIdentity;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.sync.PacketSynchService;

/**
 * @author Leona Mary S
 *
 *         Validating whether Packet upload service is working as expected for
 *         invalid and valid inputs
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PacketUploadServiceTest extends BaseIntegrationTest {

	PacketSynchServiceTest PacketSyncTest = new PacketSynchServiceTest();

	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	PacketHandlerService Phandlerservice;
	@Autowired
	PacketSynchService PsyncService;
	@Autowired
	private RidGenerator<String> ridGeneratorImpl;
	@Autowired
	private GlobalParamService globalParamService;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	RegistrationDAO regDAO;
	@Autowired
	PacketUploadService PUploadservice;

	private static Properties prop = DBUtil.loadPropertiesFile();
	static List<String> a = new ArrayList<String>(100);

	@BeforeClass
	public static void getdbdata() {
		System.out.println("------- Before Class ----");
		DBUtil.createConnection();
		a = DBUtil.get_selectQuery(prop.getProperty("GET_SYNC_PACKETIDs"));
	}

	@Before
	public void SetUp() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());
		// SessionContext.getInstance().getUserContext().setUserId("110011");
	}

	@Test
	public void validate_getSynchedPackets_1() {

		System.out.println("Test case 1");
		List<String> actualres = a;
		List<String> expectedres = new ArrayList<String>(100);
		// Fetching Data from database through JAVA API
		List<Registration> details = PUploadservice.getSynchedPackets();
		// System.out.println("==== "+details.size());
		for (int i = 0; i < details.size(); i++) {
			// System.out.println("==== "+details.get(i).getId());
			expectedres.add(details.get(i).getId());
		}
		for (String i : actualres) {
			if (expectedres.contains(i)) {
				System.out.println("Packet ID fetched from Local database through API is equal");
				break;
			}
		}
	}

	@Test
	public void validate_pushPacket_2() {
		System.out.println("Test case 2");

		String expectedmsg = "Success";
		String RID = "";
		try {
			RID = testHandelPacket(RegistrationClientStatusCode.APPROVED.getCode());
			PsyncService.packetSync(RID);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File fileData = new File("..//PacketStore/20-Mar-2019/" + RID + ".zip");
		try {
			ResponseDTO res = PUploadservice.pushPacket(fileData);
			String actualmsg = res.getSuccessResponseDTO().getCode();
			// Map<String, Object> m1 = (Map<String, Object>) res;
			// Map<String,String>m2=(Map<String, String>) m1.get("error");
			// String actualmsg = res.get("response");
			assertEquals(expectedmsg, actualmsg);
		} catch (RegBaseCheckedException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) { // TODO Auto-generated
			e.printStackTrace();
		}
	}

	@Test
	public void validate_pushPacket_Neg_6() {

		System.out.println("Test case 6");

		// String expectedmsg="Registration packet is not in Sync with Sync table";
		String expectedmsg = "ERROR";
		File fileData = new File(
				"src/test/resources/testData/PacketUploadServiceData/10031100110016820190225124201.zip");
		try {

			ResponseDTO res = PUploadservice.pushPacket(fileData);
			// Map<String,Object> m1=(Map<String, Object>)res;
			// Map<String,String>m2=(Map<String, String>) m1.get("error");
			String actualmsg = res.getErrorResponseDTOs().get(0).getCode();
			assertEquals(expectedmsg, actualmsg);
		} catch (RegBaseCheckedException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) { // TODO Auto-generated
			e.printStackTrace();
		}
	}

	@Test
	public void validate_updateStatus_3() {
		System.out.println("Test case 3");
		List<Registration> details = PUploadservice.getSynchedPackets();

		List<PacketStatusDTO> packetDto = new ArrayList<>();
		for (Registration reg : details) {
			packetDto.add(packetStatusDtoPreperation(reg));
		}

		Boolean res = PUploadservice.updateStatus(packetDto);
		System.out.println("validate_updateStatus== " + res);

	}

	@Test
	public void validate_uploadPacket_4() {
		String RID = "";
		try {
			RID = testHandelPacket(RegistrationClientStatusCode.APPROVED.getCode());
			PsyncService.packetSync(RID);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PUploadservice.uploadPacket(RID);

	}

	@Test

	public void zvalidate_uploadEODPackets_5() {

		List<String> regIdsData = new ArrayList<String>();
		String RID = "";
		for (int i = 0; i < 3; i++) {
			try {
			RID = testHandelPacket(RegistrationClientStatusCode.APPROVED.getCode());
			PsyncService.packetSync(RID);
			regIdsData.add(RID);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		//Static data for the method
/*		try {
			regIdsData = PacketUploadServiceTest.testData(
					"src/test/resources/testData/PacketUploadServiceData/PacketUploadService_syncEODPackets_regIds.json");
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
		PUploadservice.uploadEODPackets(regIdsData);

	}

	public static List<String> testData(String Path) throws IOException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader(Path);
		{
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONArray jArray = (JSONArray) obj;

			String s = jArray.toString();

			List<String> regIds_data = mapper.readValue(s,
					mapper.getTypeFactory().constructCollectionType(List.class, String.class));
			return regIds_data;
		}
	}

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

	/**
	 * Convertion of Registration to Packet Status DTO
	 * 
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

}