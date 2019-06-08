/*package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testng.Assert.assertFalse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

//import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
//import org.testng.annotations.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.service.UserOnboardService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.test.integrationtest.util.TestCaseReader;
import io.mosip.registration.test.integrationtest.util.TestDataGenerator;

*//**
 * @author Leona Mary S
 * 
 *         M1050139
 * 
 *         Class for validating Integration Scenario is working as expected for
 *         following services LoginService, UserOnboardService,
 *         PacketHandlerService, PacketSynchService, PacketUploadService,
 *         RegPacketStatusService.
 *//*
@ContextConfiguration(classes = { AppConfig.class, DaoConfig.class })
@TestPropertySource("classpath:/integrationTesting/integ-scenario-config.properties")
public class IntegrationScenario_UserOnboard_PacketDeletion extends AbstractTestNGSpringContextTests {

	// public class IntegrationScenario_UserOnboard_PacketDeletion extends
	// BaseIntegrationTest {

	@Autowired
	private Environment env;
	*//**
	 * Service for Login
	 **//*
	@Autowired
	LoginService loginService;

	*//**
	 * Service to create a packet
	 **//*
	@Autowired
	PacketHandlerService packetHandlerService;

	*//**
	 * Service to Upload the packets
	 **//*
	@Autowired
	PacketUploadService packetUploadservice;

	*//**
	 * Service for Useronboard of RO/Supervisor
	 **//*
	@Autowired
	UserOnboardService userOBservice;

	*//**
	 * Service for synching the packet
	 **//*
	@Autowired
	PacketSynchService psyncService;

	*//**
	 * Class contains common methods used across various test cases
	 **//*
	@Autowired
	CommonUtil commonUtil;

	@Autowired
	private GlobalParamService globalParamService;
	
	*//**
	 * Class to retrieve the Registration packet details from DB
	 *//*
	@Autowired
	RegistrationDAO regDAO;

	*//**
	 * Class to retrieve the the Registration Packet Status
	 *//*
	@Autowired
	RegPacketStatusService regPacket;

	*//** The registration repository. *//*
	@Autowired
	private RegistrationRepository registrationRepository;

	*//**
	 * Instance of LOGGER
	 *//*
	private static final Logger LOGGER = AppConfig.getLogger(IntegrationScenario_UserOnboard_PacketDeletion.class);

	IntegrationTestConstants integconstant=new IntegrationTestConstants();	

	*//**
	 * Declaring CenterID,StationID global
	 *//*
	private String centerID = null;
	private String stationID = null;
	private final String module = "integrationTesting";
	private final String funcationality = "scenario1";
	private final String masterDataFileName = "testData";

	@DataProvider(name = "scenario1-test-cases")
	public Object[][] readTestCase(ITestContext context) {
		// String testType = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";

		return (TestCaseReader.testCaseNames(env, testType, "scenario1_testCaseName"));
	}

	*//**
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *//*

	@Test(dataProvider = "scenario1-test-cases")
	public void UserOnboardPacketCreation(String testCaseName)
			throws InterruptedException, ParseException, JsonParseException, JsonMappingException, IOException {
		try {
			TestDataGenerator testDataGenerator = new TestDataGenerator();
			String password = null;
			String userJsonFile = null;
			String identityJsonFile = null;
			String proofOfIdentity = null;
			String userName = null;
			String packetId = null;
			String packetSyncResult = null;
			List<String> loginList = new ArrayList<String>();
			switch (testCaseName) {

			case "Valid_ROUserOnboard_ResidentPacketCreation":

				password = testDataGenerator.getYamlData(module, funcationality, masterDataFileName, "password_valid");
				userJsonFile = testDataGenerator.getYamlData(module, funcationality, masterDataFileName,
						"userJsonFile_valid");
				identityJsonFile = testDataGenerator.getYamlData(module, funcationality, masterDataFileName,
						"identityJsonFile_valid");
				proofOfIdentity = testDataGenerator.getYamlData(module, funcationality, masterDataFileName,
						"proofOfIdentity_valid");
				userName = commonUtil.getRandomValue(Integer.parseInt(env.getProperty("userNameLength")));
				preSetUp(userName, Integer.parseInt(env.getProperty("userOnBoardThresholdLimit")));
				commonUtil.userOnboardDBUpdate(userName);
				loginList = commonUtil.verifyUserName(userName);
				if (!loginList.isEmpty()) {
					if (loginList.contains("PWD")) {
						commonUtil.validateModeOfLogin(loginList, userName, password, "", "", "", "");
					}
				} else {
					Assert.assertTrue(false);
				}

				if (validateCreateSyncUploadPacket(packetId, userJsonFile, identityJsonFile, proofOfIdentity, userName,
						centerID, stationID, packetSyncResult).equals(env.getProperty("packetFuntionalitySucess")))
					assertTrue(true);
				else
					assertFalse(false);

				break;

			case "InvalidPassword_Valid_ROUserOnboard":
				System.out.println("------------------------------" + testCaseName + "------------------------------");
				password = testDataGenerator.getYamlData(module, funcationality, masterDataFileName,
						"password_invalid");
				userName = commonUtil.getRandomValue(Integer.parseInt(env.getProperty("userNameLength")));
				preSetUp(userName, Integer.parseInt(env.getProperty("userOnBoardThresholdLimit")));
				commonUtil.userOnboardDBUpdate(userName);
				loginList = commonUtil.verifyUserName(userName);
				if (!loginList.isEmpty()) {
					if (loginList.contains("PWD")) {
						commonUtil.validateModeOfLogin(loginList, userName, password, "", "", "", "");
					}
				} else {
					Assert.assertTrue(false);
				}
				break;

			case "Valid_UserOnboard_PacketCreation_UploadEOD_Process_Y":
				System.out.println("------------------------------" + testCaseName + "------------------------------");
				password = testDataGenerator.getYamlData(module, funcationality, masterDataFileName, "password_valid");

				break;
			default:
				System.out.println("Default switch");
				break;
			}
		} catch (

				Exception e) {
			e.printStackTrace();
		}

	}

	*//**
	 * @param userID
	 *            randomly generated UserID for UserOnboard
	 * @param userThresholdLimit
	 *            setting the value for UserOnboardLimit
	 *//*
	public void preSetUp(String userID, int userThresholdLimit) {
		*//**
		 * get CenterID,StationID using UserOnboardService from Local database
		 *//*
		centerID = userOBservice.getMachineCenterId().get(integconstant.CENTERIDLBL);
		stationID = userOBservice.getMachineCenterId().get(integconstant.STATIONIDLBL);
		RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
		registrationCenter.setRegistrationCenterId(centerID);
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
		SessionContext.userContext().setUserId(userID);
		ApplicationContext.map().put(RegistrationConstants.USER_CENTER_ID, centerID);
		ApplicationContext.map().put(RegistrationConstants.USER_ON_BOARD_THRESHOLD_LIMIT,
				integconstant.USERONBOARDTHRESHOLDLIMITVAL);
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());

	}

	*//**
	 * this method return the packet create,sync,upload status
	 * 
	 * @param packetId
	 * @param userJsonFile
	 * @param identityJsonFile
	 * @param proofOfIdentity
	 * @param userName
	 * @param centerID2
	 * @param stationID2
	 * @param packetSyncResult
	 * @return
	 *//*
	public String validateCreateSyncUploadPacket(String packetId, String userJsonFile, String identityJsonFile,
			String proofOfIdentity, String userName, String centerID2, String stationID2, String packetSyncResult)
					throws JsonParseException, JsonMappingException, IOException, RegBaseCheckedException {
		packetId = commonUtil.packetCreation(RegistrationClientStatusCode.APPROVED.getCode(), userJsonFile,
				identityJsonFile, proofOfIdentity, userName, centerID, stationID);
		if (packetId == null)
			return env.getProperty("packetCreationFailed");
		packetSyncResult = psyncService.packetSync(packetId);
		if (packetSyncResult.isEmpty()) {

			packetUploadservice.uploadPacket(packetId);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				ResponseDTO responseDTO = regPacket.packetSyncStatus(RegistrationConstants.JOB_TRIGGER_POINT_USER);
			if (responseDTO.getSuccessResponseDTO().getMessage().contains("PACKET_STATUS_SYNC_SUCCESS_MESSAGE")) {
				assertEquals("PACKET_STATUS_SYNC_SUCCESS_MESSAGE", responseDTO.getSuccessResponseDTO().getMessage());

			} else if ((responseDTO.getSuccessResponseDTO().getMessage().contains("DECRYPTION_FAILED")))
				return env.getProperty("packetDecryptionFailed");

			else if ((responseDTO.getSuccessResponseDTO().getMessage().contains("STRUCTURE_VALIDATION_FAILED")))

				return env.getProperty("structureValidationFailed");
			else if ((responseDTO.getSuccessResponseDTO().getMessage().contains("PACKET_UPLOADED_TO_VIRUS_SCAN")))
				return env.getProperty("packetUploadToVirusFailed");

			System.out.println(responseDTO.getSuccessResponseDTO().getMessage());
			
			 * else {
			 * 
			 * System.out.println(responseDTO.getErrorResponseDTOs().get(0).getMessage());
			 * assertTrue(false); }
			 
		} else {
			System.out.println(packetSyncResult);
			return env.getProperty("packectSyncFailed");
		}
		return env.getProperty("packetFuntionalitySucess");

	}
}
*/