package io.mosip.registration.processor.packet.receiver.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.impl.scan.AnnotatedWithAnyPackageScanFilter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.service.impl.PacketReceiverServiceImpl;
import io.mosip.registration.processor.packet.receiver.stage.PacketReceiverStage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.mosip.registration.processor.status.utilities.RegistrationStatusMapUtil;

@RefreshScope
@RunWith(SpringRunner.class)
public class PacketReceiverServiceTest {

	private static final String fileExtension = ".zip";

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	@Mock
	private InternalRegistrationStatusDto mockDto;

	@Mock
	private SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService;

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	PacketReceiverStage packetReceiverStage;

	@Mock
	private Environment env;

	@Mock
	private RegistrationStatusMapUtil registrationStatusMapUtil;

	@Mock
	RegistrationExceptionMapperUtil registrationStatusMapperUtil;

	private String stageName = "PacketReceiverStage";

	@InjectMocks
	private PacketReceiverService<File, MessageDTO> packetReceiverService = new PacketReceiverServiceImpl() {

		@Override
		public String getExtention() {
			return fileExtension;
		}

		@Override
		public long getMaxFileSize() {
			// max file size 5 mb
			return (5 * 1024 * 1024);
		}
	};

	SyncRegistrationEntity regEntity;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private File mockMultipartFile, invalidPacket, largerFile;

	List<RegistrationStatusDto> registrations = new ArrayList<>();
	RegistrationStatusDto registrationStatusDto = new RegistrationStatusDto();

	@Before
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		when(env.getProperty("registration.processor.packet.ext")).thenReturn(".zip");
		when(env.getProperty("registration.processor.max.file.size")).thenReturn("5");
		System.setProperty("registration.processor.packet.ext", ".zip");
		System.setProperty("registration.processor.max.file.size", "5");

		regEntity = new SyncRegistrationEntity();
		regEntity.setCreateDateTime(LocalDateTime.now());
		regEntity.setCreatedBy("Mosip");
		regEntity.setId("001");
		regEntity.setIsActive(true);
		regEntity.setLangCode("eng");
		regEntity.setRegistrationId("0000");
		regEntity.setRegistrationType("new");
		regEntity.setStatusCode("NEW_REGISTRATION");
		regEntity.setStatusComment("registration begins");

		registrationStatusDto.setStatusCode(RegistrationStatusCode.VIRUS_SCAN_FAILED.toString());
		registrationStatusDto.setRegistrationId("12345");
		registrations.add(registrationStatusDto);
		Mockito.when(registrationStatusService.getByIds(anyList())).thenReturn(registrations);
		Mockito.when(registrationStatusMapUtil.getExternalStatus(any()))
				.thenReturn(RegistrationExternalStatusCode.REREGISTER);

		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("0000.zip").getFile());
			mockMultipartFile = file;

			File invalidFile = new File(classLoader.getResource("1111.txt").getFile());

			invalidPacket = new File(invalidFile.getParentFile() + "/file");
			FileUtils.copyFile(invalidFile, invalidPacket);
			// invalidPacket = new MockMultipartFile("file", "1111.txt", "text/plain", new
			// FileInputStream(invalidFile));

			byte[] bytes = new byte[1024 * 1024 * 6];
			largerFile = new File(invalidFile.getParentFile() + "/2222.zip");
			// FileUtils.writeByteArrayToFile(new
			// File(invalidFile.getParentFile()+"2222.zip"), bytes);

			// largerFile = new MockMultipartFile("2222.zip", "2222.zip", "mixed/multipart",
			// bytes);

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {

			when(syncRegistrationService.isPresent(anyString())).thenReturn(true);
			AuditResponseDto auditResponseDto = new AuditResponseDto();
			ResponseWrapper<AuditResponseDto> responseWrapper = new ResponseWrapper<>();
			Mockito.doReturn(responseWrapper).when(auditLogRequestBuilder).createAuditRequestBuilder(
					"test case description", EventId.RPR_401.toString(), EventName.ADD.toString(),
					EventType.BUSINESS.toString(), "1234testcase", ApiName.AUDIT);

		}

		/*
		 * Mockito.doReturn(auditRequestDto).when(auditRequestBuilder).build();
		 * Mockito.doReturn(true).when(auditHandler).writeAudit(ArgumentMatchers.any());
		 *
		 * AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
		 * AuditRequestDto auditRequest1 = new AuditRequestDto();
		 *
		 * Field f
		 * =CoreAuditRequestBuilder.class.getDeclaredField("auditRequestBuilder");
		 * f.setAccessible(true); f.set(coreAuditRequestBuilder, auditRequestBuilder);
		 * Field f1 = AuditRequestBuilder.class.getDeclaredField("auditRequest");
		 * f1.setAccessible(true); f1.set(auditRequestBuilder, auditRequest1);
		 */

	}

	@Test
	public void testPacketStorageSuccess() throws IOException, URISyntaxException {

		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");

		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));

		MessageDTO successResult = packetReceiverService.storePacket(mockMultipartFile, stageName);

		assertEquals(true, successResult.getIsValid());
	}
	
	@Test
	public void testRetryIfNotNull() throws IOException, URISyntaxException {

		mockDto=new InternalRegistrationStatusDto();
		mockDto.setRetryCount(3);
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(mockDto).when(registrationStatusService).getRegistrationStatus("0000");
		
		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));
		ClassLoader classLoader = getClass().getClassLoader();
		
		File file = new File(classLoader.getResource("0000.zip").getFile());
		mockMultipartFile = file;
		Mockito.when(registrationStatusMapUtil.getExternalStatus(anyString(), anyInt()))
		.thenReturn(RegistrationExternalStatusCode.RESEND);

		MessageDTO successResult = packetReceiverService.storePacket(mockMultipartFile, stageName);

		assertEquals(true, successResult.getIsValid());
	}
	
	@Test
	public void testRetryIfNull() throws IOException, URISyntaxException {

		mockDto=new InternalRegistrationStatusDto();
		mockDto.setRetryCount(null);
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(mockDto).when(registrationStatusService).getRegistrationStatus("0000");
		
		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));
		ClassLoader classLoader = getClass().getClassLoader();
		
		File file = new File(classLoader.getResource("0000.zip").getFile());
		mockMultipartFile = file;
		Mockito.when(registrationStatusMapUtil.getExternalStatus(anyString(), anyInt()))
		.thenReturn(RegistrationExternalStatusCode.RESEND);

		MessageDTO successResult = packetReceiverService.storePacket(mockMultipartFile, stageName);

		assertEquals(true, successResult.getIsValid());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = DuplicateUploadRequestException.class)
	public void testDuplicateUploadRequest() throws IOException, URISyntaxException {
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);

		Mockito.doReturn(mockDto).when(registrationStatusService).getRegistrationStatus("0000");

		packetReceiverService.storePacket(mockMultipartFile, stageName);

	}

	@SuppressWarnings("unchecked")
	@Test(expected = PacketNotValidException.class)
	public void testInvalidPacketFormat() {
		regEntity.setRegistrationId("1111");
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);

		packetReceiverService.storePacket(invalidPacket, stageName);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {

			@Override
			public boolean matches(Object argument) {
				return ((LoggingEvent) argument).getFormattedMessage().contains("Packet format is different");

			}

		}));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = FileSizeExceedException.class)
	public void testFileSizeExceeded() {

		regEntity.setRegistrationId("2222");
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);

		packetReceiverService.storePacket(largerFile, stageName);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {

			@Override
			public boolean matches(Object argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("File size is greater than provided limit");

			}
		}));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = PacketNotSyncException.class)
	public void packetNotSyncExcpetionTest() {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);

		Mockito.when(syncRegistrationService.isPresent(anyString())).thenReturn(false);

		packetReceiverService.storePacket(mockMultipartFile, stageName);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {

			@Override
			public boolean matches(Object argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("Registration Packet is Not yet sync in Sync table");
			}
		}));
	}

	@Test
	public void testIoException() throws IOException {
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");
		Mockito.doThrow(new IOException()).when(fileManager).put(any(), any(), any());
		Mockito.when(registrationStatusMapperUtil.getStatusCode(any())).thenReturn("ERROR");
		MessageDTO result = packetReceiverService.storePacket(mockMultipartFile, stageName);

		assertFalse(result.getIsValid());
	}

}
