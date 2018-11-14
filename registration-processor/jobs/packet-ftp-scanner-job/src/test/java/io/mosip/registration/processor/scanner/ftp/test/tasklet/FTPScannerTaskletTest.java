package io.mosip.registration.processor.scanner.ftp.test.tasklet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.scanner.ftp.tasklet.FTPScannerTasklet;


@RunWith(SpringRunner.class)
@PrepareForTest({ FTPScannerTasklet.class })
public class FTPScannerTaskletTest {

	@InjectMocks
	FTPScannerTasklet ftpScannerTasklet;

	@Mock
	FileManager<DirectoryPathDto, InputStream> filemanager;

	@Mock
	PacketReceiverService<MultipartFile, Boolean> packetHandlerService;

	@MockBean
	StepContribution stepContribution;

	@MockBean
	ChunkContext chunkContext;

	private MockMultipartFile mockMultipartFile = Mockito.mock(MockMultipartFile.class);

	private String directoryPath;

	@Before
	public void setup() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("FTP").getFile());
		this.directoryPath = file.getAbsolutePath();
	}

	@Test
	public void testFtpToLandingZoneTaskletSuccess() throws Exception {
		Mockito.when(filemanager.getCurrentDirectory()).thenReturn(this.directoryPath);
		Mockito.when(packetHandlerService.storePacket(this.mockMultipartFile)).thenReturn(true);
		Mockito.doNothing().when(filemanager).cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class),
				any(String.class), any(String.class));
		RepeatStatus status = ftpScannerTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testFtpToLandingZoneTaskletDuplicateFile() throws Exception {

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		Mockito.when(filemanager.getCurrentDirectory()).thenReturn(this.directoryPath);
		DuplicateUploadRequestException duplicateUploadRequestException = new DuplicateUploadRequestException(
				"Duplicate file uploading to landing zone");
		Mockito.doThrow(duplicateUploadRequestException).when(packetHandlerService).storePacket(any());
		Mockito.doNothing().when(filemanager).cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class),
				any(String.class), any(String.class));

		ftpScannerTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("Duplicate file uploading to landing zone");
			}
		}));

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testFileNotInDestination() throws Exception {

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender mockAppender = mock(Appender.class);
		FileNotFoundInDestinationException fileNotFoundInDestinationException = new FileNotFoundInDestinationException(
				"File not found");

		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		Mockito.when(filemanager.getCurrentDirectory()).thenReturn(this.directoryPath);
		Mockito.when(packetHandlerService.storePacket(this.mockMultipartFile)).thenReturn(true);
		Mockito.doThrow(fileNotFoundInDestinationException).when(filemanager).cleanUpFile(any(DirectoryPathDto.class),
				any(DirectoryPathDto.class), any(String.class), any(String.class));

		ftpScannerTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("RPR-PKM-003");
			}
		}));

	}

}

