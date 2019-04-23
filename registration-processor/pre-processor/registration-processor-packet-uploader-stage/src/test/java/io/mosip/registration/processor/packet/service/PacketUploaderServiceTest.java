package io.mosip.registration.processor.packet.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.packet.uploader.service.PacketUploaderService;
import io.mosip.registration.processor.packet.uploader.service.impl.PacketUploaderServiceImpl;

@RefreshScope
@RunWith(SpringRunner.class)
//@RunWith(PowerMockRunner.class)
public class PacketUploaderServiceTest {
	
	@Mock
	private PacketUploaderService<MessageDTO> packetuploaderservice = new PacketUploaderServiceImpl();

	@Before
	public void setUp()
	{
		ReflectionTestUtils.setField(packetuploaderservice, "extention", ".zip");
		ReflectionTestUtils.setField(packetuploaderservice, "fileSize", "5");
	}
}
