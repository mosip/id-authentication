/*package io.mosip.registration.processor.packet.uploader.stage.test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
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
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.EventId;
import io.mosip.registration.processor.core.constant.EventName;
import io.mosip.registration.processor.core.constant.EventType;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.uploader.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.uploader.stage.PacketUploaderStage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

*//**
 * The Class PacketUploaderJobTest.
 * 
 * @author M1049387
 *//*
@RunWith(MockitoJUnitRunner.class)
public class PacketUploaderJobTest {

	 max retry count 
	private static final int maxRetryCount = 5;

	*//** The Constant stream. *//*
	private static final InputStream stream = Mockito.mock(InputStream.class);

	*//** The packet uploader stage. *//*
	@InjectMocks
	PacketUploaderStage packetUploaderStage = new PacketUploaderStage() {
		@Override
		public int getMaxRetryCount() {
			return maxRetryCount;
		}

		@Override
		public MosipEventBus getEventBus(Object verticleName, String url, int instanceNumber) {
			return null;
		}

		@Override
		public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
				MessageBusAddress toAddress) {
		}
	};
	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;
	*//** The audit log request builder. *//*
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder = new AuditLogRequestBuilder();

	*//** The registration status service. *//*
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	*//** The packet archiver. *//*
	@Mock
	private PacketArchiver packetArchiver;

	*//** The env. *//*
	@Mock
	private Environment env;

	*//** The file manager. *//*
	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	*//** The adapter. *//*
	@Mock
	private FileSystemAdapter adapter;

	*//** The dto. *//*
	MessageDTO dto = new MessageDTO();

	*//** The foo logger. *//*
	private Logger fooLogger;

	*//** The list appender. *//*
	private ListAppender<ILoggingEvent> listAppender;

	*//** The entry. *//*
	InternalRegistrationStatusDto entry = new InternalRegistrationStatusDto();
	@Mock
	File filech = new File("");

	*//**
	 * Setup.
	 *
	 * @throws PacketNotFoundException
	 *             the packet not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *//*
	@Before
	public void setup() throws PacketNotFoundException, IOException {
		dto.setRid("1001");
		entry.setRegistrationId("1001");
		entry.setRetryCount(0);
		entry.setStatusComment("virus scan");
		when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(entry);
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString())).thenReturn("src/test/resources/");
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString())).thenReturn("src/test/resources/");
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_UNPACK.toString())).thenReturn("src/test/resources/");
		fooLogger = (Logger) LoggerFactory.getLogger(PacketUploaderStage.class);
		listAppender = new ListAppender<>();
		doNothing().when(fileManager).deletePacket(any(), any());
		doNothing().when(fileManager).deleteFolder(any(), any());
		ResponseWrapper<AuditResponseDto> responseWrapper = new ResponseWrapper<>();
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		responseWrapper.setResponse(auditResponseDto);
		Mockito.doReturn(responseWrapper).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_401.toString(), EventName.ADD.toString(),
				EventType.BUSINESS.toString(), "1234testcase", ApiName.DMZAUDIT);
	}

	*//**
	 * Test deploy verticle.
	 *
	 * @throws Exception
	 *             the exception
	 *//*
	@Test
	public void testDeployVerticle() {
		packetUploaderStage.deployVerticle();
	}

	*//**
	 * Uploading success if file present test.
	 *
	 * @throws Exception
	 *             the exception
	 *//*
	@Test
	public void UploadingSuccessIfFilePresentTest() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("1001.zip").getFile());
		Mockito.doNothing().when(registrationStatusService)
				.updateRegistrationStatus(any(InternalRegistrationStatusDto.class));
		Mockito.when(adapter.storePacket("1001", file)).thenReturn(Boolean.TRUE);
		Mockito.when(adapter.isPacketPresent("1001")).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(adapter).unpackPacket("1001");
		packetUploaderStage.process(dto);
		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.contains(Tuple.tuple(Level.INFO,
						"SESSIONID - REGISTRATIONID - 1001 - File is Already exists in File Store And its now Deleted from Virus scanner job"));

	}

	*//**
	 * Gets the by satus exception test.
	 *
	 * @return the by satus exception test
	 * @throws Exception
	 *             the exception
	 *//*

	@Test
	public void getRegistrationStatusExceptionTest() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		Mockito.doNothing().when(packetArchiver).archivePacket("1001");
		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService)
				.getRegistrationStatus("1001");
		packetUploaderStage.process(dto);
		Assertions.assertThatExceptionOfType(TablenotAccessibleException.class);

	}

	@Test
	public void SystemExceptionTest() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		Mockito.doThrow(Exception.class).when(packetArchiver).archivePacket("1001");

		packetUploaderStage.process(dto);
	}

	@Test
	public void PacketNotFoundExceptionTest() throws IOException {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		Mockito.when(filech.exists()).thenReturn(Boolean.FALSE);
		PacketNotFoundException packetNotFoundException = new PacketNotFoundException(
				"1001 unable to delete after sending to DFS.");
		Mockito.doThrow(packetNotFoundException).when(packetArchiver).archivePacket(anyString());
		packetUploaderStage.process(dto);
		Assertions.assertThatExceptionOfType(PacketNotFoundException.class);

	}

	@Test
	public void testDfsNotAccessible() throws Exception {
		listAppender.start();
		fooLogger.addAppender(listAppender);
		Mockito.doNothing().when(packetArchiver).archivePacket(any());
		Mockito.when(adapter.isPacketPresent("1001")).thenReturn(Boolean.FALSE);
		Mockito.doThrow(FSAdapterException.class).when(adapter).storePacket(anyString(), any(InputStream.class));
		packetUploaderStage.process(dto);

		Assertions.assertThatExceptionOfType(FSAdapterException.class);
		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.contains(Tuple.tuple(Level.ERROR,
						"SESSIONID - REGISTRATIONID - 1001 - RPR_PUM_PACKET_STORE_NOT_ACCESSIBLEnull"));
	}

	@Test
	public void testIoExceptionUploadPacket() throws PacketNotFoundException, IOException {
		listAppender.start();
		fooLogger.addAppender(listAppender);
		Mockito.doThrow(IOException.class).when(packetArchiver).archivePacket(any());
		packetUploaderStage.process(dto);

		Assertions.assertThatIOException();
	}

	@Test
	public void retryFailure() {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		Mockito.doNothing().when(registrationStatusService)
				.updateRegistrationStatus(any(InternalRegistrationStatusDto.class));
		dto.setRid("1001");
		entry.setRegistrationId("1001");
		entry.setRetryCount(5);
		entry.setStatusComment("virus scan");
		when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(entry);
		Mockito.doNothing().when(registrationStatusService)
				.updateRegistrationStatus(any(InternalRegistrationStatusDto.class));

		MessageDTO object = packetUploaderStage.process(dto);
		assertTrue("Expecting Internal error to be true if Retry count is greater than Max Retry count",
				object.getInternalError());
	}
}
*/