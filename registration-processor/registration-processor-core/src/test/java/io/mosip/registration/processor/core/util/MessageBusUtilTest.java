package io.mosip.registration.processor.core.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import org.springframework.test.context.junit4.SpringRunner;


import io.mosip.registration.processor.core.exception.ApisResourceAccessException;


@RunWith(SpringRunner.class)
public class MessageBusUtilTest {
	@InjectMocks
	MessageBusUtil messageBusUtil;
	@Test
	public void testGetMessageBusAddress() throws ApisResourceAccessException, IOException {
		String address=messageBusUtil.getMessageBusAdress("PacketReceiverStage");
		assertEquals(address,"packet-receiver");
		
	}
	
}
