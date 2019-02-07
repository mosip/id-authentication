package io.mosip.kernel.masterdata.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.masterdata.constant.BlacklistedWordsErrorCode;
import io.mosip.kernel.masterdata.constant.LocationErrorCode;
import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.dto.BlackListedWordsRequest;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.getresponse.ApplicationResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.BiometricAttributeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.BiometricTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.BlackListedWordsResponse;
import io.mosip.kernel.masterdata.dto.getresponse.DocumentCategoryResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LanguageResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResgistrationCenterStatusResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.TemplateResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.ValidDocumentTypeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostLocationCodeResponseDto;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.IdType;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.entity.id.HolidayID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.repository.IdTypeRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.masterdata.service.ApplicationService;
import io.mosip.kernel.masterdata.service.BiometricAttributeService;
import io.mosip.kernel.masterdata.service.BiometricTypeService;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;
import io.mosip.kernel.masterdata.service.DocumentTypeService;
import io.mosip.kernel.masterdata.service.LanguageService;
import io.mosip.kernel.masterdata.service.LocationService;
import io.mosip.kernel.masterdata.service.RegistrationCenterService;
import io.mosip.kernel.masterdata.service.TemplateFileFormatService;
import io.mosip.kernel.masterdata.service.TemplateService;

/**
 * 
 * @author Bal Vikash Sharma
 * @author Neha Sinha
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MasterdataControllerTest {

//	private static final String JSON_STRING_RESPONCE = "{\"uinLength\":24,\"numberOfWrongAttemptsForOtp\":5,\"accountFreezeTimeoutInHours\":10,\"mobilenumberlength\":10,\"archivalPolicy\":\"arc_policy_2\",\"tokenIdLength\":23,\"restrictedNumbers\":[\"8732\",\"321\",\"65\"],\"registrationCenterId\":\"KDUE83CJ3\",\"machineId\":\"MCBD3UI3\",\"supportedLanguages\":[\"eng\",\"hnd\",\"ara\",\"deu\",\"FRN\"],\"tspIdLength\":24,\"otpTimeOutInMinutes\":2,\"notificationtype\":\"SMS|EMAIL\",\"pridLength\":32,\"vidLength\":32}";

	private static final String JSON_STRING_RESPONSE = "{\r\n" + "\"registrationConfiguration\":\r\n"
			+ "							{\"keyValidityPeriodPreRegPack\":\"3\",\"smsNotificationTemplateRegCorrection\":\"OTP for your request is $otp\",\"defaultDOB\":\"1-Jan\",\"smsNotificationTemplateOtp\":\"OTP for your request is $otp\",\"supervisorVerificationRequiredForExceptions\":\"true\",\"keyValidityPeriodRegPack\":\"3\",\"irisRetryAttempts\":\"10\",\"fingerprintQualityThreshold\":\"120\",\"multifactorauthentication\":\"true\",\"smsNotificationTemplateUpdateUIN\":\"OTP for your request is $otp\",\"supervisorAuthType\":\"password\",\"maxDurationRegPermittedWithoutMasterdataSyncInDays\":\"10\",\"modeOfNotifyingIndividual\":\"mobile\",\"emailNotificationTemplateUpdateUIN\":\"Hello $user the OTP is $otp\",\"maxDocSizeInMB\":\"150\",\"emailNotificationTemplateOtp\":\"Hello $user the OTP is $otp\",\"emailNotificationTemplateRegCorrection\":\"Hello $user the OTP is $otp\",\"faceRetry\":\"12\",\"noOfFingerprintAuthToOnboardUser\":\"10\",\"smsNotificationTemplateLostUIN\":\"OTP for your request is $otp\",\"supervisorAuthMode\":\"IRIS\",\"operatorRegSubmissionMode\":\"fingerprint\",\"officerAuthType\":\"password\",\"faceQualityThreshold\":\"25\",\"gpsDistanceRadiusInMeters\":\"3\",\"automaticSyncFreqServerToClient\":\"25\",\"maxDurationWithoutMasterdataSyncInDays\":\"7\",\"loginMode\":\"bootable dongle\",\"irisQualityThreshold\":\"25\",\"retentionPeriodAudit\":\"3\",\"fingerprintRetryAttempts\":\"234\",\"emailNotificationTemplateNewReg\":\"Hello $user the OTP is $otp\",\"passwordExpiryDurationInDays\":\"3\",\"emailNotificationTemplateLostUIN\":\"Hello $user the OTP is $otp\",\"blockRegistrationIfNotSynced\":\"10\",\"noOfIrisAuthToOnboardUser\":\"10\",\"smsNotificationTemplateNewReg\":\"OTP for your request is $otp\"},\r\n"
			+ "\r\n" + "\"globalConfiguration\":\r\n"
			+ "						{\"mosip.kernel.crypto.symmetric-algorithm-name\":\"AES\",\"mosip.kernel.virus-scanner.port\":\"3310\",\"mosip.kernel.email.max-length\":\"50\",\"mosip.kernel.email.domain.ext-max-lenght\":\"7\",\"mosip.kernel.rid.sequence-length\":\"5\",\"mosip.kernel.uin.uin-generation-cron\":\"0 * * * * *\",\"mosip.kernel.rid.centerid-length\":\"5\",\"mosip.kernel.email.special-char\":\"!#$%&'*+-\\/=?^_`{|}~.\",\"mosip.kernel.rid.timestamp-length\":\"14\",\"mosip.kernel.vid.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.asymmetric-algorithm-length\":\"2048\",\"mosip.kernel.uin.min-unused-threshold\":\"100000\",\"mosip.kernel.prid.sequence-limit\":\"3\",\"auth.role.prefix\":\"ROLE_\",\"mosip.kernel.email.domain.ext-min-lenght\":\"2\",\"auth.server.validate.url\":\"http:\\/\\/localhost:8091\\/auth\\/validate_token\",\"mosip.kernel.machineid.length\":\"4\",\"mosip.supported-languages\":\"eng,ara,fra,hin,deu\",\"mosip.kernel.prid.length\":\"14\",\"auth.header.name\":\"Authorization\",\"mosip.kernel.crypto.asymmetric-algorithm-name\":\"RSA\",\"mosip.kernel.phone.min-length\":\"9\",\"mosip.kernel.uin.length\":\"10\",\"mosip.kernel.virus-scanner.host\":\"104.211.209.102\",\"mosip.kernel.email.min-length\":\"7\",\"mosip.kernel.rid.machineid-length\":\"5\",\"mosip.kernel.prid.repeating-block-limit\":\"3\",\"mosip.kernel.vid.length.repeating-block-limit\":\"2\",\"mosip.kernel.rid.length\":\"29\",\"mosip.kernel.phone.max-length\":\"15\",\"mosip.kernel.prid.repeating-limit\":\"2\",\"mosip.kernel.uin.restricted-numbers\":\"786,666\",\"mosip.kernel.email.domain.special-char\":\"-\",\"mosip.kernel.vid.length.repeating-limit\":\"2\",\"mosip.kernel.registrationcenterid.length\":\"4\",\"mosip.kernel.phone.special-char\":\"+ -\",\"mosip.kernel.uin.uins-to-generate\":\"200000\",\"mosip.kernel.vid.length\":\"16\",\"mosip.kernel.tokenid.length\":\"36\",\"mosip.kernel.uin.length.repeating-block-limit\":\"2\",\"mosip.kernel.tspid.length\":\"4\",\"mosip.kernel.tokenid.sequence-limit\":\"3\",\"mosip.kernel.uin.length.repeating-limit\":\"2\",\"mosip.kernel.uin.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.symmetric-algorithm-length\":\"256\",\"mosip.kernel.data-key-splitter\":\"#KEY_SPLITTER#\"}\r\n"
			+ "}";
	
	@Autowired
	public MockMvc mockMvc;

	@MockBean
	private RestTemplate restTemplate;

	@MockBean
	private BiometricTypeService biometricTypeService;

	private BiometricTypeDto biometricTypeDto1 = new BiometricTypeDto();
	private BiometricTypeDto biometricTypeDto2 = new BiometricTypeDto();

	private List<BiometricTypeDto> biometricTypeDtoList = new ArrayList<>();
	private BiometricTypeResponseDto biometricTypeResponseDto;

	@MockBean
	private ApplicationService applicationService;

	private ApplicationDto applicationDto = new ApplicationDto();

	private List<ApplicationDto> applicationDtoList = new ArrayList<>();

	@MockBean
	private BiometricAttributeService biometricAttributeService;

	private final String BIOMETRIC_ATTRIBUTE_EXPECTED = "{ \"biometricattributes\": [ { \"code\": \"iric_black\", \"name\": \"black\", \"description\": null, \"isActive\": true},{\"code\": \"iric_brown\", \"name\": \"brown\", \"description\": null,\"isActive\": true } ] }";

	private List<BiometricAttributeDto> biometricattributes;

	private DocumentCategoryDto documentCategoryDto1;
	private DocumentCategoryDto documentCategoryDto2;

	private List<DocumentCategoryDto> documentCategoryDtoList = new ArrayList<>();

	@MockBean
	private DocumentCategoryService documentCategoryService;
	private DocumentCategoryResponseDto documentCategoryResponseDto = new DocumentCategoryResponseDto();

	@MockBean
	private DocumentTypeService documentTypeService;

	private final String DOCUMENT_TYPE_EXPECTED = "{ \"documents\": [ { \"code\": \"addhar\", \"name\": \"adhar card\", \"description\": \"Uid\", \"langCode\": \"eng\"}, { \"code\": \"residensial\", \"name\": \"residensial_prof\", \"description\": \"document for residential prof\", \"langCode\": \"eng\" } ] }";

	ValidDocumentTypeResponseDto validDocumentTypeResponseDto = null;

	List<DocumentTypeDto> documentTypeDtos = null;

	@MockBean
	private IdTypeRepository repository;

	private IdType idType;
	private BlacklistedWordsDto blacklistedWordsDto;
	private BlackListedWordsRequest blackListedWordsRequest;

	private BlackListedWordsResponse blackListedWordsResponse;

	@MockBean
	private LanguageService languageService;

	private static final String LANGUAGE_JSON_STRING = "{ \"languages\": [   {      \"code\": \"hin\", \"name\": \"hindi\",      \"family\": \"hindi\",   \"nativeName\": \"hindi\" } ]}";

	private LanguageResponseDto respDto;
	private List<LanguageDto> languages;
	private LanguageDto hin;

	private static String LOCATION_JSON_EXPECTED_POST = null;
	LocationHierarchyDto locationHierarchyDto = null;
	@MockBean
	private LocationService locationService;

	LocationDto locationDto = null;
	LocationResponseDto locationResponseDto = null;
	List<Object[]> locObjList = null;
	LocationHierarchyResponseDto locationHierarchyResponseDto = null;
	PostLocationCodeResponseDto locationCodeDto = null;

	@MockBean
	private HolidayRepository holidayRepository;
	@MockBean
	private RegistrationCenterRepository registrationCenterRepository;

	@MockBean
	private RegistrationCenterService registrationCenterService;

	private RegistrationCenter registrationCenter;
	private List<Holiday> holidays;

	private ApplicationResponseDto applicationResponseDto = new ApplicationResponseDto();

	@MockBean
	private TemplateService templateService;

	private CodeAndLanguageCodeID codeAndLanguageCodeId;

	@MockBean
	private BlacklistedWordsService blacklistedWordsService;

	private List<TemplateDto> templateDtoList = new ArrayList<>();

	@MockBean
	private TemplateFileFormatService templateFileFormatService;

	private ObjectMapper mapper;

	@Before
	public void setUp() {
		mapper = new ObjectMapper();
		Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(JSON_STRING_RESPONSE);
		biometricTypeSetup();

		applicationSetup();

		biometricAttributeSetup();

		documentCategorySetup();

		documentTypeSetup();

		idTypeSetup();

		locationSetup();

		registrationCenterController();
		blackListedWordSetUp();
		templateSetup();

		templateFileFormatSetup();

	}

	private void templateSetup() {
		TemplateDto templateDto = new TemplateDto();

		templateDto.setId("3");
		templateDto.setName("Email template");
		templateDto.setFileFormatCode("xml");
		templateDto.setTemplateTypeCode("EMAIL");
		templateDto.setLangCode("HIN");
		templateDtoList.add(templateDto);
	}

	private void templateFileFormatSetup() {
		codeAndLanguageCodeId = new CodeAndLanguageCodeID();
		codeAndLanguageCodeId.setCode("xml");
		codeAndLanguageCodeId.setLangCode("FRE");
	}

	private void registrationCenterController() {
		LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		LocalDate date = LocalDate.of(2018, Month.NOVEMBER, 7);
		registrationCenter = new RegistrationCenter();
		Location location = new Location();
		location.setCode("KAR_59");
		registrationCenter.setAddressLine1("7th Street");
		registrationCenter.setAddressLine2("Lane 2");
		registrationCenter.setAddressLine3("Mylasandra-560001");
		registrationCenter.setIsActive(true);
		registrationCenter.setCenterTypeCode("PAR");
		registrationCenter.setContactPhone("987654321");
		registrationCenter.setCreatedBy("John");
		registrationCenter.setCreatedDateTime(specificDate);
		registrationCenter.setHolidayLocationCode("KAR");
		registrationCenter.setLocationCode("LOC");
		registrationCenter.setId("REG_CR_001");
		registrationCenter.setLanguageCode("eng");
		registrationCenter.setWorkingHours("9");
		registrationCenter.setLatitude("12.87376");
		registrationCenter.setLongitude("12.76372");
		registrationCenter.setName("RV Niketan REG CENTER");

		holidays = new ArrayList<>();

		Holiday holiday = new Holiday();
		holiday.setId(1);
		holiday.setHolidayId(new HolidayID("KAR", date, "eng", "Diwali"));
		holiday.setCreatedBy("John");
		holiday.setCreatedDateTime(specificDate);
		holiday.setHolidayDesc("Diwali");
		holiday.setIsActive(true);

		holidays.add(holiday);
	}

	private void locationSetup() {
		List<LocationDto> locationHierarchies = new ArrayList<>();
		List<LocationHierarchyDto> locationHierarchyDtos = new ArrayList<>();
		locationResponseDto = new LocationResponseDto();
		locationDto = new LocationDto();
		locationDto.setCode("IND");
		locationDto.setName("INDIA");
		locationDto.setHierarchyLevel(0);
		locationDto.setHierarchyName(null);
		locationDto.setParentLocCode(null);
		locationDto.setLangCode("HIN");

		locationDto.setIsActive(true);
		locationHierarchies.add(locationDto);
		locationDto.setCode("KAR");
		locationDto.setName("KARNATAKA");
		locationDto.setHierarchyLevel(1);
		locationDto.setHierarchyName("STATE");
		locationDto.setParentLocCode("IND");
		locationDto.setLangCode("eng");
		locationDto.setIsActive(true);

		locationDto.setIsActive(true);
		locationHierarchies.add(locationDto);
		locationResponseDto.setLocations(locationHierarchies);
		LocationHierarchyDto locationHierarchyDto = new LocationHierarchyDto();
		locationHierarchyDto.setLocationHierarchylevel((short) 1);
		locationHierarchyDto.setLocationHierarchyName("COUNTRY");
		locationHierarchyDtos.add(locationHierarchyDto);
		locationHierarchyResponseDto = new LocationHierarchyResponseDto();
		locationHierarchyResponseDto.setLocations(locationHierarchyDtos);
		locationCodeDto = new PostLocationCodeResponseDto();
		locationCodeDto.setCode("TN");
		locationCodeDto.setLangCode("eng");
		RequestDto<LocationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.create.location");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(locationDto);

		try {
			LOCATION_JSON_EXPECTED_POST = mapper.writeValueAsString(requestDto);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	private void idTypeSetup() {
		idType = new IdType();
		idType.setIsActive(true);
		idType.setCreatedBy("testCreation");
		idType.setLangCode("eng");
		idType.setCode("POA");
		idType.setDescr("Proof Of Address");
	}

	private void documentTypeSetup() {
		documentTypeDtos = new ArrayList<DocumentTypeDto>();
		DocumentTypeDto documentType = new DocumentTypeDto();
		documentType.setCode("addhar");
		documentType.setName("adhar card");
		documentType.setDescription("Uid");
		documentType.setLangCode("eng");

		documentTypeDtos.add(documentType);
		DocumentTypeDto documentType1 = new DocumentTypeDto();
		documentType1.setCode("residensial");
		documentType1.setName("residensial_prof");
		documentType1.setDescription("document for residential prof");
		documentType1.setLangCode("eng");
		documentTypeDtos.add(documentType1);
		validDocumentTypeResponseDto = new ValidDocumentTypeResponseDto(documentTypeDtos);
	}

	private void documentCategorySetup() {
		documentCategoryDto1 = new DocumentCategoryDto();
		documentCategoryDto1.setCode("101");
		documentCategoryDto1.setName("POI");
		documentCategoryDto1.setLangCode("eng");

		documentCategoryDto2 = new DocumentCategoryDto();
		documentCategoryDto2.setCode("102");
		documentCategoryDto2.setName("POR");
		documentCategoryDto2.setLangCode("eng");

		documentCategoryDtoList.add(documentCategoryDto1);
		documentCategoryDtoList.add(documentCategoryDto2);
	}

	private void biometricAttributeSetup() {
		biometricattributes = new ArrayList<>();
		BiometricAttributeDto biometricAttribute = new BiometricAttributeDto();
		biometricAttribute.setCode("iric_black");
		biometricAttribute.setName("black");
		biometricAttribute.setDescription(null);
		biometricAttribute.setIsActive(true);
		biometricattributes.add(biometricAttribute);
		BiometricAttributeDto biometricAttribute1 = new BiometricAttributeDto();
		biometricAttribute1.setCode("iric_brown");
		biometricAttribute1.setName("brown");
		biometricAttribute.setDescription(null);
		biometricAttribute1.setIsActive(true);
		biometricattributes.add(biometricAttribute1);
		new BiometricAttributeResponseDto(biometricattributes);
	}

	private void applicationSetup() {
		applicationDto.setCode("101");
		applicationDto.setName("pre-registeration");
		applicationDto.setDescription("Pre-registration Application Form");
		applicationDto.setLangCode("eng");

		applicationDtoList.add(applicationDto);

		codeAndLanguageCodeId = new CodeAndLanguageCodeID();
		codeAndLanguageCodeId.setCode("101");
		codeAndLanguageCodeId.setLangCode("eng");
	}

	private void biometricTypeSetup() {
		biometricTypeDto1.setCode("1");
		biometricTypeDto1.setName("DNA MATCHING");
		biometricTypeDto1.setDescription(null);
		biometricTypeDto1.setLangCode("eng");

		biometricTypeDto2.setCode("3");
		biometricTypeDto2.setName("EYE SCAN");
		biometricTypeDto2.setDescription(null);
		biometricTypeDto2.setLangCode("eng");

		biometricTypeDtoList.add(biometricTypeDto1);
		biometricTypeDtoList.add(biometricTypeDto2);

		biometricTypeResponseDto = new BiometricTypeResponseDto();
		biometricTypeResponseDto.setBiometrictypes(biometricTypeDtoList);

		codeAndLanguageCodeId = new CodeAndLanguageCodeID();
		codeAndLanguageCodeId.setCode("1");
		codeAndLanguageCodeId.setLangCode("DNA MATCHING");
	}

	private void blackListedWordSetUp() {
		blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setLangCode("TST");
		blacklistedWordsDto.setIsActive(true);
		blacklistedWordsDto.setDescription("Test");
		blacklistedWordsDto.setWord("testword");
		blackListedWordsRequest = new BlackListedWordsRequest();
		blackListedWordsRequest.setBlacklistedword(blacklistedWordsDto);
		blackListedWordsResponse = new BlackListedWordsResponse();
		blackListedWordsResponse.setLangCode("TST");
		blackListedWordsResponse.setWord("testword");

	}

	// -------------------------------BiometricTypeControllerTest--------------------------
	@Test
	public void fetchAllBioMetricTypeTest() throws Exception {
		Mockito.when(biometricTypeService.getAllBiometricTypes()).thenReturn(biometricTypeResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/biometrictypes"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchAllBiometricTypeUsingLangCodeTest() throws Exception {
		Mockito.when(biometricTypeService.getAllBiometricTypesByLanguageCode(Mockito.anyString()))
				.thenReturn(biometricTypeResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/biometrictypes/eng"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchBiometricTypeUsingCodeAndLangCode() throws Exception {
		BiometricTypeResponseDto biometricTypeResponseDto = new BiometricTypeResponseDto();
		List<BiometricTypeDto> biometricTypeDtos = new ArrayList<>();
		biometricTypeDtos.add(biometricTypeDto1);
		biometricTypeResponseDto.setBiometrictypes(biometricTypeDtos);
		Mockito.when(biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(biometricTypeResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/biometrictypes/1/eng"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void addBiometricTypeTest() throws Exception {
		Mockito.when(biometricTypeService.createBiometricType(Mockito.any())).thenReturn(codeAndLanguageCodeId);

		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/biometrictypes").contentType(MediaType.APPLICATION_JSON)
				.content("{\n" + "  \"id\": \"string\",\n" + "  \"ver\": \"string\",\n"
						+ "  \"timestamp\": \"2018-12-17T07:22:22.233Z\",\n" + "  \"request\": {\n"
						+ "    \"code\": \"1\",\n" + "    \"description\": \"string\",\n" + "    \"isActive\": true,\n"
						+ "    \"langCode\": \"eng\",\n" + "    \"name\": \"Abc\"\n" + "  }\n" + "}"))
				.andExpect(status().isOk());
	}

	@Test
	public void addBiometricTypeLanguageValidationTest() throws Exception {
		Mockito.when(biometricTypeService.createBiometricType(Mockito.any())).thenReturn(codeAndLanguageCodeId);

		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/biometrictypes").contentType(MediaType.APPLICATION_JSON)
				.content("{\n" + "  \"id\": \"string\",\n" + "  \"ver\": \"string\",\n"
						+ "  \"timestamp\": \"2018-12-17T07:22:22.233Z\",\n" + "  \"request\": {\n"
						+ "    \"code\": \"1\",\n" + "    \"description\": \"string\",\n" + "    \"isActive\": true,\n"
						+ "    \"langCode\": \"akk\",\n" + "    \"name\": \"Abc\"\n" + "  }\n" + "}"))
				.andExpect(status().isOk());
	}

	// -------------------------------ApplicationControllerTest--------------------------//
	@Test
	public void fetchAllApplicationTest() throws Exception {
		applicationResponseDto.setApplicationtypes(applicationDtoList);
		Mockito.when(applicationService.getAllApplication()).thenReturn(applicationResponseDto);

		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/applicationtypes"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchAllApplicationUsingLangCodeTest() throws Exception {
		applicationResponseDto.setApplicationtypes(applicationDtoList);
		Mockito.when(applicationService.getAllApplicationByLanguageCode(Mockito.anyString()))
				.thenReturn(applicationResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/applicationtypes/eng"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchApplicationUsingCodeAndLangCode() throws Exception {
		List<ApplicationDto> applicationDtos = new ArrayList<>();
		applicationDtos.add(applicationDto);
		applicationResponseDto.setApplicationtypes(applicationDtos);
		Mockito.when(applicationService.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(applicationResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/applicationtypes/101/eng"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void addApplicationTest() throws Exception {
		Mockito.when(applicationService.createApplication(Mockito.any())).thenReturn(codeAndLanguageCodeId);

		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/applicationtypes").contentType(MediaType.APPLICATION_JSON)
				.content("{\n" + "  \"id\": \"string\",\n" + "  \"ver\": \"string\",\n"
						+ "  \"timestamp\": \"2018-12-17T07:15:06.724Z\",\n" + "  \"request\": {\n"
						+ "    \"code\": \"101\",\n" + "    \"description\": \"Pre-registration Application Form\",\n"
						+ "    \"isActive\": true,\n" + "    \"langCode\": \"eng\",\n"
						+ "    \"name\": \"pre-registeration\"\n" + "  }\n" + "}"))
				.andExpect(status().isOk());
	}

	// -------------------------------BiometricAttributeControllerTest--------------------------

	@Test
	public void testGetBiometricAttributesByBiometricType() throws Exception {

		Mockito.when(biometricAttributeService.getBiometricAttribute(Mockito.anyString(), Mockito.anyString()))
				.thenReturn((biometricattributes));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/getbiometricattributesbyauthtype/eng/iric"))
				.andExpect(MockMvcResultMatchers.content().json(BIOMETRIC_ATTRIBUTE_EXPECTED))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testBiometricTypeBiometricAttributeNotFoundException() throws Exception {
		Mockito.when(biometricAttributeService.getBiometricAttribute(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new DataNotFoundException("KER-MAS-00000",
						"No biometric attributes found for specified biometric code type and language code"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/getbiometricattributesbyauthtype/eng/face"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testBiometricTypeFetchException() throws Exception {
		Mockito.when(biometricAttributeService.getBiometricAttribute(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new MasterDataServiceException("KER-DOC-00000", "exception duringfatching data from db"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/getbiometricattributesbyauthtype/eng/iric"))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}

	// -------------------------------DocumentCategoryControllerTest--------------------------
	@Test
	public void fetchAllDocumentCategoryTest() throws Exception {
		documentCategoryResponseDto.setDocumentcategories(documentCategoryDtoList);
		Mockito.when(documentCategoryService.getAllDocumentCategory()).thenReturn(documentCategoryResponseDto);

		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/documentcategories"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchAllDocumentCategoryUsingLangCodeTest() throws Exception {
		documentCategoryResponseDto.setDocumentcategories(documentCategoryDtoList);
		Mockito.when(documentCategoryService.getAllDocumentCategoryByLaguageCode(Mockito.anyString()))
				.thenReturn(documentCategoryResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/documentcategories/eng"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchDocumentCategoryUsingCodeAndLangCode() throws Exception {
		List<DocumentCategoryDto> documentCategoryDtos = new ArrayList<>();
		documentCategoryDtos.add(documentCategoryDto1);
		documentCategoryResponseDto.setDocumentcategories(documentCategoryDtos);
		Mockito.when(
				documentCategoryService.getDocumentCategoryByCodeAndLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(documentCategoryResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/documentcategories/101/eng"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	// -------------------------------DocumentTypeControllerTest--------------------------
	@Test
	public void testGetDoucmentTypesForDocumentCategoryAndLangCode() throws Exception {

		Mockito.when(documentTypeService.getAllValidDocumentType(Mockito.anyString(), Mockito.anyString()))
				.thenReturn((documentTypeDtos));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/documenttypes/poa/eng"))
				.andExpect(MockMvcResultMatchers.content().json(DOCUMENT_TYPE_EXPECTED))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testDocumentTypeNotFoundException() throws Exception {
		Mockito.when(documentTypeService.getAllValidDocumentType(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new DataNotFoundException("KER-DOC-10001",
						"No documents found for specified document category code and language code"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/documenttypes/poc/eng"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testDocumentTypeFetchException() throws Exception {
		Mockito.when(documentTypeService.getAllValidDocumentType(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new MasterDataServiceException("KER-DOC-10000", "exception during fatching data from db"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/documenttypes/poc/eng"))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}
	// -------------------------------IdTypesControllerTest--------------------------

	@Test
	public void testIdTypeController() throws Exception {
		List<IdType> idTypeList = new ArrayList<>();
		idTypeList.add(idType);
		Mockito.when(repository.findByLangCode(anyString())).thenReturn(idTypeList);
		mockMvc.perform(get("/v1.0/idtypes/{languagecode}", "eng")).andExpect(status().isOk());
	}

	// -------------------------------LanguageControllerTest--------------------------
	@Test
	public void testGetAllLanguages() throws Exception {
		loadSuccessData();
		Mockito.when(languageService.getAllLaguages()).thenReturn(respDto);

		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/languages"))
				.andExpect(MockMvcResultMatchers.content().json(LANGUAGE_JSON_STRING))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testGetAllLanguagesForLanguageNotFoundException() throws Exception {
		Mockito.when(languageService.getAllLaguages())
				.thenThrow(new DataNotFoundException("KER-MAS-0987", "No Language found"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/languages"))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testGetAllLanguagesForLanguageFetchException() throws Exception {
		Mockito.when(languageService.getAllLaguages())
				.thenThrow(new MasterDataServiceException("KER-MAS-0988", "Error occured while fetching language"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/languages"))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}

	private void loadSuccessData() {
		respDto = new LanguageResponseDto();
		languages = new ArrayList<>();

		// creating language
		hin = new LanguageDto();
		hin.setCode("hin");
		hin.setName("hindi");
		hin.setFamily("hindi");
		hin.setNativeName("hindi");

		// adding language to list
		languages.add(hin);

		respDto.setLanguages(languages);

	}

	// -------------------------------LocationControllerTest--------------------------

	@Test
	public void testGetAllLocationHierarchy() throws Exception {

		Mockito.when(locationService.getLocationDetails(Mockito.anyString())).thenReturn(locationHierarchyResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/eng"))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testGetLocatonHierarchyByLocCodeAndLangCode() throws Exception {
		Mockito.doReturn(locationResponseDto).when(locationService).getLocationHierarchyByLangCode(Mockito.anyString(),
				Mockito.anyString());

		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/KAR/eng"))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testGetAllLocationsNoRecordsFoundException() throws Exception {
		Mockito.when(locationService.getLocationDetails(Mockito.anyString()))
				.thenThrow(new MasterDataServiceException("1111111", "Error from database"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/eng"))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}

	@Test
	public void testGetAllLocationsDataBaseException() throws Exception {
		Mockito.when(locationService.getLocationDetails(Mockito.anyString()))
				.thenThrow(new DataNotFoundException("3333333", "Location Hierarchy does not exist"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/eng"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testGetLocationsByLangCodeAndLocCodeDataBaseException() throws Exception {
		Mockito.when(locationService.getLocationHierarchyByLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new MasterDataServiceException("1111111", "Error from database"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/KAR/eng"))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}

	@Test
	public void testGetLocationsByLangCodeAndLocCodeNoRecordsFoundException() throws Exception {
		Mockito.when(locationService.getLocationHierarchyByLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new DataNotFoundException("3333333", "Location Hierarchy does not exist"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/KAR/eng"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testSaveLocationHierarchy() throws Exception {
		Mockito.when(locationService.createLocationHierarchy(Mockito.any())).thenReturn(locationCodeDto);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/locations").contentType(MediaType.APPLICATION_JSON)
				.content(LOCATION_JSON_EXPECTED_POST)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testNegativeSaveLocationHierarchy() throws Exception {
		Mockito.when(locationService.createLocationHierarchy(Mockito.any()))
				.thenThrow(new MasterDataServiceException("1111111", "Error from database"));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/locations").contentType(MediaType.APPLICATION_JSON)
				.content(LOCATION_JSON_EXPECTED_POST))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}

	@Test
	public void testUpdateLocationDetails() throws Exception {
		Mockito.when(locationService.updateLocationDetails(Mockito.any())).thenReturn(locationCodeDto);
		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/locations").contentType(MediaType.APPLICATION_JSON)
				.content(LOCATION_JSON_EXPECTED_POST)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testUpdateLocationDetailsException() throws Exception {
		Mockito.when(locationService.updateLocationDetails(Mockito.any()))
				.thenThrow(new MasterDataServiceException("1111111", "Error from database"));
		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/locations").contentType(MediaType.APPLICATION_JSON)
				.content(LOCATION_JSON_EXPECTED_POST))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}

	@Test
	public void getImmediateChildrenTest() throws Exception {
		Mockito.when(locationService.getImmediateChildrenByLocCodeAndLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(locationResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/immediatechildren/eng/KAR"))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void getImmediateChildrenServiceExceptionTest() throws Exception {
		Mockito.when(locationService.getImmediateChildrenByLocCodeAndLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new MasterDataServiceException("1111111", "Error from database"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/immediatechildren/eng/KAR"))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());

	}

	@Test
	public void getImmediateChildrenDataNotFoundExceptionTest() throws Exception {
		Mockito.when(locationService.getImmediateChildrenByLocCodeAndLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new DataNotFoundException("111111", "data not found"));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/immediatechildren/eng/KAR"))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testDeleteLocationDetails() throws Exception {
		Mockito.when(locationService.deleteLocationDetials(Mockito.anyString())).thenReturn(new CodeResponseDto());
		mockMvc.perform(MockMvcRequestBuilders.delete("/v1.0/locations/KAR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void getLocationDataByHierarchyNameSuccessTest() throws Exception {

		Mockito.when(locationService.getLocationDataByHierarchyName(Mockito.anyString()))
				.thenReturn(locationResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/locationhierarchy/state"))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void dataNotfoundExceptionTest() throws Exception {

		Mockito.when(locationService.getLocationDataByHierarchyName(Mockito.anyString()))
				.thenThrow(new DataNotFoundException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage()));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/locationhierarchy/123"))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void masterDataServiceExceptionTest() throws Exception {

		Mockito.when(locationService.getLocationDataByHierarchyName(Mockito.anyString()))
				.thenThrow(new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
						LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage()));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/locations/locationhierarchy/123"))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());

	}

	/*
	 * //
	 * -------------------------------RegistrationCenterControllerTest--------------
	 * ------------
	 * 
	 * @Test public void testGetRegistraionCenterHolidaysSuccess() throws Exception
	 * {
	 * Mockito.when(registrationCenterRepository.findByIdAndLanguageCode(anyString()
	 * , anyString())) .thenReturn(registrationCenter);
	 * Mockito.when(holidayRepository.findAllByLocationCodeYearAndLangCode(anyString
	 * (), anyString(), anyInt())) .thenReturn(holidays); mockMvc.perform(get(
	 * "/v1.0/getregistrationcenterholidays/{languagecode}/{registrationcenterid}/{year}",
	 * "eng", "REG_CR_001", 2018)).andExpect(status().isOk()); }
	 * 
	 * @Test public void testGetRegistraionCenterHolidaysNoRegCenterFound() throws
	 * Exception { mockMvc.perform(get(
	 * "/v1.0/getregistrationcenterholidays/{languagecode}/{registrationcenterid}/{year}",
	 * "eng", "REG_CR_001", 2017)).andExpect(status().isNotFound()); }
	 * 
	 * @Test public void
	 * testGetRegistraionCenterHolidaysRegistrationCenterFetchException() throws
	 * Exception {
	 * Mockito.when(registrationCenterRepository.findByIdAndLanguageCode(anyString()
	 * , anyString())) .thenThrow(DataRetrievalFailureException.class);
	 * mockMvc.perform(get(
	 * "/v1.0/getregistrationcenterholidays/{languagecode}/{registrationcenterid}/{year}",
	 * "eng", "REG_CR_001", 2017)).andExpect(status().isInternalServerError()); }
	 * 
	 * @Test public void testGetRegistraionCenterHolidaysHolidayFetchException()
	 * throws Exception {
	 * Mockito.when(registrationCenterRepository.findByIdAndLanguageCode(anyString()
	 * , anyString())) .thenReturn(registrationCenter);
	 * Mockito.when(holidayRepository.findAllByLocationCodeYearAndLangCode(anyString
	 * (), anyString(), anyInt())) .thenThrow(DataRetrievalFailureException.class);
	 * mockMvc.perform(get(
	 * "/v1.0/getregistrationcenterholidays/{languagecode}/{registrationcenterid}/{year}",
	 * "eng", "REG_CR_001", 2018)).andExpect(status().isInternalServerError()); }
	 */

	// -------------------------------TemplateControllerTest--------------------------
	@Test
	public void getAllTemplateByTest() throws Exception {
		TemplateResponseDto templateResponseDto = new TemplateResponseDto();
		templateResponseDto.setTemplates(templateDtoList);
		Mockito.when(templateService.getAllTemplate()).thenReturn(templateResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/templates")).andExpect(status().isOk());
	}

	@Test
	public void getAllTemplateByLanguageCodeTest() throws Exception {
		TemplateResponseDto templateResponseDto = new TemplateResponseDto();
		templateResponseDto.setTemplates(templateDtoList);
		Mockito.when(templateService.getAllTemplateByLanguageCode(Mockito.anyString())).thenReturn(templateResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/templates/HIN")).andExpect(status().isOk());
	}

	@Test
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeTest() throws Exception {
		TemplateResponseDto templateResponseDto = new TemplateResponseDto();
		templateResponseDto.setTemplates(templateDtoList);
		Mockito.when(templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode(Mockito.anyString(),
				Mockito.anyString())).thenReturn(templateResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/templates/HIN/EMAIL")).andExpect(status().isOk());
	}

	// -----------------------------TemplateFileFormatControllerTest------------------------

	@Test
	public void createTemplateFileFormatTest() throws Exception {
		Mockito.when(templateFileFormatService.createTemplateFileFormat(Mockito.any()))
				.thenReturn(codeAndLanguageCodeId);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/templatefileformats").contentType(MediaType.APPLICATION_JSON)
				.content("{\n" + "  \"id\": \"string\",\n" + "  \"ver\": \"string\",\n"
						+ "  \"timestamp\": \"2018-12-17T07:19:33.655Z\",\n" + "  \"request\": {\n"
						+ "    \"code\": \"xml\",\n" + "    \"description\": \"string\",\n"
						+ "    \"isActive\": true,\n" + "    \"langCode\": \"eng\"\n" + "  }\n" + "}"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void createTemplateFileFormatLanguageCodeValidatorTest() throws Exception {
		Mockito.when(templateFileFormatService.createTemplateFileFormat(Mockito.any()))
				.thenReturn(codeAndLanguageCodeId);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/templatefileformats").contentType(MediaType.APPLICATION_JSON)
				.content("{\n" + "  \"id\": \"string\",\n" + "  \"ver\": \"string\",\n"
						+ "  \"timestamp\": \"2018-12-17T07:19:33.655Z\",\n" + "  \"request\": {\n"
						+ "    \"code\": \"xml\",\n" + "    \"description\": \"string\",\n"
						+ "    \"isActive\": true,\n" + "    \"langCode\": \"xxx\"\n" + "  }\n" + "}"))
				.andExpect(status().isOk());
	}

	@Test
	public void validateWordsTest() throws Exception {
		List<String> words = new ArrayList<>();
		words.add("test");
		String str = "[\"string\"]";
		Mockito.when(blacklistedWordsService.validateWord(words)).thenReturn(true);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1.0/blacklistedwords/words")
				.characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON).content(str);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void validateWordsFalseTest() throws Exception {
		List<String> words = new ArrayList<>();
		words.add("test");
		String str = "[\"string\"]";
		Mockito.when(blacklistedWordsService.validateWord(words)).thenReturn(false);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1.0/blacklistedwords/words")
				.characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON).content(str);
		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$", is("Invalid")));
	}

	@Test
	public void validateWordsExceptionTest() throws Exception {
		List<String> words = new ArrayList<>();
		words.add("test");
		String str = "[\"string\"]";
		Mockito.when(blacklistedWordsService.validateWord(Mockito.any()))
				.thenThrow(new MasterDataServiceException(
						BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorCode(),
						BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorMessage()));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1.0/blacklistedwords/words")
				.characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON).content(str);
		mockMvc.perform(requestBuilder).andExpect(status().isInternalServerError());
	}

	// --------------------------Registration
	// center-validatetimeStamp------------------//

	@Test
	public void validateTimestampWithRegistrationCenter() throws Exception {
		ResgistrationCenterStatusResponseDto resgistrationCenterStatusResponseDto = new ResgistrationCenterStatusResponseDto();
		resgistrationCenterStatusResponseDto.setStatus("Accepted");
		Mockito.when(registrationCenterService.validateTimeStampWithRegistrationCenter(Mockito.anyString(),
				Mockito.anyString())).thenReturn(resgistrationCenterStatusResponseDto);

		mockMvc.perform(get("/v1.0/registrationcenters/validate/1/2017-12-12T17:59:59.999Z"))
				.andExpect(status().isOk());

	}

	@Test
	public void validateTimestampWithRegistrationCenterMasterDataExceptionTest() throws Exception {
		ResgistrationCenterStatusResponseDto resgistrationCenterStatusResponseDto = new ResgistrationCenterStatusResponseDto();
		resgistrationCenterStatusResponseDto.setStatus("Accepted");
		Mockito.when(registrationCenterService.validateTimeStampWithRegistrationCenter(Mockito.anyString(),
				Mockito.anyString())).thenThrow(new MasterDataServiceException("11111", "Database exception"));

		mockMvc.perform(get("/v1.0/registrationcenters/validate/1/2017-12-12T17:59:59.999Z"))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void validateTimestampWithRegistrationCenterDataNotFoundExceptionTest() throws Exception {
		ResgistrationCenterStatusResponseDto resgistrationCenterStatusResponseDto = new ResgistrationCenterStatusResponseDto();
		resgistrationCenterStatusResponseDto.setStatus("Accepted");
		Mockito.when(registrationCenterService.validateTimeStampWithRegistrationCenter(Mockito.anyString(),
				Mockito.anyString())).thenThrow(new DataNotFoundException("11111", "Data not found exception"));

		mockMvc.perform(get("/v1.0/registrationcenters/validate/1/2017-12-12T17:59:59.999Z"))
				.andExpect(status().isOk());

	}

}