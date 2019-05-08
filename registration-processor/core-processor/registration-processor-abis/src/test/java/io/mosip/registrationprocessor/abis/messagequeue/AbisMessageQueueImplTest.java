package io.mosip.registrationprocessor.abis.messagequeue;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import javax.jms.Message;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.abis.messagequeue.AbisMessageQueueImpl;
import io.mosip.registration.processor.abis.service.AbisService;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertRequestDto;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.QueueListener;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.packet.storage.utils.Utilities;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class})
@PowerMockIgnore({ "javax.management.*", "javax.net.*" })
@PropertySource("classpath:bootstrap.properties")
public class AbisMessageQueueImplTest {
	@InjectMocks
	AbisMessageQueueImpl abisMessageQueueImpl=new AbisMessageQueueImpl();
	@Mock
	AbisService abisService;
	
	@Mock
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	@Mock
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;
	
	@Mock
	private MosipQueue queue;
	
	@Mock
	private AbisInsertRequestDto abisInsertRequestDto;
	
	@Mock
	private AbisIdentifyRequestDto identifyRequestDto;
	ObjectMapper obj = new ObjectMapper(); 
	@Before
	public void setup() throws Exception  {
		System.setProperty("server.port", "8099");
		System.setProperty("registration.processor.queue.username", "admin");
		System.setProperty("registration.processor.queue.password", "admin");
		System.setProperty("registration.processor.queue.url", "tcp://104.211.200.46:61616");
		System.setProperty("registration.processor.queue.typeOfQueue", "ACTIVEMQ");
		System.setProperty("registration.processor.abis.inbound.queue1", "test");
		System.setProperty("registration.processor.abis.inbound.queue2", "test");
		System.setProperty("registration.processor.abis.inbound.queue3", "test");
		System.setProperty("registration.processor.abis.outbound.queue1", "test");
		System.setProperty("registration.processor.abis.outbound.queue2", "test");
		System.setProperty("registration.processor.abis.outbound.queue3", "test");
		System.setProperty("mosip.kernel.xsdstorage-uri", "http://104.211.212.28:51000");
		System.setProperty("mosip.kernel.xsdfile", "mosip-cbeff.xsd");
		
		Mockito.when(mosipConnectionFactory.createConnection(anyString(), anyString(), anyString(), anyString()))
		.thenReturn(queue);
		
		Mockito.when(mosipQueueManager.send(any(), any(), anyString())).thenReturn(true);
		
		QueueListener listener = new QueueListener() {
			@Override
			public void setListener(Message message) {
				abisMessageQueueImpl.consumeLogic(message,"");
			}
		};
		PowerMockito.whenNew(QueueListener.class).withNoArguments().thenReturn(listener);
	}
	@Test
	public void testrunAbisQueueforInsert() throws Exception {
		AbisInsertRequestDto dto= new AbisInsertRequestDto();
		dto.setId("mosip.abis.insert");
		dto.setReferenceId("01234567-89AB-CDEF-0123-456789ABCDEF");
		dto.setReferenceURL("https://mosip.io/biometric/45678");
		dto.setRequestId("01234567-89AB-CDEF-0123-456789ABCDEF");
		dto.setTimestamp("1539777717");
		dto.setVer("1.0");
		String request = obj.writeValueAsString(dto);
        ActiveMQBytesMessage amq= new ActiveMQBytesMessage();
        ByteSequence byteSeq = new ByteSequence();
        byteSeq.setData(request.getBytes());
        amq.setContent(byteSeq);
       assertTrue( abisMessageQueueImpl.consumeLogic(amq,""));
        
	}
	@Test
	public void testrunAbisQueueforIdentify() throws Exception {
		AbisIdentifyRequestDto  dto= new AbisIdentifyRequestDto ();
		dto.setId("mosip.abis.identify");
		dto.setReferenceId("01234567-89AB-CDEF-0123-456789ABCDEF");
		dto.setMaxResults(10);
		dto.setTargetFPIR(10);
		dto.setRequestId("01234567-89AB-CDEF-0123-456789ABCDEF");
		dto.setTimestamp("1539777717");
		dto.setVer("1.0");
		String request = obj.writeValueAsString(dto);
        ActiveMQBytesMessage amq= new ActiveMQBytesMessage();
        ByteSequence byteSeq = new ByteSequence();
        byteSeq.setData(request.getBytes());
        amq.setContent(byteSeq);
        assertTrue( abisMessageQueueImpl.consumeLogic(amq,""));
        
	}
	@Test
	public void testrunAbisQueueforInvalidRequest() throws Exception {
		AbisIdentifyRequestDto  dto= new AbisIdentifyRequestDto ();
		dto.setId("mosip.abis.invalid");
		dto.setReferenceId("01234567-89AB-CDEF-0123-456789ABCDEF");
		dto.setMaxResults(10);
		dto.setTargetFPIR(10);
		dto.setRequestId("01234567-89AB-CDEF-0123-456789ABCDEF");
		dto.setTimestamp("1539777717");
		dto.setVer("1.0");
		String request = obj.writeValueAsString(dto);
        ActiveMQBytesMessage amq= new ActiveMQBytesMessage();
        ByteSequence byteSeq = new ByteSequence();
        byteSeq.setData(request.getBytes());
        amq.setContent(byteSeq);
        assertTrue( abisMessageQueueImpl.consumeLogic(amq,""));
        
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testrunAbisQueueException() throws Exception {
		AbisIdentifyRequestDto  dto= new AbisIdentifyRequestDto ();
		dto.setId("mosip.abis.identify");
		dto.setReferenceId("01234567-89AB-CDEF-0123-456789ABCDEF");
		dto.setMaxResults(10);
		dto.setTargetFPIR(10);
		dto.setRequestId("01234567-89AB-CDEF-0123-456789ABCDEF");
		dto.setTimestamp("1539777717");
		dto.setVer("1.0");
		String request = obj.writeValueAsString(dto);
        ActiveMQBytesMessage amq= new ActiveMQBytesMessage();
        ByteSequence byteSeq = new ByteSequence();
        byteSeq.setData(request.getBytes());
        amq.setContent(byteSeq);
        Mockito.when(abisService.performDedupe(any())).thenThrow(ApisResourceAccessException.class);
        abisMessageQueueImpl.consumeLogic(amq,"");
        
	}
}
