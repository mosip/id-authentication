package io.mosip.registration.processor.packet.decrypter.job.stage.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.uploader.exception.DFSNotAccessibleException;
import io.mosip.registration.processor.packet.uploader.stage.PacketUploaderStage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;


/**
 * The Class PacketUploaderJobTest.
 * @author M1049387
 */
@RunWith(MockitoJUnitRunner.class)
public class PacketUploaderJobTest {

	
	/** The Constant stream. */
	private static final InputStream stream = Mockito.mock(InputStream.class);

	
	/** The packet uploader stage. */
	@InjectMocks
	PacketUploaderStage packetUploaderStage = new PacketUploaderStage() {
		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String clusterAddress, String localhost) {
			return null;
		}

		@Override
		public void consume(MosipEventBus mosipEventBus, MessageBusAddress fromAddress) {
		}
	};
	
	/** The audit log request builder. */
	@Mock
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet archiver. */
	@Mock
	private PacketArchiver packetArchiver;

	/** The env. */
	@Mock
	private Environment env;
	
	/** The file manager. */
	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	/** The adapter. */
	@Mock
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The dto. */
	MessageDTO dto = new MessageDTO();

	/** The foo logger. */
	private Logger fooLogger;

	/** The list appender. */
	private ListAppender<ILoggingEvent> listAppender;

	/** The entry. */
	InternalRegistrationStatusDto entry = new InternalRegistrationStatusDto();
	
	
	/**
	 * Setup.
	 *
	 * @throws PacketNotFoundException the packet not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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
		//FileInputStream fileInputStream = Mockito.mock(FileInputStream.class);
	}

	/**
	 * Test deploy verticle.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testDeployVerticle() {
		packetUploaderStage.deployVerticle();
	}

	@Test
	public void UploadingSuccessIfFileNotPresentTest() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("1001.zip").getFile());
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any(InternalRegistrationStatusDto.class));
		Mockito.when(adapter.storePacket("1001", file)).thenReturn(Boolean.TRUE);
		Mockito.when(adapter.isPacketPresent("1001")).thenReturn(Boolean.FALSE);
		Mockito.doNothing().when(adapter).unpackPacket("1001");
		packetUploaderStage.process(dto);
		Assertions.assertThat(listAppender.list).extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage).containsExactly(Tuple.tuple( Level.INFO, "1001 - File Stored in File System and same has been deleted from virus scanner job."));
		
	}
	
	/**
	 * Uploading success if file present test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void UploadingSuccessIfFilePresentTest() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("1001.zip").getFile());
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any(InternalRegistrationStatusDto.class));
		Mockito.when(adapter.storePacket("1001", file)).thenReturn(Boolean.TRUE);
		Mockito.when(adapter.isPacketPresent("1001")).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(adapter).unpackPacket("1001");
		packetUploaderStage.process(dto);
		Assertions.assertThat(listAppender.list).extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage).containsExactly(Tuple.tuple( Level.INFO, "1001 - File is Already exists in DFS location And its now Deleted from Virus scanner job"));
		
	}

	/**
	 * Gets the by satus exception test.
	 *
	 * @return the by satus exception test
	 * @throws Exception
	 *             the exception
	 */

	@Test
	public void getRegistrationStatusExceptionTest() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		Mockito.doNothing().when(packetArchiver).archivePacket("1001");
		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService).getRegistrationStatus("1001");
		packetUploaderStage.process(dto);

		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.containsExactly(Tuple.tuple(Level.ERROR, "The Registration Status table is not accessible - null"));

	}

	
	
	@Test
	public void StatusUpdateExceptionTest() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		Mockito.doNothing().when(packetArchiver).archivePacket("1001");
		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService).updateRegistrationStatus(entry);
		packetUploaderStage.process(dto);
	}
	
	@Test
	public void IOExceptionTest() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);

		Mockito.doNothing().when(packetArchiver).archivePacket(any());
	
		Mockito.doThrow(IOException.class).when(adapter).unpackPacket(any(String.class));

	
		packetUploaderStage.process(dto);

		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.containsExactly(
						Tuple.tuple(Level.ERROR, "1001 unable to delete after sending to DFS. - {}"));

	}
	
	/*@Test
	public void PacketNotFoundExceptionTest() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		Mockito.doThrow(PacketNotFoundException.class).when(packetArchiver).archivePacket(any(String.class));
		packetUploaderStage.process(dto);
		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.containsExactly(
						Tuple.tuple(Level.ERROR, "1001 unable to delete after sending to DFS. - {}"));

	}*/

	@Test
	public void testDfsNotAccessible() throws Exception {
		listAppender.start();
		fooLogger.addAppender(listAppender);
		Mockito.doNothing().when(packetArchiver).archivePacket(any());
		Mockito.when(adapter.isPacketPresent("1001")).thenReturn(Boolean.FALSE);
		Mockito.doThrow(DFSNotAccessibleException.class).when(adapter).storePacket(anyString(), any(InputStream.class));
		packetUploaderStage.process(dto);
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The DFS Path set by the System is not accessible - {}")); 
	}

}
