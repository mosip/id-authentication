package io.mosip.registration.main;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.operator.UserDetailService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.operator.UserSaltDetailsService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.service.sync.PolicySyncService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.service.sync.impl.PublicKeySyncImpl;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.DBUtil;
import io.mosip.registration.util.DbQueries;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;;

public class PacketCreation extends BaseConfiguration {

	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	Environment env;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	CommonUtil commonUtil;
	@Autowired
	PublicKeySyncImpl publicKeySyncImpl;
	@Autowired
	GlobalParamService globalParamService;
	@Autowired
	UserDetailService userDetailService;
	@Autowired
	MasterSyncService masterSyncService;
	@Autowired
	UserSaltDetailsService userSaltDetailsService;
	@Autowired
	PolicySyncService policySyncService;
	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;
	@Autowired
	PreRegistrationDataSyncService preRegistrationDataSyncService;

	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	private static Logger logger = AppConfig.getLogger(PacketCreation.class);
	private static final String serviceName = "PacketHandlerService";
	private static final String subServiceName = "PreReg_RegClient_PacketCreation";
	private static final String testDataFileName = "PacketCreationTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";

	@DataProvider(name = "PacketHandlerDataProvider")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "regression");
	}

	@BeforeClass(alwaysRun = true)
	public void dataSetUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
		preRegistrationDataSyncService.getPreRegistrationIds(RegistrationConstants.JOB_TRIGGER_POINT_USER);
	}

	@Test(dataProvider = "PacketHandlerDataProvider", alwaysRun = true)
	public void validatePacketCreation(String testCaseName, JSONObject object) {
		logger.info("PACKET_HANDLER SERVICE WITH PRE REG TEST - ", APPLICATION_NAME, APPLICATION_ID, testCaseName);
		mTestCaseName = testCaseName;
		System.out.println("testCaseName==== " + testCaseName);
		try {
			Properties prop = commonUtil.readPropertyFile(serviceName + "/" + subServiceName, testCaseName,
					testCasePropertyFileName);
			SessionContext.map().put(RegistrationConstants.IS_Child, Boolean.parseBoolean(prop.getProperty("isChild")));
			ApplicationContext.map().put(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG,
					prop.getProperty("eodConfigFlag"));
			// Set Roles
			ArrayList<String> roles = new ArrayList<>();
			for (String role : prop.getProperty("roles").split(","))
				roles.add(role);
			SessionContext.userContext().setRoles(roles);
			// Fetch value from PreId.json
			HashMap<String, String> preRegIDs = commonUtil.getPreRegIDs();

			if (prop.getProperty("UniqueCBEFF").equalsIgnoreCase("YES")) {
				// Set CBEFF to UNIQUE
				ApplicationContext.map().put(RegistrationConstants.CBEFF_UNQ_TAG, ConstantValues.YES);
				ApplicationContext.map().put(RegistrationConstants.PACKET_STORE_LOCATION,
						// "src/main/resources/packets/UniqueCBEFF_Packets"
						prop.getProperty("UniqueCBEFF_path"));
			} else {
				// Set CBEFF to UNIQUE & DUPLICATE
				ApplicationContext.map().put(RegistrationConstants.CBEFF_UNQ_TAG, ConstantValues.NO);
				ApplicationContext.getInstance().map().put(RegistrationConstants.PACKET_STORE_LOCATION,
						// "src/main/resources/packets/DuplicateCBEFF_Packets");
						prop.getProperty("DuplicateCBEFF_path"));
			}
			
			String uin = dataGenerator.getYamlData(serviceName, testDataFileName, "parentUIN", prop.getProperty("UIN"));
			logger.info("PACKET_HANDLER SERVICE WITH PRE REG TEST - ", APPLICATION_NAME, APPLICATION_ID,
					"uin: " + uin);
			System.out.println("------------ " + uin);
			String statusCode = dataGenerator.getYamlData(serviceName, testDataFileName, "statusCode",
					prop.getProperty("CreatePacket"));
			logger.info("PACKET_HANDLER SERVICE WITH PRE REG TEST - ", APPLICATION_NAME, APPLICATION_ID,
					"StatusCode: " + statusCode);
			String biometricDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "bioPath",
					prop.getProperty("bioPath"));
			logger.info("PACKET_HANDLER SERVICE WITH PRE REG TEST - ", APPLICATION_NAME, APPLICATION_ID,
					"Resident Biometric data Path: " + biometricDataPath);
			System.out.println("==== Passport " + biometricDataPath);
			String demographicDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "demoPath",
					prop.getProperty("demoPath"));
			logger.info("PACKET_HANDLER SERVICE WITH PRE REG TEST - ", APPLICATION_NAME, APPLICATION_ID,
					"Resident Demographic data Path: " + demographicDataPath);
			System.out.println("==== Passport " + demographicDataPath);
			String proofImagePath = dataGenerator.getYamlData(serviceName, testDataFileName, "imagePath",
					prop.getProperty("imagePath"));
			logger.info("PACKET_HANDLER SERVICE WITH PRE REG TEST - ", APPLICATION_NAME, APPLICATION_ID,
					"Resident Proof data Path: " + proofImagePath);
			String packetType = prop.getProperty("PacketType");
			logger.info("PACKET_HANDLER SERVICE WITH PRE REG TEST - ", APPLICATION_NAME, APPLICATION_ID,
					"Type Of packet created using PreRegistraiton ID: " + packetType);

			HashMap<String, String> packetresponse;

			RegistrationDTO preRegistrationDTO = null;
			// creating RegistrationDTO
			commonUtil.createRegistrationDTOObject(ConstantValues.REGISTRATIONCATEGORY, centerID, stationID);

			if (!(packetType.equalsIgnoreCase("RegClientPacket"))) {
				// Get Pre Registration details
				preRegistrationDTO = commonUtil.getPreRegistrationDetails(preRegIDs.get(packetType));

			} else {
				preRegistrationDTO = (RegistrationDTO) SessionContext.map()
						.get(RegistrationConstants.REGISTRATION_DATA);
			}

			// Create Packet
			packetresponse = commonUtil.preRegPacketCreation(preRegistrationDTO, statusCode, biometricDataPath,
					demographicDataPath, proofImagePath, System.getProperty("userID"), centerID, stationID, packetType,uin);

			commonUtil.verifyAssertionResponse(prop.getProperty("ExpectedResponse"),
					packetresponse.get(prop.getProperty("AssertValue")));

			// Verify whether created packet exist in the local database
			System.out.println(packetresponse.get("RANDOMID"));
			boolean isPresentInDB = DBUtil.checkRegID(packetresponse.get("RANDOMID"), DbQueries.GET_PACKETIDs);
			Assert.assertEquals(isPresentInDB, true);
			logger.info("PACKET_HANDLER SERVICE WITH PRE REG TEST - ", APPLICATION_NAME, APPLICATION_ID,
					"Created Registration ID in database: " + isPresentInDB);
		} catch (NullPointerException nullPointerException) {

			logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
			Reporter.log(ExceptionUtils.getStackTrace(nullPointerException));
			nullPointerException.printStackTrace();
		}
	}

}
