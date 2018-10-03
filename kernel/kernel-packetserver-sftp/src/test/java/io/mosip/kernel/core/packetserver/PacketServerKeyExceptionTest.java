package io.mosip.kernel.core.packetserver;
/*package io.mosip.kernel.core.packetserver;

import static org.mockito.Mockito.doThrow;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import io.mosip.kernel.packetserver.constants.PacketServerExceptionConstants;
import io.mosip.kernel.packetserver.exception.MosipInvalidSpecException;
import io.mosip.kernel.packetserver.packetutils.PacketUtils;
import io.mosip.kernel.packetserver.serverdefination.PacketServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)

//@TestPropertySource("classpath:/test-configuration.properties")
//@SpringBootTest(classes = {PacketServer.class, PacketUtils.class})
public class PacketServerKeyExceptionTest {

	@Mock
	PacketServer packetServer;

	@Test(expected = MosipInvalidSpecException.class)
	public void testInvalidSpec() throws IOException {
		doThrow(new MosipInvalidSpecException(
				PacketServerExceptionConstants.MOSIP_INVALID_SPEC_EXCEPTION))
						.when(packetServer).afterPropertiesSet();
		packetServer.afterPropertiesSet();
	}

}
*/