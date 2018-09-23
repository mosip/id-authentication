package org.mosip.kernel.core.packetserver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.kernel.packetserver.exception.MosipIllegalStateException;
import org.mosip.kernel.packetserver.packetutils.PacketUtils;
import org.mosip.kernel.packetserver.serverdefination.PacketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)

@TestPropertySource("classpath:/test-configuration.properties")
@SpringBootTest(classes = { PacketServer.class, PacketUtils.class })
public class PacketServerTest {

	@Autowired
	PacketServer packetServer;

	@Test
	public void testStop() {
		packetServer.stop();
		packetServer.start();
	}

	@Test(expected = MosipIllegalStateException.class)
	public void testAfterPropertiesSet() {
		packetServer.afterPropertiesSet();
	}
}
