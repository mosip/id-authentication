package io.mosip.registration.processor.ftp.scanner.job.stage.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.ftp.scanner.job.stage.FtpScannerStage;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;

@RunWith(MockitoJUnitRunner.class)
public class FtpScannerStageTest {

	@InjectMocks
	FtpScannerStage ftpScannerStage;

	@Mock
	FileManager<DirectoryPathDto, InputStream> filemanager;

	@Mock
	PacketReceiverService<MultipartFile, Boolean> packetHandlerService;

	private MockMultipartFile mockMultipartFile = Mockito.mock(MockMultipartFile.class);

	private String directoryPath;
	
	private Logger fooLogger;
	
	private ListAppender<ILoggingEvent> listAppender;
	
	@Before
	public void setup() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("FTP").getFile());
		this.directoryPath = file.getAbsolutePath();
		
        fooLogger = (Logger) LoggerFactory.getLogger(FtpScannerStage.class);
        listAppender = new ListAppender<>();
	}
	
	@Test
	public void testFtpToLandingZoneTaskletSuccess() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
		
        when(filemanager.getCurrentDirectory()).thenReturn(this.directoryPath);
		when(packetHandlerService.storePacket(this.mockMultipartFile)).thenReturn(true);
		doNothing().when(filemanager).cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class),any(String.class), any(String.class));

		MessageDTO msg = new MessageDTO();
		ftpScannerStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
		.extracting(ILoggingEvent::getLoggerName).containsExactly();
	
	}
	
	@Test
	public void testFtpToLandingZoneTaskletDuplicateFile() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
		when(filemanager.getCurrentDirectory()).thenReturn(this.directoryPath);
		DuplicateUploadRequestException duplicateUploadRequestException = new DuplicateUploadRequestException(
				"Duplicate file uploading to landing zone");
		doThrow(duplicateUploadRequestException).when(packetHandlerService).storePacket(any());
		doNothing().when(filemanager).cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class),
				any(String.class), any(String.class));
		
		MessageDTO msg = new MessageDTO();
		ftpScannerStage.process(msg);

		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "Duplicate file uploading to landing zone - {}")); 
		

	}
	
	@Test
	public void testFileNotInDestination() throws Exception {

        listAppender.start();
        fooLogger.addAppender(listAppender);

        FileNotFoundInDestinationException fileNotFoundInDestinationException = new FileNotFoundInDestinationException(
				"File not found");
		when(filemanager.getCurrentDirectory()).thenReturn(this.directoryPath);
		when(packetHandlerService.storePacket(this.mockMultipartFile)).thenReturn(true);
		doThrow(fileNotFoundInDestinationException).when(filemanager).cleanUpFile(any(DirectoryPathDto.class),
				any(DirectoryPathDto.class), any(String.class), any(String.class));

		MessageDTO msg = new MessageDTO();
		ftpScannerStage.process(msg);

		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "RPR-PKM-003")); 
		
	}

}
