package org.mosip.kernel.core.packetserver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.kernel.packetserver.exception.MosipPublicKeyException;
import org.mosip.kernel.packetserver.packetutils.PacketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)

@SpringBootTest(classes = { PacketUtils.class })
public class PacketUtilsExceptionTest {

	@Autowired
	PacketUtils packetUtils;

	@Test(expected = MosipPublicKeyException.class)
	public void testIsRunning() {
		packetUtils.getFileBytes("/id.pub");
	}
}
