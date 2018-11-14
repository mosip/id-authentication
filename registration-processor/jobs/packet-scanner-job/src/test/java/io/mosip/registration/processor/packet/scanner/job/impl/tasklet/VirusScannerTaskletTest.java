package io.mosip.registration.processor.packet.scanner.job.impl.tasklet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import io.mosip.kernel.virusscanner.clamav.service.VirusScannerService;
import io.mosip.registration.processor.auditmanager.requestbuilder.ClientAuditRequestBuilder;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.scanner.job.exception.DFSNotAccessibleException;
import io.mosip.registration.processor.packet.scanner.job.exception.RetryFolderNotAccessibleException;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(MockitoJUnitRunner.class)
public class VirusScannerTaskletTest {

	@InjectMocks
	private VirusScannerTasklet virusScannerTasklet;

	@Mock
	private Environment env;

	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;

	@Mock
	private VirusScannerService<Boolean, String> virusScanner;

	@MockBean
	StepContribution stepContribution;

	@MockBean
	private ChunkContext chunkContext;

	@Mock
	private ClientAuditRequestBuilder clientAuditRequestBuilder = new ClientAuditRequestBuilder();

	@Before
	public void setup() throws Exception {

		InternalRegistrationStatusDto entry = new InternalRegistrationStatusDto();
		entry.setRegistrationId("1000.zip");
		entry.setRetryCount(0);
		entry.setStatusComment("Landing");

		List<InternalRegistrationStatusDto> sample = new ArrayList<InternalRegistrationStatusDto>();
		sample.add(entry);

		Mockito.when(
				registrationStatusService.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN.toString()))
				.thenReturn(sample);
		Mockito.when(env.getProperty(DirectoryPathDto.VIRUS_SCAN.toString())).thenReturn("/resources/Disk/sde");

		/*AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
		AuditRequestDto auditRequest1 = new AuditRequestDto();

		Field f = CoreAuditRequestBuilder.class.getDeclaredField("auditRequestBuilder");
		f.setAccessible(true);
		f.set(clientAuditRequestBuilder, auditRequestBuilder);
		Field f1 = AuditRequestBuilder.class.getDeclaredField("auditRequest");
		f1.setAccessible(true);
		f1.set(auditRequestBuilder, auditRequest1);*/

	}

	@Test
	public void testSuccessfulVirusScanSendToDfs() throws Exception {

		Mockito.when(virusScanner.scanFile(anyString())).thenReturn(Boolean.FALSE);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any());

		RepeatStatus output = virusScannerTasklet.execute(stepContribution, chunkContext);

		assertEquals("Uninfected Files should be moved to DFS", RepeatStatus.FINISHED, output);
	}

	@Test
	public void testVirusScanFailureMoveToRetry() throws Exception {

		Mockito.when(virusScanner.scanFile(anyString())).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any());

		RepeatStatus output = virusScannerTasklet.execute(stepContribution, chunkContext);

		assertEquals("Infected files is detected in virus scan", RepeatStatus.FINISHED, output);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testRegistrationStatusTableIsNotAccessible() throws Exception {

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender mockAppender = mock(Appender.class);
		root.addAppender(mockAppender);

		Mockito.when(registrationStatusService.getByStatus(anyString())).thenThrow(TablenotAccessibleException.class);

		virusScannerTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher() {
			@Override
			public boolean matches(final Object argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("The Enrolment Status table is not accessible");
			}
		}));

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testDfsNotAccessible() throws Exception {

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender mockAppender = mock(Appender.class);
		root.addAppender(mockAppender);

		Mockito.when(virusScanner.scanFile(anyString())).thenReturn(Boolean.TRUE);
		Mockito.doThrow(DFSNotAccessibleException.class).when(filesystemCephAdapterImpl).storePacket(anyString(),
				any(File.class));

		virusScannerTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher() {
			@Override
			public boolean matches(final Object argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("The DFS Path set by the System is not accessible");
			}
		}));

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testPacketCopyFailure() throws Exception {

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender mockAppender = mock(Appender.class);
		root.addAppender(mockAppender);

		Mockito.when(virusScanner.scanFile(anyString())).thenReturn(Boolean.FALSE);
		Mockito.doThrow(RetryFolderNotAccessibleException.class).when(fileManager).copy(anyString(), any(), any());

		virusScannerTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher() {
			@Override
			public boolean matches(final Object argument) {
				return ((LoggingEvent) argument).getFormattedMessage().contains("The Retry Folder set by the System");
			}
		}));

	}

}