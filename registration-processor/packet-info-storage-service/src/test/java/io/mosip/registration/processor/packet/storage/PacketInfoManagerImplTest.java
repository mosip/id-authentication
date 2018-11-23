
package io.mosip.registration.processor.packet.storage;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.registration.processor.core.packet.dto.Applicant;
import io.mosip.registration.processor.core.packet.dto.Biometric;
import io.mosip.registration.processor.core.packet.dto.BiometricDetails;
import io.mosip.registration.processor.core.packet.dto.BiometricException;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.Introducer;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicDedupeDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.dto.PhotographDto;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicInfoJsonEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantFingerprintEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantIrisEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.BiometricExceptionEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.exception.FileNotFoundInPacketStore;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.TablenotAccessibleException;
import io.mosip.registration.processor.packet.storage.exception.UnableToInsertData;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.service.impl.PacketInfoManagerImpl;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;

@RunWith(MockitoJUnitRunner.class)
public class PacketInfoManagerImplTest {
	@InjectMocks
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManagerImpl = new PacketInfoManagerImpl();

	@Mock
	AuditLogRequestBuilder auditLogRequestBuilder;
	@Mock
	private BasePacketRepository<ApplicantDocumentEntity, String> applicantDocumentRepository;

	@Mock
	private BasePacketRepository<BiometricExceptionEntity, String> biometricExceptionRepository;

	@Mock
	private BasePacketRepository<ApplicantFingerprintEntity, String> applicantFingerprintRepository;

	@Mock
	private BasePacketRepository<ApplicantIrisEntity, String> applicantIrisRepository;

	@Mock
	private BasePacketRepository<ApplicantPhotographEntity, String> applicantPhotographRepository;

	@Mock
	private BasePacketRepository<RegOsiEntity, String> regOsiRepository;

	@Mock
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> applicantDemographicRepository;

	@Mock
	private BasePacketRepository<RegCenterMachineEntity, String> regCenterMachineRepository;

	@Mock
	private BasePacketRepository<ApplicantDemographicInfoJsonEntity, String> demographicJsonRepository;

	@Mock
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	@Mock
	private Utilities utility;

	@Mock
	private PacketInfoDao packetInfoDao;

	@Mock
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;

	private Identity identity;
	private ApplicantDocumentEntity applicantDocumentEntity;
	private ApplicantDocumentPKEntity applicantDocumentPKEntity;
	private List<FieldValue> metaDataList;
	private DataAccessLayerException exp;
	private InputStream demographicJsonStream;
	private File demographicJsonFile;

	@Before
	public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, FileNotFoundException {
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
		applicant.setLeftSlap(leftPalm);
		applicant.setRightEye(rightEye);
		applicant.setRightSlap(rightPalm);
		applicant.setThumbs(bothThumbs);
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

		Document document = new Document();
		List<Document> documents = new ArrayList<Document>();
		document.setDocumentCategory("poA");
		document.setDocumentOwner("self");
		document.setDocumentName("ResidenceCopy");
		document.setDocumentType("Passport");
		documents.add(document);
		identity.setDocuments(documents);

		BiometricException thumb = new BiometricException();
		thumb.setExceptionDescription("Lost in accident");
		thumb.setExceptionType("Permanent");
		thumb.setLanguage("eng");
		thumb.setMissingBiometric("LeftThumb");
		thumb.setType("fingerprint");

		BiometricException leftForefinger = new BiometricException();
		leftForefinger.setExceptionDescription("Lost in accident");
		leftForefinger.setExceptionType("Permanent");
		leftForefinger.setLanguage("eng");
		leftForefinger.setMissingBiometric("LeftForefinger");
		leftForefinger.setType("fingerprint");

		BiometricException rightEyeexp = new BiometricException();
		rightEyeexp.setExceptionDescription("By birth");
		rightEyeexp.setExceptionType("Permanent");
		rightEyeexp.setLanguage("eng");
		rightEyeexp.setMissingBiometric("LeftThumb");
		rightEyeexp.setType("iris");

		List<BiometricException> excptionBiometrics = new ArrayList<>();
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

		applicantDocumentEntity = new ApplicantDocumentEntity();
		applicantDocumentPKEntity = new ApplicantDocumentPKEntity();
		applicantDocumentPKEntity.setRegId("2018782130000224092018121229");
		applicantDocumentPKEntity.setDocTypCode("passport");
		applicantDocumentPKEntity.setDocCatCode("poA");

		applicantDocumentEntity.setId(applicantDocumentPKEntity);
		applicantDocumentEntity.setPreRegId("PEN1345T");
		applicantDocumentEntity.setDocFileFormat(".zip");
		applicantDocumentEntity.setDocOwner("self");
		String byteArray = "Binary Data";
		applicantDocumentEntity.setActive(true);
		applicantDocumentEntity.setCrBy("Mosip_System");
		applicantDocumentEntity.setCrDtimes(LocalDateTime.now());
		applicantDocumentEntity.setUpdBy("MOSIP_SYSTEM");

		applicantDocumentEntity.setDocStore(byteArray.getBytes());

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

		Mockito.when(filesystemCephAdapterImpl.getFile(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(inputStream);
		exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.toString(), "errorMessage", new Exception());

		demographicJsonFile = new File("..\\packet-info-storage-service\\src\\test\\resources\\DemographicInfo.json");
		demographicJsonStream = new FileInputStream(demographicJsonFile);

		Mockito.when(utility.getConfigServerFileStorageURL())
				.thenReturn("http://104.211.212.28:51000/registration-processor/default/DEV/");
		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");
		Mockito.when(utility.getGetRegProcessorIdentityJson()).thenReturn("RegistrationProcessorIdentity.json");
	}

	@Test
	public void savePacketTest() throws IOException {

		packetInfoManagerImpl.savePacketData(identity);

		// test to cover IoException
		InputStream inputStream = Mockito.mock(InputStream.class);

		Mockito.when(filesystemCephAdapterImpl.getFile(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(inputStream);

		Mockito.when(inputStream.read(ArgumentMatchers.any())).thenThrow(new IOException());

		packetInfoManagerImpl.savePacketData(identity);
		assertEquals(inputStream, filesystemCephAdapterImpl.getFile("1234", PacketFiles.DEMOGRAPHIC.name()));

	}

	@Test(expected = TablenotAccessibleException.class)
	public void savePacketDataTableNotAccessibleTest() throws IOException {

		Mockito.when(applicantDocumentRepository.save(ArgumentMatchers.any())).thenThrow(exp);

		packetInfoManagerImpl.savePacketData(identity);

	}

	@Test
	public void saveDemographicInfoJsonTest() {

		packetInfoManagerImpl.saveDemographicInfoJson(demographicJsonStream, metaDataList);
		assertEquals("identity", utility.getGetRegProcessorDemographicIdentity());
	}

	@Test(expected = FileNotFoundInPacketStore.class)
	public void fileNotFoundInPacketStoreTest() {
		packetInfoManagerImpl.saveDemographicInfoJson(null, metaDataList);
	}

	@Test(expected = UnableToInsertData.class)
	public void unableToInsertDataTest() {

		Mockito.when(demographicDedupeRepository.save(ArgumentMatchers.any())).thenThrow(exp);

		packetInfoManagerImpl.saveDemographicInfoJson(demographicJsonStream, metaDataList);
	}

	@Test(expected = UnableToInsertData.class)
	public void demographicDedupeUnableToInsertDataTest1() {

		Mockito.when(demographicDedupeRepository.save(ArgumentMatchers.any())).thenThrow(exp);
		packetInfoManagerImpl.saveDemographicInfoJson(demographicJsonStream, metaDataList);

	}

	@Test(expected = IdentityNotFoundException.class)
	public void identityNotFoundExceptionTest() {

		Mockito.when(utility.getConfigServerFileStorageURL())
				.thenReturn("http://104.211.212.28:51000/registration-processor/default/DEV/");
		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("test");
		Mockito.when(utility.getGetRegProcessorIdentityJson()).thenReturn("RegistrationProcessorIdentity.json");
		packetInfoManagerImpl.saveDemographicInfoJson(demographicJsonStream, metaDataList);
	}

	@Test
	public void getPacketsForQCUsersTest() {
		List<ApplicantInfoDto> applicantInfoDtoList = new ArrayList<>();
		ApplicantInfoDto applicantInfoDto = new ApplicantInfoDto();
		PhotographDto photographDto = new PhotographDto();
		photographDto.setRegId("2018782130000224092018121229");
		photographDto.setPreRegId("PEN1345T");
		photographDto.setNoOfRetry(4);
		photographDto.setHasExcpPhotograph(false);

		DemographicDedupeDto demoDto = new DemographicDedupeDto();
		List<DemographicDedupeDto> demoDedupeList = new ArrayList<>();
		demoDto.setRegId("2018782130000224092018121229");
		demoDto.setPreRegId("PEN1345T");
		demoDto.setFirstName("firstName");
		demoDto.setLangCode("ar");
		demoDedupeList.add(demoDto);
		applicantInfoDto.setDemoDedupeList(demoDedupeList);
		applicantInfoDto.setApplicantPhotograph(photographDto);
		applicantInfoDtoList.add(applicantInfoDto);

		Mockito.when(packetInfoDao.getPacketsforQCUser(ArgumentMatchers.any())).thenReturn(applicantInfoDtoList);
		packetInfoManagerImpl.getPacketsforQCUser("1234");
		List<ApplicantInfoDto> applicantList = packetInfoDao.getPacketsforQCUser("1234");
		assertEquals(applicantInfoDtoList, applicantList);

	}

	@Test(expected = TablenotAccessibleException.class)
	public void getPacketsForQcUserTablenotAccessibleExceptionTest() {

		Mockito.when(packetInfoDao.getPacketsforQCUser(ArgumentMatchers.any())).thenThrow(exp);
		packetInfoManagerImpl.getPacketsforQCUser("1234");

	}

	@Test(expected = UnableToInsertData.class)
	public void saveJsonUnableToInsertDataTest() {

		Mockito.when(demographicJsonRepository.save(ArgumentMatchers.any())).thenThrow(exp);
		packetInfoManagerImpl.saveDemographicInfoJson(demographicJsonStream, metaDataList);

	}
}
