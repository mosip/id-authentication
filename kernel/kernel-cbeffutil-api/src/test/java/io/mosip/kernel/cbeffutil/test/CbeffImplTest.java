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
import io.mosip.kernel.core.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CbeffImplTest {
	
	@Autowired
	private CbeffUtil cbeffUtilImpl;
	
	/*
	 * XSD storage path from config server
	 * */
	
	@Value("${mosip.kernel.xsdstorage-uri}")
	private String configServerFileStorageURL;
	
	/*
	 * XSD file name
	 * */
	
	@Value("${mosip.kernel.xsdfile}")
	private String schemaName;

	
	private List<BIR> createList;
	private List<BIR> updateList;
	private static final String localpath = "./src/main/resources";

	@Before
	public void setUp() throws Exception {
		byte[] fingerImg = CbeffISOReader.readISOImage(localpath + "/images/" + "ISOImage.iso", "Finger");
		byte[] irisImg = CbeffISOReader.readISOImage(localpath + "/images/" + "Sample_IRIS.iso", "Iris");
		BIR rFinger = new BIR.BIRBuilder().withBdb(fingerImg)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right IndexFinger MiddleFinger RingFinger LittleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(LocalDateTime.now(ZoneId.of("UTC")))
						.build())
				.build();

		BIR lFinger = new BIR.BIRBuilder().withBdb(fingerImg)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Left IndexFinger MiddleFinger RingFinger LittleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(LocalDateTime.now(ZoneId.of("UTC")))
						.build())
				.build();

		BIR thumb = new BIR.BIRBuilder().withBdb(fingerImg)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Left Right Thumb")).withPurpose(PurposeType.ENROLL)
						.withLevel(ProcessedLevelType.RAW).withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
				.build();

		BIR face = new BIR.BIRBuilder().withBdb(new String("Test").getBytes())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(8))
						.withQuality(90).withType(Arrays.asList(SingleType.FACE)).withPurpose(PurposeType.ENROLL)
						.withLevel(ProcessedLevelType.RAW).withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
				.build();

		BIR leftIris = new BIR.BIRBuilder().withBdb(new String(irisImg).getBytes())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(9))
						.withQuality(80).withType(Arrays.asList(SingleType.IRIS)).withSubtype(Arrays.asList("Left"))
						.withPurpose(PurposeType.ENROLL).withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).withLevel(ProcessedLevelType.RAW).build())
				.build();

		BIR rightIris = new BIR.BIRBuilder().withBdb(new String(irisImg).getBytes())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(9))
						.withQuality(90).withType(Arrays.asList(SingleType.IRIS)).withSubtype(Arrays.asList("Right"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
				.build();

		createList = new ArrayList<>();
		createList.add(rFinger);
		createList.add(lFinger);
		createList.add(thumb);
		createList.add(leftIris);
		createList.add(rightIris);
		createList.add(face);

		// Finger Minutiae is of Single Type - Finger and BDB Format Type - 2
		BIR fingerMinutiae1 = new BIR.BIRBuilder().withBdb(fingerImg)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(2))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right IndexFinger MiddleFinger RingFinger LittleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(LocalDateTime.now(ZoneId.of("UTC")))
						.build())
				.build();
		
		BIR fingerMinutiae2 = new BIR.BIRBuilder().withBdb(new String("fingerminutae").getBytes())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(2))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right IndexFinger MiddleFinger RingFinger LittleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(LocalDateTime.now(ZoneId.of("UTC")))
						.build())
				.build();

		updateList = new ArrayList<>();
		updateList.add(fingerMinutiae1);
		updateList.add(fingerMinutiae2);

	}

	@Test
	public void testCreateXML() throws Exception {
		byte[] createXml = cbeffUtilImpl.createXML(createList);
		createXMLFile(createXml, "createCbeff");
		assertEquals(new String(createXml), new String(readCreatedXML("createCbeff")));

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

	@Test
	public void testUpdateXML() throws Exception {
		byte[] updateXml = cbeffUtilImpl.updateXML(updateList, readCreatedXML("createCbeff"));
		createXMLFile(updateXml, "updateCbeff");
		assertEquals(new String(updateXml), new String(readCreatedXML("updateCbeff")));
	}

	@Test
	public void testValidateXML() throws IOException, Exception {
		assertTrue(cbeffUtilImpl.validateXML(readCreatedXML("createCbeff"), getXSDfromConfigServer()));
	}
	
	private byte[] getXSDfromConfigServer() throws URISyntaxException, IOException {
		InputStream input = new URL(configServerFileStorageURL+schemaName).openStream();
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

	@Test
	public void testGetBDBBasedOnType() throws IOException, Exception {
		Map<String,String> testMap = cbeffUtilImpl.getBDBBasedOnType(readCreatedXML("updateCbeff"), "FMR", "Right");
		Set<String> testSet1 = new HashSet<>();
		testSet1.add("FINGER_Right_2");
		assertEquals(testMap.keySet(),testSet1);
		Map<String,String> testMap1 = cbeffUtilImpl.getBDBBasedOnType(readCreatedXML("updateCbeff"), "FMR", null);
		Set<String> testSet2 = new HashSet<>();
		testSet2.add("FINGER_Right IndexFinger MiddleFinger RingFinger LittleFinger_2");
		assertEquals(testMap1.keySet(),testSet2);
		Map<String,String> testMap2 = cbeffUtilImpl.getBDBBasedOnType(readCreatedXML("updateCbeff"), null, "Right");
		Set<String> testSet3 = new HashSet<>();
		testSet3.add("FINGER_Right IndexFinger MiddleFinger RingFinger LittleFinger_7");
		testSet3.add("IRIS_Right_9");
		testSet3.add("FINGER_Right IndexFinger MiddleFinger RingFinger LittleFinger_2");
		testSet3.add("FINGER_Left Right Thumb_7");
		assertEquals(testMap2.keySet(),testSet3);
	}


}
