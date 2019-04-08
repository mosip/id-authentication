package io.mosip.kernel.syncdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.syncdata.entity.AppAuthenticationMethod;
import io.mosip.kernel.syncdata.entity.AppDetail;
import io.mosip.kernel.syncdata.entity.AppRolePriority;
import io.mosip.kernel.syncdata.entity.ApplicantValidDocument;
import io.mosip.kernel.syncdata.entity.Application;
import io.mosip.kernel.syncdata.entity.BiometricAttribute;
import io.mosip.kernel.syncdata.entity.BiometricType;
import io.mosip.kernel.syncdata.entity.BlacklistedWords;
import io.mosip.kernel.syncdata.entity.Device;
import io.mosip.kernel.syncdata.entity.DeviceSpecification;
import io.mosip.kernel.syncdata.entity.DeviceType;
import io.mosip.kernel.syncdata.entity.DocumentCategory;
import io.mosip.kernel.syncdata.entity.DocumentType;
import io.mosip.kernel.syncdata.entity.Gender;
import io.mosip.kernel.syncdata.entity.Holiday;
import io.mosip.kernel.syncdata.entity.IdType;
import io.mosip.kernel.syncdata.entity.IndividualType;
import io.mosip.kernel.syncdata.entity.Language;
import io.mosip.kernel.syncdata.entity.Location;
import io.mosip.kernel.syncdata.entity.Machine;
import io.mosip.kernel.syncdata.entity.MachineSpecification;
import io.mosip.kernel.syncdata.entity.MachineType;
import io.mosip.kernel.syncdata.entity.ProcessList;
import io.mosip.kernel.syncdata.entity.ReasonCategory;
import io.mosip.kernel.syncdata.entity.ReasonList;
import io.mosip.kernel.syncdata.entity.RegistrationCenter;
import io.mosip.kernel.syncdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.syncdata.entity.RegistrationCenterDeviceHistory;
import io.mosip.kernel.syncdata.entity.RegistrationCenterDeviceHistoryPk;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachineDeviceHistory;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachineHistory;
import io.mosip.kernel.syncdata.entity.RegistrationCenterType;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUser;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUserHistory;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUserMachine;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.syncdata.entity.ScreenAuthorization;
import io.mosip.kernel.syncdata.entity.ScreenDetail;
import io.mosip.kernel.syncdata.entity.Template;
import io.mosip.kernel.syncdata.entity.TemplateFileFormat;
import io.mosip.kernel.syncdata.entity.TemplateType;
import io.mosip.kernel.syncdata.entity.Title;
import io.mosip.kernel.syncdata.entity.ValidDocument;
import io.mosip.kernel.syncdata.entity.id.ApplicantValidDocumentID;
import io.mosip.kernel.syncdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.syncdata.entity.id.HolidayID;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterDeviceID;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineDeviceHistoryID;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineDeviceID;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineHistoryID;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineID;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineUserID;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterUserID;
import io.mosip.kernel.syncdata.repository.AppAuthenticationMethodRepository;
import io.mosip.kernel.syncdata.repository.AppDetailRepository;
import io.mosip.kernel.syncdata.repository.AppRolePriorityRepository;
import io.mosip.kernel.syncdata.repository.ApplicantValidDocumentRespository;
import io.mosip.kernel.syncdata.repository.ApplicationRepository;
import io.mosip.kernel.syncdata.repository.BiometricAttributeRepository;
import io.mosip.kernel.syncdata.repository.BiometricTypeRepository;
import io.mosip.kernel.syncdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.syncdata.repository.DeviceRepository;
import io.mosip.kernel.syncdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.syncdata.repository.DeviceTypeRepository;
import io.mosip.kernel.syncdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.syncdata.repository.DocumentTypeRepository;
import io.mosip.kernel.syncdata.repository.GenderRepository;
import io.mosip.kernel.syncdata.repository.HolidayRepository;
import io.mosip.kernel.syncdata.repository.IdTypeRepository;
import io.mosip.kernel.syncdata.repository.IndividualTypeRepository;
import io.mosip.kernel.syncdata.repository.LanguageRepository;
import io.mosip.kernel.syncdata.repository.LocationRepository;
import io.mosip.kernel.syncdata.repository.MachineRepository;
import io.mosip.kernel.syncdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.syncdata.repository.MachineTypeRepository;
import io.mosip.kernel.syncdata.repository.ProcessListRepository;
import io.mosip.kernel.syncdata.repository.ReasonCategoryRepository;
import io.mosip.kernel.syncdata.repository.ReasonListRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterDeviceHistoryRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterMachineDeviceHistoryRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterMachineDeviceRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterMachineHistoryRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterMachineRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserHistoryRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserMachineHistoryRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserMachineRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserRepository;
import io.mosip.kernel.syncdata.repository.ScreenAuthorizationRepository;
import io.mosip.kernel.syncdata.repository.ScreenDetailRepository;
import io.mosip.kernel.syncdata.repository.TemplateFileFormatRepository;
import io.mosip.kernel.syncdata.repository.TemplateRepository;
import io.mosip.kernel.syncdata.repository.TemplateTypeRepository;
import io.mosip.kernel.syncdata.repository.TitleRepository;
import io.mosip.kernel.syncdata.repository.ValidDocumentRepository;
import io.mosip.kernel.syncdata.service.SyncConfigDetailsService;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class SyncDataIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RestTemplate restTemplate;

	private List<Application> applications;
	private List<Machine> machines;
	private List<MachineSpecification> machineSpecification;
	private List<MachineType> machineType;
	private List<RegistrationCenter> registrationCenters;
	private List<RegistrationCenterType> registrationCenterType;
	private List<Device> devices;
	private List<DeviceSpecification> deviceSpecification;
	private List<DeviceType> deviceType;
	private List<Holiday> holidays;
	private List<BlacklistedWords> blackListedWords;
	private List<Title> titles;
	private List<Gender> genders;
	private List<Language> languages;
	private List<Template> templates;
	private List<TemplateFileFormat> templateFileFormats;
	private List<TemplateType> templateTypes;
	private List<BiometricAttribute> biometricAttributes;
	private List<BiometricType> biometricTypes;
	private List<DocumentCategory> documentCategories;
	private List<DocumentType> documentTypes;
	private List<ValidDocument> validDocuments;
	private List<ReasonCategory> reasonCategories;
	private List<ReasonList> reasonLists;
	private List<IdType> idTypes;
	private List<Location> locations;
	private List<RegistrationCenterMachine> registrationCenterMachines;
	private List<RegistrationCenterDevice> registrationCenterDevices;
	private List<RegistrationCenterMachineDevice> registrationCenterMachineDevices;
	private List<RegistrationCenterUserMachine> registrationCenterUserMachines;
	private List<RegistrationCenterUser> registrationCenterUsers;
	private List<RegistrationCenterMachineHistory> registrationCenterMachineHistory;
	private List<RegistrationCenterDeviceHistory> registrationCenterDeviceHistory;
	private List<RegistrationCenterUserHistory> registrationCenterUserHistory;
	private List<RegistrationCenterMachineDeviceHistory> registrationCenterMachineDeviceHistory;
	private List<RegistrationCenterUserMachineHistory> registrationCenterUserMachineHistory;
	private List<ApplicantValidDocument> applicantValidDocumentList;
	private List<IndividualType> individualTypeList;
	private List<Object[]> objectArrayList;
	private List<AppAuthenticationMethod> appAuthenticationMethods = null;
	private List<AppDetail> appDetails = null;
	private List<AppRolePriority> appRolePriorities = null;
	private List<ScreenAuthorization> screenAuthorizations = null;
	private List<ProcessList> processList = null;
	private List<ScreenDetail> screenDetailList = null;
	@MockBean
	private ApplicationRepository applicationRepository;
	@MockBean
	private MachineRepository machineRepository;
	@MockBean
	private MachineTypeRepository machineTypeRepository;
	@MockBean
	private RegistrationCenterRepository registrationCenterRepository;
	@MockBean
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;
	@MockBean
	private TemplateRepository templateRepository;
	@MockBean
	private TemplateFileFormatRepository templateFileFormatRepository;
	@MockBean
	private ReasonCategoryRepository reasonCategoryRepository;
	@MockBean
	private HolidayRepository holidayRepository;
	@MockBean
	private BlacklistedWordsRepository blacklistedWordsRepository;
	@MockBean
	private BiometricTypeRepository biometricTypeRepository;
	@MockBean
	private BiometricAttributeRepository biometricAttributeRepository;
	@MockBean
	private TitleRepository titleRepository;
	@MockBean
	private LanguageRepository languageRepository;
	@MockBean
	private GenderRepository genderTypeRepository;
	@MockBean
	private DeviceRepository deviceRepository;
	@MockBean
	private DocumentCategoryRepository documentCategoryRepository;
	@MockBean
	private DocumentTypeRepository documentTypeRepository;
	@MockBean
	private IdTypeRepository idTypeRepository;
	@MockBean
	private DeviceSpecificationRepository deviceSpecificationRepository;
	@MockBean
	private LocationRepository locationRepository;
	@MockBean
	private TemplateTypeRepository templateTypeRepository;
	@MockBean
	private MachineSpecificationRepository machineSpecificationRepository;
	@MockBean
	private DeviceTypeRepository deviceTypeRepository;
	@MockBean
	private ValidDocumentRepository validDocumentRepository;
	@MockBean
	private ReasonListRepository reasonListRepository;
	@MockBean
	private RegistrationCenterMachineRepository registrationCenterMachineRepository;
	@MockBean
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;
	@MockBean
	private RegistrationCenterMachineDeviceRepository registrationCenterMachineDeviceRepository;
	@MockBean
	private RegistrationCenterUserMachineRepository registrationCenterUserMachineRepository;
	@MockBean
	private RegistrationCenterUserHistoryRepository registrationCenterUserHistoryRepository;
	@MockBean
	private RegistrationCenterUserMachineHistoryRepository registrationCenterUserMachineHistoryRepository;
	@MockBean
	private RegistrationCenterMachineDeviceHistoryRepository registrationCenterMachineDeviceHistoryRepository;
	@MockBean
	private RegistrationCenterDeviceHistoryRepository registrationCenterDeviceHistoryRepository;
	@MockBean
	private RegistrationCenterMachineHistoryRepository registrationCenterMachineHistoryRepository;

	@MockBean
	private AppAuthenticationMethodRepository appAuthenticationMethodRepository;
	@MockBean
	private AppDetailRepository appDetailRepository;
	@MockBean
	private AppRolePriorityRepository appRolePriorityRepository;
	@MockBean
	private ScreenAuthorizationRepository screenAuthorizationRepository;
	@MockBean
	private ProcessListRepository processListRepository;
	@Autowired
	private SyncConfigDetailsService syncConfigDetailsService;
	@MockBean
	private ScreenDetailRepository screenDetailRepo;

	@Value("${mosip.kernel.syncdata.admin-base-url:http://localhost:8095/admin/syncjobdef}")
	private String baseUri;
	/*
	 * @MockBean private RestTemplate restTemplateM;
	 */

	// ###########################CONFIG START#########################
	private static final String JSON_CONFIG_RESPONSE = "{\r\n" + "\"registrationConfiguration\":\r\n"
			+ "							{\"keyValidityPeriodPreRegPack\":\"3\",\"smsNotificationTemplateRegCorrection\":\"OTP for your request is $otp\",\"defaultDOB\":\"1-Jan\",\"smsNotificationTemplateOtp\":\"OTP for your request is $otp\",\"supervisorVerificationRequiredForExceptions\":\"true\",\"keyValidityPeriodRegPack\":\"3\",\"irisRetryAttempts\":\"10\",\"fingerprintQualityThreshold\":\"120\",\"multifactorauthentication\":\"true\",\"smsNotificationTemplateUpdateUIN\":\"OTP for your request is $otp\",\"supervisorAuthType\":\"password\",\"maxDurationRegPermittedWithoutMasterdataSyncInDays\":\"10\",\"modeOfNotifyingIndividual\":\"mobile\",\"emailNotificationTemplateUpdateUIN\":\"Hello $user the OTP is $otp\",\"maxDocSizeInMB\":\"150\",\"emailNotificationTemplateOtp\":\"Hello $user the OTP is $otp\",\"emailNotificationTemplateRegCorrection\":\"Hello $user the OTP is $otp\",\"faceRetry\":\"12\",\"noOfFingerprintAuthToOnboardUser\":\"10\",\"smsNotificationTemplateLostUIN\":\"OTP for your request is $otp\",\"supervisorAuthMode\":\"IRIS\",\"operatorRegSubmissionMode\":\"fingerprint\",\"officerAuthType\":\"password\",\"faceQualityThreshold\":\"25\",\"gpsDistanceRadiusInMeters\":\"3\",\"automaticSyncFreqServerToClient\":\"25\",\"maxDurationWithoutMasterdataSyncInDays\":\"7\",\"loginMode\":\"bootable dongle\",\"irisQualityThreshold\":\"25\",\"retentionPeriodAudit\":\"3\",\"fingerprintRetryAttempts\":\"234\",\"emailNotificationTemplateNewReg\":\"Hello $user the OTP is $otp\",\"passwordExpiryDurationInDays\":\"3\",\"emailNotificationTemplateLostUIN\":\"Hello $user the OTP is $otp\",\"blockRegistrationIfNotSynced\":\"10\",\"noOfIrisAuthToOnboardUser\":\"10\",\"smsNotificationTemplateNewReg\":\"OTP for your request is $otp\"},\r\n"
			+ "\r\n" + "\"globalConfiguration\":\r\n"
			+ "						{\"mosip.kernel.crypto.symmetric-algorithm-name\":\"AES\",\"mosip.kernel.virus-scanner.port\":\"3310\",\"mosip.kernel.email.max-length\":\"50\",\"mosip.kernel.email.domain.ext-max-lenght\":\"7\",\"mosip.kernel.rid.sequence-length\":\"5\",\"mosip.kernel.uin.uin-generation-cron\":\"0 * * * * *\",\"mosip.kernel.rid.centerid-length\":\"5\",\"mosip.kernel.email.special-char\":\"!#$%&'*+-\\/=?^_`{|}~.\",\"mosip.kernel.rid.timestamp-length\":\"14\",\"mosip.kernel.vid.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.asymmetric-algorithm-length\":\"2048\",\"mosip.kernel.uin.min-unused-threshold\":\"100000\",\"mosip.kernel.prid.sequence-limit\":\"3\",\"auth.role.prefix\":\"ROLE_\",\"mosip.kernel.email.domain.ext-min-lenght\":\"2\",\"auth.server.validate.url\":\"http:\\/\\/localhost:8091\\/auth\\/validate_token\",\"mosip.kernel.machineid.length\":\"4\",\"mosip.supported-languages\":\"eng,ara,fra\",\"mosip.kernel.prid.length\":\"14\",\"auth.header.name\":\"Authorization\",\"mosip.kernel.crypto.asymmetric-algorithm-name\":\"RSA\",\"mosip.kernel.phone.min-length\":\"9\",\"mosip.kernel.uin.length\":\"10\",\"mosip.kernel.virus-scanner.host\":\"104.211.209.102\",\"mosip.kernel.email.min-length\":\"7\",\"mosip.kernel.rid.machineid-length\":\"5\",\"mosip.kernel.prid.repeating-block-limit\":\"3\",\"mosip.kernel.vid.length.repeating-block-limit\":\"2\",\"mosip.kernel.rid.length\":\"29\",\"mosip.kernel.phone.max-length\":\"15\",\"mosip.kernel.prid.repeating-limit\":\"2\",\"mosip.kernel.uin.restricted-numbers\":\"786,666\",\"mosip.kernel.email.domain.special-char\":\"-\",\"mosip.kernel.vid.length.repeating-limit\":\"2\",\"mosip.kernel.registrationcenterid.length\":\"4\",\"mosip.kernel.phone.special-char\":\"+ -\",\"mosip.kernel.uin.uins-to-generate\":\"200000\",\"mosip.kernel.vid.length\":\"16\",\"mosip.kernel.tokenid.length\":\"36\",\"mosip.kernel.uin.length.repeating-block-limit\":\"2\",\"mosip.kernel.tspid.length\":\"4\",\"mosip.kernel.tokenid.sequence-limit\":\"3\",\"mosip.kernel.uin.length.repeating-limit\":\"2\",\"mosip.kernel.uin.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.symmetric-algorithm-length\":\"256\",\"mosip.kernel.data-key-splitter\":\"#KEY_SPLITTER#\"}\r\n"
			+ "}";
	private static final String JSON_REGISTRATION_CONFIG_RESPONSE = "{\"keyValidityPeriodPreRegPack\":\"3\",\"smsNotificationTemplateRegCorrection\":\"OTP for your request is $otp\",\"defaultDOB\":\"1-Jan\",\"smsNotificationTemplateOtp\":\"OTP for your request is $otp\",\"supervisorVerificationRequiredForExceptions\":\"true\",\"keyValidityPeriodRegPack\":\"3\",\"irisRetryAttempts\":\"10\",\"fingerprintQualityThreshold\":\"120\",\"multifactorauthentication\":\"true\",\"smsNotificationTemplateUpdateUIN\":\"OTP for your request is $otp\",\"supervisorAuthType\":\"password\",\"maxDurationRegPermittedWithoutMasterdataSyncInDays\":\"10\",\"modeOfNotifyingIndividual\":\"mobile\",\"emailNotificationTemplateUpdateUIN\":\"Hello $user the OTP is $otp\",\"maxDocSizeInMB\":\"150\",\"emailNotificationTemplateOtp\":\"Hello $user the OTP is $otp\",\"emailNotificationTemplateRegCorrection\":\"Hello $user the OTP is $otp\",\"faceRetry\":\"12\",\"noOfFingerprintAuthToOnboardUser\":\"10\",\"smsNotificationTemplateLostUIN\":\"OTP for your request is $otp\",\"supervisorAuthMode\":\"IRIS\",\"operatorRegSubmissionMode\":\"fingerprint\",\"officerAuthType\":\"password\",\"faceQualityThreshold\":\"25\",\"gpsDistanceRadiusInMeters\":\"3\",\"automaticSyncFreqServerToClient\":\"25\",\"maxDurationWithoutMasterdataSyncInDays\":\"7\",\"loginMode\":\"bootable dongle\",\"irisQualityThreshold\":\"25\",\"retentionPeriodAudit\":\"3\",\"fingerprintRetryAttempts\":\"234\",\"emailNotificationTemplateNewReg\":\"Hello $user the OTP is $otp\",\"passwordExpiryDurationInDays\":\"3\",\"emailNotificationTemplateLostUIN\":\"Hello $user the OTP is $otp\",\"blockRegistrationIfNotSynced\":\"10\",\"noOfIrisAuthToOnboardUser\":\"10\",\"smsNotificationTemplateNewReg\":\"OTP for your request is $otp\"}";
	private static final String JSON_GLOBAL_CONFIG_RESPONSE = "{\"mosip.kernel.crypto.symmetric-algorithm-name\":\"AES\",\"mosip.kernel.virus-scanner.port\":\"3310\",\"mosip.kernel.email.max-length\":\"50\",\"mosip.kernel.email.domain.ext-max-lenght\":\"7\",\"mosip.kernel.rid.sequence-length\":\"5\",\"mosip.kernel.uin.uin-generation-cron\":\"0 * * * * *\",\"mosip.kernel.rid.centerid-length\":\"5\",\"mosip.kernel.email.special-char\":\"!#$%&'*+-\\/=?^_`{|}~.\",\"mosip.kernel.rid.timestamp-length\":\"14\",\"mosip.kernel.vid.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.asymmetric-algorithm-length\":\"2048\",\"mosip.kernel.uin.min-unused-threshold\":\"100000\",\"mosip.kernel.prid.sequence-limit\":\"3\",\"auth.role.prefix\":\"ROLE_\",\"mosip.kernel.email.domain.ext-min-lenght\":\"2\",\"auth.server.validate.url\":\"http:\\/\\/localhost:8091\\/auth\\/validate_token\",\"mosip.kernel.machineid.length\":\"4\",\"mosip.supported-languages\":\"eng,ara,fra\",\"mosip.kernel.prid.length\":\"14\",\"auth.header.name\":\"Authorization\",\"mosip.kernel.crypto.asymmetric-algorithm-name\":\"RSA\",\"mosip.kernel.phone.min-length\":\"9\",\"mosip.kernel.uin.length\":\"10\",\"mosip.kernel.virus-scanner.host\":\"104.211.209.102\",\"mosip.kernel.email.min-length\":\"7\",\"mosip.kernel.rid.machineid-length\":\"5\",\"mosip.kernel.prid.repeating-block-limit\":\"3\",\"mosip.kernel.vid.length.repeating-block-limit\":\"2\",\"mosip.kernel.rid.length\":\"29\",\"mosip.kernel.phone.max-length\":\"15\",\"mosip.kernel.prid.repeating-limit\":\"2\",\"mosip.kernel.uin.restricted-numbers\":\"786,666\",\"mosip.kernel.email.domain.special-char\":\"-\",\"mosip.kernel.vid.length.repeating-limit\":\"2\",\"mosip.kernel.registrationcenterid.length\":\"4\",\"mosip.kernel.phone.special-char\":\"+ -\",\"mosip.kernel.uin.uins-to-generate\":\"200000\",\"mosip.kernel.vid.length\":\"16\",\"mosip.kernel.tokenid.length\":\"36\",\"mosip.kernel.uin.length.repeating-block-limit\":\"2\",\"mosip.kernel.tspid.length\":\"4\",\"mosip.kernel.tokenid.sequence-limit\":\"3\",\"mosip.kernel.uin.length.repeating-limit\":\"2\",\"mosip.kernel.uin.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.symmetric-algorithm-length\":\"256\",\"mosip.kernel.data-key-splitter\":\"#KEY_SPLITTER#\"}";
	private static final String JSON_SYNC_JOB_DEF = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-04-02T07:49:18.454Z\", \"metadata\": null, \"response\": { \"syncJobDefinitions\": [ { \"id\": \"LCS_J00002\", \"name\": \"Login Credentials Sync\", \"apiName\": null, \"parentSyncJobId\": \"NULL\", \"syncFreq\": \"0 0 11 * * ?\", \"lockDuration\": \"NULL\" } ] }, \"errors\": null } ";
	// ###########################CONFIG END#########################

	@MockBean
	private RegistrationCenterUserRepository registrationCenterUserRepository;

	@MockBean
	private ApplicantValidDocumentRespository applicantValidDocumentRespository;
	@MockBean
	private IndividualTypeRepository individualTypeRepository;

	StringBuilder builder;

	@Value("${mosip.kernel.syncdata.auth-manager-base-uri}")
	private String authBaseUri;

	@Value("${mosip.kernel.syncdata.auth-manager-roles}")
	private String authAllRolesUri;

	private String syncDataUrlMacAdress = "/masterdata?macaddress=e1:01:2b:c2:1d:b0";
	private String syncDataUrlSerialNum = "/masterdata?serialnumber=NM5328114630";
	private String syncDataUrl = "/masterdata?lastupdated=ssserialnumber=NM5328114630&macAddress=e1:01:2b:c2:1d:b0";
	private String syncDataUrlWithRegId = "/masterdata/{regcenterId}?serialnumber=NM532811463";
	private String syncDataUrlWithoutMacAddressAndSno = "/masterdata";

	@Before
	public void setup() {
		LocalDateTime localdateTime = LocalDateTime.parse("2018-11-01T01:01:01");
		LocalTime localTime = LocalTime.parse("09:00:00");
		applications = new ArrayList<>();
		applications.add(new Application("101", "ENG", "MOSIP", "MOSIP"));
		machines = new ArrayList<>();
		machines.add(new Machine("1001", "Laptop", "9876427", "172.12.01.128", "21:21:21:12", "1001", "ENG",
				localdateTime, null));
		machineSpecification = new ArrayList<>();
		machineSpecification.add(
				new MachineSpecification("1001", "Laptop", "Lenovo", "T480", "1001", "1.0", "Laptop", "ENG", null));
		machineType = new ArrayList<>();
		machineType.add(new MachineType("1001", "ENG", "System", "System"));
		devices = new ArrayList<>();
		Device device = new Device();
		device.setId("1000");
		device.setName("Printer");
		device.setLangCode("eng");
		device.setIsActive(true);
		device.setMacAddress("127.0.0.0");
		device.setIpAddress("127.0.0.10");
		device.setSerialNum("234");
		device.setDeviceSpecId("234");
		device.setValidityDateTime(localdateTime);
		devices.add(device);

		deviceSpecification = new ArrayList<>();
		deviceSpecification.add(new DeviceSpecification("1011", "SP-1011", "HP", "E1011", "T1011", "1.0", "HP-SP1011",
				"Hp Printer", null));
		deviceType = new ArrayList<>();
		deviceType.add(new DeviceType("T1011", "ENG", "device", "deviceDescriptiom"));
		registrationCenters = new ArrayList<>();
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setId("1011");
		registrationCenter.setAddressLine1("address-line1");
		registrationCenter.setAddressLine2("address-line2");
		registrationCenter.setAddressLine3("address-line3");
		registrationCenter.setCenterEndTime(localTime);
		registrationCenter.setCenterStartTime(localTime);
		registrationCenter.setCenterTypeCode("T1011");
		registrationCenter.setContactPerson("admin");
		registrationCenter.setContactPhone("9865123456");
		registrationCenter.setHolidayLocationCode("LOC01");
		registrationCenter.setIsActive(true);
		registrationCenter.setLangCode("ENG");
		registrationCenter.setWorkingHours("9");
		registrationCenter.setLunchEndTime(localTime);
		registrationCenter.setLunchStartTime(localTime);
		registrationCenters.add(registrationCenter);

		registrationCenterType = new ArrayList<>();
		RegistrationCenterType regCenterType = new RegistrationCenterType();
		regCenterType.setCode("T01");
		registrationCenterType.add(regCenterType);

		templates = new ArrayList<>();
		Template template = new Template();
		template.setId("T222");
		template.setLangCode("eng");
		template.setName("Email template");
		template.setTemplateTypeCode("EMAIL");
		template.setFileFormatCode("XML");
		template.setModuleId("preregistation");
		template.setIsActive(Boolean.TRUE);
		templates.add(template);
		templateFileFormats = new ArrayList<>();
		templateFileFormats.add(new TemplateFileFormat("T101", "ENG", "Email"));
		templateTypes = new ArrayList<>();
		templateTypes.add(new TemplateType("T101", "ENG", "Description"));
		holidays = new ArrayList<>();
		Holiday holiday = new Holiday();
		LocalDate date = LocalDate.of(2018, Month.NOVEMBER, 7);
		holiday = new Holiday();
		holiday.setHolidayId(new HolidayID("KAR", date, "eng", "Diwali"));
		holiday.setId(1);
		holiday.setCreatedBy("John");
		holiday.setCreatedDateTime(localdateTime);
		holiday.setHolidayDesc("Diwali");
		holiday.setIsActive(true);

		Holiday holiday2 = new Holiday();
		holiday2.setHolidayId(new HolidayID("KAH", date, "eng", "Durga Puja"));
		holiday2.setId(1);
		holiday2.setCreatedBy("John");
		holiday2.setCreatedDateTime(localdateTime);
		holiday2.setHolidayDesc("Diwali");
		holiday2.setIsActive(true);

		holidays.add(holiday);
		holidays.add(holiday2);
		blackListedWords = new ArrayList<>();
		blackListedWords.add(new BlacklistedWords("ABC", "ENG", "description"));
		titles = new ArrayList<>();
		titles.add(new Title(new CodeAndLanguageCodeID("1011", "ENG"), "title", "titleDescription"));
		genders = new ArrayList<>();
		genders.add(new Gender("G1011", "MALE", "description"));
		languages = new ArrayList<>();
		languages.add(new Language("ENG", "english", "family", "native name"));
		idTypes = new ArrayList<>();
		idTypes.add(new IdType("ID101", "ENG", "ID", "descr"));
		validDocuments = new ArrayList<>();
		validDocuments.add(new ValidDocument("D101", "DC101", null, null, "ENG"));
		biometricAttributes = new ArrayList<>();
		biometricAttributes.add(new BiometricAttribute("B101", "101", "Fingerprint", "description", "BT101", null));
		biometricTypes = new ArrayList<>();
		biometricTypes.add(new BiometricType("BT101", "ENG", "name", "description"));
		documentCategories = new ArrayList<>();
		documentCategories.add(new DocumentCategory("DC101", "ENG", "DC name", "description"));
		documentTypes = new ArrayList<>();
		documentTypes.add(new DocumentType("DT101", "ENG", "DT Type", "description"));
		reasonCategories = new ArrayList<>();
		reasonCategories.add(new ReasonCategory("RC101", "101", "R-1", "description", null));
		reasonLists = new ArrayList<>();
		reasonLists.add(new ReasonList("RL101", "RL1", "ENG", "RL", "description", null));
		locations = new ArrayList<>();
		Location locationHierarchy = new Location();
		locationHierarchy.setCode("PAT");
		locationHierarchy.setName("PATANA");
		locationHierarchy.setHierarchyLevel(2);
		locationHierarchy.setHierarchyName("Distic");
		locationHierarchy.setParentLocCode("BHR");
		locationHierarchy.setLangCode("ENG");
		locationHierarchy.setCreatedBy("admin");
		locationHierarchy.setUpdatedBy("admin");
		locationHierarchy.setIsActive(true);
		locations.add(locationHierarchy);
		registrationCenterMachines = new ArrayList<>();
		RegistrationCenterMachineID rmId = new RegistrationCenterMachineID();
		rmId.setMachineId("10001");
		rmId.setRegCenterId("10001");
		RegistrationCenterMachine registrationCenterMachine = new RegistrationCenterMachine();
		registrationCenterMachine.setRegistrationCenterMachinePk(rmId);
		registrationCenterMachine.setIsActive(true);
		registrationCenterMachine.setLangCode("eng");
		registrationCenterMachine.setCreatedBy("admin");
		registrationCenterMachine.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		registrationCenterMachine.setIsDeleted(false);
		registrationCenterMachines.add(registrationCenterMachine);
		registrationCenterDevices = new ArrayList<>();
		RegistrationCenterDevice registrationCenterDevice = new RegistrationCenterDevice();
		RegistrationCenterDeviceID rcId = new RegistrationCenterDeviceID();
		rcId.setDeviceId("10001");
		rcId.setRegCenterId("10001");
		registrationCenterDevice.setRegistrationCenterDevicePk(rcId);
		registrationCenterDevice.setIsActive(true);
		registrationCenterDevice.setLangCode("eng");
		registrationCenterDevice.setCreatedBy("admin");
		registrationCenterDevice.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		registrationCenterDevice.setIsDeleted(false);
		registrationCenterDevices.add(registrationCenterDevice);
		RegistrationCenterMachineDevice registrationCenterMachineDevice = new RegistrationCenterMachineDevice();
		RegistrationCenterMachineDeviceID rcmdId = new RegistrationCenterMachineDeviceID();
		rcmdId.setDeviceId("101");
		rcmdId.setMachineId("1789");
		rcmdId.setRegCenterId("1");
		registrationCenterMachineDevice.setRegistrationCenterMachineDevicePk(rcmdId);
		registrationCenterMachineDevice.setIsActive(true);
		registrationCenterMachineDevice.setLangCode("eng");
		registrationCenterMachineDevice.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		registrationCenterMachineDevice.setCreatedBy("admin");

		registrationCenterMachineDevices = new ArrayList<>();
		registrationCenterMachineDevices.add(registrationCenterMachineDevice);
		RegistrationCenterUserMachine registrationCenterUserMachine = new RegistrationCenterUserMachine();
		RegistrationCenterMachineUserID registrationCenterMachineUserID = new RegistrationCenterMachineUserID();
		registrationCenterMachineUserID.setCntrId("REG001");
		registrationCenterMachineUserID.setUsrId("QC001");
		registrationCenterMachineUserID.setMachineId("MAC001");
		registrationCenterUserMachine.setLangCode("eng");
		registrationCenterUserMachine.setRegistrationCenterMachineUserID(registrationCenterMachineUserID);
		registrationCenterUserMachines = new ArrayList<>();
		registrationCenterUserMachines.add(registrationCenterUserMachine);
		registrationCenterUsers = new ArrayList<>();
		registrationCenterUsers.add(new RegistrationCenterUser(new RegistrationCenterUserID("01010", "qc001"), "eng"));

		builder = new StringBuilder();
		builder.append(authBaseUri).append(authAllRolesUri);

		registrationCenterDeviceHistory = new ArrayList<>();
		registrationCenterDeviceHistory.add(new RegistrationCenterDeviceHistory(
				new RegistrationCenterDeviceHistoryPk("1001", "1001", LocalDateTime.now()), "eng"));

		registrationCenterMachineDeviceHistory = new ArrayList<>();
		registrationCenterMachineDeviceHistory.add(new RegistrationCenterMachineDeviceHistory(
				new RegistrationCenterMachineDeviceHistoryID("1001", "1001", "1001", LocalDateTime.now()), "eng"));

		registrationCenterMachineHistory = new ArrayList<>();
		registrationCenterMachineHistory.add(new RegistrationCenterMachineHistory(
				new RegistrationCenterMachineHistoryID("1001", "1001", LocalDateTime.now()), "eng"));

		registrationCenterUserHistory = new ArrayList<>();
		registrationCenterUserHistory
				.add(new RegistrationCenterUserHistory("1001", "1001", LocalDateTime.now(), "eng"));

		registrationCenterUserMachineHistory = new ArrayList<>();
		registrationCenterUserMachineHistory
				.add(new RegistrationCenterUserMachineHistory("10001", "1001", "10001", LocalDateTime.now(), "eng"));
		IndividualType individualType = new IndividualType();
		CodeAndLanguageCodeID codeLangCode = new CodeAndLanguageCodeID();
		codeLangCode.setCode("FR");
		codeLangCode.setLangCode("ENG");
		individualType.setName("Foreigner");
		individualType.setCodeAndLanguageCodeId(codeLangCode);
		individualTypeList = new ArrayList<>();
		individualTypeList.add(individualType);
		ApplicantValidDocument applicantValidDoc = new ApplicantValidDocument();
		ApplicantValidDocumentID appId = new ApplicantValidDocumentID();
		appId.setAppTypeCode("001");
		appId.setDocCatCode("POA");
		appId.setDocTypeCode("RNC");
		applicantValidDoc.setApplicantValidDocumentId(appId);
		applicantValidDoc.setLangCode("eng");
		applicantValidDocumentList = new ArrayList<>();
		applicantValidDocumentList.add(applicantValidDoc);
		Object[] objects = { "10001", "10001" };
		objectArrayList = new ArrayList<>();
		objectArrayList.add(objects);
		AppAuthenticationMethod appAuthenticationMethod = new AppAuthenticationMethod();
		appAuthenticationMethod.setAppId("REGISTRATION");
		appAuthenticationMethod.setAuthMethodCode("sddd");
		appAuthenticationMethod.setMethodSequence(1000);
		appAuthenticationMethods = new ArrayList<>();
		appAuthenticationMethods.add(appAuthenticationMethod);
		AppDetail appDetail = new AppDetail();
		appDetail.setDescr("reg");
		appDetail.setId("1");
		appDetail.setLangCode("eng");
		appDetail.setName("reg");
		appDetails = new ArrayList<>();
		appDetails.add(appDetail);
		AppRolePriority appRolePriority = new AppRolePriority();
		appRolePriority.setAppId("10001");
		appRolePriority.setLangCode("eng");
		appRolePriority.setPriority(1);
		appRolePriority.setProcessId("login_auth");
		appRolePriority.setRoleCode("OFFICER");
		appRolePriorities = new ArrayList<>();
		appRolePriorities.add(appRolePriority);
		ScreenAuthorization screenAuthorization = new ScreenAuthorization();
		screenAuthorization.setIsPermitted(true);
		screenAuthorization.setRoleCode("OFFICER");
		screenAuthorization.setScreenId("loginroot");
		screenAuthorizations = new ArrayList<>();
		screenAuthorizations.add(screenAuthorization);
		ProcessList processListObj = new ProcessList();
		processListObj.setDescr("Packet authentication");
		processListObj.setName("packet authentication");
		processListObj.setLangCode("eng");
		processList = new ArrayList<>();
		processList.add(processListObj);
		ScreenDetail screenDetail = new ScreenDetail();
		screenDetail.setAppId("REGISTRATION");
		screenDetail.setId("REG");
		screenDetail.setDescr("registration");
		screenDetail.setLangCode("eng");
		screenDetailList = new ArrayList<>();
		screenDetailList.add(screenDetail);

	}

	private void mockSuccess() {

		when(registrationCenterRepository.findRegistrationCenterByIdAndIsActiveIsTrue(Mockito.anyString()))
				.thenReturn(registrationCenters);
		when(registrationCenterMachineRepository.getRegCenterIdWithRegIdAndMachineId(Mockito.anyString(),
				Mockito.anyString())).thenReturn(registrationCenterMachines.get(0));
		when(registrationCenterMachineRepository.getRegistrationCenterMachineWithMacAddress(Mockito.anyString()))
				.thenReturn(objectArrayList);
		when(registrationCenterMachineRepository.getRegistrationCenterMachineWithSerialNumber(Mockito.anyString()))
				.thenReturn(objectArrayList);
		when(registrationCenterMachineRepository
				.getRegistrationCenterMachineWithMacAddressAndSerialNum(Mockito.anyString(), Mockito.anyString()))
						.thenReturn(objectArrayList);
		when(applicationRepository.findAll()).thenReturn(applications);
		when(applicationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenReturn(applications);
		when(machineRepository.findMachineById(Mockito.anyString())).thenReturn(machines);
		when(machineRepository.findAllLatestCreatedUpdateDeleted(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn(machines);
		when(machineSpecificationRepository.findByMachineId(Mockito.anyString())).thenReturn(machineSpecification);
		when(machineSpecificationRepository.findLatestByRegCenterId(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn(machineSpecification);
		when(machineTypeRepository.findAllByMachineId(Mockito.anyString())).thenReturn(machineType);
		when(machineTypeRepository.findLatestByRegCenterId(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn(machineType);
		when(templateRepository.findAll()).thenReturn(templates);
		when(templateRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any())).thenReturn(templates);
		when(templateFileFormatRepository.findAllTemplateFormat()).thenReturn(templateFileFormats);
		when(templateFileFormatRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenReturn(templateFileFormats);
		when(templateTypeRepository.findAll()).thenReturn(templateTypes);
		when(templateTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenReturn(templateTypes);
		when(holidayRepository.findAllByMachineId(Mockito.anyString())).thenReturn(holidays);
		when(holidayRepository.findAllLatestCreatedUpdateDeletedByMachineId(Mockito.anyString(), Mockito.any(),
				Mockito.any())).thenReturn(holidays);
		when(blacklistedWordsRepository.findAll()).thenReturn(blackListedWords);
		when(blacklistedWordsRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenReturn(blackListedWords);
		when(registrationCenterRepository.findRegistrationCenterByMachineId(Mockito.anyString()))
				.thenReturn(registrationCenters);
		when(registrationCenterRepository.findLatestRegistrationCenterByMachineId(Mockito.anyString(), Mockito.any(),
				Mockito.any())).thenReturn(registrationCenters);
		when(registrationCenterTypeRepository.findRegistrationCenterTypeByMachineId(Mockito.anyString()))
				.thenReturn(registrationCenterType);
		when(registrationCenterTypeRepository.findLatestRegistrationCenterTypeByMachineId(Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenReturn(registrationCenterType);
		when(genderTypeRepository.findAll()).thenReturn(genders);
		when(genderTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any())).thenReturn(genders);
		when(idTypeRepository.findAll()).thenReturn(idTypes);
		when(idTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any())).thenReturn(idTypes);
		when(deviceRepository.findDeviceByMachineId(Mockito.anyString())).thenReturn(devices);
		when(deviceRepository.findLatestDevicesByRegCenterId(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn(devices);
		when(deviceSpecificationRepository.findDeviceTypeByMachineId(Mockito.anyString()))
				.thenReturn(deviceSpecification);
		when(deviceSpecificationRepository.findLatestDeviceTypeByRegCenterId(Mockito.anyString(), Mockito.any(),
				Mockito.any())).thenReturn(deviceSpecification);
		when(deviceTypeRepository.findDeviceTypeByMachineId(Mockito.anyString())).thenReturn(deviceType);
		when(deviceTypeRepository.findLatestDeviceTypeByRegCenterId(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn(deviceType);
		when(languageRepository.findAll()).thenReturn(languages);
		when(languageRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any())).thenReturn(languages);
		when(reasonCategoryRepository.findAllReasons()).thenReturn(reasonCategories);
		when(reasonCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenReturn(reasonCategories);
		when(reasonListRepository.findAll()).thenReturn(reasonLists);
		when(reasonListRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenReturn(reasonLists);
		when(documentCategoryRepository.findAll()).thenReturn(documentCategories);
		when(documentCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenReturn(documentCategories);
		when(documentTypeRepository.findAll()).thenReturn(documentTypes).thenReturn(documentTypes);
		when(documentTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenReturn(documentTypes);
		when(validDocumentRepository.findAll()).thenReturn(validDocuments);
		when(validDocumentRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenReturn(validDocuments);
		when(biometricAttributeRepository.findAll()).thenReturn(biometricAttributes);
		when(biometricAttributeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenReturn(biometricAttributes);
		when(biometricTypeRepository.findAll()).thenReturn(biometricTypes);
		when(titleRepository.findAll()).thenReturn(titles);
		when(titleRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any())).thenReturn(titles);
		when(locationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any())).thenReturn(locations);
		when(locationRepository.findAll()).thenReturn(locations);
		when(registrationCenterMachineRepository.findAllByMachineId(Mockito.any()))
				.thenReturn(registrationCenterMachines);
		when(registrationCenterMachineRepository.findAllLatestCreatedUpdatedDeleted(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(registrationCenterMachines);
		when(registrationCenterDeviceRepository.findAllByRegistrationCenter(Mockito.any()))
				.thenReturn(registrationCenterDevices);
		when(registrationCenterDeviceRepository.findAllLatestByRegistrationCenterCreatedUpdatedDeleted(Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(registrationCenterDevices);
		when(registrationCenterMachineDeviceRepository.findAllByRegistrationCenterId(Mockito.any()))
				.thenReturn(registrationCenterMachineDevices);
		when(registrationCenterMachineDeviceRepository.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(registrationCenterMachineDevices);
		when(registrationCenterUserMachineRepository.findAllByRegistrationCenterId(Mockito.any()))
				.thenReturn(registrationCenterUserMachines);
		when(registrationCenterUserMachineRepository.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(registrationCenterUserMachines);
		when(registrationCenterUserRepository.findAllByRegistrationCenterId(Mockito.any()))
				.thenReturn(registrationCenterUsers);
		when(registrationCenterUserRepository.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(registrationCenterUsers);
		when(registrationCenterUserHistoryRepository.findLatestRegistrationCenterUserHistory(Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenReturn(registrationCenterUserHistory);
		when(registrationCenterUserMachineHistoryRepository
				.findLatestRegistrationCenterUserMachineHistory(Mockito.anyString(), Mockito.any(), Mockito.any()))
						.thenReturn(registrationCenterUserMachineHistory);
		when(registrationCenterDeviceHistoryRepository.findLatestRegistrationCenterDeviceHistory(Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenReturn(registrationCenterDeviceHistory);
		when(registrationCenterMachineHistoryRepository.findLatestRegistrationCenterMachineHistory(Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenReturn(registrationCenterMachineHistory);
		when(registrationCenterMachineDeviceHistoryRepository
				.findLatestRegistrationCenterMachineDeviceHistory(Mockito.anyString(), Mockito.any(), Mockito.any()))
						.thenReturn(registrationCenterMachineDeviceHistory);
		when(applicantValidDocumentRespository.findAllByTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(applicantValidDocumentList);
		when(individualTypeRepository.findAllIndvidualTypeByTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(individualTypeList);
		when(appAuthenticationMethodRepository.findByLastUpdatedAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(appAuthenticationMethods);
		when(appDetailRepository.findByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(appDetails);
		when(appRolePriorityRepository.findByLastUpdatedAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(appRolePriorities);
		when(screenAuthorizationRepository.findByLastUpdatedAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(screenAuthorizations);
		when(processListRepository.findByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(processList);

		when(screenDetailRepo.findByLastUpdatedAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(screenDetailList);
		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(baseUri + "/1970-01-01T00:00")).andRespond(withSuccess().body(JSON_SYNC_JOB_DEF));

	}

	@Test
	public void syncMasterDataSuccess() throws Exception {
		mockSuccess();
		mockMvc.perform(get(syncDataUrl)).andExpect(status().isOk());
	}

	//@Test
	public void syncMasterDataSuccessWithSerialNum() throws Exception {
		mockSuccess();
		mockMvc.perform(get(syncDataUrlSerialNum)).andExpect(status().isOk());
	}

	//@Test
	public void syncMasterDataSuccessWithMachAddress() throws Exception {
		mockSuccess();

		mockMvc.perform(get(syncDataUrlMacAdress)).andExpect(status().isOk());
	}

	//@Test
	public void syncMasterDataSuccessWithRegId() throws Exception {
		mockSuccess();
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isOk());
	}

	//@Test
	public void syncMasterDataSuccessWithlastUpadtedTimestamp() throws Exception {
		mockSuccess();
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isOk());
	}

	@Test
	public void syncMasterDataInvalidTimeStampException() throws Exception {
		mockSuccess();
		mockMvc.perform(
				get("/masterdata/{regcenterId}?lastupdated=2018-11-01T12:101:01.021Z&macaddress=00:11:22:33", "1001"))
				.andExpect(status().isOk());
	}

	@Test
	public void syncMasterDataApplicationFetchException() throws Exception {
		mockSuccess();
		when(applicationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataMachineFetchException() throws Exception {
		mockSuccess();
		when(machineRepository.findAllLatestCreatedUpdateDeleted(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataMachineSpecFetchException() throws Exception {
		mockSuccess();
		when(machineSpecificationRepository.findLatestByRegCenterId(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataMachineTypeFetchException() throws Exception {
		mockSuccess();
		when(machineTypeRepository.findLatestByRegCenterId(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDeviceFetchException() throws Exception {
		mockSuccess();
		when(deviceRepository.findLatestDevicesByRegCenterId(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDeviceSpecFetchException() throws Exception {
		mockSuccess();
		when(deviceSpecificationRepository.findLatestDeviceTypeByRegCenterId(Mockito.anyString(), Mockito.any(),
				Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDeviceTypeFetchException() throws Exception {
		mockSuccess();
		when(deviceTypeRepository.findLatestDeviceTypeByRegCenterId(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataTemplateFetchException() throws Exception {
		mockSuccess();
		when(templateRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataTemplateFileFormatFetchException() throws Exception {
		mockSuccess();
		when(templateFileFormatRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataTemplateTypeFetchException() throws Exception {
		mockSuccess();
		when(templateTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataHolidayFetchException() throws Exception {
		mockSuccess();
		when(holidayRepository.findAllLatestCreatedUpdateDeletedByMachineId(Mockito.anyString(), Mockito.any(),
				Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataBiometricAttrFetchException() throws Exception {
		mockSuccess();
		when(biometricAttributeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataBiometricTypeFetchException() throws Exception {
		mockSuccess();
		when(biometricTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDocCategoryFetchException() throws Exception {
		mockSuccess();
		when(documentCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDocTypeFetchException() throws Exception {
		mockSuccess();
		when(documentTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataLanguageFetchException() throws Exception {
		mockSuccess();
		when(languageRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataGenderFetchException() throws Exception {
		mockSuccess();
		when(genderTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataLocationFetchException() throws Exception {
		mockSuccess();
		when(locationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataIdTypesFetchException() throws Exception {
		mockSuccess();
		when(idTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterRepository.findLatestRegistrationCenterByMachineId(Mockito.anyString(), Mockito.any(),
				Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterTypeFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterTypeRepository.findLatestRegistrationCenterTypeByMachineId(Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataValidFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterTypeRepository.findLatestRegistrationCenterTypeByMachineId(Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataBlackListedWordFetchException() throws Exception {
		mockSuccess();
		when(blacklistedWordsRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataReasonCatFetchException() throws Exception {
		mockSuccess();
		when(reasonCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataReasonListFetchException() throws Exception {
		mockSuccess();
		when(reasonListRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataTitleFetchException() throws Exception {
		mockSuccess();
		when(titleRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDatavalidDocumentFetchException() throws Exception {
		mockSuccess();
		when(validDocumentRepository.findAllLatestCreatedUpdateDeleted(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterMachineFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterMachineRepository.findAllLatestCreatedUpdatedDeleted(Mockito.anyString(), Mockito.any(),
				Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterDeviceFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterDeviceRepository.findAllLatestByRegistrationCenterCreatedUpdatedDeleted(
				Mockito.anyString(), Mockito.any(), Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(
				"/masterdata/{machineId}?lastupdated=2018-11-01T12:10:01.021Z&macaddress=11:a1:b0:i87&serialnumber=NM123456BT",
				"111")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterMachineDeviceFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterMachineDeviceRepository
				.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.anyString(), Mockito.any(), Mockito.any()))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterUserMachineFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterUserMachineRepository
				.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.anyString(), Mockito.any(), Mockito.any()))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterUserFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterUserRepository.findAllByRegistrationCenterIdCreatedUpdatedDeleted(Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterUserHistoryFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterUserHistoryRepository.findLatestRegistrationCenterUserHistory(Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterUserMachineHistoryFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterUserMachineHistoryRepository
				.findLatestRegistrationCenterUserMachineHistory(Mockito.anyString(), Mockito.any(), Mockito.any()))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterMachineHistoryFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterMachineHistoryRepository.findLatestRegistrationCenterMachineHistory(Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterDeviceHistoryFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterDeviceHistoryRepository.findLatestRegistrationCenterDeviceHistory(Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataRegistrationCenterMachineDeviceHistoryFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterMachineDeviceHistoryRepository
				.findLatestRegistrationCenterMachineDeviceHistory(Mockito.anyString(), Mockito.any(), Mockito.any()))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void getRegistrationCenterUserMasterDataNotFoundExcepetion() throws Exception {
		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(Mockito.anyString()))
				.thenReturn(new ArrayList<RegistrationCenterUser>());

		mockMvc.perform(get("/registrationcenteruser/1")).andExpect(status().isNotFound());
	}

	/*
	 * @Test public void IsMachineIdPresentServiceExceptionTest() throws Exception {
	 * when(machineRepository.findByMachineIdAndIsActive(Mockito.anyString()))
	 * .thenThrow(DataRetrievalFailureException.class);
	 * 
	 * mockMvc.perform(get(syncDataUrlWithRegId,
	 * "1001")).andExpect(status().isInternalServerError()); }
	 */
	@Test
	public void findApplicantValidDocServiceExceptionTest() throws Exception {
		mockSuccess();
		when(applicantValidDocumentRespository.findAllByTimeStamp(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void individualTypeExceptionTest() throws Exception {

		mockSuccess();
		when(individualTypeRepository.findAllIndvidualTypeByTimeStamp(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());

	}

	@Test
	public void registrationCetnerDevicesServiceExceptionTest() throws Exception {

		mockSuccess();
		when(registrationCenterDeviceRepository.findAllLatestByRegistrationCenterCreatedUpdatedDeleted(
				Mockito.anyString(), Mockito.any(), Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());

	}

	@Test
	public void registrationCenterTest() throws Exception {

		mockSuccess();
		when(registrationCenterRepository.findRegistrationCenterByIdAndIsActiveIsTrue(Mockito.anyString()))
				.thenReturn(new ArrayList<RegistrationCenter>());
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isOk());

	}

	@Test
	public void registrationCenterMachineExceptionTest() throws Exception {
		mockSuccess();
		when(registrationCenterMachineRepository.getRegCenterIdWithRegIdAndMachineId(Mockito.anyString(),
				Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void registrationCenterMachineNullTest() throws Exception {
		mockSuccess();
		when(registrationCenterMachineRepository.getRegCenterIdWithRegIdAndMachineId(Mockito.anyString(),
				Mockito.anyString())).thenReturn(null);
		mockMvc.perform(get(syncDataUrlWithRegId, "1001")).andExpect(status().isOk());
	}

	@Test
	public void noMacAddressAndNoSNoNumTest() throws Exception {
		mockSuccess();

		mockMvc.perform(get(syncDataUrlWithoutMacAddressAndSno)).andExpect(status().isOk());
	}

	@Test
	public void syncMasterdataWithServiceException() throws Exception {
		mockSuccess();
		when(registrationCenterMachineRepository.getRegistrationCenterMachineWithMacAddress(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlMacAdress, "10001")).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterdataWithMachineListEmptyException() throws Exception {
		mockSuccess();
		when(registrationCenterMachineRepository.getRegistrationCenterMachineWithMacAddress(Mockito.anyString()))
				.thenReturn(new ArrayList<Object[]>());
		mockMvc.perform(get(syncDataUrlMacAdress, "10001")).andExpect(status().isOk());
	}

	@Test
	public void appAuthMethodExceptionTest() throws Exception {

		mockSuccess();
		when(appAuthenticationMethodRepository.findByLastUpdatedAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);

		mockMvc.perform(get(syncDataUrlMacAdress, "10001")).andExpect(status().isInternalServerError());

	}

	@Test
	public void appDetailExceptionTest() throws Exception {

		mockSuccess();
		when(appDetailRepository.findByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);

		mockMvc.perform(get(syncDataUrlMacAdress, "10001")).andExpect(status().isInternalServerError());

	}

	@Test
	public void appPriorityExceptionTest() throws Exception {

		mockSuccess();
		when(appRolePriorityRepository.findByLastUpdatedAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);

		mockMvc.perform(get(syncDataUrlMacAdress, "10001")).andExpect(status().isInternalServerError());

	}

	@Test
	public void screenAuthExceptionTest() throws Exception {

		mockSuccess();
		when(screenAuthorizationRepository.findByLastUpdatedAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);

		mockMvc.perform(get(syncDataUrlMacAdress, "10001")).andExpect(status().isInternalServerError());

	}

	@Test
	public void processListExceptionTest() throws Exception {

		mockSuccess();
		when(processListRepository.findByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);

		mockMvc.perform(get(syncDataUrlMacAdress, "10001")).andExpect(status().isInternalServerError());

	}

	@Test
	public void screenDetailException() throws Exception {
		mockSuccess();
		when(screenDetailRepo.findByLastUpdatedAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(syncDataUrlMacAdress, "10001")).andExpect(status().isInternalServerError());

	}

	@Test
	public void syncJobDefException() throws Exception {
		mockSuccess();
		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(baseUri + "/1970-01-01T00:00")).andRespond(withServerError().body(JSON_SYNC_JOB_DEF));
		mockMvc.perform(get(syncDataUrlMacAdress, "10001")).andExpect(status().isInternalServerError());
	}

}
