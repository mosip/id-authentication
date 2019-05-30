package io.mosip.registration.processor.packet.receiver.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.virusscanner.exception.VirusScannerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.receiver.decrypter.Decryptor;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.exception.PacketReceiverAppException;
import io.mosip.registration.processor.packet.receiver.exception.PacketSizeNotInSyncException;
import io.mosip.registration.processor.packet.receiver.exception.UnequalHashSequenceException;
import io.mosip.registration.processor.packet.receiver.service.impl.PacketReceiverServiceImpl;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

@RefreshScope
@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOUtils.class, HMACUtils.class })
public class PacketReceiverServiceTest {

	private static final String fileExtension = ".zip";

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	@Mock
	private InternalRegistrationStatusDto mockDto;

	@Mock
	private VirusScanner<Boolean, InputStream> virusScannerService;

	@Mock
	private SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService;

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	InputStream is;

	@Mock
	private Decryptor decryptor;


	@Mock
	RegistrationExceptionMapperUtil registrationStatusMapperUtil;

	private String stageName = "PacketReceiverStage";

	@InjectMocks
	private PacketReceiverService<File, MessageDTO> packetReceiverService = new PacketReceiverServiceImpl();

	private SyncRegistrationEntity regEntity;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private File mockMultipartFile, invalidPacket, largerFile;

	List<RegistrationStatusDto> registrations = new ArrayList<>();
	RegistrationStatusDto registrationStatusDto = new RegistrationStatusDto();

	@Before
	public void setup() throws Exception {
		ReflectionTestUtils.setField(packetReceiverService, "extention", ".zip");
		ReflectionTestUtils.setField(packetReceiverService, "fileSize", "5");

		regEntity = new SyncRegistrationEntity();
		regEntity.setCreateDateTime(LocalDateTime.now());
		regEntity.setCreatedBy("Mosip");
		regEntity.setId("001");
		regEntity.setLangCode("eng");
		regEntity.setRegistrationId("0000");
		regEntity.setRegistrationType("NEW");
		regEntity.setStatusCode("NEW_REGISTRATION");
		regEntity.setStatusComment("registration begins");
		regEntity.setPacketHashValue("abcd1234");
		BigInteger size = new BigInteger("2291584");
		regEntity.setPacketSize(size);

		registrationStatusDto.setStatusCode("RESEND");
		registrationStatusDto.setRegistrationId("12345");
		registrations.add(registrationStatusDto);
		Mockito.when(registrationStatusService.getByIds(anyList())).thenReturn(registrations);
		PowerMockito.mockStatic(HMACUtils.class);
		PowerMockito.when(HMACUtils.digestAsPlainText(any())).thenReturn("abcd1234");
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("0000.zip").getFile());
			mockMultipartFile = file;
			is = new FileInputStream(file);
			File invalidFile = new File(classLoader.getResource("1111.txt").getFile());

			invalidPacket = new File(invalidFile.getParentFile() + "/file");
			FileUtils.copyFile(invalidFile, invalidPacket);
			byte[] bytes = new byte[1024 * 1024 * 6];
			largerFile = new File(invalidFile.getParentFile() + "/2222.zip");

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
	}

	@Test
	public void testPacketStorageSuccess()
			throws IOException, URISyntaxException, PacketDecryptionFailureException, ApisResourceAccessException {

		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");
		Mockito.when(decryptor.decrypt(any(InputStream.class), any())).thenReturn(is);

		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));
		Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenReturn(Boolean.TRUE);
		MessageDTO successResult = packetReceiverService.validatePacket(mockMultipartFile, stageName);

		assertEquals(true, successResult.getIsValid());
	}

	@Test
	public void testRetryIfNotNull()
			throws IOException, URISyntaxException, PacketDecryptionFailureException, ApisResourceAccessException {

		mockDto = new InternalRegistrationStatusDto();
		mockDto.setRetryCount(3);
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(mockDto).when(registrationStatusService).getRegistrationStatus("0000");
		Mockito.when(decryptor.decrypt(any(InputStream.class), any())).thenReturn(is);
		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));
		ClassLoader classLoader = getClass().getClassLoader();
		Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenReturn(Boolean.TRUE);
		File file = new File(classLoader.getResource("0000.zip").getFile());
		mockMultipartFile = file;
		MessageDTO successResult = packetReceiverService.validatePacket(mockMultipartFile, stageName);

		assertEquals(true, successResult.getIsValid());
	}

	@Test
	public void testRetryIfNull()
			throws IOException, URISyntaxException, PacketDecryptionFailureException, ApisResourceAccessException {

		mockDto = new InternalRegistrationStatusDto();
		mockDto.setRetryCount(null);
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(mockDto).when(registrationStatusService).getRegistrationStatus("0000");

		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));
		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource("0000.zip").getFile());
		mockMultipartFile = file;
		Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenReturn(Boolean.TRUE);
		Mockito.when(decryptor.decrypt(any(InputStream.class), any())).thenReturn(is);
		MessageDTO successResult = packetReceiverService.validatePacket(mockMultipartFile, stageName);

		assertEquals(true, successResult.getIsValid());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = DuplicateUploadRequestException.class)
	public void testDuplicateUploadRequest() throws IOException, URISyntaxException {

		registrationStatusDto.setStatusCode("REREGISTER");
		registrations.add(registrationStatusDto);
		Mockito.when(registrationStatusService.getByIds(anyList())).thenReturn(registrations);

		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);

		Mockito.doReturn(mockDto).when(registrationStatusService).getRegistrationStatus("0000");

		packetReceiverService.validatePacket(mockMultipartFile, stageName);

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

		packetReceiverService.validatePacket(invalidPacket, stageName);

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
		regEntity.setPacketSize(new BigInteger("6241828"));
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);

		packetReceiverService.validatePacket(largerFile, stageName);

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

		packetReceiverService.validatePacket(mockMultipartFile, stageName);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {

			@Override
			public boolean matches(Object argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("Registration Packet is Not yet sync in Sync table");
			}
		}));
	}

	@Test(expected = PacketReceiverAppException.class)
	@Ignore
	public void testIoException() throws IOException, PacketDecryptionFailureException, ApisResourceAccessException {
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");
		// Mockito.doThrow(new IOException()).when(fileManager).put(any(), any(),
		// any());
		Mockito.when(registrationStatusMapperUtil.getStatusCode(any())).thenReturn("ERROR");
		// Mockito.when(decryptor.decrypt(any(InputStream.class),any())).thenReturn(is);
		// Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenReturn(Boolean.TRUE);
		File mockedFile = Mockito.mock(File.class);
		Mockito.when(mockedFile.getName()).thenReturn("Abc.txt");
		Mockito.when(mockedFile.exists()).thenReturn(Boolean.TRUE);
		// Mockito.when(mockedFile.getAbsolutePath()).thenThrow(new IOException());
		Mockito.doThrow(new IOException()).when(mockedFile.getAbsolutePath());
		MessageDTO result = packetReceiverService.validatePacket(mockMultipartFile, stageName);

		assertFalse(result.getIsValid());
	}

	@Test
	public void testVirusScanFailed()
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");
		Mockito.when(decryptor.decrypt(any(InputStream.class), any())).thenReturn(is);

		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));
		Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenReturn(Boolean.FALSE);
		MessageDTO successResult = packetReceiverService.processPacket(mockMultipartFile);

		assertEquals(false, successResult.getIsValid());
	}

	@Test
	public void testVirusscannerException()
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");
		Mockito.when(decryptor.decrypt(any(InputStream.class), any())).thenReturn(is);

		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));
		Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenThrow(new VirusScannerException());
		MessageDTO successResult = packetReceiverService.processPacket(mockMultipartFile);

		assertEquals(false, successResult.getIsValid());
	}

	@Test
	public void testApisResourceAccessException()
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");
		Mockito.when(decryptor.decrypt(any(InputStream.class), any())).thenThrow(new ApisResourceAccessException());

		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));
		Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenReturn(Boolean.TRUE);
		MessageDTO successResult = packetReceiverService.processPacket(mockMultipartFile);

		assertEquals(false, successResult.getIsValid());
	}

	@Test(expected = PacketReceiverAppException.class)
	public void testDataAccessException()
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");
		File mockedFile = Mockito.mock(File.class);
		Mockito.when(mockedFile.getName()).thenReturn("Abc.txt");
		Mockito.when(mockedFile.exists()).thenReturn(Boolean.TRUE);
		Mockito.when(mockedFile.getAbsolutePath()).thenThrow(new IncorrectResultSizeDataAccessException(2));
		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));
		Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenReturn(Boolean.TRUE);
		MessageDTO successResult = packetReceiverService.validatePacket(mockedFile, stageName);

		assertEquals(false, successResult.getIsValid());
	}

	@Test
	public void testPacketDecryptionFailureException()
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");
		Mockito.when(decryptor.decrypt(any(InputStream.class), any()))
				.thenThrow(new PacketDecryptionFailureException("", ""));

		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));
		Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenReturn(Boolean.TRUE);
		MessageDTO successResult = packetReceiverService.processPacket(mockMultipartFile);

		assertEquals(false, successResult.getIsValid());
	}

	@Test
	public void testPacketStorageToLandingZone()
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");
		Mockito.when(decryptor.decrypt(any(InputStream.class), any())).thenReturn(is);

		Mockito.doNothing().when(fileManager).put(anyString(), any(InputStream.class), any(DirectoryPathDto.class));
		Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenReturn(Boolean.TRUE);
		MessageDTO successResult = packetReceiverService.processPacket(mockMultipartFile);

		assertEquals(true, successResult.getIsValid());
	}

	@Test
	public void testIOException() throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");
		Mockito.when(decryptor.decrypt(any(InputStream.class), any())).thenReturn(is);

		Mockito.doThrow(new IOException()).when(fileManager).put(anyString(), any(InputStream.class),
				any(DirectoryPathDto.class));
		Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenReturn(Boolean.TRUE);
		MessageDTO successResult = packetReceiverService.processPacket(mockMultipartFile);

		assertEquals(false, successResult.getIsValid());

	}

	@Test
	public void testdataAccessException()
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");
		Mockito.when(decryptor.decrypt(any(InputStream.class), any())).thenReturn(is);

		Mockito.doThrow(new DataIntegrityViolationException("")).when(fileManager).put(anyString(),
				any(InputStream.class), any(DirectoryPathDto.class));
		Mockito.when(virusScannerService.scanFile(any(InputStream.class))).thenReturn(Boolean.TRUE);
		MessageDTO successResult = packetReceiverService.processPacket(mockMultipartFile);

		assertEquals(false, successResult.getIsValid());
	}

	@Test(expected = PacketReceiverAppException.class)
	public void testIOExceptionForValidatePacket()
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {

		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.toByteArray(any(InputStream.class))).thenThrow(new IOException());
		MessageDTO successResult = packetReceiverService.validatePacket(mockMultipartFile, stageName);
		assertEquals(false, successResult.getIsValid());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = PacketSizeNotInSyncException.class)
	public void testPacketSize() {

		regEntity.setRegistrationId("2222");
		regEntity.setPacketSize(new BigInteger("624182"));
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);

		packetReceiverService.validatePacket(largerFile, stageName);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {

			@Override
			public boolean matches(Object argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("Synced packet size not same as uploaded packet");

			}
		}));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = UnequalHashSequenceException.class)
	public void testHashSequence() {

		regEntity.setRegistrationId("2222");
		regEntity.setPacketHashValue("abcd");
		regEntity.setPacketSize(new BigInteger("624182"));
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);

		packetReceiverService.validatePacket(largerFile, stageName);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {

			@Override
			public boolean matches(Object argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("The Registration Packet HashSequence is not equal as synced packet HashSequence");

			}
		}));
	}
}
