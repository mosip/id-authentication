package io.mosip.registration.test.integrationtest;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.poi.util.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.RegistrationPacketSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.MoroccoIdentity;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.login.LoginService; 
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.common.OTPManager;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

/**
 * @author M1050139
 *
 *         Class contains common methods used across various test cases
 */

@Component
public class CommonUtil extends BaseService {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(CommonUtil.class);

	private boolean isNewUser = false;
	/**
	 * Class to retrieve the the Registration Packet Status
	 */
	@Autowired
	PacketSynchService packetSyncService;
	@Autowired
	RegPacketStatusService regPacket;
	@Autowired
	LoginService loginService;
	@Autowired
	PacketHandlerService packetHandlerService;

	@Autowired
	RidGenerator<String> ridGeneratorImpl;

	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	PacketUploadService PacketUploadservice;
	@Autowired
	RegistrationDAO regDAO;
	@Autowired
	OTPManager otpManager;

	/**
	 * Class to load and access the properties file
	 */
	private static Properties prop = DBUtil.loadPropertiesFile();
	IntegrationTestConstants integconstant = new IntegrationTestConstants();

	boolean UserOnboard = true;
	final static String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	Map<String, Object> sessionContextMap = SessionContext.getInstance().getMapObject();

	////////////////////////////////////////// VALIDATE USER NAME
	/**
	 * @param userID To validate given User ID
	 * @return boolean Login_status
	 */
	public List<String> verifyUserName(String userID) {

		LOGGER.info("REGISTRATION - INTEGRATION SCENARIO", APPLICATION_NAME, APPLICATION_ID,
				"VERIFYING USER NAME IS ONBOARDED OR NOT");

		List<String> loginList = new ArrayList<>();
		loginList = validateUserId(userID);

		return loginList;

	}

	////////////////////////////////////////// UserOnboard
	/**
	 * @param UserID
	 */
	public void verifyUserOnboard(String UserID) {

		try {
			LOGGER.info("REGISTRATION - USERDETAIL - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
					"USER ONBOARD INITIATED");
			setFlag("N");

			String expectedmsg = integconstant.USERONBOARDSUCCESSMSG;
			BiometricDTO biodto = null;
			biodto = testData(integconstant.USERONBOARDVALIDATENULLPATH);
			ResponseDTO dto = userOBservice.validate(biodto);
			String actualmsg = dto.getSuccessResponseDTO().getMessage();

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param loginList
	 * @param userID
	 * @return
	 */
	public void validateModeOfLogin(List<String> LoginList, String UserID, String PWD, String OTP, String FP,
			String Iris, String Face) {

		Boolean UserOnboard_Status = (Boolean) sessionContextMap.get(RegistrationConstants.ONBOARD_USER);
		Boolean loginSuccess = false;
		for (int i = 0; i < LoginList.size(); i++) {
			if (LoginList.contains("PWD")) {
				LOGGER.info("REGISTRATION - USERDETAIL - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
						"LOGIN USING PASSWORD INITIATED");
				// Password check for login Check if Password is same
				String hashPassword = null;
				String password = integconstant.PASSWORD;
				byte[] bytePassword = password.getBytes();
				hashPassword = HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytePassword));

				AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
				authenticationValidatorDTO.setUserId(UserID);
				authenticationValidatorDTO.setPassword(hashPassword);

				String passwordCheck = "";
				// if
				// (userDetail.getUserPassword().getPwd().equals(authenticationValidatorDTO.getPassword()))
				// {
				if (PWD.equals(authenticationValidatorDTO.getPassword())) {
					passwordCheck = RegistrationConstants.PWD_MATCH;
					loginSuccess = true;

				} else {
					passwordCheck = RegistrationConstants.PWD_MISMATCH;
					loginSuccess = false;
				}
				// Assert.assertTrue(loginSuccess);
				LOGGER.info("REGISTRATION - USERDETAIL - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
						"LOGIN COMPLETED SUCCESSFULLY USING PASSWORD");
			}
			/*
			 * else if (LoginList.contains("OTP")) { //loginSuccess=true;
			 * 
			 * }
			 */

			if (loginSuccess) {
				if (UserOnboard_Status) {
					verifyUserOnboard(UserID);
				} else {
					LOGGER.info("REGISTRATION - USERDETAIL - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
							"User has already onboarded");

				}
			} else {
				Assert.assertTrue(loginSuccess);
			}

		}

	}

	// IntegrationTestConstants.UsernameOnboard
	/**
	 * @param UserID
	 * @return
	 */
	public List<String> validateUserId(String UserID) {

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
				"Validating Credentials entered through API ");
		List<String> loginList = new ArrayList<>();

		if (UserID.isEmpty()) {
			LOGGER.info("", "", "", "USERNAME_FIELD_EMPTY");

		} else {

			try {

				UserDetail userDetail = loginService.getUserDetail(UserID);

				if (userDetail.getId() == null) {
					LOGGER.info("", "", "", "UserDetail is Empty= " + userDetail.getId());
					return loginList;
				}

				String centerId = userOBservice.getMachineCenterId().get(RegistrationConstants.USER_CENTER_ID);

				if (userDetail != null
						&& userDetail.getRegCenterUser().getRegCenterUserId().getRegcntrId().equals(centerId)) {
					ApplicationContext.map().put(RegistrationConstants.USER_CENTER_ID, centerId);

					if (userDetail.getStatusCode().equalsIgnoreCase(RegistrationConstants.BLOCKED)) {
						LOGGER.info("", "", "", "BLOCKED_USER_ERROR");
					} else {

						// Set Dongle Serial Number in ApplicationContext Map
						for (UserMachineMapping userMachineMapping : userDetail.getUserMachineMapping()) {
							ApplicationContext.map().put(RegistrationConstants.DONGLE_SERIAL_NUMBER,
									userMachineMapping.getMachineMaster().getSerialNum());
						}

						Set<String> roleList = new LinkedHashSet<>();

						userDetail.getUserRole().forEach(roleCode -> {
							if (roleCode.getIsActive()) {
								roleList.add(String.valueOf(roleCode.getUserRoleID().getRoleCode()));
							}
						});

						LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
								"Validating roles");
						// Checking roles
						if (roleList.isEmpty() || !(roleList.contains(RegistrationConstants.OFFICER)
								|| roleList.contains(RegistrationConstants.SUPERVISOR)
								|| roleList.contains(RegistrationConstants.ADMIN_ROLE))) {
							// generateAlert(RegistrationConstants.ERROR,
							// RegistrationUIConstants.ROLES_EMPTY_ERROR);
							LOGGER.info("", "", "", "ROLES_EMPTY_ERROR");

						} else {

							ApplicationContext.map().put(RegistrationConstants.USER_STATION_ID,
									userOBservice.getMachineCenterId().get(RegistrationConstants.USER_STATION_ID));

							/*
							 * boolean status = getCenterMachineStatus(userDetail);
							 * 
							 * sessionContextMap.put(RegistrationConstants.ONBOARD_USER, !status);
							 * sessionContextMap.put(RegistrationConstants.ONBOARD_USER_UPDATE, false);
							 * loginList = status ?
							 * loginService.getModesOfLogin(ProcessNames.LOGIN.getType(), roleList) :
							 * loginService.getModesOfLogin(ProcessNames.ONBOARD.getType(), roleList);
							 */

							if (getCenterMachineStatus(userDetail)) {
								sessionContextMap.put(RegistrationConstants.ONBOARD_USER, isNewUser);
								System.out.println(sessionContextMap.get(RegistrationConstants.ONBOARD_USER));
								sessionContextMap.put(RegistrationConstants.ONBOARD_USER_UPDATE, false);
								loginList = loginService.getModesOfLogin(ProcessNames.LOGIN.getType(), roleList);
								UserOnboard = false;
							} else {
								sessionContextMap.put(RegistrationConstants.ONBOARD_USER, true);
								System.out.println(sessionContextMap.get(RegistrationConstants.ONBOARD_USER));
								sessionContextMap.put(RegistrationConstants.ONBOARD_USER_UPDATE, false);
								loginList = loginService.getModesOfLogin(ProcessNames.ONBOARD.getType(), roleList);
								UserOnboard = true; // [PWD]
							}

						}
					}
				}
			} catch (RegBaseUncheckedException regBaseUncheckedException) {

				LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
						regBaseUncheckedException.getMessage()
								+ ExceptionUtils.getStackTrace(regBaseUncheckedException));
			}
		}
		return loginList;
	}

	//////////////////////////////////////////////////// PacketHandler

	/**
	 * @param statusCode
	 * @param userJsonFile
	 * @param identityJsonFile
	 * @param POAPOBPORPOIJpg
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String packetCreation(String statusCode, String userJsonFile, String identityJsonFile,
			String POAPOBPORPOIJpg, String userID, String centerID, String stationID)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JSR310Module());
		mapper.addMixInAnnotations(DemographicInfoDTO.class, DemographicInfoDTOMix.class);

		RegistrationDTO obj = mapper.readValue(new File(userJsonFile), RegistrationDTO.class);
		MoroccoIdentity identity = mapper.readValue(new File(identityJsonFile), MoroccoIdentity.class);

		byte[] data = IOUtils.toByteArray(new FileInputStream(new File(POAPOBPORPOIJpg)));

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

		SessionContext.getInstance().getUserContext().setUserId(userID);

		SessionContext.getInstance().setMapObject(new HashMap<String, Object>());

		RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
		registrationCenter.setRegistrationCenterId(centerID);
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);

		String randomId = ridGeneratorImpl.generateId(centerID, stationID);
		System.out.println(randomId);

		// System.out.println("centerID " + centerID);
		// System.out.println("stationID " + stationID);

		obj.setRegistrationId(randomId);

		ResponseDTO response = packetHandlerService.handle(obj);
		String response_msg = null;

		if (response.getSuccessResponseDTO().getMessage() == null) {
			System.out.println(response.getErrorResponseDTOs().get(0).getMessage());
			response_msg = response.getErrorResponseDTOs().get(0).getMessage();
		} else {
			String jsonInString = mapper.writeValueAsString(response);
			System.out.println(jsonInString);
			Assert.assertEquals(response.getSuccessResponseDTO().getCode().toString(), "0000");
			Assert.assertEquals(response.getSuccessResponseDTO().getMessage().toString(), "Success");
			response_msg = response.getSuccessResponseDTO().getMessage().toString();
		}

		if (response_msg.contains("Success")) {
			Registration regi = regDAO.getRegistrationById(RegistrationClientStatusCode.CREATED.getCode(), randomId);// processed
			System.out.println("beFORE=== " + regi.getClientStatusCode());
			regi.setClientStatusCode(statusCode); // status being updated setServerStatusCode
			PacketStatusDTO packetStatusDTO = new PacketStatusDTO();
			packetStatusDTO.setFileName(regi.getId());
			packetStatusDTO.setPacketClientStatus(statusCode);
			regDAO.updatePacketSyncStatus(packetStatusDTO);
			System.out.println("aFTER=== " + regi.getClientStatusCode());
		}
		return randomId;
	}

	/**
	 * Fetching and Validating machine and center id
	 * 
	 * @param userDetail the userDetail
	 * @return boolean
	 * @throws RegBaseCheckedException
	 */
	private boolean getCenterMachineStatus(UserDetail userDetail) {
		List<String> machineList = new ArrayList<>();
		List<String> centerList = new ArrayList<>();

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
				"Validating User machine and center mapping");

		userDetail.getUserMachineMapping().forEach(machineMapping -> {
			if (machineMapping.getIsActive()) {
				machineList.add(machineMapping.getMachineMaster().getMacAddress());
				centerList.add(machineMapping.getUserMachineMappingId().getCentreID());
			}
		});
		return machineList.contains(RegistrationSystemPropertiesChecker.getMachineId())
				&& centerList.contains(userDetail.getRegCenterUser().getRegCenterUserId().getRegcntrId());
	}

	/**
	 * @param Path
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public BiometricDTO testData(String Path) throws IOException, ParseException {

		ObjectMapper mapper = new ObjectMapper();
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader(Path);
		// Read JSON file
		Object obj = jsonParser.parse(reader);
		// JSONArray jArray = (JSONArray) obj;

		String s = obj.toString();

		BiometricDTO biodto = mapper.readValue(s, BiometricDTO.class);
		// mapper.readValue(s,mapper.getTypeFactory().constructCollectionType(List.class,
		// BiometricDTO.class));
		biodto.getApplicantBiometricDTO();

		return biodto;

	}

	/**
	 * @param val
	 */
	public void setFlag(String val) {
		ApplicationContext.map().put(RegistrationConstants.FINGERPRINT_DISABLE_FLAG, val);
		ApplicationContext.map().put(RegistrationConstants.IRIS_DISABLE_FLAG, val);
		ApplicationContext.map().put(RegistrationConstants.FACE_DISABLE_FLAG, val);
	}

	/**
	 * @return
	 */
	public String getDate() {

		String pattern = "dd-MMM-yyyy";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		// System.out.println(date);

		return date;

	}

	public String getRandomValue(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		System.out.println(builder.toString());
		return builder.toString();
	}

	public String getQuery(String value, String replaceVal) {

		String query = value;
		query.replaceAll(replaceVal, query);
		return query;
	}

	public void performOTPGenerationValidation(String userId) {

		ResponseDTO responseDto = otpManager.getOTP(userId);
		System.out.println("responseDto obtained in CommonUtil:");
		System.out.println(responseDto.getSuccessResponseDTO().getCode() + " ....HELLO.... "
				+ responseDto.getSuccessResponseDTO().getMessage());

		System.out.println(responseDto.getErrorResponseDTOs());
		String otp = fetchOTPFromKernelDB(userId);
		System.out.println("OTP fetched from Kernel DB is " + otp);
		ResponseDTO otpValidationResponse = otpManager.validateOTP(userId, otp);
		System.out.println(otpValidationResponse);
		SuccessResponseDTO successResponse = otpValidationResponse.getSuccessResponseDTO();
		//String message = successResponse.getMessage();
		//String code = successResponse.getCode();
		assertNotNull(successResponse.getCode());
		//System.out.println(code + " " + message);

	}

	private String fetchOTPFromKernelDB(String userId) {

		String otp = "";
		String kernelDb_host = "52.172.54.231:9001";
		String kernelDb_name = "mosip_kernel";
		String kernelDb_username = "kerneluser";
		String KernelDb__password = "Mosip@dev123";
		String dbConnection_url = "jdbc:postgresql://" + kernelDb_host + "/" + kernelDb_name;
		try (Connection connection = DriverManager.getConnection(dbConnection_url, kernelDb_username,
				KernelDb__password)) {
			System.out.println("Connected to PostgreSQL database!");
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT otp FROM kernel.otp_transaction where id='" + userId
					+ "' order by generated_dtimes desc limit 1");
			while (resultSet.next()) {
				otp = resultSet.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return otp;

	}

	public void userOnboardDBUpdate(String userID) {

		try {
			for (int i = 0; i < integconstant.ONBOARDQUERIES.length; i++) {
				String Q = prop.getProperty(integconstant.ONBOARDQUERIES[i]);
				String replacestr = Q.replace(integconstant.RANDOMVAL, userID);
				System.out.println(replacestr);
				DBUtil.updateQuery(replacestr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dbUpdateEODProcess() {

		try {
			DBUtil.updateQuery(prop.getProperty("UPDATE_EOD_PROCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteProcessedPackets(String syncResult) {

		LOGGER.info("REGISTRATION - INTEGRATION SCENARIO", APPLICATION_NAME, APPLICATION_ID, "DELETE PROCESSED PACKET");

		ResponseDTO delepacketStatus = regPacket.deleteRegistrationPackets();

		Assert.assertEquals("REGISTRATION_DELETION_BATCH_JOBS_SUCCESS",
				delepacketStatus.getSuccessResponseDTO().getMessage());
	}

	// public String getPreRegIdFromDB() {
	// String preRegID = null;
	// Connection con;
	// PreparedStatement pre;
	// try {
	// Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	//
	// con = DriverManager
	// .getConnection("jdbc:derby:" + System.getProperty("user.dir") +
	// "/reg;bootPassword=mosip12345");
	// pre = con.prepareStatement("select prereg_id from
	// reg.pre_registration_list");
	// ResultSet res = pre.executeQuery();
	//
	// if (res.next()) {
	// System.out.println("Pre-Registration ID fetched from database");
	// preRegID = res.getString("PREREG_ID");
	// }
	//
	// pre.close();
	// con.close();
	// } catch (Exception e) {
	// System.out.println("Unable to fetch pre-registration ID from database");
	// }
	// System.out.println("Pre-reg ID present in DB is " + preRegID);
	// return preRegID;
	// }

	public void createRegistrationDTOObject(String registrationCategory) {
		RegistrationDTO registrationDTO = new RegistrationDTO();

		// Create objects for Biometric DTOS
		// BiometricDTO biometricDTO = new BiometricDTO();
		// biometricDTO.setApplicantBiometricDTO(createBiometricInfoDTO());
		// biometricDTO.setIntroducerBiometricDTO(createBiometricInfoDTO());
		// biometricDTO.setOperatorBiometricDTO(createBiometricInfoDTO());
		// biometricDTO.setSupervisorBiometricDTO(createBiometricInfoDTO());
		// registrationDTO.setBiometricDTO(biometricDTO);

		// Create object for Demographic DTOS
		DemographicDTO demographicDTO = new DemographicDTO();
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
		applicantDocumentDTO.setDocuments(new HashMap<>());

		demographicDTO.setApplicantDocumentDTO(applicantDocumentDTO);
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		Identity identity = new Identity();
		demographicInfoDTO.setIdentity(identity);
		demographicDTO.setDemographicInfoDTO(demographicInfoDTO);

		applicantDocumentDTO.setDocuments(new HashMap<>());

		registrationDTO.setDemographicDTO(demographicDTO);

		// Create object for OSIData DTO
		registrationDTO.setOsiDataDTO(new OSIDataDTO());

		// Create object for RegistrationMetaData DTO
		RegistrationMetaDataDTO registrationMetaDataDTO = new RegistrationMetaDataDTO();
		registrationMetaDataDTO.setRegistrationCategory(registrationCategory);

		RegistrationCenterDetailDTO registrationCenter = SessionContext.userContext().getRegistrationCenterDetailDTO();

		if (RegistrationConstants.ENABLE
				.equalsIgnoreCase(getGlobalConfigValueOf(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG))) {
			registrationMetaDataDTO
					.setGeoLatitudeLoc(Double.parseDouble(registrationCenter.getRegistrationCenterLatitude()));
			registrationMetaDataDTO
					.setGeoLongitudeLoc(Double.parseDouble(registrationCenter.getRegistrationCenterLongitude()));
		}

		Map<String, Object> applicationContextMap = ApplicationContext.map();

		registrationMetaDataDTO.setCenterId((String) applicationContextMap.get(RegistrationConstants.USER_CENTER_ID));
		registrationMetaDataDTO.setMachineId((String) applicationContextMap.get(RegistrationConstants.USER_STATION_ID));
		registrationMetaDataDTO
				.setDeviceId((String) applicationContextMap.get(RegistrationConstants.DONGLE_SERIAL_NUMBER));

		registrationDTO.setRegistrationMetaDataDTO(registrationMetaDataDTO);

		System.out.println("registrationMetaDataDTO.getCenterId()");
		System.out.println(registrationMetaDataDTO.getCenterId());
		System.out.println("registrationMetaDataDTO.getMachineId()");
		System.out.println(registrationMetaDataDTO.getMachineId());
		// Set RID
		String registrationID = ridGeneratorImpl.generateId(registrationMetaDataDTO.getCenterId(),
				registrationMetaDataDTO.getMachineId());

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Registration Started for RID  : [ " + registrationID + " ] ");

		registrationDTO.setRegistrationId(registrationID);
		// Put the RegistrationDTO object to SessionContext Map
		SessionContext.map().put(RegistrationConstants.REGISTRATION_DATA, registrationDTO);
	}

	public static List<FingerprintDetailsDTO> fingerPrintTestData(String Path) throws IOException, ParseException {

		ObjectMapper mapper = new ObjectMapper();
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader(Path);
		{
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONArray jArray = (JSONArray) obj;

			String s = jArray.toString();

			List<FingerprintDetailsDTO> fingerprintdetailsData = mapper.readValue(s,
					mapper.getTypeFactory().constructCollectionType(List.class, FingerprintDetailsDTO.class));
			return fingerprintdetailsData;
		}
	}

	public RegistrationPacketSyncDTO syncdatatoserver_Testdata(String userID, String centerID, String stationID,
			Set<String> dbData) {
		for (int i = 0; i < 3; i++) {
			try {
				String RID = packetCreation(RegistrationClientStatusCode.APPROVED.getCode(),
						IntegrationTestConstants.REGDETAILSJSON, IntegrationTestConstants.IDENTITYJSON,
						IntegrationTestConstants.POAPOBPORPOIJPG, userID, centerID, stationID);
				dbData.add(RID);
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

		List<PacketStatusDTO> packetsToBeSynched = packetSyncService.fetchPacketsToBeSynched();
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		if (!packetsToBeSynched.isEmpty()) {
			for (PacketStatusDTO packetToBeSynch : packetsToBeSynched // packetDto

			) {
				SyncRegistrationDTO syncDto = new SyncRegistrationDTO();
				/*syncDto.setLangCode("ENG");
				syncDto.setStatusComment(packetToBeSynch.getPacketClientStatus() + " " + "-" + " "
						+ packetToBeSynch.getClientStatusComments());
				syncDto.setRegistrationId(packetToBeSynch.getFileName());
				syncDto.setSyncStatus(RegistrationConstants.PACKET_STATUS_PRE_SYNC);
				syncDto.setSyncType(RegistrationConstants.PACKET_STATUS_SYNC_TYPE);*/
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
		/*syncDto.setStatusComment("APPROVED - null");
		syncDto.setRegistrationId("100111001100053201903190727");
		syncDto.setSyncStatus(RegistrationConstants.PACKET_STATUS_PRE_SYNC);
		syncDto.setSyncType(RegistrationConstants.PACKET_STATUS_SYNC_TYPE);*/
		syncDtoList.add(syncDto);
		// registrationPacketSyncDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
		registrationPacketSyncDTO.setSyncRegistrationDTOs(syncDtoList);
		registrationPacketSyncDTO.setId(RegistrationConstants.PACKET_SYNC_STATUS_ID);
		registrationPacketSyncDTO.setVersion(RegistrationConstants.PACKET_SYNC_VERSION);

		return registrationPacketSyncDTO;

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
