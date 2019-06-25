
package io.mosip.registration.processor.packet.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.registration.processor.core.code.DedupeSourceName;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.packet.dto.Applicant;
import io.mosip.registration.processor.core.packet.dto.Biometric;
import io.mosip.registration.processor.core.packet.dto.BiometricDetails;
import io.mosip.registration.processor.core.packet.dto.BiometricExceptionDTO;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.Introducer;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisApplicationDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDetDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegBioRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegDemoDedupeListDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.dto.PhotographDto;
import io.mosip.registration.processor.packet.storage.entity.AbisApplicationEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisApplicationPKEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestPKEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationEntity;
import io.mosip.registration.processor.packet.storage.entity.RegAbisRefEntity;
import io.mosip.registration.processor.packet.storage.entity.RegBioRefEntity;
import io.mosip.registration.processor.packet.storage.entity.RegBioRefPKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegDemoDedupeListEntity;
import io.mosip.registration.processor.packet.storage.entity.RegDemoDedupeListPKEntity;
import io.mosip.registration.processor.packet.storage.exception.FileNotFoundInPacketStore;
import io.mosip.registration.processor.packet.storage.exception.ParsingException;
import io.mosip.registration.processor.packet.storage.exception.TablenotAccessibleException;
import io.mosip.registration.processor.packet.storage.exception.UnableToInsertData;
import io.mosip.registration.processor.packet.storage.mapper.PacketInfoMapper;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.service.impl.PacketInfoManagerImpl;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;

/**
 * The Class PacketInfoManagerImplTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class, PacketInfoMapper.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class PacketInfoManagerImplTest {

	/** The packet info manager impl. */
	@InjectMocks
	PacketInfoManagerImpl packetInfoManagerImpl;

	/** The audit log request builder. */
	@Mock
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The applicant demographic repository. */
	@Mock
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> applicantDemographicRepository;

	/** The demographic dedupe repository. */
	@Mock
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The utility. */
	@Mock
	private Utilities utility;

	/** The packet info dao. */
	@Mock
	private PacketInfoDao packetInfoDao;

	/** The filesystem adapter impl. */
	@Mock
	private PacketManager filesystemAdapterImpl;

	/** The reg abis ref repository. */
	@Mock
	private BasePacketRepository<RegAbisRefEntity, String> regAbisRefRepository;

	/** The reg abis ref entity. */
	@Mock
	private RegAbisRefEntity regAbisRefEntity;

	/** The manual verfication repository. */
	@Mock
	private BasePacketRepository<ManualVerificationEntity, String> manualVerficationRepository;

	/** The reg bio ref repository. */
	@Mock
	private BasePacketRepository<RegBioRefEntity, String> regBioRefRepository;

	/** The reg bio ref entity. */
	@Mock
	private RegBioRefEntity regBioRefEntity;

	/** The reg abis request repository. */
	@Mock
	private BasePacketRepository<AbisRequestEntity, String> regAbisRequestRepository;

	/** The abis request entity. */
	@Mock
	private AbisRequestEntity abisRequestEntity;

	/** The reg abis application repository. */
	@Mock
	private BasePacketRepository<AbisApplicationEntity, String> regAbisApplicationRepository;

	/** The abis application entity. */
	@Mock
	private AbisApplicationEntity abisApplicationEntity;

	@Mock
	PacketInfoMapper packetInfoMapper;

	@Mock
	private BasePacketRepository<RegDemoDedupeListEntity, String> regDemoDedupeListRepository;

	@Mock
	private LogDescription description;

	/** The byte array. */
	byte[] byteArray = null;

	/** The identity. */
	private Identity identity;

	/** The meta data list. */
	private List<FieldValue> metaDataList;

	/** The exp. */
	private DataAccessLayerException exp;

	/** The demographic json stream. */
	private InputStream demographicJsonStream;

	/** The demographic json file. */
	private File demographicJsonFile;

	/** The Constant CONFIG_SERVER_URL. */
	private static final String CONFIG_SERVER_URL = "url";

	/** The identity mappingjson string. */
	private String identityMappingjsonString;

	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		Mockito.doNothing().when(description).setMessage(any());
		Mockito.when(description.getMessage()).thenReturn("DESCRIPTION");
		ClassLoader classLoader = getClass().getClassLoader();
		demographicJsonFile = new File(classLoader.getResource("ID.json").getFile());
		demographicJsonStream = new FileInputStream(demographicJsonFile);
		try {
			byteArray = IOUtils.toByteArray(demographicJsonStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File identityMappingjson = new File(classLoader.getResource("RegistrationProcessorIdentity.json").getFile());
		InputStream identityMappingjsonStream = new FileInputStream(identityMappingjson);

		try {
			identityMappingjsonString = IOUtils.toString(identityMappingjsonStream, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", CONFIG_SERVER_URL, "RegistrationProcessorIdentity.json")
				.thenReturn(identityMappingjsonString);
		identity = new Identity();
		Photograph applicantPhotograph = new Photograph();

		applicantPhotograph.setLabel("label");
		applicantPhotograph.setLanguage("eng");
		applicantPhotograph.setNumRetry(4);
		applicantPhotograph.setPhotographName("applicantPhoto");
		applicantPhotograph.setQualityScore(80.0);
		identity.setApplicantPhotograph(applicantPhotograph);

		Photograph exceptionPhotograph = new Photograph();
		exceptionPhotograph.setLabel("label");
		exceptionPhotograph.setLanguage("eng");
		exceptionPhotograph.setNumRetry(4);
		exceptionPhotograph.setPhotographName("excep");
		exceptionPhotograph.setQualityScore(80.0);
		identity.setExceptionPhotograph(exceptionPhotograph);

		BiometricDetails lefteye = new BiometricDetails();
		lefteye.setForceCaptured(false);
		lefteye.setImageName("Iris1");
		lefteye.setLabel("label");
		lefteye.setLanguage("eng");
		lefteye.setNumRetry(2);
		lefteye.setQualityScore(80.0);
		lefteye.setType("LeftEye");

		BiometricDetails rightEye = new BiometricDetails();
		rightEye.setForceCaptured(false);
		rightEye.setImageName("Iris2");
		rightEye.setLabel("label");
		rightEye.setLanguage("eng");
		rightEye.setNumRetry(2);
		rightEye.setQualityScore(80.0);
		rightEye.setType("RightEye");

		BiometricDetails leftPalm = new BiometricDetails();
		leftPalm.setForceCaptured(false);
		leftPalm.setImageName("LeftPalm");
		leftPalm.setLabel("label");
		leftPalm.setLanguage("eng");
		leftPalm.setNumRetry(2);
		leftPalm.setQualityScore(80.0);
		leftPalm.setType("fingerprint");

		BiometricDetails rightPalm = new BiometricDetails();
		rightPalm.setForceCaptured(false);
		rightPalm.setImageName("RightPalm");
		rightPalm.setLabel("label");
		rightPalm.setLanguage("eng");
		rightPalm.setNumRetry(2);
		rightPalm.setQualityScore(80.0);
		rightPalm.setType("fingerprint");

		BiometricDetails bothThumbs = new BiometricDetails();
		bothThumbs.setForceCaptured(false);
		bothThumbs.setImageName("BothThumbs");
		bothThumbs.setLabel("label");
		bothThumbs.setLanguage("eng");
		bothThumbs.setNumRetry(2);
		bothThumbs.setQualityScore(80.0);
		bothThumbs.setType("fingerprint");

		BiometricDetails rightThumb = new BiometricDetails();
		rightThumb.setForceCaptured(false);
		rightThumb.setImageName("RightThumb");
		rightThumb.setLabel("label");
		rightThumb.setLanguage("eng");
		rightThumb.setNumRetry(2);
		rightThumb.setQualityScore(80.0);
		rightThumb.setType("fingerprint");

		BiometricDetails face = new BiometricDetails();
		face.setForceCaptured(false);
		face.setImageName("face");
		face.setLabel("label");
		face.setLanguage("eng");
		face.setNumRetry(2);
		face.setQualityScore(80.0);
		face.setType("face");

		BiometricDetails introducerIris = new BiometricDetails();
		introducerIris.setForceCaptured(false);
		introducerIris.setImageName("RightEye");
		introducerIris.setLabel("label");
		introducerIris.setLanguage("eng");
		introducerIris.setNumRetry(2);
		introducerIris.setQualityScore(80.0);
		introducerIris.setType("iris");

		Applicant applicant = new Applicant();
		applicant.setLeftEye(lefteye);
		// applicant.setLeftSlap(leftPalm);
		applicant.setRightEye(rightEye);
		// applicant.setRightSlap(rightPalm);
		// applicant.setThumbs(bothThumbs);
		Introducer introducer = new Introducer();
		introducer.setIntroducerFingerprint(rightThumb);
		introducer.setIntroducerImage(face);
		introducer.setIntroducerIris(introducerIris);
		Biometric biometric = new Biometric();
		biometric.setApplicant(applicant);
		biometric.setIntroducer(introducer);
		identity.setBiometric(biometric);

		FieldValue registrationService = new FieldValue();
		registrationService.setLabel("registration-service.jar");
		registrationService.setValue("65gfhab67586cjhsabcjk78");

		FieldValue registrationUi = new FieldValue();
		registrationUi.setLabel("registration-ui.jar");
		registrationUi.setValue("uygdfajkdjkHHD56TJHASDJKA");
		List<FieldValue> checksum = new ArrayList<FieldValue>();
		checksum.add(registrationService);
		checksum.add(registrationUi);
		identity.setCheckSum(checksum);

		BiometricExceptionDTO thumb = new BiometricExceptionDTO();
		thumb.setExceptionDescription("Lost in accident");
		thumb.setExceptionType("Permanent");
		thumb.setLanguage("eng");
		thumb.setMissingBiometric("LeftThumb");
		thumb.setType("fingerprint");

		BiometricExceptionDTO leftForefinger = new BiometricExceptionDTO();
		leftForefinger.setExceptionDescription("Lost in accident");
		leftForefinger.setExceptionType("Permanent");
		leftForefinger.setLanguage("eng");
		leftForefinger.setMissingBiometric("LeftForefinger");
		leftForefinger.setType("fingerprint");

		BiometricExceptionDTO rightEyeexp = new BiometricExceptionDTO();
		rightEyeexp.setExceptionDescription("By birth");
		rightEyeexp.setExceptionType("Permanent");
		rightEyeexp.setLanguage("eng");
		rightEyeexp.setMissingBiometric("LeftThumb");
		rightEyeexp.setType("iris");

		List<BiometricExceptionDTO> excptionBiometrics = new ArrayList<>();
		excptionBiometrics.add(rightEyeexp);
		excptionBiometrics.add(leftForefinger);
		excptionBiometrics.add(thumb);
		identity.setExceptionBiometrics(excptionBiometrics);
		FieldValueArray applicantBiometricSequence = new FieldValueArray();
		applicantBiometricSequence.setLabel("applicantBiometricSequence");
		applicantBiometricSequence.setValue(Arrays.asList("BothThumbs", "LeftPalm", "RightPalm", "LeftEye"));

		FieldValueArray introducerBiometricSequence = new FieldValueArray();
		introducerBiometricSequence.setLabel("introducerBiometricSequence");
		introducerBiometricSequence.setValue(Arrays.asList("introducerLeftThumb"));

		FieldValueArray applicantDemographicSequence = new FieldValueArray();
		applicantDemographicSequence.setLabel("applicantDemographicSequence");
		applicantDemographicSequence.setValue(Arrays.asList("DemographicInfo", "ProofOfIdentity", "ProofOfAddress",
				"ApplicantPhoto", "ExceptionPhoto", "RegistrationAcknowledgement"));

		List<FieldValueArray> hashSequence = new ArrayList<>();

		hashSequence.add(applicantDemographicSequence);
		hashSequence.add(applicantBiometricSequence);
		hashSequence.add(introducerBiometricSequence);

		identity.setHashSequence(hashSequence);

		FieldValue geoLocLatitude = new FieldValue();
		geoLocLatitude.setLabel("geoLocLatitude");
		geoLocLatitude.setValue("13.0049");

		FieldValue geoLoclongitude = new FieldValue();
		geoLoclongitude.setLabel("geoLoclongitude");
		geoLoclongitude.setValue("80.24492");

		FieldValue registrationType = new FieldValue();
		registrationType.setLabel("registrationType");
		registrationType.setValue("Child");

		FieldValue applicantType = new FieldValue();
		applicantType.setLabel("applicantType");
		applicantType.setValue("New");

		FieldValue preRegistrationId = new FieldValue();
		preRegistrationId.setLabel("preRegistrationId");
		preRegistrationId.setValue("PEN1345T");

		FieldValue registrationId = new FieldValue();
		registrationId.setLabel("registrationId");
		registrationId.setValue("2018782130000113112018183925");
		FieldValue registrationIdHash = new FieldValue();
		registrationIdHash.setLabel("registrationIdHash");
		registrationIdHash.setValue("271D3A33DE70801BE09CF84573CB0CEDF019568C08AB18EAAF912D456FEB185F");

		FieldValue machineId = new FieldValue();
		machineId.setLabel("machineId");
		machineId.setValue("yyeqy26356");

		FieldValue centerId = new FieldValue();
		centerId.setLabel("centerId");
		centerId.setValue("12245");

		FieldValue uin = new FieldValue();
		uin.setLabel("uin");
		uin.setValue(null);

		FieldValue previousRID = new FieldValue();
		previousRID.setLabel("previousRID");
		previousRID.setValue(null);

		FieldValue introducerType = new FieldValue();
		introducerType.setLabel("introducerType");
		introducerType.setValue(null);

		FieldValue introducerRID = new FieldValue();
		introducerRID.setLabel("introducerRID");
		introducerRID.setValue("2018234500321157812");

		FieldValue introducerRIDHash = new FieldValue();
		introducerRIDHash.setLabel("introducerRIDHash");
		introducerRIDHash.setValue("271D3A33DE70801BE09CF84573CB0CEDF019568C08AB18EAAF912D456JAN123");

		FieldValue introducerUIN = new FieldValue();
		introducerUIN.setLabel("introducerUIN");
		introducerUIN.setValue(null);

		FieldValue introducerUINHash = new FieldValue();
		introducerUINHash.setLabel("introducerUINHash");
		introducerUINHash.setValue("271D3A33DE70801BE09CF84573CB0CEDF019568C08AB18EAAF912D7767HGGY7");

		FieldValue officerFingerprintType = new FieldValue();
		officerFingerprintType.setLabel("officerFingerprintType");
		officerFingerprintType.setValue("LeftThumb");

		FieldValue officerIrisType = new FieldValue();
		officerIrisType.setLabel("officerIrisType");
		officerIrisType.setValue(null);

		FieldValue supervisorFingerprintType = new FieldValue();
		supervisorFingerprintType.setLabel("supervisorFingerprintType");
		supervisorFingerprintType.setValue("LeftThumb");

		FieldValue supervisorIrisType = new FieldValue();
		supervisorIrisType.setLabel("supervisorIrisType");
		supervisorIrisType.setValue(null);

		identity.setMetaData(Arrays.asList(geoLocLatitude, geoLoclongitude, registrationType, applicantType,
				preRegistrationId, registrationId, registrationIdHash, machineId, centerId, uin, previousRID,
				introducerType, introducerRID, introducerRIDHash, introducerUIN, introducerUINHash,
				officerFingerprintType, officerIrisType, supervisorFingerprintType, supervisorIrisType));

		FieldValue officerId = new FieldValue();
		officerId.setLabel("officerId");
		officerId.setValue("op0r0s12");

		FieldValue officerFingerprintImage = new FieldValue();
		officerFingerprintImage.setLabel("officerFingerprintImage");
		officerFingerprintImage.setValue("registrationOfficerLeftThumb");

		FieldValue officerIrisImage = new FieldValue();
		officerIrisImage.setLabel("officerIrisImage");
		officerIrisImage.setValue(null);

		FieldValue supervisiorId = new FieldValue();
		supervisiorId.setLabel("supervisiorId");
		supervisiorId.setValue("s9ju2jhu");

		FieldValue supervisorFingerprintImage = new FieldValue();
		supervisorFingerprintImage.setLabel("supervisorFingerprintImage");
		supervisorFingerprintImage.setValue("supervisorLeftThumb");

		FieldValue supervisorPassword = new FieldValue();
		supervisorPassword.setLabel("supervisorPassword");
		supervisorPassword.setValue(null);

		FieldValue supervisorIrisImage = new FieldValue();
		supervisorIrisImage.setLabel("supervisorIrisImage");
		supervisorIrisImage.setValue(null);

		FieldValue officerPassword = new FieldValue();
		officerPassword.setLabel("officerPassword");
		officerPassword.setValue(null);

		FieldValue supervisiorPIN = new FieldValue();
		supervisiorPIN.setLabel("supervisiorPIN");
		supervisiorPIN.setValue(null);

		FieldValue officerPIN = new FieldValue();
		officerPIN.setLabel("officerPIN");
		officerPIN.setValue(null);

		FieldValue officerAuthenticationImage = new FieldValue();
		officerAuthenticationImage.setLabel("officerAuthenticationImage");
		officerAuthenticationImage.setValue(null);

		FieldValue supervisorAuthenticationImage = new FieldValue();
		supervisorAuthenticationImage.setLabel("supervisorAuthenticationImage");
		supervisorAuthenticationImage.setValue(null);

		identity.setOsiData(Arrays.asList(officerId, officerFingerprintImage, officerIrisImage, supervisiorId,
				supervisorFingerprintImage, supervisorIrisImage, supervisorPassword, officerPassword, supervisiorPIN,
				officerPIN, officerAuthenticationImage, supervisorAuthenticationImage));

		metaDataList = new ArrayList<>();
		FieldValue regId = new FieldValue();
		regId.setLabel("registrationId");
		regId.setValue("2018782130000120112018104200");

		FieldValue preRegId = new FieldValue();
		preRegId.setLabel("preRegistrationId");
		preRegId.setValue("PEN1345T");

		metaDataList.add(regId);
		metaDataList.add(preRegId);

		String inputString = "test";
		InputStream inputStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));

		Mockito.when(filesystemAdapterImpl.getFile(any(), any())).thenReturn(inputStream);
		exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.toString(), "errorMessage", new Exception());
		classLoader = getClass().getClassLoader();
		demographicJsonFile = new File(classLoader.getResource("ID.json").getFile());
		demographicJsonStream = new FileInputStream(demographicJsonFile);
		Mockito.when(utility.getConfigServerFileStorageURL()).thenReturn(CONFIG_SERVER_URL);
		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");
		Mockito.when(utility.getGetRegProcessorIdentityJson()).thenReturn("RegistrationProcessorIdentity.json");

	}

	/**
	 * Save demographic info json test.
	 */
	@Test
	public void saveDemographicInfoJsonTest() {

		packetInfoManagerImpl.saveDemographicInfoJson(byteArray, "2018782130000224092018121229", metaDataList);
		assertEquals("identity", utility.getGetRegProcessorDemographicIdentity());
	}

	/**
	 * File not found in packet store test.
	 */
	@Test(expected = FileNotFoundInPacketStore.class)
	public void fileNotFoundInPacketStoreTest() {
		packetInfoManagerImpl.saveDemographicInfoJson(null, "2018782130000224092018121229", metaDataList);
	}

	/**
	 * Unable to insert data test.
	 */
	@Test(expected = UnableToInsertData.class)
	public void unableToInsertDataTest() {

		Mockito.when(demographicDedupeRepository.save(any())).thenThrow(exp);

		packetInfoManagerImpl.saveDemographicInfoJson(byteArray, "2018782130000224092018121229", metaDataList);
	}

	/**
	 * Demographic dedupe unable to insert data test.
	 */
	@Test(expected = UnableToInsertData.class)
	public void demographicDedupeUnableToInsertDataTest() {

		Mockito.when(demographicDedupeRepository.save(any())).thenThrow(exp);
		packetInfoManagerImpl.saveDemographicInfoJson(byteArray, "2018782130000224092018121229", metaDataList);

	}

	/**
	 * Identity not found exception test.
	 */
	@Test(expected = ParsingException.class)
	public void identityNotFoundExceptionTest() {

		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn(null);
		packetInfoManagerImpl.saveDemographicInfoJson(byteArray, "2018782130000224092018121229", metaDataList);
	}

	/**
	 * Gets the packets for QC users test.
	 *
	 * @return the packets for QC users test
	 */
	@Test
	public void getPacketsForQCUsersTest() {
		List<ApplicantInfoDto> applicantInfoDtoList = new ArrayList<>();
		ApplicantInfoDto applicantInfoDto = new ApplicantInfoDto();
		PhotographDto photographDto = new PhotographDto();
		photographDto.setRegId("2018782130000224092018121229");
		photographDto.setPreRegId("PEN1345T");
		photographDto.setNoOfRetry(4);
		photographDto.setHasExcpPhotograph(false);

		DemographicInfoDto demoDto = new DemographicInfoDto();
		List<DemographicInfoDto> demoDedupeList = new ArrayList<>();
		demoDto.setRegId("2018782130000224092018121229");
		demoDto.setName("firstName+LastName");
		demoDto.setLangCode("ar");
		demoDedupeList.add(demoDto);
		applicantInfoDto.setDemoDedupeList(demoDedupeList);
		applicantInfoDto.setApplicantPhotograph(photographDto);
		applicantInfoDtoList.add(applicantInfoDto);

		Mockito.when(packetInfoDao.getPacketsforQCUser(any())).thenReturn(applicantInfoDtoList);
		packetInfoManagerImpl.getPacketsforQCUser("1234");
		List<ApplicantInfoDto> applicantList = packetInfoDao.getPacketsforQCUser("1234");
		assertEquals(applicantInfoDtoList, applicantList);

	}

	/**
	 * Gets the packets for qc user tablenot accessible exception test.
	 *
	 * @return the packets for qc user tablenot accessible exception test
	 */
	@Test(expected = TablenotAccessibleException.class)
	public void getPacketsForQcUserTablenotAccessibleExceptionTest() {

		Mockito.when(packetInfoDao.getPacketsforQCUser(any())).thenThrow(exp);
		packetInfoManagerImpl.getPacketsforQCUser("1234");

	}

	/**
	 * Find demo by id test.
	 *
	 * @throws ParseException
	 *             the parse exception
	 */
	@Test
	public void findDemoByIdTest() throws ParseException {
		List<DemographicInfoDto> depdupeList = new ArrayList<>();
		String dob = "Thu Jan 01 00:00:00 IST 2015";
		Date date = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(dob);
		DemographicInfoDto uinDto = new DemographicInfoDto();
		uinDto.setRegId("2018782130000103122018105604");
		uinDto.setGenderCode("mâle");
		uinDto.setLangCode("fr");
		uinDto.setName("IbrahimAli");
		uinDto.setDob(date.toString());

		DemographicInfoDto uinDto1 = new DemographicInfoDto();
		uinDto1.setRegId("2018782130000103122018105604");
		uinDto1.setGenderCode("الذكر");
		uinDto1.setLangCode("ar");
		uinDto1.setName("ابراهيمعلي");
		uinDto1.setDob(date.toString());

		depdupeList.add(uinDto);
		depdupeList.add(uinDto1);

		Mockito.when(packetInfoDao.findDemoById(anyString())).thenReturn(depdupeList);

		List<DemographicInfoDto> result = packetInfoManagerImpl.findDemoById("2018782130000103122018100224");

		assertEquals(
				"Fetching all dedupe records from db based on id. Verifing id, expected id is 2018782130000103122018105604",
				"2018782130000103122018105604", result.iterator().next().getRegId());

	}

	/**
	 * Gets the applicant finger print image name by id test.
	 *
	 * @return the applicant finger print image name by id test
	 */
	/*
	 * @Test public void getApplicantFingerPrintImageNameByIdTest() { List<String>
	 * applicantFingerPrintImages = new ArrayList<>();
	 * applicantFingerPrintImages.add("LeftThumb");
	 * applicantFingerPrintImages.add("RightThumb");
	 *
	 * Mockito.when(packetInfoDao.getApplicantFingerPrintImageNameById(anyString()))
	 * .thenReturn(applicantFingerPrintImages);
	 *
	 * List<String> resultList = packetInfoManagerImpl
	 * .getApplicantFingerPrintImageNameById("2018782130000103122018100224");
	 * assertEquals(
	 * "Fetching applicant finger print images from db. verifing image name of first record, expected value is LeftThumb"
	 * , "LeftThumb", resultList.get(0));
	 *
	 * }
	 */


	/**
	 * Test get reference id by rid.
	 */
	@Test
	public void testGetReferenceIdByRid() {
		String rid = "27847657360002520181208094056";
		List<String> referenceIdList = new ArrayList<>();
		referenceIdList.add("01234567-89AB-CDEF-0123-456789ABCDEF");
		Mockito.when(packetInfoManagerImpl.getReferenceIdByRid(rid)).thenReturn(referenceIdList);

		List<String> resultList = packetInfoManagerImpl.getReferenceIdByRid(rid);
		assertEquals("01234567-89AB-CDEF-0123-456789ABCDEF", resultList.get(0));
	}

	/**
	 * Test get rid by reference id.
	 */
	@Test
	public void testGetRidByReferenceId() {
		String referenceId = "01234567-89AB-CDEF-0123-456789ABCDEF";
		List<String> regIdList = new ArrayList<>();
		regIdList.add("27847657360002520181208094056");
		Mockito.when(packetInfoManagerImpl.getRidByReferenceId(referenceId)).thenReturn(regIdList);

		List<String> resultList = packetInfoManagerImpl.getRidByReferenceId(referenceId);
		assertEquals("27847657360002520181208094056", resultList.get(0));
	}

	/**
	 * Test save manual adjudication data success.
	 */
	@Test
	public void testSaveManualAdjudicationDataSuccess() {
		String registrationId = "1234";
		List<String> uniqueMatchedRefIds = Arrays.asList("123av", "124abc", "125abcd");

		packetInfoManagerImpl.saveManualAdjudicationData(uniqueMatchedRefIds, registrationId, DedupeSourceName.DEMO);

	}

	/**
	 * Test save manual adjudication data exception.
	 */
	@Test(expected = UnableToInsertData.class)
	public void testSaveManualAdjudicationDataException() {
		Mockito.when(manualVerficationRepository.save(any())).thenThrow(exp);
		String registrationId = "1234";
		List<String> uniqueMatchedRefIds = Arrays.asList("123av", "124abc", "125abcd");

		packetInfoManagerImpl.saveManualAdjudicationData(uniqueMatchedRefIds, registrationId, DedupeSourceName.DEMO);

	}

	/**
	 * Test save abis ref success.
	 */
	@Test
	public void testSaveAbisRefSuccess() {

		RegAbisRefDto regAbisRefDto = new RegAbisRefDto();
		regAbisRefDto.setAbis_ref_id("ref1234");
		regAbisRefDto.setReg_id("1234");
		Mockito.when(regAbisRefRepository.save(any())).thenReturn(regAbisRefEntity);
		packetInfoManagerImpl.saveAbisRef(regAbisRefDto);

	}

	/**
	 * Save abis ref test exception.
	 */
	@Test(expected = UnableToInsertData.class)
	public void saveAbisRefTestException() {
		Mockito.when(regAbisRefRepository.save(any())).thenThrow(exp);
		RegAbisRefDto regAbisRefDto = new RegAbisRefDto();
		regAbisRefDto.setAbis_ref_id("ref1234");
		regAbisRefDto.setReg_id("1234");
		packetInfoManagerImpl.saveAbisRef(regAbisRefDto);
	}

	/**
	 * Test save bio ref success.
	 */
	@Test
	public void testSaveBioRefSuccess() {
		Mockito.when(regBioRefRepository.save(any())).thenReturn(regBioRefEntity);
		RegBioRefDto regBioRefDto = new RegBioRefDto();
		regBioRefDto.setBioRefId("abc-efg-123");
		regBioRefDto.setRegId("1234567890");
		packetInfoManagerImpl.saveBioRef(regBioRefDto);
	}

	/**
	 * Test save bio ref exception.
	 */
	@Test(expected = UnableToInsertData.class)
	public void testSaveBioRefException() {
		Mockito.when(regBioRefRepository.save(any())).thenThrow(exp);
		RegBioRefDto regBioRefDto = new RegBioRefDto();
		regBioRefDto.setBioRefId("abc-efg-123");
		regBioRefDto.setRegId("1234567890");
		packetInfoManagerImpl.saveBioRef(regBioRefDto);
	}

	/**
	 * Test save abis request success.
	 */
	@Test
	public void testSaveAbisRequestSuccess() {
		Mockito.when(regAbisRequestRepository.save(any())).thenReturn(regAbisRefEntity);
		AbisRequestDto dto = new AbisRequestDto();
		dto.setBioRefId("abc-efg-123");
		dto.setRequestType("INSERT");
		packetInfoManagerImpl.saveAbisRequest(dto);
	}

	/**
	 * Test save abis request exception.
	 */
	@Test(expected = UnableToInsertData.class)
	public void testSaveAbisRequestException() {
		Mockito.when(regAbisRequestRepository.save(any())).thenThrow(exp);
		AbisRequestDto dto = new AbisRequestDto();
		dto.setBioRefId("abc-efg-123");
		dto.setRequestType("INSERT");
		packetInfoManagerImpl.saveAbisRequest(dto);
	}

	/**
	 * Test get demo list by transaction id.
	 */
	@Test
	public void testGetDemoListByTransactionId() {
		List<RegDemoDedupeListEntity> regDemoDedupeListEntityList = new ArrayList<>();
		RegDemoDedupeListEntity regDemoDedupeListEntity = new RegDemoDedupeListEntity();
		regDemoDedupeListEntity.setRegId("1234567890");
		RegDemoDedupeListPKEntity entity = new RegDemoDedupeListPKEntity();
		entity.setMatchedRegId("abc-efg");
		regDemoDedupeListEntity.setId(entity);
		regDemoDedupeListEntityList.add(regDemoDedupeListEntity);

		Mockito.when(packetInfoDao.getDemoListByTransactionId(any())).thenReturn(regDemoDedupeListEntityList);
		List<RegDemoDedupeListDto> result = packetInfoManagerImpl.getDemoListByTransactionId("abc-efg-123456");
		assertEquals("1234567890", result.get(0).getRegId());
	}

	/**
	 * Test get abis requests by bio ref id.
	 */
	@Test
	public void testGetAbisRequestsByBioRefId() {
		List<AbisRequestEntity> abisRequestEntityList = new ArrayList<>();
		AbisRequestEntity entity = new AbisRequestEntity();
		entity.setAbisAppCode("code1");
		AbisRequestPKEntity pkEntity = new AbisRequestPKEntity();
		pkEntity.setId("12345677");
		entity.setId(pkEntity);
		abisRequestEntityList.add(entity);
		Mockito.when(packetInfoDao.getAbisRequestsByBioRefId(any())).thenReturn(abisRequestEntityList);
		List<AbisRequestDto> result = packetInfoManagerImpl.getAbisRequestsByBioRefId("abc-efg-12345");
		assertEquals("code1", result.get(0).getAbisAppCode());
	}

	/**
	 * Test get all abis details.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAllAbisDetails() {
		List<AbisApplicationEntity> abisApplicationEntityList = new ArrayList<>();
		AbisApplicationEntity abisApplicationEntity = new AbisApplicationEntity();
		abisApplicationEntity.setName("Abis");
		AbisApplicationPKEntity pkEntity = new AbisApplicationPKEntity();
		pkEntity.setCode("abis");
		abisApplicationEntity.setId(pkEntity);
		abisApplicationEntityList.add(abisApplicationEntity);

		Mockito.when(regAbisApplicationRepository.findAll(any(Class.class))).thenReturn(abisApplicationEntityList);

		List<AbisApplicationDto> result = packetInfoManagerImpl.getAllAbisDetails();
		assertEquals("Abis", result.get(0).getName());
	}

	/**
	 * Test get bio ref id by reg id.
	 */
	@Test
	public void testGetBioRefIdByRegId() {
		List<RegBioRefEntity> regBioRefEntityList = new ArrayList<>();
		RegBioRefEntity regBioRefEntity = new RegBioRefEntity();
		regBioRefEntity.setBioRefId("abc-efg");
		RegBioRefPKEntity pkEntity = new RegBioRefPKEntity();
		pkEntity.setRegId("12345678");
		regBioRefEntity.setId(pkEntity);
		regBioRefEntityList.add(regBioRefEntity);

		Mockito.when(packetInfoDao.getBioRefIdByRegId(any())).thenReturn(regBioRefEntityList);
		List<RegBioRefDto> result = packetInfoManagerImpl.getBioRefIdByRegId("1234567890");
		assertEquals("abc-efg", result.get(0).getBioRefId());
	}

	/**
	 * Testget identify by transaction id.
	 */
	@Test
	public void testgetIdentifyByTransactionId() {
		List<AbisRequestEntity> abisRequestEntityList = new ArrayList<>();
		AbisRequestEntity entity = new AbisRequestEntity();
		abisRequestEntityList.add(entity);

		Mockito.when(packetInfoDao.getIdentifyByTransactionId(any(), any())).thenReturn(abisRequestEntityList);

		assertTrue(packetInfoManagerImpl.getIdentifyByTransactionId("abc-efg", "IDENTIFY"));
	}

	@Test
	public void testgetAbisResponseRecords() {
		List<AbisResponseDto> list = new ArrayList<>();
		AbisResponseDto abisEntity = new AbisResponseDto();
		list.add(abisEntity);
		Mockito.when(packetInfoDao.getAbisResponseRecords(any(), any(), any())).thenReturn(list);
		List<AbisResponseDto> output = packetInfoManagerImpl.getAbisResponseRecords("", "", "");
		assertEquals(output, list);
	}

	@Test
	public void testgetAbisResponseDetails() {
		List<AbisResponseDetDto> list = new ArrayList<>();
		AbisResponseDetDto abisEntity = new AbisResponseDetDto();
		list.add(abisEntity);
		PowerMockito.mockStatic(PacketInfoMapper.class);
		Mockito.when(PacketInfoMapper.convertAbisResponseDetEntityListToDto(any())).thenReturn(list);
		List<AbisResponseDetDto> output = packetInfoManagerImpl.getAbisResponseDetails("");
		assertEquals(output, list);
	}

	@Test
	public void testgetInsertOrIdentifyRequest() {
		List<AbisRequestDto> list = new ArrayList<>();
		AbisRequestDto abisEntity = new AbisRequestDto();
		list.add(abisEntity);
		List<AbisRequestEntity> abisRequestEntities=new ArrayList<AbisRequestEntity>();
		AbisRequestEntity entity=new AbisRequestEntity();
		abisRequestEntities.add(entity);
		Mockito.when(packetInfoDao.getInsertOrIdentifyRequest(any(), any())).thenReturn(abisRequestEntities);
		PowerMockito.mockStatic(PacketInfoMapper.class);
		Mockito.when(PacketInfoMapper.convertAbisRequestEntityListToDto(any())).thenReturn(list);
		List<AbisRequestDto> output = packetInfoManagerImpl.getInsertOrIdentifyRequest("", "");
		assertEquals(output, list);
	}

	@Test
	public void testgetAbisResponseRecordswithoutTransctionId() {
		List<AbisResponseDto> list = new ArrayList<>();
		AbisResponseDto abisEntity = new AbisResponseDto();
		list.add(abisEntity);
		Mockito.when(packetInfoDao.getAbisResponseRecords(any(), any())).thenReturn(list);
		List<AbisResponseDto> output = packetInfoManagerImpl.getAbisResponseRecords("", "");
		assertEquals(output, list);
	}

	@Test
	public void testgetAbisResponseIDs() {
		List<AbisResponseDto> list = new ArrayList<>();
		AbisResponseDto abisEntity = new AbisResponseDto();
		list.add(abisEntity);
		PowerMockito.mockStatic(PacketInfoMapper.class);
		Mockito.when(PacketInfoMapper.convertAbisResponseEntityListToDto(any())).thenReturn(list);
		List<AbisResponseDto> output = packetInfoManagerImpl.getAbisResponseIDs("");
		assertEquals(output, list);
	}

	@Test
	public void testgetAbisResponseDetRecords() {
		List<AbisResponseDetDto> list = new ArrayList<>();
		AbisResponseDetDto abisEntity = new AbisResponseDetDto();
		list.add(abisEntity);
		Mockito.when(packetInfoDao.getAbisResponseDetailedRecords( any())).thenReturn(list);
		List<AbisResponseDetDto> output = packetInfoManagerImpl.getAbisResponseDetRecords(new AbisResponseDto());
		assertEquals(output, list);
	}

	@Test
	public void testsaveIndividualDemographicDedupe() {

		IndividualDemographicDedupe demographicData = new IndividualDemographicDedupe();
		demographicData.setDateOfBirth("2019-03-02T06:29:41.011Z");
		List<IndividualDemographicDedupeEntity> applicantDemographicEntities=new ArrayList<>();
		IndividualDemographicDedupeEntity entity=new IndividualDemographicDedupeEntity();
		entity.setDob("2019-03-02T06:29:41.011Z");
		applicantDemographicEntities.add(entity);
		PowerMockito.mockStatic(PacketInfoMapper.class);
		Mockito.when(PacketInfoMapper.converDemographicDedupeDtoToEntity(any(),any())).thenReturn(applicantDemographicEntities);
		Mockito.when(demographicDedupeRepository.save(any())).thenReturn(entity);
		packetInfoManagerImpl.saveIndividualDemographicDedupeUpdatePacket(demographicData, "1001");

	}

	@Test(expected=UnableToInsertData.class)
	public void testsaveIndividualDemographicDedupeException() {

		IndividualDemographicDedupe demographicData = new IndividualDemographicDedupe();
		demographicData.setDateOfBirth("2019-03-02T06:29:41.011Z");
		List<IndividualDemographicDedupeEntity> applicantDemographicEntities=new ArrayList<>();
		IndividualDemographicDedupeEntity entity=new IndividualDemographicDedupeEntity();
		entity.setDob("2019-03-02T06:29:41.011Z");
		applicantDemographicEntities.add(entity);
		PowerMockito.mockStatic(PacketInfoMapper.class);
		Mockito.when(PacketInfoMapper.converDemographicDedupeDtoToEntity(any(),any())).thenReturn(applicantDemographicEntities);
		Mockito.when(demographicDedupeRepository.save(any())).thenThrow(exp);
		packetInfoManagerImpl.saveIndividualDemographicDedupeUpdatePacket(demographicData, "1001");

	}

	@Test
	public void testsaveDemoDedupePotentialData() {

		RegDemoDedupeListDto regDemoDedupeListDto=new RegDemoDedupeListDto();
		RegDemoDedupeListEntity regDemoDedupeListEntity=new RegDemoDedupeListEntity();
		PowerMockito.mockStatic(PacketInfoMapper.class);
		Mockito.when(PacketInfoMapper.convertDemoDedupeEntityToDto(any())).thenReturn(regDemoDedupeListEntity);
		Mockito.when(regDemoDedupeListRepository.save(any())).thenReturn(new RegDemoDedupeListEntity());
		packetInfoManagerImpl.saveDemoDedupePotentialData(regDemoDedupeListDto);
	}

	@Test(expected=UnableToInsertData.class)
	public void testsaveDemoDedupePotentialDataException() {

		RegDemoDedupeListDto regDemoDedupeListDto=new RegDemoDedupeListDto();
		RegDemoDedupeListEntity regDemoDedupeListEntity=new RegDemoDedupeListEntity();
		PowerMockito.mockStatic(PacketInfoMapper.class);
		Mockito.when(PacketInfoMapper.convertDemoDedupeEntityToDto(any())).thenReturn(regDemoDedupeListEntity);
		Mockito.when(regDemoDedupeListRepository.save(any())).thenThrow(exp);
		packetInfoManagerImpl.saveDemoDedupePotentialData(regDemoDedupeListDto);
	}

	@Test
	public void testgetBatchStatusbyBatchId() {
		Mockito.when(packetInfoDao.getBatchStatusbyBatchId(any())).thenReturn(Arrays.asList("PROCESSED","PROCESSED","PROCESSED"));
		List<String> list=packetInfoManagerImpl.getBatchStatusbyBatchId("123312");
		assertEquals(list.get(0),"PROCESSED");
	}

	@Test
	public void testgetAbisRequestByRequestId() {
		AbisRequestEntity entity=new AbisRequestEntity();
		entity.setLangCode("eng");
		List<AbisRequestEntity> abisRequestList= new ArrayList<AbisRequestEntity>();
		abisRequestList.add(entity);
		List<AbisRequestDto> abisRequestDtoList=new ArrayList<AbisRequestDto>();
		AbisRequestDto dto=new AbisRequestDto();
		dto.setLangCode("eng");
		abisRequestDtoList.add(dto);
		Mockito.when(packetInfoDao.getAbisRequestByRequestId(any())).thenReturn(abisRequestList);
		PowerMockito.mockStatic(PacketInfoMapper.class);
		Mockito.when(PacketInfoMapper.convertAbisRequestEntityListToDto(any())).thenReturn(abisRequestDtoList);
		AbisRequestDto dt=packetInfoManagerImpl.getAbisRequestByRequestId("123312");
		assertEquals(dt.getLangCode(),"eng");
	}

	@Test
	public void testgetBatchIdByRequestId() {
		Mockito.when(packetInfoDao.getBatchIdByRequestId(any())).thenReturn("123312");
		String batchId=packetInfoManagerImpl.getBatchIdByRequestId("114");
		assertEquals(batchId,"123312");
	}


}
