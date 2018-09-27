package org.mosip.kernel.core.packetserver;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mosip.kernel.packetserver.packetutils.PacketUtils;
import org.mosip.kernel.packetserver.serverdefination.PacketServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)

@TestPropertySource("classpath:/test-configuration.properties")
@SpringBootTest(classes = { PacketServer.class, PacketUtils.class })
public class PacketServerMockTest {

	@Mock
	PacketServer packetServer;

	@Test
	public void testStart() {
		packetServer.start();
		verify(packetServer, times(1)).start();
	}

	@Test
	public void testStop() {
		packetServer.start();
		packetServer.stop();
		verify(packetServer, times(1)).stop();
	}

	@Test
	public void testAfterPropertiesSet() {
		packetServer.afterPropertiesSet();
		verify(packetServer, times(1)).afterPropertiesSet();
	}
}
