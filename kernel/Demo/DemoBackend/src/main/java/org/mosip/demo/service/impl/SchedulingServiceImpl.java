package org.mosip.demo.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.mosip.demo.controller.SchedulingController;
import org.mosip.demo.dto.OtpGeneratorRequestDto;
import org.mosip.demo.dto.OtpGeneratorResponseDto;
import org.mosip.demo.dto.OtpValidatorResponseDto;
import org.mosip.demo.dto.PersonDto;
import org.mosip.demo.entity.Enrollment;
import org.mosip.demo.entity.Person;
import org.mosip.demo.exception.InvalidRequestException;
import org.mosip.demo.masterdata.BlacklistedWord;
import org.mosip.demo.masterdata.DemographicField;
import org.mosip.demo.masterdata.EnrollmentCenter;
import org.mosip.demo.masterdata.MasterData;
import org.mosip.demo.repository.EnrollmentRepository;
import org.mosip.demo.repository.PersonRepository;
import org.mosip.demo.service.SchedulingService;
//import org.mosip.kernel.core.audit.builder.AuditRequestBuilder;
//import org.mosip.kernel.core.audit.handler.AuditHandler;
import org.mosip.kernel.core.logging.Logger;
import org.mosip.kernel.core.logging.appenders.MosipFileAppender;
import org.mosip.kernel.core.logging.factory.Logfactory;
import org.mosip.kernel.core.mosipsecurity.constants.MosipSecurityMethod;
import org.mosip.kernel.core.mosipsecurity.decryption.MosipDecryptor;
import org.mosip.kernel.core.mosipsecurity.encryption.MosipEncryptor;
import org.mosip.kernel.core.mosipsecurity.exception.MosipInvalidDataException;
import org.mosip.kernel.core.mosipsecurity.exception.MosipInvalidKeyException;
import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.kernel.core.utils.exception.MosipJsonGenerationException;
import org.mosip.kernel.core.utils.exception.MosipJsonMappingException;
import org.mosip.kernel.core.utils.exception.MosipJsonParseException;
import org.mosip.kernel.core.utils.json.JsonUtil;
import org.mosip.kernel.core.utils.string.StringUtil;
import org.mosip.kernel.core.utils.zip.ZipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Scheduling service implementation with functions to get all enrollment
 * centers and add new enrollment schedule
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
@Transactional
public class SchedulingServiceImpl implements SchedulingService {

	/**
	 * 
	 */
	private static final String CONSTBORDER = "**********************";

	private RestTemplate restTemplate;

	private static final String RESOURCE_URL = "http://localhost:8080/otpmanager/";

	private static final String DEMO_FOLDER = "D:\\project\\mosip\\mosip-code\\mosip-root\\core-kernel\\Demo\\";

	/**
	 * Console Appender is used to append logs in specified format on the console
	 */
	private static final MosipFileAppender FILE_APPENDER = new MosipFileAppender();
	static {
		FILE_APPENDER.setAppenderName("PREENROLLMENT-APPENDER");
		FILE_APPENDER.setFileName("D:/logger.log");
	}

	/**
	 * Default Mosip console logger instance
	 */
	private static final Logger LOGGER = Logfactory.getFileLogger(FILE_APPENDER,
			SchedulingController.class);

	/**
	 * Field for {@link EnrollmentDao} having data access operations related to
	 * enrollment
	 */
	@Autowired
	private EnrollmentRepository enrollmentRepository;

	/**
	 * Field for {@link PersonDao} having data acces operations related to Person
	 */
	@Autowired
	private PersonRepository personRepository;

	/**
	 * Field for audit handler
	 */
//	 @Autowired
//	 private AuditHandler handler;

	@Autowired
	public void restTemplateBeanBuilder(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	/* (non-Javadoc)
	 * @see org.mosip.demo.service.SchedulingService#getEnrolmentCenters()
	 */
	@Override
	public List<Enrollment> getEnrolmentCenters() {

		// **********************
		// LogManager Demo
		// **********************
		LOGGER.debug(getSessionId(), "NA", "NA",
				"Function to show how debug can be used. Here getEnrolmentCenters() is called");

		LOGGER.info(getSessionId(), "NA", "NA", "getEnrolmentCenters() called");

		// **********************
		// Data Access findall Demo
		// **********************
		List<Enrollment> enrollments = enrollmentRepository.findAll(Enrollment.class);

		LOGGER.debug(getSessionId(), "NA", "NA", "This is demo to show getEnrolmentCenters() return value");

		LOGGER.info(getSessionId(), "NA", "NA", "getEnrolmentCenters() returned " + enrollments);

		return enrollments;
	}

	private String getSessionId() {
		return String.valueOf(UUID.randomUUID().toString());
	}

	/* (non-Javadoc)
	 * @see org.mosip.demo.service.SchedulingService#addSchedule(org.mosip.demo.dto.PersonDto)
	 */
	@Override
	public Person addSchedule(PersonDto personDto) {

		// **********************
		// Exception Demo
		// **********************
		if (personDto == null) {
			throw new InvalidRequestException("PRE-SCH-ERR-012", "Invalid Request");
		}

		// **********************
		// StringUtil Demo
		// **********************
		String actor = StringUtil.capitalizeFirstLetter(personDto.getFirstName());

		// **********************
		// Audit Manager JavaApi Demo
		// **********************
//		 AuditRequestBuilder initialAuditRequest = new AuditRequestBuilder()
//		 .setActionTimeStamp(OffsetDateTime.now())
//		 .setApplicationId("DEMO-SCH-001")
//		 .setApplicationName("Demo Application")
//		 .setCreatedBy(actor)
//		 .setDescription("Address: "+personDto.getAddress())
//		 .setEventId("PRE-ID-121")
//		 .setEventName("Adding new preid issuance schedule")
//		 .setEventType("ADDING_NEW_ENROLLMENT")
//		 .setHostIp("hostIp")
//		 .setHostName("hostName")
//		 .setId("NA")
//		 .setIdType("NA")
//		 .setModuleId("PRE-ID-201")
//		 .setModuleName("SCH-CK-021")
//		 .setSessionUserId("997824c5-qe11-zaf0-66826-2t64c4c5b0bb")
//		 .setSessionUserName(actor);
//		 handler.writeAudit(initialAuditRequest.build());

		Enrollment enrolment = new Enrollment();
		enrolment.setEnrollmentId(personDto.getEnrollmentId());
		Person person = new Person();
		person.setAddress(personDto.getAddress());
		person.setAge(personDto.getAge());
		person.setFirstName(personDto.getFirstName());
		person.setLastName(personDto.getLastName());
		person.setEnrollmentCenter(enrolment);

		// **********************
		// Data Access Create Demo
		// **********************
		Person createdPerson = personRepository.create(person);

		return createdPerson;
	}

	@Override
	public boolean daoDemo() {

		// **********************
		// Data access findById Demo
		// **********************
		System.out.println(CONSTBORDER);
		System.out.println(enrollmentRepository.findById(Enrollment.class, 2).toString());
		System.out.println(CONSTBORDER);
		
		// **********************
		// Data access delete Demo
		// **********************
		enrollmentRepository.delete(Enrollment.class, 2);

		// **********************
		// Data access createQuerySelect Demo
		// **********************
		String qlString = "SELECT enrollmentCenter FROM " + Enrollment.class.getSimpleName()
				+ " enrollmentCenter WHERE enrollmentCenter.enrollmentCenterName LIKE :name";
		HashMap<String, Object> params = new HashMap<>();
		params.put("name", "Bangalore Main");
		System.out.println(CONSTBORDER);
		System.out.println(enrollmentRepository.createQuerySelect(qlString, params).toString());
		System.out.println(CONSTBORDER);
		
		// **********************
		// Data access createQueryUpdateOrDelete Demo
		// **********************
		qlString = "UPDATE  " + Enrollment.class.getSimpleName()
				+ " SET enrollmentCenterName = :name WHERE enrollmentId=:id";
		params.clear();
		params.put("id", 1);
		params.put("name", "Pune Main");
		enrollmentRepository.createQueryUpdateOrDelete(qlString, params);

		// **********************
		// Data access createNamedQuerySelect Demo
		// **********************
		String name = "findAllCenterWithName";
		params.clear();
		params.put("name", "Pune Main");
		System.out.println(CONSTBORDER);
		System.out.println(enrollmentRepository.createNamedQuerySelect(name, Enrollment.class, params).toString());
		System.out.println(CONSTBORDER);
		
		return true;
	}

	@Override
	public boolean securitydemo()
			throws IOException, MosipInvalidDataException, MosipInvalidKeyException, NoSuchAlgorithmException {

		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
		gen.initialize(2048);
		KeyPair key = gen.genKeyPair();

		File file = new File(DEMO_FOLDER + "SecurityDemo\\java.jpg");

		byte[] data = Files.readAllBytes(file.toPath());

		// **********************
		// Encryption Demo
		// **********************
		byte[] encrypted = MosipEncryptor.asymmetricPublicEncrypt(key.getPublic().getEncoded(), data,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);

		FileUtils.writeByteArrayToFile(new File(DEMO_FOLDER + "SecurityDemo\\encrypted.jpg"), encrypted);

		// **********************
		// Decryption Demo
		// **********************
		byte[] decrypted = MosipDecryptor.asymmetricPrivateDecrypt(key.getPrivate().getEncoded(), encrypted,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);

		FileUtils.writeByteArrayToFile(new File(DEMO_FOLDER + "SecurityDemo\\decrypted.jpg"), decrypted);

		return true;
	}

	@Override
	public boolean jsonDemo()
			throws MosipJsonGenerationException, MosipJsonMappingException, MosipIOException, MosipJsonParseException {
		MasterData masterData = new MasterData();
		ArrayList<BlacklistedWord> blacklistedWords = new ArrayList<>();
		blacklistedWords.add(new BlacklistedWord("@#$%"));
		List<DemographicField> demographicFields = new ArrayList<>();
		demographicFields.add(new DemographicField("Name"));
		demographicFields.add(new DemographicField("Age"));
		List<EnrollmentCenter> enrollmentCenters = new ArrayList<>();
		enrollmentCenters.add(new EnrollmentCenter("Bangalore Main", "KA-BAN-023", "Global village"));
		enrollmentCenters.add(new EnrollmentCenter("Bangalore Central", "KA-BAN-403", "Indira Nagar"));
		enrollmentCenters.add(new EnrollmentCenter("Chennai Main", "TN-CHE-176", "Neville Block"));

		masterData.setVersion("1.0.0");
		masterData.setBlacklistedWords(blacklistedWords);
		masterData.setDemographicFields(demographicFields);
		masterData.setEnrollmentCenters(enrollmentCenters);

		// **********************
		// JsonUtil Demo
		// **********************
		JsonUtil.javaObjectToJsonFile(masterData, "test.json");

		MasterData masterDataFromJson = (MasterData) JsonUtil.jsonFileToJavaObject(MasterData.class, "test.json");
		System.out.println(CONSTBORDER);
		System.out.println(masterDataFromJson.getVersion());
		System.out.println(masterDataFromJson.getBlacklistedWords().toString());
		System.out.println(masterDataFromJson.getDemographicFields().toString());
		System.out.println(masterDataFromJson.getEnrollmentCenters().toString());
		System.out.println(CONSTBORDER);
		return true;
	}

	@Override
	public boolean zipDemo() throws MosipIOException {

		String inputDir = DEMO_FOLDER + "ZipDemo\\testDir";
		String outputZipLocation = DEMO_FOLDER + "ZipDemo\\output.zip";

		// **********************
		// ZipUtil Demo
		// **********************
		ZipUtil.zipDirectory(inputDir, outputZipLocation);

		String outputDir = DEMO_FOLDER + "ZipDemo\\outputDir";
		ZipUtil.unZipDirectory(outputZipLocation, outputDir);

		return true;
	}

	@Override
	public OtpGeneratorResponseDto getOtp(OtpGeneratorRequestDto generatorRequestDto) {

		Map<String, String> map = new HashMap<>();
		map.put("key", generatorRequestDto.getKey());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		final HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, headers);

		// **********************
		// OTP Generator Demo
		// **********************
		ResponseEntity<OtpGeneratorResponseDto> responseEntity = restTemplate.exchange(RESOURCE_URL + "otps",
				HttpMethod.POST, entity, OtpGeneratorResponseDto.class);

		return responseEntity.getBody();
	}

	@Override
	public OtpValidatorResponseDto validateOtp(String key, String otp)
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		OtpValidatorResponseDto otpValidatorResponseDto = new OtpValidatorResponseDto();

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(RESOURCE_URL + "otps").queryParam("key", key)
				.queryParam("otp", otp);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<OtpValidatorResponseDto> responseEntity = null;

		// **********************
		// OTP Validator Demo
		// **********************
		try {
			responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
					OtpValidatorResponseDto.class);
		} catch (HttpStatusCodeException e) {
			otpValidatorResponseDto = (OtpValidatorResponseDto) JsonUtil
					.jsonStringToJavaObject(OtpValidatorResponseDto.class, e.getResponseBodyAsString());

		}
		if (responseEntity != null) {
			otpValidatorResponseDto = responseEntity.getBody();
		}
		return otpValidatorResponseDto;

	}

}
