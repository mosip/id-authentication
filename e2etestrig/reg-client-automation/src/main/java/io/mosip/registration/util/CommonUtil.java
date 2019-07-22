package io.mosip.registration.util;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.util.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.FileNotFoundException;
import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.IndividualIdentity;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.packet.RegistrationApprovalService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;

@Service
public class CommonUtil {
	@Autowired
	PreRegistrationDataSyncService preRegistrationDataSyncService;
	@Autowired
	PacketHandlerService packetHandlerService;

	@Autowired
	RidGenerator<String> ridGeneratorImpl;
	@Autowired
	RegistrationApprovalService registrationApprovalService;

	ApplicationContext applicationContext = ApplicationContext.getInstance();

	private static Logger LOGGER = AppConfig.getLogger(CommonUtil.class);

	public HashMap<String, String> getPreRegIDs() {
		HashMap<String, String> response = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		/**
		 * Read JSON from a file into a Map
		 */
		try {
			response = mapper.readValue(new File("src/main/resources/PreRegIds.json"),
					new TypeReference<Map<String, Object>>() {
					});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * @param pageName
	 *            accept the name of the config properties file
	 * @return return config file object to read config file
	 */
	public Properties readPropertyFile(String apiname, String testCaseName, String propertyFileName) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "readPropertyFile");
		Properties prop = new Properties();
		InputStream input = null;
		String propertiesFilePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator
				+ apiname + File.separator + testCaseName + File.separator + propertyFileName + ".properties";
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Property File Path - " + propertiesFilePath);
		try {
			input = new FileInputStream(propertiesFilePath);
			prop.load(input);
		} catch (IOException ioException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(ioException));
		}

		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ioExceptionFinally) {
					LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
							ExceptionUtils.getStackTrace(ioExceptionFinally));
				}
			}
		}
		return prop;
	}

	public RegistrationDTO getPreRegistrationDetails(String preRegId) {

		ResponseDTO responseDTO = preRegistrationDataSyncService.getPreRegistration(preRegId);
		RegistrationDTO registrationDTO = new RegistrationDTO();
		SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
		List<ErrorResponseDTO> errorResponseDTOList = responseDTO.getErrorResponseDTOs();

		if (successResponseDTO != null && successResponseDTO.getOtherAttributes() != null
				&& successResponseDTO.getOtherAttributes().containsKey(RegistrationConstants.REGISTRATION_DTO)) {
			SessionContext.map().put(RegistrationConstants.REGISTRATION_DATA,
					successResponseDTO.getOtherAttributes().get(RegistrationConstants.REGISTRATION_DTO));
			System.out.println(successResponseDTO.getMessage());
			registrationDTO = (RegistrationDTO) successResponseDTO.getOtherAttributes()
					.get(RegistrationConstants.REGISTRATION_DTO);
		} else if (errorResponseDTOList != null && !errorResponseDTOList.isEmpty()) {
			LOGGER.info("PACKET CREATION TEST - ", "REGISTRATION", "REG",
					RegistrationConstants.ERROR + errorResponseDTOList.get(0).getMessage());
			System.out.println(errorResponseDTOList.get(0).getMessage());
		}
		return registrationDTO;
	}

	public static final String THUMB_JPG = "/thumb.jpg";

	public static List<FingerprintDetailsDTO> getFingerprintDetailsDTO(String personType)
			throws RegBaseCheckedException {
		List<FingerprintDetailsDTO> fingerList = new ArrayList<>();

		if (personType.equals("applicant")) {
			FingerprintDetailsDTO fingerprint = buildFingerPrintDetailsDTO(THUMB_JPG, "BothThumbs.jpg", 85.0, false,
					"thumbs", 0);
			fingerprint.getSegmentedFingerprints()
					.add(buildFingerPrintDetailsDTO(THUMB_JPG, "rightThumb.jpg", 80.0, false, "rightThumb", 2));
			fingerList.add(fingerprint);

			fingerprint = buildFingerPrintDetailsDTO(THUMB_JPG, "LeftPalm.jpg", 80.0, false, "leftSlap", 3);
			fingerprint.getSegmentedFingerprints()
					.add(buildFingerPrintDetailsDTO(THUMB_JPG, "leftIndex.jpg", 80.0, false, "leftIndex", 3));
			fingerprint.getSegmentedFingerprints()
					.add(buildFingerPrintDetailsDTO(THUMB_JPG, "leftMiddle.jpg", 80.0, false, "leftMiddle", 1));
			fingerprint.getSegmentedFingerprints()
					.add(buildFingerPrintDetailsDTO(THUMB_JPG, "leftRing.jpg", 80.0, false, "leftRing", 2));
			fingerprint.getSegmentedFingerprints()
					.add(buildFingerPrintDetailsDTO(THUMB_JPG, "leftLittle.jpg", 80.0, false, "leftLittle", 0));
			fingerList.add(fingerprint);

			fingerprint = buildFingerPrintDetailsDTO(THUMB_JPG, "RightPalm.jpg", 95.0, false, "rightSlap", 2);
			fingerprint.getSegmentedFingerprints()
					.add(buildFingerPrintDetailsDTO(THUMB_JPG, "rightIndex.jpg", 80.0, false, "rightIndex", 3));
			fingerprint.getSegmentedFingerprints()
					.add(buildFingerPrintDetailsDTO(THUMB_JPG, "rightMiddle.jpg", 80.0, false, "rightMiddle", 1));
			fingerprint.getSegmentedFingerprints()
					.add(buildFingerPrintDetailsDTO(THUMB_JPG, "rightRing.jpg", 80.0, false, "rightRing", 2));
			fingerprint.getSegmentedFingerprints()
					.add(buildFingerPrintDetailsDTO(THUMB_JPG, "rightLittle.jpg", 80.0, false, "rightLittle", 0));
			fingerList.add(fingerprint);
		} else {
			fingerList
					.add(buildFingerPrintDetailsDTO(THUMB_JPG, personType + "LeftThumb.jpg", 0, false, "leftThumb", 0));
		}

		return fingerList;
	}

	private static FingerprintDetailsDTO buildFingerPrintDetailsDTO(String imageLoc, String fingerprintImageName,
			double qualityScore, boolean isForceCaptured, String fingerType, int numRetry)
			// buildFingerPrintDetailsDTO(THUMB_JPG,
			// "rightIndex.jpg", 80.0, false, "rightIndex", 3
			//
			throws RegBaseCheckedException {
		FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
		fingerprintDetailsDTO.setFingerPrint(getImageBytes(imageLoc));
		fingerprintDetailsDTO.setFingerprintImageName(fingerprintImageName);
		fingerprintDetailsDTO.setQualityScore(qualityScore);
		fingerprintDetailsDTO.setForceCaptured(isForceCaptured);
		fingerprintDetailsDTO.setFingerType(fingerType);
		fingerprintDetailsDTO.setNumRetry(numRetry);
		fingerprintDetailsDTO.setSegmentedFingerprints(new ArrayList<>());
		return fingerprintDetailsDTO;
	}

	public static byte[] getImageBytes(String filePath) throws RegBaseCheckedException {
		filePath = "/RegClient".concat(filePath);

		try {
			InputStream file = CommonUtil.class.getResourceAsStream(filePath);
			byte[] bytesArray = new byte[(int) file.available()];
			file.read(bytesArray);
			file.close();

			return bytesArray;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationConstants.SERVICE_DATA_PROVIDER_UTIL,
					"Unable to read the Image bytes", ioException);
		}
	}

	private static ApplicantDocumentDTO setApplicantDocumentDTO() throws RegBaseCheckedException {
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();

		applicantDocumentDTO.setAcknowledgeReceipt(getImageBytes("/acknowledgementReceipt.jpg"));
		applicantDocumentDTO.setAcknowledgeReceiptName("RegistrationAcknowledgement.jpg");
		return applicantDocumentDTO;
	}

	/**
	 * 
	 * @param Path
	 *            - To fetch the Finger print byte array from json file
	 * @return - the list of FingerPrint details
	 * @throws IOException
	 * @throws ParseException
	 */
	public static List<FingerprintDetailsDTO> getFingerPrintTestData(String Path) {

		List<FingerprintDetailsDTO> fingerprintdetailsData = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			JSONParser jsonParser = new JSONParser();
			FileReader reader = new FileReader(Path);
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONArray jArray = (JSONArray) obj;

			String s = jArray.toString();
			fingerprintdetailsData = mapper.readValue(s,
					mapper.getTypeFactory().constructCollectionType(List.class, FingerprintDetailsDTO.class));

		} catch (IOException | ParseException e) {
			// TODO: handle exception
		}
		return fingerprintdetailsData;

	}

	/**
	 * 
	 * @param Path
	 *            - To fetch the Finger print byte array from json file
	 * @return - the list of FingerPrint details
	 * @throws IOException
	 * @throws ParseException
	 */
	public static List<IrisDetailsDTO> getIrisTestData(String Path) {

		List<IrisDetailsDTO> irisData = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			JSONParser jsonParser = new JSONParser();
			FileReader reader = new FileReader(Path);
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONArray jArray = (JSONArray) obj;

			String s = jArray.toString();
			irisData = mapper.readValue(s,
					mapper.getTypeFactory().constructCollectionType(List.class, IrisDetailsDTO.class));

		} catch (IOException | ParseException e) {
			// TODO: handle exception
		}
		return irisData;

	}

	/**
	 * This method will create registration DTO object
	 */
	public RegistrationDTO createRegistrationDTOObject(String registrationCategory, String centerID, String stationID) {
		RegistrationDTO registrationDTO = new RegistrationDTO();

		// Create objects for Biometric DTOS
		BiometricDTO biometricDTO = new BiometricDTO();
		biometricDTO.setApplicantBiometricDTO(createBiometricInfoDTO());
		biometricDTO.setIntroducerBiometricDTO(createBiometricInfoDTO());
		biometricDTO.setOperatorBiometricDTO(createBiometricInfoDTO());
		biometricDTO.setSupervisorBiometricDTO(createBiometricInfoDTO());
		registrationDTO.setBiometricDTO(biometricDTO);

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

		if (RegistrationConstants.ENABLE.equalsIgnoreCase(
				(String) ApplicationContext.map().get(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG))) {
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

		// Set RID
		String registrationID = ridGeneratorImpl.generateId(centerID, stationID);

		LOGGER.info("PACKET CREATION TEST - ", "REGISTRATION", "REG",
				"Registration Started for RID  : [ " + registrationID + " ] ");

		registrationDTO.setRegistrationId(registrationID);
		// Put the RegistrationDTO object to SessionContext Map

		SessionContext.map().put(RegistrationConstants.REGISTRATION_DATA, registrationDTO);
		return registrationDTO;
	}

	/**
	 * This method will create the biometrics info DTO
	 */
	public BiometricInfoDTO createBiometricInfoDTO() {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setBiometricExceptionDTO(new ArrayList<>());
		biometricInfoDTO.setFingerprintDetailsDTO(new ArrayList<>());
		biometricInfoDTO.setIrisDetailsDTO(new ArrayList<>());
		biometricInfoDTO.setFace(new FaceDetailsDTO());
		biometricInfoDTO.setExceptionFace(new FaceDetailsDTO());
		return biometricInfoDTO;
	}

	/**
	 * 
	 * @param String
	 *            - Biometric json file path
	 * @throws ParseException
	 * @throws IOException
	 */
	public BiometricDTO getBiotestData(String Path) throws IOException, ParseException {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "getBiometrictestData");
		BiometricDTO biodto = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			JSONParser jsonParser = new JSONParser();
			FileReader reader = new FileReader(Path);
			Object obj = jsonParser.parse(reader);
			String s = obj.toString();
			biodto = mapper.readValue(s, BiometricDTO.class);
			biodto.getApplicantBiometricDTO();
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		}
		return biodto;
	}

	/**
	 * 
	 * @param String
	 *            - Value to Set Flag
	 */
	public void setFlag(String val) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "setFlag");
		try {
			ApplicationContext.map().put(RegistrationConstants.FINGERPRINT_DISABLE_FLAG, val);
			ApplicationContext.map().put(RegistrationConstants.IRIS_DISABLE_FLAG, val);
			ApplicationContext.map().put(RegistrationConstants.FACE_DISABLE_FLAG, val);
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		}
	}

	/**
	 * create packet using PRE REG IDs
	 * 
	 * @return HashMap<String, String>
	 */
	public HashMap<String, String> preRegPacketCreation(RegistrationDTO preRegistrationDTO, String statusCode,
			String userJsonFile, String identityJsonFile, String documentFile, String userID, String centerID,
			String stationID, String packetType, String uin) {
		ObjectMapper mapper = new ObjectMapper();
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
				"preRegPacketCreation - create packets using PRE REG ID");
		RegistrationDTO registrationDTO;
		HashMap<String, String> returnValues = new HashMap<>();
		Map<String, DocumentDetailsDTO> documents = new HashMap<String, DocumentDetailsDTO>();
		IndividualIdentity identity = null;
		String randomId = "";
		try {

			registrationDTO = mapper.readValue(new File(userJsonFile), RegistrationDTO.class);

			if (packetType.equalsIgnoreCase("RegClientPacket")) {

				// Creating Registration Client individual packet
				// Set Demographic details to IndividualIdentity
				identity = mapper.readValue(new File(identityJsonFile), IndividualIdentity.class);
				// Set Documents in IndividualIdentity and Get documents to Set in
				// applicantDocumentDTO
				documents = setDocumentDetailsDTO(identity, documentFile);
				registrationDTO.getDemographicDTO().setApplicantDocumentDTO(setApplicantDocumentDTO());
				registrationDTO.getDemographicDTO().getApplicantDocumentDTO().setDocuments(documents);
				// Set IndividualIdentity to RegistrationDTO
				registrationDTO.getDemographicDTO().getDemographicInfoDTO().setIdentity(identity);
				// Generate Registration ID and set to RegistrationDTO
				randomId = ridGeneratorImpl.generateId(centerID, stationID);
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						"Valid Registration ID generated - " + randomId);
				returnValues.put("RANDOMID", randomId);
				registrationDTO.setRegistrationId(randomId);
				System.out.println("ID ==== " + randomId);

			} else if ((packetType.equalsIgnoreCase("PrIdOfAdultWithDocs")
					|| packetType.equalsIgnoreCase("childPreidWithDocs"))) {
				// Set PreRegistration ID to RegistrationDTO
				registrationDTO.setPreRegistrationId(preRegistrationDTO.getPreRegistrationId());
				// Set Registration ID to RegistrationDTO
				registrationDTO.setRegistrationId(preRegistrationDTO.getRegistrationId());
				// Get identity from preRegistrationDTO to RegistrationDTO
				identity = (IndividualIdentity) preRegistrationDTO.getDemographicDTO().getDemographicInfoDTO()
						.getIdentity();
				// Set Documents in IndividualIdentity and Get documents to Set in
				// applicantDocumentDTO
				documents = preRegistrationDTO.getDemographicDTO().getApplicantDocumentDTO().getDocuments();
				// setDocumentDetailsDTO(identity, documentFile);
				registrationDTO.getDemographicDTO().setApplicantDocumentDTO(setApplicantDocumentDTO());
				registrationDTO.getDemographicDTO().getApplicantDocumentDTO().setDocuments(documents);
				// Set IndividualIdentity to RegistrationDTO
				registrationDTO.getDemographicDTO().getDemographicInfoDTO().setIdentity(identity);

				registrationDTO.setRegistrationMetaDataDTO(preRegistrationDTO.getRegistrationMetaDataDTO());

				registrationDTO.getRegistrationMetaDataDTO().setCenterId(centerID);
				registrationDTO.getRegistrationMetaDataDTO().setMachineId(stationID);
			} else {
				// Create RegistrationDTO without docs
				// Set Registration ID to RegistrationDTO
				registrationDTO.setPreRegistrationId(preRegistrationDTO.getPreRegistrationId());
				// Set Registration ID to RegistrationDTO
				registrationDTO.setRegistrationId(preRegistrationDTO.getRegistrationId());
				// Get identity from preRegistrationDTO to RegistrationDTO
				identity = (IndividualIdentity) preRegistrationDTO.getDemographicDTO().getDemographicInfoDTO()
						.getIdentity();

				documents = setDocumentDetailsDTO(identity, documentFile);
				registrationDTO.getDemographicDTO().setApplicantDocumentDTO(setApplicantDocumentDTO());
				registrationDTO.getDemographicDTO().getApplicantDocumentDTO().setDocuments(documents);
				registrationDTO.getDemographicDTO().getDemographicInfoDTO().setIdentity(identity);

				registrationDTO.setRegistrationMetaDataDTO(preRegistrationDTO.getRegistrationMetaDataDTO());
				registrationDTO.getRegistrationMetaDataDTO().setCenterId(centerID);
				registrationDTO.getRegistrationMetaDataDTO().setMachineId(stationID);
			}

			if (packetType.equalsIgnoreCase("childPreidWithDocs")
					|| packetType.equalsIgnoreCase("PrIdOfChildWithoutDocs")) {
				registrationDTO.getSelectionListDTO().setUinId(uin);
				registrationDTO.getRegistrationMetaDataDTO().setUin(uin);
			}

			registrationDTO.getOsiDataDTO().setOperatorID(userID);
			registrationDTO.getOsiDataDTO().setOperatorAuthenticatedByPassword(true);
			returnValues.put("RANDOMID", registrationDTO.getRegistrationId());
			ResponseDTO response = packetHandlerService.handle(registrationDTO);

			if (!(response.getSuccessResponseDTO().getMessage() == null)) {

				returnValues.put("SUCCESSRESPONSE", response.getSuccessResponseDTO().getMessage());
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						response.getSuccessResponseDTO().getMessage());
				if ((response.getSuccessResponseDTO().getMessage().contains(ConstantValues.SUCCESS))
						&& (((String) ApplicationContext.map().get(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG))
								.equalsIgnoreCase(RegistrationConstants.DISABLE))) {
					registrationApprovalService.updateRegistration(registrationDTO.getRegistrationId(),
							RegistrationConstants.EMPTY, RegistrationClientStatusCode.APPROVED.getCode());

				}
			}
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValues;
	}

	/**
	 * 
	 * @param Path
	 *            - To create DocumentDetailsDTO from JSON
	 * @return - the list of FingerPrint details
	 * @throws IOException
	 * @throws ParseException
	 */

	public Map<String, DocumentDetailsDTO> setDocumentDetailsDTO(IndividualIdentity identity, String path) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "setDocumentDetailsDTO");
		byte[] data;
		Map<String, DocumentDetailsDTO> documents = new HashMap<String, DocumentDetailsDTO>();
		try {
			data = IOUtils.toByteArray(new FileInputStream(new File(path)));

			DocumentDetailsDTO documentDetailsDTOAddress = new DocumentDetailsDTO();
			documentDetailsDTOAddress.setDocument(getImageBytes("/proofOfAddress.jpg"));
			documentDetailsDTOAddress.setType("Passport");
			documentDetailsDTOAddress.setFormat("jpg");
			documentDetailsDTOAddress.setValue("ProofOfIdentity");
			documentDetailsDTOAddress.setOwner("Self");
			identity.setProofOfIdentity(documentDetailsDTOAddress);
			documents.put("POI", documentDetailsDTOAddress);

			DocumentDetailsDTO documentDetailsResidenceDTO = new DocumentDetailsDTO();
			documentDetailsResidenceDTO.setDocument(getImageBytes("/proofOfAddress.jpg"));
			documentDetailsResidenceDTO.setType("Passport");
			documentDetailsResidenceDTO.setFormat("jpg");
			documentDetailsResidenceDTO.setValue("ProofOfAddress");
			documentDetailsResidenceDTO.setOwner("hof");
			identity.setProofOfAddress(documentDetailsResidenceDTO);
			documents.put("POA", documentDetailsResidenceDTO);

			DocumentDetailsDTO documentDetailsRelationShipDTO = new DocumentDetailsDTO();
			documentDetailsRelationShipDTO.setDocument(getImageBytes("/proofOfAddress.jpg"));
			documentDetailsRelationShipDTO.setType("Passport");
			documentDetailsRelationShipDTO.setFormat("jpg");
			documentDetailsRelationShipDTO.setValue("ProofOfRelationship");
			documentDetailsRelationShipDTO.setOwner("Self");
			identity.setProofOfRelationship(documentDetailsRelationShipDTO);
			documents.put("POR", documentDetailsRelationShipDTO);

			DocumentDetailsDTO documentDetailsDOBDTO = new DocumentDetailsDTO();
			documentDetailsDOBDTO.setDocument(getImageBytes("/proofOfAddress.jpg"));
			documentDetailsDOBDTO.setType("Passport");
			documentDetailsDOBDTO.setFormat("jpg");
			documentDetailsDOBDTO.setValue("ProofOfDateOfBirth");
			documentDetailsDOBDTO.setOwner("Self");
			identity.setProofOfDateOfBirth(documentDetailsDOBDTO);
			documents.put("POB", documentDetailsDOBDTO);

			DocumentDetailsDTO documentDetailsDTO = identity.getProofOfIdentity();
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = identity.getProofOfAddress();
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = identity.getProofOfRelationship();
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = identity.getProofOfDateOfBirth();
			documentDetailsDTO.setDocument(data);

		} catch (IOException ioException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(ioException));
		} catch (RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return documents;
	}

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to assert response DTO
	 */
	public void verifyAssertionResponse(String expectedMsg, ResponseDTO response) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Assert ResponseDTO");
		try {
			if (!response.getSuccessResponseDTO().getMessage().isEmpty()) {
				Assert.assertEquals(response.getSuccessResponseDTO().getMessage(), expectedMsg);
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						"SUCCESS MESSAGE - " + response.getSuccessResponseDTO().getMessage());
				// Reporter.log(ExceptionUtils.getStackTrace(nullPointerException));
			}
		} catch (NullPointerException e) {

			Assert.assertEquals(response.getErrorResponseDTOs().get(0).getMessage(), expectedMsg);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"FAILURE MESSAGE - " + response.getErrorResponseDTOs().get(0).getMessage());
		}
	}

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to assert response DTO
	 */
	public void verifyAssertNotNull(ResponseDTO response) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Assert ResponseDTO");
		try {
			Assert.assertNotNull(response.getSuccessResponseDTO().getMessage());
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"SUCCESS MESSAGE - " + response.getSuccessResponseDTO().getMessage());
			// Reporter.log(ExceptionUtils.getStackTrace(nullPointerException));
		} catch (NullPointerException e) {
			Assert.assertNotNull(response.getErrorResponseDTOs().get(0).getMessage());
			// Assert.assertEquals(response.getErrorResponseDTOs().get(0).getMessage(),
			// expectedMsg);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"FAILURE MESSAGE - " + response.getErrorResponseDTOs().get(0).getMessage());
		}
	}

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to assert String
	 */

	public void verifyAssertionResponse(String expectedMsg, String response) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Assert String");
		try {
			if (response.equalsIgnoreCase("Success")) {
				Assert.assertEquals(response, expectedMsg);
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "SUCCESS MESSAGE - " + response);
			}
		} catch (NullPointerException e) {
			Assert.assertEquals(response, expectedMsg);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "FAILURE MESSAGE - " + response);
		}
	}

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to assert String
	 */

	public boolean verifyAssertionResponseMessage(String expectedMsg, String response) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Assert String");
		boolean output = false;
		try {
			if (response.equalsIgnoreCase("SYNC_SUCCESS") || response.equalsIgnoreCase("Sync successful")
					|| response.equalsIgnoreCase("Success")) {
				Assert.assertEquals(response, expectedMsg);
				output = true;
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "SUCCESS MESSAGE - " + response);
			}
		} catch (NullPointerException e) {
			Assert.assertEquals(response, expectedMsg);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "FAILURE MESSAGE - " + response);
		}
		return output;
	}

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to verify file exist in given path
	 */

	public boolean verifyIfFileExist(String filePath) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "verifyIfFileExist");

		boolean result = false;
		File file = new File(filePath.concat(RegistrationConstants.ZIP_FILE_EXTENSION));
		if (file.exists()) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"File exist in the specified loacation-- " + filePath);
			result = true;
		}
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Return Value - " + result);
		return result;
	}

}
