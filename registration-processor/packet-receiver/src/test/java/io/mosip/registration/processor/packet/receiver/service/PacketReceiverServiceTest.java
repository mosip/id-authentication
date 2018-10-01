package io.mosip.registration.processor.packet.receiver.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.packet.receiver.service.impl.PacketReceiverServiceImpl;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(SpringRunner.class)
public class PacketReceiverServiceTest {

	private static final String fileExtension = ".zip";

	@Mock
	private RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	@Mock
	private RegistrationStatusDto mockDto;

	@InjectMocks
	private PacketReceiverService<MultipartFile, Boolean> packetReceiverService = new PacketReceiverServiceImpl() {
		@Override
		public String getFileExtension() {
			return fileExtension;
		}

		@Override
		public long getMaxFileSize() {
			// max file size 5 mb
			return (5 * 1024 * 1024);
		}
	};

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private MockMultipartFile mockMultipartFile;

	@Before
	public void setup() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("0000.zip").getFile());
			mockMultipartFile = new MockMultipartFile("0000.zip", "0000.zip", "mixed/multipart",
					new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	@Test
	public void packetStorageSuccessCheck() throws IOException, URISyntaxException {

		Mockito.doReturn(null).when(registrationStatusService).getRegistrationStatus("0000");

		Mockito.doNothing().when(fileManager).put(mockMultipartFile.getOriginalFilename(),
				mockMultipartFile.getInputStream(), DirectoryPathDto.LANDING_ZONE);

		boolean successResult = packetReceiverService.storePacket(mockMultipartFile);

		assertEquals(true, successResult);
	}

	@Test(expected = DuplicateUploadRequestException.class)
	public void packetStorageFailureCheck() throws IOException, URISyntaxException {

		Mockito.doReturn(mockDto).when(registrationStatusService).getRegistrationStatus("0000");

		packetReceiverService.storePacket(mockMultipartFile);
	}

}
