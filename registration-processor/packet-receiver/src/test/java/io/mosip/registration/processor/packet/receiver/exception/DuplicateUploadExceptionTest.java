package io.mosip.registration.processor.packet.receiver.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

import io.mosip.registration.processor.status.dto.SyncResponseDto;
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

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.packet.receiver.service.impl.PacketReceiverServiceImpl;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

@RunWith(SpringRunner.class)
public class DuplicateUploadExceptionTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String fileExtension = ".zip";

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
    private SyncRegistrationService syncRegistrationService;

	SyncRegistrationEntity regEntity;
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

	@Mock
	private InternalRegistrationStatusDto dto;

	@Test
	public void TestDuplicateUploadException() {
		MockMultipartFile mockMultipartFile = null;
		regEntity=new SyncRegistrationEntity();
		regEntity.setCreateDateTime(LocalDateTime.now());
		regEntity.setCreatedBy("Mosip");
		regEntity.setId("001");
		regEntity.setIsActive(true);
		regEntity.setLangCode("eng");
		regEntity.setRegistrationId("0000");
		regEntity.setRegistrationType("new");
		regEntity.setStatusCode("NEW_REGISTRATION");
		regEntity.setStatusComment("registration begins");
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

		Mockito.doReturn(dto).when(registrationStatusService).getRegistrationStatus("0000");
		when(syncRegistrationService.isPresent(anyString())).thenReturn(true);
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		try {
			packetReceiverService.storePacket(mockMultipartFile);
			fail();
		} catch (DuplicateUploadRequestException e) {
			assertThat("Should throw duplicate exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(PlatformErrorMessages.RPR_PKR_DUPLICATE_PACKET_RECIEVED.getCode()));
			assertThat("Should throw duplicate exception with correct messages",
					e.getErrorText().equalsIgnoreCase(PlatformErrorMessages.RPR_PKR_DUPLICATE_PACKET_RECIEVED.getMessage()));

		}
	}

}
