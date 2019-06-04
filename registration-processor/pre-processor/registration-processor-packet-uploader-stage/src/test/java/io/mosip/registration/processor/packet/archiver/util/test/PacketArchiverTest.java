package io.mosip.registration.processor.packet.archiver.util.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.JschConnectionException;
import io.mosip.registration.processor.core.exception.SftpFileOperationException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.packet.dto.SftpJschConnectionDto;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileSystemManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.ServerUtil;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.uploader.exception.PacketNotFoundException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditRequestDto;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;

/**
 * @author M1039285
 *
 */
@RunWith(SpringRunner.class)
public class PacketArchiverTest {

	/** The filesystem adapter impl. */
	@Mock
	private FileSystemManager filesystemAdapterImpl;

	/** The filemanager. */
	@Mock
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	/** The audit request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	@Mock
	private Environment env;

	AuditResponseDto auditResponseDto = null;

	/** The packet archiver. */
	@InjectMocks
	private PacketArchiver packetArchiver;


	/** The source. */
	private String source = "Sample input Steam";

	/** The registration id. */
	private String registrationId = "1001";

	@Mock
	private SftpJschConnectionDto jschConnectionDto;

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
		//ReflectionTestUtils.setField(packetArchiver,"dmzPort", "5161");
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto = new AuditRequestDto();
		auditRequestDto.setDescription("description");
		auditRequestDto.setActionTimeStamp(LocalDateTime.now().toString());
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
		jschConnectionDto = new SftpJschConnectionDto();
		try {
			auditResponseDto = (AuditResponseDto) registrationProcessorRestService.postApi(ApiName.DMZAUDIT, "", "",
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
	 * @throws SftpFileOperationException
	 * @throws JschConnectionException
	 */
	@Test
	public void archivePacketSuccessCheck() throws IOException, IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException, PacketNotFoundException, JschConnectionException, SftpFileOperationException {
		InputStream in = IOUtils.toInputStream(source, "UTF-8");
		ResponseWrapper<AuditResponseDto> responseWrapper = new ResponseWrapper<>();
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		responseWrapper.setResponse(auditResponseDto);
		Mockito.when(auditLogRequestBuilder.createAuditRequestBuilder("description", "eventId", "eventName",
				"eventType", registrationId, ApiName.DMZAUDIT)).thenReturn(responseWrapper);
		Mockito.doNothing().when(filemanager).put(any(), any(), any());
        Mockito.when(filemanager.copy(any(),any(),any(),any())).thenReturn(Boolean.TRUE);
       assertTrue(packetArchiver.archivePacket(registrationId,jschConnectionDto));

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
	 * @throws SftpFileOperationException
	 * @throws JschConnectionException
	 */
	@Test(expected = PacketNotFoundException.class)
	public void archivePacketAdaptedFailureCheck() throws PacketNotFoundException, IOException, JschConnectionException, SftpFileOperationException {
		InputStream in = IOUtils.toInputStream(source, "UTF-8");
		ResponseWrapper<AuditResponseDto> responseWrapper = new ResponseWrapper<>();
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		responseWrapper.setResponse(auditResponseDto);
		Mockito.when(auditLogRequestBuilder.createAuditRequestBuilder("description", "eventId", "eventName",
				"eventType", registrationId, ApiName.DMZAUDIT)).thenReturn(responseWrapper);
		Mockito.doNothing().when(filemanager).put(any(), any(), any());
        Mockito.when(filemanager.copy(any(),any(),any(),any())).thenReturn(Boolean.FALSE);
        assertFalse(packetArchiver.archivePacket(registrationId,jschConnectionDto));

	}

}