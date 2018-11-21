/*
package io.mosip.registration.processor.packet.storage;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.cms.MetaData;
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
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantFingerprintEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantIrisEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.BiometricExceptionEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.exception.TablenotAccessibleException;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.service.impl.PacketInfoManagerImpl;

@RunWith(MockitoJUnitRunner.class)
public class PacketInfoManagerImplTest {
	@InjectMocks
	PacketInfoManager<PacketInfo, Demographic, MetaData,ApplicantInfoDto> packetInfoManagerImpl = new PacketInfoManagerImpl();

	@Mock
	ClientAuditRequestBuilder clientAuditRequestBuilder;

	@Mock
	AuditmanagerClient auditmanagerClient;

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
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;


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
	private Demographic demographicInfo;
	private DemographicInfo demoInLocalLang;
	private DemographicInfo demoInUserLang;

	@Mock
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;

	@Mock
	private PacketInfoDao packetInfoDao;

	private List<ApplicantInfoDto>  listDto= new ArrayList<ApplicantInfoDto>();

	QcuserRegistrationIdEntity qcuserRegistrationIdEntity1;
	QcuserRegistrationIdEntity qcuserRegistrationIdEntity2;
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
		applicantDocumentEntity.setCrDtimes(LocalDateTime.now());
		applicantDocumentEntity.setUpdBy("MOSIP_SYSTEM");

		applicantDocumentEntity.setDocStore(byteArray.getBytes());

		demographicInfo = new Demographic();
		demoInLocalLang = new DemographicInfo();
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setLine1("line1");
		addressDTO.setLine2("line2");
		addressDTO.setLine3("line3");
		demoInLocalLang.setAddressDTO(addressDTO);
		demoInLocalLang.setChild(false);
		demoInLocalLang.setEmailId("testMosip@mosip.com");

		demoInLocalLang.setFirstName("FirstNameTest");
		demoInLocalLang.setLastName("Lastnametest");
		demoInLocalLang.setDateOfBirth("1539674005050");

		demoInLocalLang.setFullName("FullNametest");
		demoInLocalLang.setGender("male");

		demoInLocalLang.setLanguageCode("eng");
		demoInLocalLang.setMobile("9876543210");

		demographicInfo.setDemoInLocalLang(demoInLocalLang);
		demoInUserLang = new DemographicInfo();
		demoInUserLang.setAddressDTO(addressDTO);
		demoInUserLang.setChild(false);
		demoInUserLang.setEmailId("testMosip@mosip.com");
		demoInUserLang.setFirstName("FirstNameTest");
		demoInUserLang.setLastName("LastNameTest");
		demoInUserLang.setDateOfBirth("1539674005050");
		demoInUserLang.setLanguageCode("eng");

		demoInLocalLang.setFullName("FullNameTest");
		demoInLocalLang.setGender("male");

		demoInLocalLang.setMiddleName("middleNameTest");
		demoInLocalLang.setMobile("9876543210");
		demographicInfo.setDemoInUserLang(demoInUserLang);


		qcuserRegistrationIdEntity1=new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity pkid1=new QcuserRegistrationIdPKEntity();
		pkid1.setUsrId("qc001");
		pkid1.setRegId("2018782130000116102018124324");
		qcuserRegistrationIdEntity1.setCrBy("MOSIP_SYSTEM");
		qcuserRegistrationIdEntity1.setIsActive(true);
		qcuserRegistrationIdEntity1.setId(pkid1);
		qcuserRegistrationIdEntity1.setStatus_code("ACCEPTED");
		qcuserRegistrationIdEntity1.setUpdBy("MOSIP_SYSTEM");
		qcuserRegistrationIdEntity1.setCrDtimes(LocalDateTime.now());
		qcuserRegistrationIdEntity1.setIsDeleted(false);
		qcuserRegistrationIdEntity1.setUpdDtimes(LocalDateTime.now());

		qcuserRegistrationIdEntity2=new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity pkid2=new QcuserRegistrationIdPKEntity();
		pkid2.setUsrId("qc001");
		pkid2.setRegId("2018782130000116102018124325");
		qcuserRegistrationIdEntity2.setCrBy("MOSIP_SYSTEM");
		qcuserRegistrationIdEntity2.setIsActive(true);
		qcuserRegistrationIdEntity2.setId(pkid2);
		qcuserRegistrationIdEntity2.setStatus_code("ACCEPTED");
		qcuserRegistrationIdEntity2.setUpdBy("MOSIP_SYSTEM");
		qcuserRegistrationIdEntity2.setCrDtimes(LocalDateTime.now());
		qcuserRegistrationIdEntity2.setIsDeleted(false);
		qcuserRegistrationIdEntity2.setUpdDtimes(LocalDateTime.now());


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

		Field f = packetInfoManagerImpl.getClass().getDeclaredField("filesystemCephAdapterImpl");
		f.setAccessible(true);
		f.set(packetInfoManagerImpl, filesystemCephAdapterImpl);

		String inputString = "test";
		InputStream inputStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));

		Mockito.when(filesystemCephAdapterImpl.getFile(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(inputStream);

		packetInfoManagerImpl.savePacketData(packetInfo);

		packetInfoManagerImpl.saveDemographicData(demographicInfo, metaData);

		assertEquals("Verifing if Registration Id is present in DB. Expected value is true",
				metaData.getRegistrationId(), packetInfo.getMetaData().getRegistrationId());

	}

	@Test(expected = TablenotAccessibleException.class)
	public void testDemographicFailureCase() {
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.toString(), "errorMessage",
				new Exception());
		Mockito.when(applicantDemographicRepository.save(ArgumentMatchers.any())).thenThrow(exp);
		packetInfoManagerImpl.saveDemographicData(demographicInfo, metaData);

	}
	@Test
	public void getPacketsforQCUserDemographic() {
		List<ApplicantInfoDto> list = new ArrayList<>();
		ApplicantInfoDto dto = new ApplicantInfoDto();
		dto.setApplicantPhoto(photograph);
		dto.setDemoInLocalLang(demoInLocalLang);
		dto.setDemoInUserLang(demoInUserLang);
		list.add(dto);
		Mockito.when(packetInfoDao.getPacketsforQCUser(ArgumentMatchers.any())).thenReturn(list);
		assertEquals("male",packetInfoManagerImpl.getPacketsforQCUser("qc001").get(0).getDemoInLocalLang().getGender());

	}


	@Test(expected = TablenotAccessibleException.class)
	public void getPacketsforQCUserDemographicFailureCase() {

		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.toString(), "errorMessage",
				new Exception());


		ApplicantDemographicEntity[] applicantDemographicEntity=new ApplicantDemographicEntity[2];
		ApplicantDemographicPKEntity pk1= new ApplicantDemographicPKEntity();
		pk1.setLangCode("en");


		pk1.setRegId("2018782130000116102018124325");

		applicantDemographicEntity[0] = new ApplicantDemographicEntity();
		applicantDemographicEntity[0].setId(pk1);
		applicantDemographicEntity[0].setApplicantType("qc_user");
		applicantDemographicEntity[0].setCrBy("MOSIP_SYSTEM");
		applicantDemographicEntity[0].setCrDtimesz(LocalDateTime.now());
		applicantDemographicEntity[0].setGenderCode("female");
		applicantDemographicEntity[0].setLocationCode("dhe");
		applicantDemographicEntity[0].setPreRegId("1001");
		ApplicantDemographicPKEntity pk2= new ApplicantDemographicPKEntity();
		pk2.setLangCode("use");
		pk2.setRegId("2018782130000116102018124325");
		applicantDemographicEntity[1] = new ApplicantDemographicEntity();

		applicantDemographicEntity[1].setId(pk2);
		applicantDemographicEntity[1].setApplicantType("qc_user");
		applicantDemographicEntity[1].setCrBy("MOSIP_SYSTEM");
		applicantDemographicEntity[1].setCrDtimesz(LocalDateTime.now());
		applicantDemographicEntity[1].setGenderCode("female");
		applicantDemographicEntity[1].setLocationCode("dhe");
		applicantDemographicEntity[1].setPreRegId("1001");
	    List<Object[]> applicantInfo = new ArrayList<>();

		applicantInfo.add(applicantDemographicEntity);

		Mockito.when(packetInfoDao.getPacketsforQCUser(ArgumentMatchers.any())).thenThrow(exp);
		packetInfoManagerImpl.getPacketsforQCUser("qcuser1");
	}

	@Test
	public void getPacketsforQCUserPhotographic() {

		List<ApplicantInfoDto> list = new ArrayList<>();
		ApplicantInfoDto dto = new ApplicantInfoDto();
		dto.setApplicantPhoto(photograph);
		dto.setDemoInLocalLang(demoInLocalLang);
		dto.setDemoInUserLang(demoInUserLang);
		list.add(dto);
		Mockito.when(packetInfoDao.getPacketsforQCUser(ArgumentMatchers.any())).thenReturn(list);
		assertEquals(true,packetInfoManagerImpl.getPacketsforQCUser("qc001").get(0).getApplicantPhoto().isHasExceptionPhoto());


	}

	@Test(expected = TablenotAccessibleException.class)
	public void getPacketsforQCUserPhotographicfailureCase() {
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.toString(), "errorMessage", null);
		ApplicantPhotographEntity[] applicantPhotographEntity=new ApplicantPhotographEntity[1];
		applicantPhotographEntity[0]=new ApplicantPhotographEntity();
		applicantPhotographEntity[0].setImageName("new_image");;
		applicantPhotographEntity[0].setExcpPhotoName("new_image");
		applicantPhotographEntity[0].setNoOfRetry(2);
		applicantPhotographEntity[0].setHasExcpPhotograph(true);
		applicantPhotographEntity[0].setQualityScore(new BigDecimal(123456123456.78));
		List<Object[]> applicantInfo2 = new ArrayList<>();
		applicantInfo2.add(applicantPhotographEntity);

		Mockito.when(packetInfoDao.getPacketsforQCUser(ArgumentMatchers.any())).thenThrow(exp);
		packetInfoManagerImpl.getPacketsforQCUser("qcuser1");
	}

}
*/
