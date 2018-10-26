package io.mosip.registration.processor.core.spi.packetinfo.service;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.kernel.dataaccess.constant.HibernateErrorCodes;
import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.builder.CoreAuditRequestBuilder;
import io.mosip.registration.processor.core.packet.dto.AddressDTO;
import io.mosip.registration.processor.core.packet.dto.BiometericData;
import io.mosip.registration.processor.core.packet.dto.DemoInLocalLang;
import io.mosip.registration.processor.core.packet.dto.DemoInUserLang;
import io.mosip.registration.processor.core.packet.dto.DemographicInfo;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.DocumentDetail;
import io.mosip.registration.processor.core.packet.dto.ExceptionFingerprint;
import io.mosip.registration.processor.core.packet.dto.ExceptionIris;
import io.mosip.registration.processor.core.packet.dto.Fingerprint;
import io.mosip.registration.processor.core.packet.dto.FingerprintData;
import io.mosip.registration.processor.core.packet.dto.GeoLocation;
import io.mosip.registration.processor.core.packet.dto.Iris;
import io.mosip.registration.processor.core.packet.dto.IrisData;
import io.mosip.registration.processor.core.packet.dto.MetaData;
import io.mosip.registration.processor.core.packet.dto.OsiData;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantFingerprintEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantIrisEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.BiometricExceptionEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.exception.TablenotAccessibleException;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.service.impl.PacketInfoManagerImpl;

@RunWith(MockitoJUnitRunner.class)
public class PacketInfoManagerImplTest {
	@InjectMocks
	PacketInfoManager<PacketInfo, DemographicInfo, MetaData> packetInfoManagerImpl = new PacketInfoManagerImpl();

	@Mock
	CoreAuditRequestBuilder coreAuditRequestBuilder=new CoreAuditRequestBuilder();
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
	private BasePacketRepository<ApplicantDemographicEntity, String> applicantDemographicRepository;

	@Mock
	private BasePacketRepository<RegCenterMachineEntity, String> regCenterMachineRepository;
	private BiometericData biometricData;
	private List<Fingerprint> fingerprintList;
	private Fingerprint fingerprint;
	private Fingerprint fingerprint1;
	private List<ExceptionFingerprint> exceptionFingerprintList;
	private ExceptionFingerprint exceptionFingerprint;
	private ExceptionFingerprint exceptionFingerprint1;
	private FingerprintData fingerprintData;
	private IrisData irisData;
	private List<Iris> irisList;
	private Iris iris1;
	private List<ExceptionIris> exceptionIrisList;
	private ExceptionIris exceptionIris;
	private ExceptionIris exceptionIris1;
	private Document document;
	private List<DocumentDetail> documentDetailList;
	private DocumentDetail documentDetail;
	private OsiData osiData;
	private Photograph photograph;
	private MetaData metaData;
	private GeoLocation geoLocation;
	private ApplicantDocumentEntity applicantDocumentEntity;
	private ApplicantDocumentPKEntity applicantDocumentPKEntity;
	private DemographicInfo demographicInfo;
	private DemoInLocalLang demoInLocalLang;
	private DemoInUserLang demoInUserLang;

	@Before
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		biometricData = new BiometericData();
		fingerprintList = new ArrayList<>();
		fingerprint = new Fingerprint();
		fingerprint.setFingerprintImageName("leftPalm");
		fingerprint.setQualityScore(80.0);
		fingerprint.setNumRetry(4);
		fingerprint.setForceCaptured(false);
		fingerprint.setNumRetry(4);
		fingerprint.setFingerType("leftThumb");

		fingerprint1 = new Fingerprint();
		fingerprint1.setFingerprintImageName("rightPalm");
		fingerprint1.setQualityScore(80.0);
		fingerprint1.setNumRetry(4);
		fingerprint1.setForceCaptured(false);
		fingerprint1.setNumRetry(4);
		fingerprint1.setFingerType("rightThumb");

		fingerprintList.add(fingerprint);
		fingerprintList.add(fingerprint1);

		exceptionFingerprintList = new ArrayList<>();
		exceptionFingerprint = new ExceptionFingerprint();
		exceptionFingerprint.setBiometricType("fingerprint/iris");
		exceptionFingerprint.setExceptionDescription("Lost in accident");
		exceptionFingerprint.setExceptionType("Permanent");
		// exceptionFingerprint.setMissingFinger("rightPalm");
		exceptionFingerprintList.add(exceptionFingerprint);

		exceptionFingerprint1 = new ExceptionFingerprint();
		exceptionFingerprint1.setBiometricType("fingerprint/iris");
		exceptionFingerprint1.setExceptionDescription("Lost");
		exceptionFingerprint1.setExceptionType("Permanent");
		// exceptionFingerprint1.setMissingFinger("LeftPalm");
		exceptionFingerprintList.add(exceptionFingerprint1);

		fingerprintData = new FingerprintData();
		fingerprintData.setFingerprints(fingerprintList);
		fingerprintData.setExceptionFingerprints(exceptionFingerprintList);

		biometricData.setFingerprintData(fingerprintData);

		irisData = new IrisData();
		irisList = new ArrayList<>();
		Iris iris = new Iris();
		iris.setForceCaptured(false);
		iris.setIrisImageName("iris1");
		iris.setIrisType("LeftEye");
		iris.setNumRetry(4);
		iris.setQualityScore(85.5);

		iris1 = new Iris();
		iris1.setForceCaptured(false);
		iris1.setIrisImageName("iris2");
		iris1.setIrisType("rightEye");
		iris1.setNumRetry(null);
		iris1.setQualityScore(85.0);
		irisList.add(iris);
		irisList.add(iris1);

		irisData.setIris(irisList);
		exceptionIrisList = new ArrayList<>();
		exceptionIris = new ExceptionIris();
		exceptionIris.setBiometricType("fingerprint/iris");
		exceptionIris.setExceptionDescription("by birth");
		exceptionIris.setExceptionType("permanent");

		exceptionIris1 = new ExceptionIris();
		exceptionIris1.setBiometricType("fingerprint/iris");
		exceptionIris1.setExceptionDescription("Lost in Accident");
		exceptionIris1.setExceptionType("temporary");
		// exceptionIris1.setMissingIris("leftEye");
		exceptionIrisList.add(exceptionIris1);
		exceptionIrisList.add(exceptionIris);

		irisData.setExceptionIris(exceptionIrisList);
		irisData.setIris(irisList);
		irisData.setNumRetry(2);

		biometricData.setIrisData(irisData);

		document = new Document();
		documentDetailList = new ArrayList<>();
		documentDetail = new DocumentDetail();
		documentDetail.setDocumentCategory("poA");
		documentDetail.setDocumentOwner("self");
		documentDetail.setDocumentName("ResidenceCopy");
		documentDetail.setDocumentType("Passport");
		documentDetailList.add(documentDetail);
		document.setDocumentDetails(documentDetailList);
		document.setRegistrationAckCopy("acknowledgementReceipt");

		osiData = new OsiData();
		osiData.setOperatorId("123245");
		osiData.setOperatorFingerprintImage("leftThumb");
		osiData.setOperatorIrisName("leftEye");
		osiData.setSupervisorId("123456789");
		osiData.setSupervisorName("supervisor");

		osiData.setSupervisorFingerprintImage("leftThumb");

		osiData.setSupervisorIrisName("leftEye");
		osiData.setIntroducerUIN("HOF003");
		osiData.setIntroducerName("introducerTestName");

		osiData.setIntroducerUINHash("HOF003");
		osiData.setIntroducerRID("IRID");

		osiData.setIntroducerRIDHash("Introducer RIDHash");
		osiData.setIntroducerFingerprintImage("leftThumb");
		osiData.setIntroducerIrisImage("osiData");
		photograph = new Photograph();
		photograph.setPhotographName("applicantPhoto");
		photograph.setHasExceptionPhoto(true);
		photograph.setExceptionPhotoName("excep");
		photograph.setQualityScore(80.0);
		photograph.setNumRetry(0);
		metaData = new MetaData();
		geoLocation = new GeoLocation();
		geoLocation.setLatitude(13.0049);
		geoLocation.setLongitude(80.24492);
		metaData.setGeoLocation(geoLocation);

		metaData.setApplicationType("New Registration");
		metaData.setRegistrationCategory("Document/Introducer");
		metaData.setPreRegistrationId("PEN1345T");
		metaData.setRegistrationId("2018782130000224092018121229");
		metaData.setRegistrationIdHash("GHTYU76233887087JLDFDFELFLADGSDD");
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
		applicantDocumentEntity.setCrDtimesz(LocalDateTime.now());
		applicantDocumentEntity.setUpdBy("MOSIP_SYSTEM");

		applicantDocumentEntity.setDocStore(byteArray.getBytes());

		demographicInfo = new DemographicInfo();
		demoInLocalLang = new DemoInLocalLang();
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setLine1("line1");
		addressDTO.setLine2("line2");
		addressDTO.setLine3("line3");
		demoInLocalLang.setAddressDTO(addressDTO);
		demoInLocalLang.setChild(false);
		demoInLocalLang.setEmailId("testMosip@mosip.com");
		demoInLocalLang.setFamilyname("TestFamily");
		demoInLocalLang.setFirstName("FirstNameTest");
		demoInLocalLang.setLastName("Lastnametest");
		demoInLocalLang.setDateOfBirth("1539674005050");
		demoInLocalLang.setForename("ForeNametest");
		demoInLocalLang.setFullName("FullNametest");
		demoInLocalLang.setGender("Male");
		demoInLocalLang.setGivenname("Mosip");
		demoInLocalLang.setLanguageCode("eng");
		demoInLocalLang.setMobile("9876543210");
		demoInLocalLang.setSurname("Surnametest");

		demographicInfo.setDemoInLocalLang(demoInLocalLang);
		demoInUserLang = new DemoInUserLang();
		demoInUserLang.setAddressDTO(addressDTO);
		demoInUserLang.setChild(false);
		demoInUserLang.setEmailId("testMosip@mosip.com");
		demoInUserLang.setFirstName("FirstNameTest");
		demoInUserLang.setLastName("LastNameTest");
		demoInUserLang.setDateOfBirth("1539674005050");
		demoInUserLang.setLanguageCode("eng");

		demoInLocalLang.setFamilyname("FamilyName");
		demoInLocalLang.setForename("ForeNameTest");
		demoInLocalLang.setFullName("FullNameTest");
		demoInLocalLang.setGender("Male");
		demoInLocalLang.setGivenname("GivenNameTest");
		demoInLocalLang.setMiddleName("middleNameTest");
		demoInLocalLang.setMobile("9876543210");
		demographicInfo.setDemoInUserLang(demoInUserLang);

		AuditRequestBuilder auditRequestBuilder1 = new AuditRequestBuilder();
		AuditHandler<AuditRequestDto> auditHandler = new AuditHandler<AuditRequestDto>() {

			@Override
			public boolean writeAudit(AuditRequestDto arg0) {

				return true;
			}
		};
		Field f1 = CoreAuditRequestBuilder.class.getDeclaredField("auditRequestBuilder");
		f1.setAccessible(true);
		f1.set(coreAuditRequestBuilder, auditRequestBuilder1);

		Field f2 = CoreAuditRequestBuilder.class.getDeclaredField("auditHandler");
		f2.setAccessible(true);
		f2.set(coreAuditRequestBuilder, auditHandler);

	}

	@Test
	public void savePacketTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException {

		PacketInfo packetInfo = Mockito.mock(PacketInfo.class);
		Mockito.when(packetInfo.getBiometericData()).thenReturn(biometricData);
		Mockito.when(packetInfo.getDocument()).thenReturn(document);
		Mockito.when(packetInfo.getOsiData()).thenReturn(osiData);
		Mockito.when(packetInfo.getPhotograph()).thenReturn(photograph);
		Mockito.when(packetInfo.getMetaData()).thenReturn(metaData);

		FileSystemAdapter<InputStream, Boolean> fileSystemAdapter = Mockito.mock(FileSystemAdapter.class);
		Field f = packetInfoManagerImpl.getClass().getDeclaredField("fileSystemAdapter");
		f.setAccessible(true);
		f.set(packetInfoManagerImpl, fileSystemAdapter);

		String inputString = "test";
		InputStream inputStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));

		Mockito.when(fileSystemAdapter.getFile(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(inputStream);

		packetInfoManagerImpl.savePacketData(packetInfo);

		packetInfoManagerImpl.saveDemographicData(demographicInfo, metaData);

		assertEquals("Verifing if Registration Id is present in DB. Expected value is true",
				metaData.getRegistrationId(), packetInfo.getMetaData().getRegistrationId());

	}

	@Test(expected = TablenotAccessibleException.class)
	public void testDemographicFailureCase() {
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, "errorMessage",
				new Exception());
		Mockito.when(applicantDemographicRepository.save(ArgumentMatchers.any())).thenThrow(exp);
		packetInfoManagerImpl.saveDemographicData(demographicInfo, metaData);

	}

}
