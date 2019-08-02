package io.mosip.registration.processor.quality.checker.stage.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.QualityScore;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BDBInfoType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.QualityType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.RegistryIDType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.quality.checker.stage.QualityCheckerStage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class QualityCheckerStageTest {

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;
	
	@Mock
	private RegistrationExceptionMapperUtil registrationStatusMapperUtil;

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	private InternalRegistrationStatusDto registrationStatusDto;

	@Mock
	private Utilities utility;

	@Mock
	private PacketManager adapter;

	/** The cbeff util. */
	@Mock
	private CbeffUtil cbeffUtil;

	@Mock
	private IBioApi iBioApi;

	@InjectMocks
	private QualityCheckerStage qualityCheckerStage = new QualityCheckerStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String url, int instanceNumber) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}

		@Override
		public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
				MessageBusAddress toAddress) {
		}
	};

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(qualityCheckerStage, "irisThreshold", 70);
		ReflectionTestUtils.setField(qualityCheckerStage, "leftFingerThreshold", 80);
		ReflectionTestUtils.setField(qualityCheckerStage, "rightFingerThreshold", 80);
		ReflectionTestUtils.setField(qualityCheckerStage, "thumbFingerThreshold", 80);
		ReflectionTestUtils.setField(qualityCheckerStage, "faceThreshold", 25);

		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any());
		String idJsonString = "{\n" + "  \"identity\" : {\n" + "    \"fullName\" : [ {\n"
				+ "      \"language\" : \"eng\",\n" + "      \"value\" : \"Ragavendran V\"\n" + "    }, {\n"
				+ "      \"language\" : \"ara\",\n" + "      \"value\" : \"قشلشرثىيقشى ر\"\n" + "    } ],\n"
				+ "    \"individualBiometrics\" : {\n" + "      \"format\" : \"cbeff\",\n"
				+ "      \"version\" : 1.0,\n" + "      \"value\" : \"applicant_bio_CBEFF\"\n" + "    }\n" + "  }\n"
				+ "}";
		InputStream idJsonStream = IOUtils.toInputStream(idJsonString, "UTF-8");

		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");

		ClassLoader classLoader = getClass().getClassLoader();
		File cbeff1 = new File(classLoader.getResource("CBEFF1.xml").getFile());
		InputStream cbeff1Stream = new FileInputStream(cbeff1);

		Mockito.when(adapter.getFile(any(), any())).thenReturn(idJsonStream).thenReturn(cbeff1Stream);

		List<BIRType> birTypeList = new ArrayList<>();
		BIRType birType1 = new BIRType();
		BDBInfoType bdbInfoType1 = new BDBInfoType();
		RegistryIDType registryIDType=new RegistryIDType();
		registryIDType.setOrganization("Mosip");
		registryIDType.setType("257");
		QualityType quality=new QualityType();
		quality.setAlgorithm(registryIDType);
		quality.setScore(90l);
		bdbInfoType1.setQuality(quality);
		SingleType singleType1 = SingleType.FINGER;
		List<SingleType> singleTypeList1 = new ArrayList<>();
		singleTypeList1.add(singleType1);
		List<String> subtype1 = new ArrayList<>(Arrays.asList("Left", "RingFinger"));
		bdbInfoType1.setSubtype(subtype1);
		bdbInfoType1.setType(singleTypeList1);
		birType1.setBDBInfo(bdbInfoType1);
		birTypeList.add(birType1);

		BIRType birType2 = new BIRType();
		BDBInfoType bdbInfoType2 = new BDBInfoType();
		bdbInfoType2.setQuality(quality);
		SingleType singleType2 = SingleType.FINGER;
		List<SingleType> singleTypeList2 = new ArrayList<>();
		singleTypeList2.add(singleType2);
		List<String> subtype2 = new ArrayList<>(Arrays.asList("Right", "RingFinger"));
		bdbInfoType2.setSubtype(subtype2);
		bdbInfoType2.setType(singleTypeList2);
		birType2.setBDBInfo(bdbInfoType2);
		birTypeList.add(birType2);

		BIRType birType3 = new BIRType();
		BDBInfoType bdbInfoType3 = new BDBInfoType();
		bdbInfoType3.setQuality(quality);
		SingleType singleType3 = SingleType.IRIS;
		List<SingleType> singleTypeList3 = new ArrayList<>();
		singleTypeList3.add(singleType3);
		List<String> subtype3 = new ArrayList<>(Arrays.asList("Right"));
		bdbInfoType3.setSubtype(subtype3);
		bdbInfoType3.setType(singleTypeList3);
		birType3.setBDBInfo(bdbInfoType3);
		birTypeList.add(birType3);

		BIRType birType4 = new BIRType();
		BDBInfoType bdbInfoType4 = new BDBInfoType();
		bdbInfoType4.setQuality(quality);
		SingleType singleType4 = SingleType.FACE;
		List<SingleType> singleTypeList4 = new ArrayList<>();
		singleTypeList4.add(singleType4);
		List<String> subtype4 = new ArrayList<>();
		bdbInfoType4.setSubtype(subtype4);
		bdbInfoType4.setType(singleTypeList4);
		birType4.setBDBInfo(bdbInfoType4);
		birTypeList.add(birType4);

		List<BIR> birList = getBIRList(birTypeList);
		Mockito.when(cbeffUtil.getBIRDataFromXML(any())).thenReturn(birTypeList);
		Mockito.when(cbeffUtil.convertBIRTypeToBIR(any())).thenReturn(birList);
	}

	@Test
	public void testDeployVerticle() {
		qualityCheckerStage.deployVerticle();
	}

	@Test
	public void testQualityCheckerSuccess() throws BiometricException {
		QualityScore qualityScore = new QualityScore();
		qualityScore.setInternalScore(90);
		Mockito.when(iBioApi.checkQuality(any(), any())).thenReturn(qualityScore);

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890");
		MessageDTO result = qualityCheckerStage.process(dto);

		assertTrue(result.getIsValid());
	}

	@Test
	public void testQualityCheckFailure() throws BiometricException {
		QualityScore qualityScore = new QualityScore();
		qualityScore.setInternalScore(50);
		Mockito.when(iBioApi.checkQuality(any(), any())).thenReturn(qualityScore);

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890");
		MessageDTO result = qualityCheckerStage.process(dto);

		assertFalse(result.getIsValid());
	}

	@Test
	public void testParameterMissing() throws IOException, PacketDecryptionFailureException, ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		String idJsonString = "{\n" + "  \"identity\" : {\n" + "    \"fullName\" : [ {\n"
				+ "      \"language\" : \"eng\",\n" + "      \"value\" : \"Ragavendran V\"\n" + "    }, {\n"
				+ "      \"language\" : \"ara\",\n" + "      \"value\" : \"قشلشرثىيقشى ر\"\n" + "    } ]\n" + "  }\n"
				+ "}";
		InputStream idJsonStream = IOUtils.toInputStream(idJsonString, "UTF-8");
		Mockito.when(adapter.getFile(any(), any())).thenReturn(idJsonStream);

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890");
		MessageDTO result = qualityCheckerStage.process(dto);

		assertTrue(result.getIsValid());
	}

	@Test
	public void testFileNameMissing() throws IOException, PacketDecryptionFailureException, ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		String idJsonString = "{\n" + "  \"identity\" : {\n" + "    \"fullName\" : [ {\n"
				+ "      \"language\" : \"eng\",\n" + "      \"value\" : \"Ragavendran V\"\n" + "    }, {\n"
				+ "      \"language\" : \"ara\",\n" + "      \"value\" : \"قشلشرثىيقشى ر\"\n" + "    } ],\n"
				+ "    \"individualBiometrics\" : {\n" + "      \"format\" : \"cbeff\",\n"
				+ "      \"version\" : 1.0,\n" + "      \"value\" : \"\"\n" + "    }\n" + "  }\n" + "}";
		InputStream idJsonStream = IOUtils.toInputStream(idJsonString, "UTF-8");
		Mockito.when(adapter.getFile(any(), any())).thenReturn(idJsonStream);

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890");
		MessageDTO result = qualityCheckerStage.process(dto);

		assertTrue(result.getInternalError());
	}

	@Test
	public void testFsAdapterException() throws PacketDecryptionFailureException, ApisResourceAccessException, io.mosip.kernel.core.exception.IOException, IOException {
		FSAdapterException exception = new FSAdapterException("", "");
		Mockito.when(adapter.getFile(any(), any())).thenThrow(exception);
		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890");
		MessageDTO result = qualityCheckerStage.process(dto);

		assertTrue(result.getInternalError());
	}

	@Test
	public void testBiometricException() throws BiometricException {
		BiometricException exception = new BiometricException("", "");
		Mockito.when(iBioApi.checkQuality(any(), any())).thenThrow(exception);
		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890");
		MessageDTO result = qualityCheckerStage.process(dto);

		assertTrue(result.getInternalError());
	}

	private static List<BIR> getBIRList(List<BIRType> birTypeList) {
		List<BIR> birList = new ArrayList<>();
		for(BIRType birType:birTypeList)
		{
			BIR bir = new BIR.BIRBuilder().withBdb(birType.getBDB()).withElement(birType.getAny())
					.withBdbInfo(new BDBInfo.BDBInfoBuilder()
							.withQuality(birType.getBDBInfo().getQuality())
							.withType(birType.getBDBInfo().getType())
							.withSubtype(birType.getBDBInfo().getSubtype()).build())
					.build();
			birList.add(bir);
		}
		return birList;
	}

}
