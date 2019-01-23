package io.mosip.registration.processor.virus.scanner.job.stage.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.kernel.core.util.ZipUtils;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.virus.scanner.job.decrypter.Decryptor;
import io.mosip.registration.processor.virus.scanner.job.decrypter.constant.PacketDecryptionFailureExceptionConstant;
import io.mosip.registration.processor.virus.scanner.job.decrypter.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.virus.scanner.job.exceptions.VirusScanFailedException;
import io.mosip.registration.processor.virus.scanner.job.stage.VirusScannerStage;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ZipUtils.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class VirusScannerStageTest {

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private Environment env;

	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	private VirusScanner<Boolean, String> virusScanner;

	private Logger fooLogger;
	private ListAppender<ILoggingEvent> listAppender;

	@Mock
	private Decryptor decryptor;

	private static final InputStream stream = Mockito.mock(InputStream.class);

	MessageDTO dto = new MessageDTO();

	@InjectMocks
	private VirusScannerStage virusScannerStage = new VirusScannerStage() {
		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String url) {
			return null;
		}

		@Override
		public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
				MessageBusAddress toAddress) {
		}
	};

	InternalRegistrationStatusDto entry = new InternalRegistrationStatusDto();

	@Before
	public void setup() throws Exception {

		dto.setRid("1000");

		entry.setRegistrationId("1000");
		entry.setRetryCount(0);
		entry.setStatusComment("Landing");

		when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(entry);
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN.toString())).thenReturn("src/test/resources/");
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString())).thenReturn("src/test/resources/");
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString())).thenReturn("src/test/resources/");
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_UNPACK.toString())).thenReturn("src/test/resources/");
		when(env.getProperty("registration.processor.packet.ext")).thenReturn(".zip");
		PowerMockito.mockStatic(ZipUtils.class);
		PowerMockito.when(ZipUtils.class, "unZipDirectory", anyString(), anyString()).thenReturn(Boolean.TRUE);
		fooLogger = (Logger) LoggerFactory.getLogger(VirusScannerStage.class);
		listAppender = new ListAppender<>();
		doNothing().when(fileManager).deletePacket(any(), any());
		doNothing().when(fileManager).deleteFolder(any(), any());

		FileInputStream fileInputStream = Mockito.mock(FileInputStream.class);

		PowerMockito.whenNew(FileInputStream.class).withArguments(Mockito.anyString()).thenReturn(fileInputStream);

	}

	/**
	 * Test deploy verticle.
	 */
	@Test
	public void testDeployVerticle() {
		virusScannerStage.deployVerticle();
	}

	@Test
	public void testSuccessfulVirusScan() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);

		Mockito.when(virusScanner.scanFile(anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(virusScanner.scanFolder(anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(decryptor.getScanResult()).thenReturn(Boolean.TRUE);
		Mockito.when(decryptor.getScanFolderResult()).thenReturn(Boolean.TRUE);

		Mockito.doNothing().when(registrationStatusService)
				.updateRegistrationStatus(any(InternalRegistrationStatusDto.class));
		Mockito.when(decryptor.decrypt(any(InputStream.class), any(String.class))).thenReturn(stream);

		virusScannerStage.process(dto);

		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.containsExactly(
						Tuple.tuple(Level.INFO, "SESSIONID - REGISTRATIONID - 1000 - File is successfully scanned."));

	}

	@Test
	@Ignore
	public void testFailureVirusScan() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);

		Mockito.when(virusScanner.scanFile(anyString())).thenReturn(Boolean.FALSE);
		Mockito.when(decryptor.getScanResult()).thenReturn(Boolean.FALSE);
		virusScannerStage.process(dto);

		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.containsExactly(Tuple.tuple(Level.INFO, "SESSIONID - REGISTRATIONID - 1000 - File is infected."));

	}

	@Test

	public void testFailureVirusScanFiles() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);

		Mockito.when(virusScanner.scanFile(anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(virusScanner.scanFolder(anyString())).thenReturn(Boolean.FALSE);
	
		Mockito.when(decryptor.getScanResult()).thenReturn(Boolean.TRUE);
		Mockito.when(decryptor.getScanFolderResult()).thenReturn(Boolean.FALSE);
		Mockito.doNothing().when(registrationStatusService)
				.updateRegistrationStatus(any(InternalRegistrationStatusDto.class));
		Mockito.when(decryptor.decrypt(any(InputStream.class), any(String.class))).thenReturn(stream);

		virusScannerStage.process(dto);

		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.containsExactly(Tuple.tuple(Level.INFO, "SESSIONID - REGISTRATIONID - 1000 - File is infected."));

	}

	@Test
	public void testDecryptionFailure() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);

		Mockito.when(virusScanner.scanFile(anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(virusScanner.scanFolder(anyString())).thenReturn(Boolean.FALSE);
		Mockito.when(decryptor.getScanResult()).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(registrationStatusService)
				.updateRegistrationStatus(any(InternalRegistrationStatusDto.class));
		PacketDecryptionFailureException exception = new PacketDecryptionFailureException(
				PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
				PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorMessage(),
				new IOException());
		Mockito.when(decryptor.decrypt(any(InputStream.class), any(String.class))).thenThrow(exception);

		virusScannerStage.process(dto);
		assertEquals(RegistrationStatusCode.PACKET_DECRYPTION_FAILED.toString(), entry.getStatusCode());
	}

	@Test
	@Ignore
	public void testVirusScanFailureException() throws Exception {
		listAppender.start();
		fooLogger.addAppender(listAppender);

		Mockito.when(decryptor.getScanResult()).thenReturn(Boolean.TRUE);
		doThrow(VirusScanFailedException.class).when(decryptor).getScanResult();
		doThrow(VirusScanFailedException.class).when(virusScanner).scanFile(anyString());
		MessageDTO object = virusScannerStage.process(dto);

		assertTrue(object.getInternalError());
	}

	@Test
	public void exceptionTest() throws Exception {

		Mockito.when(virusScanner.scanFile(anyString())).thenThrow(new NullPointerException());

		Mockito.when(decryptor.getScanResult()).thenThrow(new NullPointerException());
		MessageDTO object = virusScannerStage.process(dto);

		assertTrue(object.getInternalError());

	}
}
