package org.mosip.kernel.core.packetserver;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.kernel.packetserver.PacketServerApplication;
import org.mosip.kernel.packetserver.exception.MosipPublicKeyException;
import org.mosip.kernel.packetserver.packetutils.PacketUtils;
import org.mosip.kernel.packetserver.serverdefination.PacketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest(classes=PacketServerApplication.class)
@ContextConfiguration(classes=PacketServer.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PacketUtilsTest {

	private static final String FILE_LOCATION="/id_rsa.pub";
	private static final String FILE_LOCATION_EXCEPTION="/id_rsa1.pub";
	@Autowired
	PacketUtils packetUtils;
	
	@Test
	public void getFileBytesTest() {
		assertThat(packetUtils.getFileBytes(FILE_LOCATION),isA(byte[].class));
	}
	
	@Test(expected=MosipPublicKeyException.class)
	public void getFileBytesTestPrivateKeyException() {
		assertThat(packetUtils.getFileBytes(FILE_LOCATION_EXCEPTION),isA(byte[].class));
	}
	
	


}
