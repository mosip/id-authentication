package io.mosip.registration.packet.decryptor.job;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.SdkClientException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.decryptor.job.Decryptor;
import io.mosip.registration.processor.packet.decryptor.job.tasklet.PacketDecryptorTasklet;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(SpringRunner.class)
public class PacketDecryptorTaskletTest {
	
	private static final InputStream stream = Mockito.mock(InputStream.class);

	@InjectMocks
	PacketDecryptorTasklet packetDecryptorTasklet;
	
	@Mock
	RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;
	
	@Mock
	private FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter = new FilesystemCephAdapterImpl();
	
	@Mock
	private Decryptor decryptor;
	
	
	
	
	@MockBean
	StepContribution stepContribution;

	@MockBean
	ChunkContext chunkContext;

	RegistrationStatusDto dto;
	List<RegistrationStatusDto> list;

	@Before
	public void setup() {
		dto =  new RegistrationStatusDto();
		dto.setRegistrationId("1001");
		dto.setStatusCode("PACKET_UPLOADED_TO_DFS");
		dto.setRetryCount(0);
		list = new ArrayList<RegistrationStatusDto>();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void decryptionSuccessTest() throws Exception {
		
		list.add(dto);
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		
		Mockito.when(registrationStatusService
				.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);
		
		Mockito.doNothing().when(adapter).unpackPacket(any(String.class));
		
		Mockito.when(adapter.getPacket(any(String.class))).thenReturn(stream);

		Mockito.when(decryptor.decrypt(any(InputStream.class), any(String.class))).thenReturn(stream);
		
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any(RegistrationStatusDto.class));
		
		
		
		RepeatStatus status = packetDecryptorTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("Packet decrypted and extracted encrypted files stored in DFS.");
			}
		}));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void decryptionFailureTest() throws Exception {
		
		list.add(dto);
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		
		Mockito.when(registrationStatusService
				.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);
		
		Mockito.doNothing().when(adapter).unpackPacket(any(String.class));
		
		Mockito.when(adapter.getPacket(any(String.class))).thenReturn(stream);

		Mockito.when(decryptor.decrypt(any(InputStream.class), any(String.class))).thenReturn(null);		
		
		RepeatStatus status = packetDecryptorTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("Packet could not be  decrypted");
			}
		}));
	}
	@SuppressWarnings("unchecked")
	@Test
	public void noFilesToBeDecryptedTest() throws Exception {
		
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		
		Mockito.when(registrationStatusService
				.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);		
		
		RepeatStatus status = packetDecryptorTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("There are currently no files to be decrypted");
			}
		}));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void StatusUpdateExceptionTest() throws Exception {
		list.add(dto);
		
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		
		Mockito.when(registrationStatusService
				.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);
		
		Mockito.doNothing().when(adapter).unpackPacket(any(String.class));
		
		Mockito.when(adapter.getPacket(any(String.class))).thenReturn(stream);

		Mockito.when(decryptor.decrypt(any(InputStream.class), any(String.class))).thenReturn(stream);
		
		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService).updateRegistrationStatus(any(RegistrationStatusDto.class));	
		
		RepeatStatus status = packetDecryptorTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("The Registration Status table is not accessible");
			}
		}));
	}
	@SuppressWarnings("unchecked")
	@Test
	public void getBySatusExceptionTest() throws Exception {
		
		
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		
		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService).
		getByStatus(any(String.class));	
		
		RepeatStatus status = packetDecryptorTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("The Registration Status table is not accessible");
			}
		}));
	}
	@SuppressWarnings("unchecked")
	@Test
	public void AmazonClientExceptionTest() throws Exception {
		
		list.add(dto);
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		
		Mockito.when(registrationStatusService
				.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);
		
		Mockito.doThrow(SdkClientException.class).when(adapter).unpackPacket(any(String.class));
		
		RepeatStatus status = packetDecryptorTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("The DFS Path set by the System is not accessible");
			}
		}));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void IOExceptionTest() throws Exception {
		
		list.add(dto);
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		
		Mockito.when(registrationStatusService
				.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);
		
		Mockito.doThrow(IOException.class).when(adapter).unpackPacket(any(String.class));
		
		RepeatStatus status = packetDecryptorTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("The DFS Path set by the System is not accessible");
			}
		}));
	}
}
