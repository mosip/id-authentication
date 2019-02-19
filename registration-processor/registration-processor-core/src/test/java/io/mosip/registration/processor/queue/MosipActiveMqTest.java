package io.mosip.registration.processor.queue;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.queue.factory.MosipActiveMq;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.MosipQueueConnectionFactoryImpl;
import io.mosip.registration.processor.core.queue.impl.MosipActiveMqImpl;
import io.mosip.registration.processor.core.queue.impl.exception.InvalidConnectionException;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;

@RunWith(SpringRunner.class)
public class MosipActiveMqTest {
	
	@InjectMocks
	MosipActiveMqImpl mosipActiveMqImpl;
	
	@Mock
	MosipActiveMq mosipActiveMq;
	
	@Mock
	ActiveMQConnectionFactory factory;
	
	MosipQueue mosipQueue;
	
	@Test
	public void testSendSuccess() throws JMSException {
		mosipQueue = new MosipActiveMq("admin", "admin","vm://localhost?broker.persistent=false");
		byte[] bytes = "message".getBytes();
		when(mosipActiveMq.getActiveMQConnectionFactory()).thenReturn(factory);
		assertTrue(mosipActiveMqImpl.send(mosipQueue, bytes, "address"));
	}
	
	@Test(expected = InvalidConnectionException.class)
	public void invalidConnectionExceptionTest() {
		mosipQueue = new MosipActiveMq("admin", "admin","vm://localhost?broker.persistent=false") {
			@Override
			public ActiveMQConnectionFactory getActiveMQConnectionFactory() {
				return null;
			}
		};
		byte[] bytes = "message".getBytes();
		when(mosipActiveMq.getActiveMQConnectionFactory()).thenReturn(factory);
		assertTrue(mosipActiveMqImpl.send(mosipQueue, bytes, "address"));
		assertNotNull(mosipActiveMqImpl.consume(mosipQueue,"address"));
	}
	
	@Test
	public void testConsumeSuccess() {
		mosipQueue = new MosipActiveMq("admin", "admin","vm://localhost?broker.persistent=false");
		byte[] bytes = "message".getBytes();
		when(mosipActiveMq.getActiveMQConnectionFactory()).thenReturn(factory);
		mosipActiveMqImpl.send(mosipQueue, bytes, "address");
		assertNotNull(mosipActiveMqImpl.consume(mosipQueue, "address"));
	}
	
}
