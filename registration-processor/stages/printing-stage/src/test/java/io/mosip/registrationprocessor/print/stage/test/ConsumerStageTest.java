package io.mosip.registrationprocessor.print.stage.test;

import static org.junit.Assert.assertTrue;

import javax.jms.Message;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.queue.factory.MosipActiveMq;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.print.stage.ConsumerStage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(ActiveMQBytesMessage.class)
public class ConsumerStageTest {

	@InjectMocks
	private ConsumerStage consumerStage = new ConsumerStage() {
		@Override
		public void send(MosipEventBus mosipEventBus, MessageBusAddress toAddress, MessageDTO message) {
		}
	};
	MessageDTO messageDto = new MessageDTO();
	@Mock
	private MosipVerticleManager verticleManager;
	@Mock
	private InternalRegistrationStatusDto registrationStatusDto;
	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;
	@Mock
	private JsonUtil jsonUtils;
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;

	@Mock
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	@Mock
	private MosipQueue mosipQueue;
	boolean isConnection = false;

	private ByteSequence response1 = new ByteSequence();
	private ByteSequence response = new ByteSequence();
	private ByteSequence response2 = new ByteSequence();

	@Before
	public void setup() throws JSONException {
		messageDto.setRid("2018701130000410092018110735");
		String str = "{\"Status\":\"Success\"}";
		byte[] arr = str.getBytes();
		String str1 = "{\"Status\":\"Resend\"}";
		byte[] arr1 = str1.getBytes();
		String str2 = "{\"Status\",\"Resend\"}";
		byte[] arr2 = str2.getBytes();
		response.setData(arr);
		response.setLength(2);
		response.setOffset(0);
		response1.setData(arr1);
		response1.setLength(1);
		response1.setOffset(0);
		response2.setData(arr2);
		registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setApplicantType("child");
		registrationStatusDto.setIsActive(Boolean.TRUE);
		registrationStatusDto.setIsDeleted(Boolean.FALSE);
		MosipActiveMq activeMq = new MosipActiveMq("abc", "user", "pwd", "broker");
		Mockito.when(registrationStatusService.getRegistrationStatus(Mockito.any())).thenReturn(registrationStatusDto);
		Mockito.when(mosipConnectionFactory.createConnection(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(activeMq);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
	}

	@Test
	public void testProcess() {

		MessageDTO obj = consumerStage.process(messageDto);
		assertTrue("2018701130000410092018110735".equals(obj.getRid()));

	}

	@Test
	public void testSendMessageforSuccess() {
		Message msg = new ActiveMQBytesMessage();
		((org.apache.activemq.command.Message) msg).setContent(response);
		consumerStage.sendMessage(msg);
	}

	@Test
	public void testSendMessageforResend() {
		Message msg = new ActiveMQBytesMessage();
		((org.apache.activemq.command.Message) msg).setContent(response1);
		consumerStage.sendMessage(msg);
	}

	@Test
	public void testException() {

		Message msg = new ActiveMQBytesMessage();
		((org.apache.activemq.command.Message) msg).setContent(response2);
		consumerStage.sendMessage(msg);
	}

}
