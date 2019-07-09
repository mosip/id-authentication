package io.mosip.registrationProcessor.perf.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.mosip.registrationProcessor.perf.regPacket.dto.RegProcIdDto;
import io.mosip.registrationProcessor.perf.service.PacketDemoDataUtil;
import io.mosip.registrationProcessor.perf.service.TestDataGenerator;
import io.mosip.registrationProcessor.perf.util.CSVUtil;
import io.mosip.registrationProcessor.perf.util.JSONUtil;

public class TestDataGeneratorTest {

	static TestDataGenerator testDataGenerator;

	static PacketDemoDataUtil packetDataUtil;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testDataGenerator = new TestDataGenerator();
		packetDataUtil = new PacketDemoDataUtil();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void testGenerateTestDataInCSV() {
//		testDataGenerator.generateTestDataInCSV();
//
//	}
//
//	@Test
//	public void testReadDataFromCSV() {
//		String filePath = "E:\\MOSIP_PT\\Data\\reg_data_sample.csv";
//		CSVUtil.loadObjectsFromCSV(filePath);
//	}
//
//	@Test
//	public void testMapJsonFileToObject() {
//		RegProcIdDto dto = JSONUtil.mapJsonFileToObject();
//		assertNotNull(dto);
//		assertEquals("sravan.kalla@mindtree.com", dto.getIdentity().getEmail());
//	}

	@Test
	public void testLocationtranslation() {
		String locationName = "Ben Mansour";
		String arabicText = packetDataUtil.convertLocationEngToArabic(locationName,2);
		String frenchText = packetDataUtil.convertLocationEngToFrench(locationName,2);
		System.out.println(arabicText + " " + frenchText);
	}

}
