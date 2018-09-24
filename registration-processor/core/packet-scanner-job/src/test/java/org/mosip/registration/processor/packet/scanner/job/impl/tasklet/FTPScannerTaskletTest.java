package org.mosip.registration.processor.packet.scanner.job.impl.tasklet;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.io.IOUtil;
import org.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import org.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import org.mosip.registration.processor.packet.manager.service.FileManager;
import org.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import org.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import org.mosip.registration.processor.packet.scanner.job.exception.FTPNotAccessibleException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.util.IOUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

@RunWith(SpringRunner.class)
@PrepareForTest({FTPScannerTasklet.class})
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

	private MockMultipartFile mockMultipartFile=Mockito.mock(MockMultipartFile.class);

//	@Before
//	public void setup() {
//
//		ClassLoader classLoader = getClass().getClassLoader();
//		File file = new File(classLoader.getResource("1000.zip").getFile());
//		try {
//
//			this.mockMultipartFile = new MockMultipartFile("file", "1000.zip", "mixed/multipart",
//					new FileInputStream(file));
//		} catch (FileNotFoundException e) {
//			
//
//		} catch (IOException e) {
//		
//		}
//
//	}

	 @Test
	 public void ftpToLandingZoneTaskletSuccessTest() throws Exception {
	 Mockito.when(filemanager.getCurrentDirectory()).thenReturn("D://FTP");
	 Mockito.when(packetHandlerService.storePacket(this.mockMultipartFile)).thenReturn(true);
	 Mockito.doNothing().when(filemanager).cleanUpFile(any(DirectoryPathDto.class),
	 any(DirectoryPathDto.class),
	 any(String.class),any(String.class));
	
	 RepeatStatus status = ftpScannerTasklet.execute(stepContribution,
	 chunkContext);
	 Assert.assertEquals(RepeatStatus.FINISHED, status);
	 }

	@Test
	public void ftpToLandingZoneTaskletDuplicateFile() throws Exception {

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		Mockito.when(filemanager.getCurrentDirectory()).thenReturn("D://FTP");
		DuplicateUploadRequestException duplicateUploadRequestException= new  DuplicateUploadRequestException("Duplicate file uploading to landing zone");
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
	
	@Test
	public void fileNotInDestination() throws Exception {

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		Mockito.when(filemanager.getCurrentDirectory()).thenReturn("D://FTP");

		Mockito.when(packetHandlerService.storePacket(this.mockMultipartFile)).thenReturn(true);
		FileNotFoundInDestinationException fileNotFoundInDestinationException= new FileNotFoundInDestinationException("File not found");
		Mockito.doThrow(fileNotFoundInDestinationException).when(filemanager).cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class),
				any(String.class), any(String.class));

		ftpScannerTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("IIS_GEN_FILE_NOT_FOUND_IN_DESTINATION");
			}
		}));

	}
	
//	@Test
//	public void ftpNotAccessibleTest() throws Exception {
//
//		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
//				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
//		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
//		when(mockAppender.getName()).thenReturn("MOCK");
//		Mockito.when(filemanager.getCurrentDirectory()).thenReturn("D:/FTP");
//		PowerMockito.mockStatic(IOUtils.class);
//		when(IOUtils.toByteArray(any())).thenThrow(IOException.class);
//		FTPNotAccessibleException ftpNotAccessibleException=new FTPNotAccessibleException("The FTP Path set by the System is not accessible");
//		Mockito.when(packetHandlerService.storePacket(this.mockMultipartFile)).thenReturn(true);
//		ftpScannerTasklet.execute(stepContribution, chunkContext);
//
//		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
//			@Override
//			public boolean matches(final ILoggingEvent argument) {
//				return ((LoggingEvent) argument).getFormattedMessage()
//						.contains("IIS_GEN_FTP_FOLDER_NOT_ACCESSIBLE");
//			}
//		}));
//
//	}

}
