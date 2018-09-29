package org.mosip.registration.processor.packet.scanner.job.impl.tasklet;

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
import org.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import org.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import org.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import org.mosip.registration.processor.packet.scanner.job.exception.DFSNotAccessibleException;
import org.mosip.registration.processor.packet.scanner.job.exception.RetryFolderNotAccessibleException;
import org.mosip.registration.processor.status.code.RegistrationStatusCode;
import org.mosip.registration.processor.status.dto.RegistrationStatusDto;
import org.mosip.registration.processor.status.exception.TablenotAccessibleException;
import org.mosip.registration.processor.status.service.RegistrationStatusService;
import org.mosip.kernel.virus.scanner.service.VirusScannerService;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

@RunWith(MockitoJUnitRunner.class)
public class VirusScannerTaskletTest {

	@InjectMocks
	private VirusScannerTasklet virusScannerTasklet;

	@Mock
	private Environment env;

	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	@Mock
	private RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

	@Mock
	private FileSystemAdapter<InputStream, ?, Boolean> adapter;

	@Mock
	private VirusScannerService<Boolean, String> virusScanner;

	@MockBean
	StepContribution stepContribution;

	@MockBean
	private ChunkContext chunkContext;

	@Before
	public void setup() throws Exception {

		RegistrationStatusDto entry = new RegistrationStatusDto();
		entry.setRegistrationId("1000.zip");
		entry.setRetryCount(0);
		entry.setStatusComment("Landing");

		List<RegistrationStatusDto> sample = new ArrayList<RegistrationStatusDto>();
		sample.add(entry);

		Mockito.when(registrationStatusService.getByStatus(RegistrationStatusCode.PACKET_FOR_VIRUS_SCAN.toString()))
				.thenReturn(sample);
		Mockito.when(env.getProperty(DirectoryPathDto.VIRUS_SCAN.toString())).thenReturn("/resources/Disk/sde");

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
		Mockito.doThrow(DFSNotAccessibleException.class).when(adapter).storePacket(anyString(), any(File.class));

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