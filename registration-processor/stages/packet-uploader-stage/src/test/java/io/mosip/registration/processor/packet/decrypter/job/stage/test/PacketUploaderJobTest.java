package io.mosip.registration.processor.packet.decrypter.job.stage.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.archiver.util.exception.UnableToAccessPathException;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.uploader.stage.PacketUploaderStage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(MockitoJUnitRunner.class)
public class PacketUploaderJobTest {

	/** The Constant stream. */
	private static final InputStream stream = Mockito.mock(InputStream.class);

	/** The packet decryptor tasklet. */
	@InjectMocks
	PacketUploaderStage packetUploaderStage = new PacketUploaderStage() {
		@Override
		public void send(MosipEventBus mosipEventBus, MessageBusAddress toAddress, MessageDTO message) {
		}
	};

	@Mock
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	InternalRegistrationStatusDto entry = new InternalRegistrationStatusDto();


	/** The packet archiver. */
	@Mock
	private PacketArchiver packetArchiver;

	@Mock
	private Environment env;
	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	

	MessageDTO dto = new MessageDTO();

	private Logger fooLogger;

	private ListAppender<ILoggingEvent> listAppender;

	/**
	 * Setup.
	 *
	 * @throws UnableToAccessPathException
	 *             the unable to access path exception
	 * @throws PacketNotFoundException
	 *             the packet not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Before
	public void setup() throws UnableToAccessPathException, PacketNotFoundException, IOException {
		dto.setRid("1000");

		entry.setRegistrationId("1000");
		entry.setRetryCount(0);
		entry.setStatusComment("virus scan");
		when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(entry);
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN.toString())).thenReturn("src/test/resources/");
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString())).thenReturn("src/test/resources/");
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString())).thenReturn("src/test/resources/");
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_UNPACK.toString())).thenReturn("src/test/resources/");
		
		
		fooLogger = (Logger) LoggerFactory.getLogger(PacketUploaderStage.class);
		listAppender = new ListAppender<>();
		
		doNothing().when(fileManager).deletePacket(any(), any());
		doNothing().when(fileManager).deleteFolder(any(), any());

		FileInputStream fileInputStream = Mockito.mock(FileInputStream.class);

	//	PowerMockito.whenNew(FileInputStream.class).withArguments(Mockito.anyString()).thenReturn(fileInputStream);
	}

	/**
	 * Test deploy verticle.
	 */
	@Test
	public void testDeployVerticle() {
		packetUploaderStage.deployVerticle();
	}

	@Test
	public void UploadingSuccessTest() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		
		
		
		UnableToAccessPathException exception = new UnableToAccessPathException("", "Unable to access path Exception");
		Mockito.doThrow(exception).when(packetArchiver).archivePacket(any());
		
	
	}

	

}
