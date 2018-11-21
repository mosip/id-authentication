package io.mosip.registration.processor.virus.scanner.job.stage.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FilePathNotAccessibleException;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.virus.scanner.job.exceptions.DFSNotAccessibleException;
import io.mosip.registration.processor.virus.scanner.job.exceptions.RetryFolderNotAccessibleException;
import io.mosip.registration.processor.virus.scanner.job.exceptions.VirusScanFailedException;
import io.mosip.registration.processor.virus.scanner.job.stage.VirusScannerStage;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;

@RunWith(MockitoJUnitRunner.class)
public class VirusScannerStageTest {
	
	@InjectMocks
	private VirusScannerStage virusScannerStage;

	@Mock
	private Environment env;

	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	private FileSystemAdapter<InputStream, Boolean> adapter;

	@Mock
	private VirusScanner<Boolean, String> virusScanner;
	
	private Logger fooLogger;
	private ListAppender<ILoggingEvent> listAppender;
	
	@Before
	public void setup() throws Exception {
		
		InternalRegistrationStatusDto entry = new InternalRegistrationStatusDto();
		entry.setRegistrationId("1000.zip");
		entry.setRetryCount(0);
		entry.setStatusComment("Landing");

		List<InternalRegistrationStatusDto> sample = new ArrayList<InternalRegistrationStatusDto>();
		sample.add(entry);

		when(registrationStatusService.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN.toString())).thenReturn(sample);
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN.toString())).thenReturn("/resources/Disk/sde");

        fooLogger = (Logger) LoggerFactory.getLogger(VirusScannerStage.class);
        listAppender = new ListAppender<>();

	}

	@Test
	public void testSuccessfulVirusScanSendToDfs() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
		
		Mockito.when(virusScanner.scanFile(anyString())).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any(InternalRegistrationStatusDto.class));

		MessageDTO msg = new MessageDTO();
		virusScannerStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.INFO, "1000.zip - File is successfully scanned. It has been sent to DFS.")); 
		
	}
	
	@Test
	public void testVirusScanFailureMoveToRetry() throws Exception {
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
		Mockito.when(virusScanner.scanFile(anyString())).thenReturn(Boolean.FALSE);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any());

		MessageDTO msg = new MessageDTO();
		virusScannerStage.process(msg);

		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.INFO, "1000.zip - File is infected. It has been sent to RETRY_FOLDER.")); 
	}
	
	@Test
	public void testRegistrationStatusTableIsNotAccessible() throws Exception {
        listAppender.start();
        fooLogger.addAppender(listAppender);

		when(registrationStatusService.getByStatus(anyString())).thenThrow(TablenotAccessibleException.class);

		MessageDTO msg = new MessageDTO();
		virusScannerStage.process(msg);

		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The Enrolment Status table is not accessible - {}")); 		
		
	}
	
	@Test
	public void testTableNotAccessibleWhileSendingToRetry() {
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
        when(virusScanner.scanFile(anyString())).thenReturn(Boolean.FALSE);
		doThrow(TablenotAccessibleException.class).when(registrationStatusService).updateRegistrationStatus(any(InternalRegistrationStatusDto.class));

		MessageDTO msg = new MessageDTO();
		virusScannerStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The Enrolment Status table is not accessible - {}")); 
	}
	
	@Test
	public void testDfsNotAccessible() throws Exception {
        listAppender.start();
        fooLogger.addAppender(listAppender);

		when(virusScanner.scanFile(anyString())).thenReturn(Boolean.TRUE);
		doThrow(DFSNotAccessibleException.class).when(adapter).storePacket(anyString(), any(File.class));

		MessageDTO msg = new MessageDTO();
		virusScannerStage.process(msg);

		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The DFS Path set by the System is not accessible - {}")); 
	}
	
	@Test
	public void testPacketCopyFailure() throws Exception {
        listAppender.start();
        fooLogger.addAppender(listAppender);

		when(virusScanner.scanFile(anyString())).thenReturn(Boolean.FALSE);
		doThrow(FilePathNotAccessibleException.class).when(fileManager).copy(anyString(),any(),any());

		MessageDTO msg = new MessageDTO();
		virusScannerStage.process(msg);

		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The Retry Folder set by the System is not accessible - {}")); 

	}
	
	@Test
	public void testVirusScanFailure() throws Exception {
        listAppender.start();
        fooLogger.addAppender(listAppender);

		doThrow(VirusScanFailedException.class).when(virusScanner).scanFile(anyString());

		MessageDTO msg = new MessageDTO();
		virusScannerStage.process(msg);

		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The Virus Scan for the Packet Failed - {}")); 

	}
	
	@Test
	public void testIfPacketIsPresent() throws Exception {
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
        when(virusScanner.scanFile(anyString())).thenReturn(Boolean.TRUE);
        when(adapter.isPacketPresent(any())).thenReturn(Boolean.TRUE);
        doNothing().when(fileManager).deletePacket(any(), any());

		MessageDTO msg = new MessageDTO();
		virusScannerStage.process(msg);

		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.INFO, "1000.zip - File is Already exists in DFS location  And its now Deleted from Virus scanner job ")); 

	}

}
