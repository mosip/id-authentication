package io.mosip.kernel.cbeffutil.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.cbeffutil.common.CbeffISOReader;
import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIRInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIRVersion;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.QualityType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.RegistryIDType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CbeffImplTest {

	@Autowired
	private CbeffUtil cbeffUtilImpl;

	/*
	 * XSD storage path from config server
	 */

	@Value("${mosip.kernel.xsdstorage-uri}:test")
	private String configServerFileStorageURL;

	/*
	 * XSD file name
	 */

	@Value("${mosip.kernel.xsdfile}:test")
	private String schemaName;

	private List<BIR> createList;
	private List<BIR> updateList;
	private static final String localpath = "./src/main/resources";

	@Before
	public void setUp() throws Exception {
		byte[] rindexFinger = CbeffISOReader.readISOImage(localpath + "/images/" + "FingerPrintRight_Index.iso", "Finger");
		byte[] rmiddleFinger = CbeffISOReader.readISOImage(localpath + "/images/" + "FingerPrintRight_Middle.iso", "Finger");
		byte[] rringFinger = CbeffISOReader.readISOImage(localpath + "/images/" + "FingerPrintRight_Ring.iso", "Finger");
		byte[] rlittleFinger = CbeffISOReader.readISOImage(localpath + "/images/" + "FingerPrintRight_Little.iso", "Finger");
		byte[] rightthumb = CbeffISOReader.readISOImage(localpath + "/images/" + "FingerPrintRight_Thumb.iso", "Finger");
		byte[] lindexFinger = CbeffISOReader.readISOImage(localpath + "/images/" + "FingerPrintLeft_Index.iso", "Finger");
		byte[] lmiddleFinger = CbeffISOReader.readISOImage(localpath + "/images/" + "FingerPrintLeft_Middle.iso", "Finger");
		byte[] lringFinger = CbeffISOReader.readISOImage(localpath + "/images/" + "FingerPrintLeft_Ring.iso", "Finger");
		byte[] llittleFinger = CbeffISOReader.readISOImage(localpath + "/images/" + "FingerPrintLeft_Little.iso", "Finger");
		byte[] leftthumb = CbeffISOReader.readISOImage(localpath + "/images/" + "FingerPrintLeft_Thumb.iso", "Finger");
		//byte[] irisImg1 = CbeffISOReader.readISOImage(localpath + "/images/" + "IrisImageRight.iso", "Iris");
		//byte[] irisImg2 = CbeffISOReader.readISOImage(localpath + "/images/" + "IrisImageLeft.iso", "Iris");
		//byte[] faceImg = CbeffISOReader.readISOImage(localpath + "/images/" + "faceImage.iso", "Face");
		RegistryIDType format = new RegistryIDType();
		format.setOrganization("257");
		format.setType("7");
		QualityType Qtype = new QualityType();
		Qtype.setScore(new Long(100));
		RegistryIDType algorithm = new RegistryIDType();
		algorithm.setOrganization("HMAC");
		algorithm.setType("SHA-256");
		Qtype.setAlgorithm(algorithm);
		createList = new ArrayList<>();
		BIR rIndexFinger = new BIR.BIRBuilder().withBdb(rindexFinger)
				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
						.withQuality(Qtype).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right IndexFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
						.withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
				.build();
		
		createList.add(rIndexFinger);
		
		BIR rMiddleFinger = new BIR.BIRBuilder().withBdb(rmiddleFinger)
				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
						.withQuality(Qtype).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right MiddleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
						.withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
				.build();
		
		createList.add(rMiddleFinger);
		
		BIR rRingFinger = new BIR.BIRBuilder().withBdb(rringFinger)
				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
						.withQuality(Qtype).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right RingFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
						.withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
				.build();
		
		createList.add(rRingFinger);
		
		BIR rLittleFinger = new BIR.BIRBuilder().withBdb(rlittleFinger)
				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
						.withQuality(Qtype).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right LittleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
						.withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
				.build();

		createList.add(rLittleFinger);

		BIR lIndexFinger = new BIR.BIRBuilder().withBdb(lindexFinger)
				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
						.withQuality(Qtype).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Left IndexFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
						.withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
				.build();
		
		createList.add(lIndexFinger);
		
		BIR lMiddleFinger = new BIR.BIRBuilder().withBdb(lmiddleFinger)
				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
						.withQuality(Qtype).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Left MiddleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
						.withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
				.build();
		
		createList.add(lMiddleFinger);
		
		BIR lRightFinger = new BIR.BIRBuilder().withBdb(lringFinger)
				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
						.withQuality(Qtype).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Left RingFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
						.withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
				.build();
		
		createList.add(lRightFinger);
		
		BIR lLittleFinger = new BIR.BIRBuilder().withBdb(llittleFinger)
				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
						.withQuality(Qtype).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Left LittleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
						.withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
				.build();
		
		createList.add(lLittleFinger);

		BIR rightThumb = new BIR.BIRBuilder().withBdb(rightthumb)
				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
						.withQuality(Qtype).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right Thumb")).withPurpose(PurposeType.ENROLL)
						.withLevel(ProcessedLevelType.RAW).withCreationDate(LocalDateTime.now(ZoneId.of("UTC")))
						.build())
				.build();
		
		createList.add(rightThumb);
		
		BIR leftThumb = new BIR.BIRBuilder().withBdb(leftthumb)
				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
						.withQuality(Qtype).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Left Thumb")).withPurpose(PurposeType.ENROLL)
						.withLevel(ProcessedLevelType.RAW).withCreationDate(LocalDateTime.now(ZoneId.of("UTC")))
						.build())
				.build();
		
		createList.add(leftThumb);

//		BIR face = new BIR.BIRBuilder().withBdb(faceImg)
//				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
//				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
//				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
//				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
//						.withQuality(Qtype).withType(Arrays.asList(SingleType.FACE)).withPurpose(PurposeType.ENROLL)
//						.withLevel(ProcessedLevelType.RAW).withCreationDate(LocalDateTime.now(ZoneId.of("UTC")))
//						.build())
//				.build();
//		
//		createList.add(face);
//
//		BIR leftIris = new BIR.BIRBuilder().withBdb(irisImg1)
//				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
//				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
//				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
//				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
//						.withQuality(Qtype).withType(Arrays.asList(SingleType.IRIS)).withSubtype(Arrays.asList("Right"))
//						.withPurpose(PurposeType.ENROLL).withCreationDate(LocalDateTime.now(ZoneId.of("UTC")))
//						.withLevel(ProcessedLevelType.RAW).build())
//				.build();
//		
//		createList.add(leftIris);
//
//		BIR rightIris = new BIR.BIRBuilder().withBdb(irisImg2)
//				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
//				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
//				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
//				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
//						.withQuality(Qtype).withType(Arrays.asList(SingleType.IRIS)).withSubtype(Arrays.asList("Left"))
//						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
//						.withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
//				.build();
//		
//		createList.add(rightIris);

	}
	//@Test
	public void testCreateXML() throws Exception {
		byte[] createXml = cbeffUtilImpl.createXML(createList);
		createXMLFile(createXml, "createCbeffLatest");
		assertEquals(new String(createXml), new String(readCreatedXML("createCbeffLatest")));

	}

	@Test
	public void testCreateXMLFromLocal() throws Exception {
		byte[] createXml = cbeffUtilImpl.createXML(createList, readXSD("updatedcbeff"));
		createXMLFile(createXml, "createCbeffLatest2");
		assertEquals(new String(createXml), new String(readCreatedXML("createCbeffLatest2")));

	}

	private byte[] readCreatedXML(String name) throws IOException {
		byte[] fileContent = Files.readAllBytes(Paths.get(localpath + "/schema/" + name + ".xml"));
		return fileContent;
	}

	private byte[] readXSD(String name) throws IOException {
		byte[] fileContent = Files.readAllBytes(Paths.get(localpath + "/schema/" + name + ".xsd"));
		return fileContent;
	}

	private static void createXMLFile(byte[] updatedXmlBytes, String name) throws Exception {
		File tempFile = new File(localpath + "/schema/" + name + ".xml");
		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.write(updatedXmlBytes);
		fos.close();
	}

	// @Test
	public void testUpdateXML() throws Exception {
		byte[] updateXml = cbeffUtilImpl.updateXML(updateList, readCreatedXML("createCbeff"));
		createXMLFile(updateXml, "updateCbeff");
		assertEquals(new String(updateXml), new String(readCreatedXML("updateCbeff")));
	}

	// @Test
	public void testValidateXML() throws IOException, Exception {
		assertTrue(cbeffUtilImpl.validateXML(readCreatedXML("createCbeff"), getXSDfromConfigServer()));
	}

	private byte[] getXSDfromConfigServer() throws URISyntaxException, IOException {
		InputStream input = new URL(configServerFileStorageURL + schemaName).openStream();
		byte[] fileContent = readbytesFromStream(input);
		return fileContent;
	}

	private byte[] readbytesFromStream(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		// this is storage overwritten on each iteration with bytes
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		// we need to know how may bytes were read to write them to the byteBuffer
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
		}
		// and then we can return your byte array.
		return byteBuffer.toByteArray();

	}

	//@Test
	public void testGetBDBBasedOnType() throws IOException, Exception {
		Map<String, String> testMap = cbeffUtilImpl.getBDBBasedOnType(readCreatedXML("createCbeffLatest2"), "Finger", "Right");
		Set<String> testSet1 = new HashSet<>();
		testSet1.add("FINGER_Right");
		assertEquals(testMap.keySet(), testSet1);
//		Map<String, String> testMap1 = cbeffUtilImpl.getBDBBasedOnType(readCreatedXML("updateCbeff"), "FMR", null);
//		Set<String> testSet2 = new HashSet<>();
//		testSet2.add("FINGER_Right IndexFinger MiddleFinger RingFinger LittleFinger_2");
//		assertEquals(testMap1.keySet(), testSet2);
//		Map<String, String> testMap2 = cbeffUtilImpl.getBDBBasedOnType(readCreatedXML("updateCbeff"), null, "Right");
//		Set<String> testSet3 = new HashSet<>();
//		testSet3.add("FINGER_Right IndexFinger MiddleFinger RingFinger LittleFinger_7");
//		testSet3.add("IRIS_Right_9");
//		testSet3.add("FINGER_Right IndexFinger MiddleFinger RingFinger LittleFinger_2");
//		testSet3.add("FINGER_Left Right Thumb_7");
//		assertEquals(testMap2.keySet(), testSet3);
	}

}
