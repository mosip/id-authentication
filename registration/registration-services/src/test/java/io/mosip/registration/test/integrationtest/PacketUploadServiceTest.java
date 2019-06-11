package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import io.mosip.registration.dto.demographic.MoroccoIdentity;
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
	@Autowired
	CommonUtil commonUtil;

	IntegrationTestConstants integConstant = new IntegrationTestConstants();
	/**
	 * Declaring CenterID,StationID global
	 */
	private String centerID = null;
	private String stationID = null;
	
	private static Properties prop = DBUtil.loadPropertiesFile();
	static Set<String> dbData = new HashSet<String>(100);
	
	@BeforeClass
	public static void getdbdata() {
		System.out.println("------- Before Class ----");
		DBUtil.createConnection();
		dbData = DBUtil.get_selectQuery(prop.getProperty("GET_SYNC_PACKETIDs"));
		try {
			DBUtil.updateQuery(prop.getProperty("UPDATE_CR_BY"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Before
	public void SetUp() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());
		centerID = userOBservice.getMachineCenterId().get(integConstant.CENTERIDLBL);
		stationID = userOBservice.getMachineCenterId().get(integConstant.STATIONIDLBL);
				
	}

	@Test
	public void validate_getSynchedPackets_1() {

		System.out.println("Test case 1");
		Set<String> actualres = dbData;
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
			RID = commonUtil.packetCreation(RegistrationClientStatusCode.APPROVED.getCode(),
					integConstant.REGDETAILSJSON, integConstant.IDENTITYJSON, integConstant.POAPOBPORPOIJPG, 
					integConstant.USERIDVAL, centerID,
					stationID);
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
			RID = commonUtil.packetCreation(RegistrationClientStatusCode.APPROVED.getCode(),
					integConstant.REGDETAILSJSON, integConstant.IDENTITYJSON, integConstant.POAPOBPORPOIJPG, 
					integConstant.USERIDVAL, centerID,
					stationID);
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
				RID =commonUtil.packetCreation(RegistrationClientStatusCode.APPROVED.getCode(),
						integConstant.REGDETAILSJSON, integConstant.IDENTITYJSON, integConstant.POAPOBPORPOIJPG, 
						integConstant.USERIDVAL, centerID,
						stationID);
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
		// Static data for the method
		/*
		 * try { regIdsData = PacketUploadServiceTest.testData(
		 * "src/test/resources/testData/PacketUploadServiceData/PacketUploadService_syncEODPackets_regIds.json"
		 * ); } catch (IOException | ParseException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
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