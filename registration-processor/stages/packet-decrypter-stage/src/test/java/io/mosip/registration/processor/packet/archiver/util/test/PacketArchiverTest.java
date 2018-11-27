package io.mosip.registration.processor.packet.archiver.util.test;

import static org.mockito.Matchers.any;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.ServerUtil;
import io.mosip.registration.processor.packet.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.archiver.util.exception.UnableToAccessPathException;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FilePathNotAccessibleException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditRequestDto;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;

///**
// * The Class PacketArchiverTest.
// * 
// * @author M1039285
// */
@RunWith(SpringRunner.class)
public class PacketArchiverTest {

	/** The filesystem ceph adapter impl. */
	@Mock
	private FileSystemAdapter<InputStream, Boolean> filesystemCephAdapterImpl;

	/** The filemanager. */
	@Mock
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	/** The audit request builder. */
	// private AuditRequestBuilder auditRequestBuilder;
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	AuditResponseDto auditResponseDto = null;

	/** The packet archiver. */
	@InjectMocks
	private PacketArchiver packetArchiver;

	/** The source. */
	private String source = "Sample input Steam";

	/** The registration id. */
	private String registrationId = "1001";

	/**
	 * Setup.
	 *
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws SecurityException
	 *             the security exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	@Before
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		AuditLogRequestBuilder auditRequestBuilder = new AuditLogRequestBuilder();
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto = new AuditRequestDto();
		auditRequestDto.setDescription("description");
		auditRequestDto.setActionTimeStamp(LocalDateTime.now());
		auditRequestDto.setApplicationId(AuditLogConstant.MOSIP_4.toString());
		auditRequestDto.setApplicationName(AuditLogConstant.REGISTRATION_PROCESSOR.toString());
		auditRequestDto.setCreatedBy(AuditLogConstant.SYSTEM.toString());
		auditRequestDto.setEventId("eventId");
		auditRequestDto.setEventName("eventName");
		auditRequestDto.setEventType("eventType");
		auditRequestDto.setHostIp(ServerUtil.getServerUtilInstance().getServerIp());
		auditRequestDto.setHostName(ServerUtil.getServerUtilInstance().getServerName());
		auditRequestDto.setId(registrationId);
		auditRequestDto.setIdType(AuditLogConstant.REGISTRATION_ID.toString());
		auditRequestDto.setModuleId(null);
		auditRequestDto.setModuleName(null);
		auditRequestDto.setSessionUserId(AuditLogConstant.SYSTEM.toString());
		auditRequestDto.setSessionUserName(null);
		try {
			auditResponseDto = (AuditResponseDto) registrationProcessorRestService.postApi(ApiName.AUDIT, "", "",
					auditRequestDto, AuditResponseDto.class);
		} catch (ApisResourceAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Archive packet success check.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws SecurityException
	 *             the security exception
	 * @throws UnableToAccessPathException
	 *             the unable to access path exception
	 * @throws PacketNotFoundException
	 *             the packet not found exception
	 */
	@Test
	public void archivePacketSuccessCheck() throws IOException, IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException, UnableToAccessPathException, PacketNotFoundException {
		InputStream in = IOUtils.toInputStream(source, "UTF-8");
		Mockito.when(auditLogRequestBuilder.createAuditRequestBuilder("description", "eventId", "eventName",
				"eventType", registrationId)).thenReturn(auditResponseDto);
		Mockito.when(filesystemCephAdapterImpl.getPacket(registrationId)).thenReturn(in);
		Mockito.doNothing().when(filemanager).put(any(), any(), any());

		packetArchiver.archivePacket(registrationId);

	}

	/**
	 * Archive packet adapted failure check.
	 *
	 * @throws UnableToAccessPathException
	 *             the unable to access path exception
	 * @throws PacketNotFoundException
	 *             the packet not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test(expected = PacketNotFoundException.class)
	public void archivePacketAdaptedFailureCheck()
			throws UnableToAccessPathException, PacketNotFoundException, IOException {

		Mockito.when(filesystemCephAdapterImpl.getPacket(registrationId)).thenReturn(null);

		packetArchiver.archivePacket(registrationId);

	}

	/**
	 * Archive packet filemanger failure check.
	 *
	 * @throws UnableToAccessPathException
	 *             the unable to access path exception
	 * @throws PacketNotFoundException
	 *             the packet not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test(expected = UnableToAccessPathException.class)
	public void archivePacketFilemangerFailureCheck()
			throws UnableToAccessPathException, PacketNotFoundException, IOException {

		InputStream in = IOUtils.toInputStream(source, "UTF-8");
		FilePathNotAccessibleException exception = new FilePathNotAccessibleException();

		Mockito.when(filesystemCephAdapterImpl.getPacket(registrationId)).thenReturn(in);
		Mockito.doThrow(exception).when(filemanager).put(any(), any(), any());

		packetArchiver.archivePacket(registrationId);
	}

}
