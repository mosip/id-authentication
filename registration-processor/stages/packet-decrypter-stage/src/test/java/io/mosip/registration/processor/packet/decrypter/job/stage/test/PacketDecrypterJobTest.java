package io.mosip.registration.processor.packet.decrypter.job.stage.test;


import static org.mockito.Matchers.any;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.packet.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.archiver.util.exception.UnableToAccessPathException;
import io.mosip.registration.processor.packet.decrypter.job.Decryptor;
import io.mosip.registration.processor.packet.decrypter.job.stage.PacketDecrypterStage;
import io.mosip.registration.processor.packet.decryptor.job.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.packet.decryptor.job.exception.constant.PacketDecryptionFailureExceptionConstant;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(MockitoJUnitRunner.class)
public class PacketDecrypterJobTest {

	/** The Constant stream. */
	private static final InputStream stream = Mockito.mock(InputStream.class);
	
	/** The packet decryptor tasklet. */
	@InjectMocks
	PacketDecrypterStage packetDecrypterStage;

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The adapter. */
	@Mock
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The packet archiver. */
	@Mock
	private PacketArchiver packetArchiver;

	/** The decryptor. */
	@Mock
	private Decryptor decryptor;

	/** The dto. */
	InternalRegistrationStatusDto dto;

	/** The list. */
	List<InternalRegistrationStatusDto> list;
	
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
		dto = new InternalRegistrationStatusDto();
		dto.setRegistrationId("1001");
		dto.setStatusCode("PACKET_UPLOADED_TO_FILESYSTEM");
		dto.setRetryCount(0);
		list = new ArrayList<InternalRegistrationStatusDto>();
		
        fooLogger = (Logger) LoggerFactory.getLogger(PacketDecrypterStage.class);
        listAppender = new ListAppender<>();
	}
	

	/**
	 * Decryption success test.
	 *
	 * @throws Exception
	 *             the exception
	 */

	@Test
	public void decryptionSuccessTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
        list.add(dto);
		UnableToAccessPathException exception = new UnableToAccessPathException("", "Unable to access path Exception");
		Mockito.doThrow(exception).when(packetArchiver).archivePacket(any());

		Mockito.when(
				registrationStatusService.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);

		Mockito.doNothing().when(adapter).unpackPacket(any(String.class));

		Mockito.when(adapter.getPacket(any(String.class))).thenReturn(stream);

		Mockito.when(decryptor.decrypt(any(InputStream.class), any(String.class))).thenReturn(stream);

		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any(InternalRegistrationStatusDto.class));
		
		MessageDTO msg = new MessageDTO();
		packetDecrypterStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple(Level.ERROR, " -  --> Unable to access path Exception - null"),
						 Tuple.tuple( Level.INFO, "1001 -  Packet decrypted and extracted encrypted files stored in DFS. - {}")); 
		
	}

	/**
	 * Null packet test.
	 *
	 * @throws Exception
	 *             the exception
	 */

	@Test
	public void nullPacketTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);

        list.add(dto);
		PacketNotFoundException exception = new PacketNotFoundException("", "Packet not found Exception");
		Mockito.doThrow(exception).when(packetArchiver).archivePacket(any());

		Mockito.when(
				registrationStatusService.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);

		Mockito.doNothing().when(adapter).unpackPacket(any(String.class));

		Mockito.when(adapter.getPacket(any(String.class))).thenReturn(stream);

		Mockito.when(decryptor.decrypt(any(InputStream.class), any(String.class))).thenReturn(null);
		
		MessageDTO msg = new MessageDTO();
		packetDecrypterStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple(Level.ERROR, " -  --> Packet not found Exception - null"),
				 Tuple.tuple( Level.INFO, "1001 -  Packet is null and could not be  decrypted  - {}")); 

	}

	/**
	 * Decryption failure test.
	 *
	 * @throws Exception
	 *             the exception
	 */

	@Test
	public void decryptionFailureTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
        list.add(dto);

		Mockito.doNothing().when(packetArchiver).archivePacket(any());

		Mockito.when(
				registrationStatusService.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);

		Mockito.doNothing().when(adapter).unpackPacket(any(String.class));

		Mockito.when(adapter.getPacket(any(String.class))).thenReturn(stream);

		PacketDecryptionFailureException exception = new PacketDecryptionFailureException(
				PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
				PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorMessage(),
				new IOException());

		Mockito.when(decryptor.decrypt(any(InputStream.class), any(String.class))).thenThrow(exception);
	
		MessageDTO msg = new MessageDTO();
		packetDecrypterStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.contains(Tuple.tuple( Level.ERROR, PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode()+" - "+
											PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
											.getErrorMessage()+" - {}")); 
		
	}

	/**
	 * No files to be decrypted test.
	 *
	 * @throws Exception
	 *             the exception
	 */

	@Test
	public void noFilesToBeDecryptedTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
		Mockito.doNothing().when(packetArchiver).archivePacket(any());

		Mockito.when(
				registrationStatusService.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);
		
		MessageDTO msg = new MessageDTO();
		packetDecrypterStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.INFO, "There are currently no files to be decrypted")); 

	}

	/**
	 * Status update exception test.
	 *
	 * @throws Exception
	 *             the exception
	 */

	@Test
	public void StatusUpdateExceptionTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);

    	list.add(dto);
		Mockito.doNothing().when(packetArchiver).archivePacket(any());

		Mockito.when(
				registrationStatusService.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);

		Mockito.doNothing().when(adapter).unpackPacket(any(String.class));

		Mockito.when(adapter.getPacket(any(String.class))).thenReturn(stream);

		Mockito.when(decryptor.decrypt(any(InputStream.class), any(String.class))).thenReturn(stream);

		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService)
				.updateRegistrationStatus(any(InternalRegistrationStatusDto.class));

		MessageDTO msg = new MessageDTO();
		packetDecrypterStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The Registration Status table is not accessible - null - {}")); 
		
	}

	/**
	 * Gets the by satus exception test.
	 *
	 * @return the by satus exception test
	 * @throws Exception
	 *             the exception
	 */

	@Test
	public void getBySatusExceptionTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);

		Mockito.doNothing().when(packetArchiver).archivePacket(any());

		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService)
				.getByStatus(any(String.class));


		MessageDTO msg = new MessageDTO();
		packetDecrypterStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The Registration Status table is not accessible - {} - {}")); 
	
	}

	/**
	 * IO exception test.
	 *
	 * @throws Exception
	 *             the exception
	 */

	@Test
	public void IOExceptionTest() throws Exception {

		byte[] by = new byte[2];
		by[0] = 1;
		by[1] = 2;
		InputStream stream = new ByteArrayInputStream(by);
		list.add(dto);
		
        listAppender.start();
        fooLogger.addAppender(listAppender);

		Mockito.doNothing().when(packetArchiver).archivePacket(any());

		Mockito.when(
				registrationStatusService.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString()))
				.thenReturn(list);

		Mockito.when(adapter.getPacket(any(String.class))).thenReturn(stream);
		Mockito.when(decryptor.decrypt(any(InputStream.class), any(String.class))).thenReturn(stream);
		Mockito.doThrow(IOException.class).when(adapter).unpackPacket(any(String.class));

		MessageDTO msg = new MessageDTO();
		packetDecrypterStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The DFS Path set by the System is not accessible - null - {}")); 

	}

}
