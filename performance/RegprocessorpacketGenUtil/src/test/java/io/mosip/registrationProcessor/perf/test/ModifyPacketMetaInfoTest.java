package io.mosip.registrationProcessor.perf.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.mosip.registrationProcessor.perf.service.PacketDemoDataUtil;

public class ModifyPacketMetaInfoTest {

	private static PacketDemoDataUtil packetDemoDataUtil;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		packetDemoDataUtil = new PacketDemoDataUtil();
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

	@Test
	public void testModifyPacketMetaInfo() {
		String packetMetaInfoFile = "C:\\MOSIP_PT\\Data\\packets\\10002100320001820190607070015\\packet_meta_info.json";
		String centerId = "1002";
		String machineId = "10032";
		String regId = "10002100320001820190607070015";
		try {
			packetDemoDataUtil.modifyPacketMetaInfo(packetMetaInfoFile, regId, centerId, machineId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
