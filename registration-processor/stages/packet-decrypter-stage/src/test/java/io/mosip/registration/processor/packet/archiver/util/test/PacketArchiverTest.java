//package io.mosip.registration.processor.packet.archiver.util.test;
//
//import static org.mockito.Matchers.any;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Field;
//
//import org.apache.commons.io.IOUtils;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.test.context.junit4.SpringRunner;
//
////import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
////import io.mosip.kernel.auditmanager.request.AuditRequestDto;
////import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
//import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
//import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
//import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
//import io.mosip.registration.processor.packet.archiver.util.PacketArchiver;
//import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
//import io.mosip.registration.processor.packet.archiver.util.exception.UnableToAccessPathException;
//import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
//import io.mosip.registration.processor.packet.manager.exception.FilePathNotAccessibleException;
//
///**
// * The Class PacketArchiverTest.
// * 
// * @author M1039285
// */
//@RunWith(SpringRunner.class)
//public class PacketArchiverTest {
//
//	/** The filesystem ceph adapter impl. */
//	@Mock
//	private FileSystemAdapter<InputStream, Boolean> filesystemCephAdapterImpl;
//
//	/** The filemanager. */
//	@Mock
//	protected FileManager<DirectoryPathDto, InputStream> filemanager;
//
//	/** The audit request builder. */
//	@Mock
//	private AuditRequestBuilder auditRequestBuilder;
//
//	/** The audit handler. */
//	@Mock
//	private AuditHandler<AuditRequestDto> auditHandler;
//
//	/** The packet archiver. */
//	@InjectMocks
//	private PacketArchiver packetArchiver;
//
//	/** The source. */
//	private String source = "Sample input Steam";
//
//	/** The registration id. */
//	private String registrationId = "1001";
//
//	/**
//	 * Setup.
//	 *
//	 * @throws NoSuchFieldException
//	 *             the no such field exception
//	 * @throws SecurityException
//	 *             the security exception
//	 * @throws IllegalArgumentException
//	 *             the illegal argument exception
//	 * @throws IllegalAccessException
//	 *             the illegal access exception
//	 */
//	@Before
//	public void setup()
//			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		
//		Mockito.when(auditHandler.writeAudit(any())).thenReturn(true);
//		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
//		AuditRequestDto auditRequest1 = new AuditRequestDto();
//
//		Field f = PacketArchiver.class.getDeclaredField("auditRequestBuilder");
//		f.setAccessible(true);
//		f.set(packetArchiver, auditRequestBuilder);
//		Field f1 = AuditRequestBuilder.class.getDeclaredField("auditRequest");
//		f1.setAccessible(true);
//		f1.set(auditRequestBuilder, auditRequest1);
//	}
//
//	/**
//	 * Archive packet success check.
//	 *
//	 * @throws IOException
//	 *             Signals that an I/O exception has occurred.
//	 * @throws IllegalArgumentException
//	 *             the illegal argument exception
//	 * @throws IllegalAccessException
//	 *             the illegal access exception
//	 * @throws NoSuchFieldException
//	 *             the no such field exception
//	 * @throws SecurityException
//	 *             the security exception
//	 * @throws UnableToAccessPathException
//	 *             the unable to access path exception
//	 * @throws PacketNotFoundException
//	 *             the packet not found exception
//	 */
//	@Test
//	public void archivePacketSuccessCheck() throws IOException, IllegalArgumentException, IllegalAccessException,
//			NoSuchFieldException, SecurityException, UnableToAccessPathException, PacketNotFoundException {
//		InputStream in = IOUtils.toInputStream(source, "UTF-8");
//		Mockito.when(filesystemCephAdapterImpl.getPacket(registrationId)).thenReturn(in);
//		Mockito.doNothing().when(filemanager).put(any(), any(),
//				any());
//
//		packetArchiver.archivePacket(registrationId);
//
//	}
//
//	/**
//	 * Archive packet adapted failure check.
//	 *
//	 * @throws UnableToAccessPathException
//	 *             the unable to access path exception
//	 * @throws PacketNotFoundException
//	 *             the packet not found exception
//	 * @throws IOException
//	 *             Signals that an I/O exception has occurred.
//	 */
//	@Test(expected = PacketNotFoundException.class)
//	public void archivePacketAdaptedFailureCheck()
//			throws UnableToAccessPathException, PacketNotFoundException, IOException {
//
//		Mockito.when(filesystemCephAdapterImpl.getPacket(registrationId)).thenReturn(null);
//
//		packetArchiver.archivePacket(registrationId);
//
//	}
//
//	/**
//	 * Archive packet filemanger failure check.
//	 *
//	 * @throws UnableToAccessPathException
//	 *             the unable to access path exception
//	 * @throws PacketNotFoundException
//	 *             the packet not found exception
//	 * @throws IOException
//	 *             Signals that an I/O exception has occurred.
//	 */
//	@Test(expected = UnableToAccessPathException.class)
//	public void archivePacketFilemangerFailureCheck()
//			throws UnableToAccessPathException, PacketNotFoundException, IOException {
//
//		InputStream in = IOUtils.toInputStream(source, "UTF-8");
//		FilePathNotAccessibleException exception = new FilePathNotAccessibleException();
//
//		Mockito.when(filesystemCephAdapterImpl.getPacket(registrationId)).thenReturn(in);
//		Mockito.doThrow(exception).when(filemanager).put(any(), any(), any());
//
//		packetArchiver.archivePacket(registrationId);
//	}
//
//}
