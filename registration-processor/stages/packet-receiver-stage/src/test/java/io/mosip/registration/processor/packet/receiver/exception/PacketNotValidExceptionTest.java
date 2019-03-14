package io.mosip.registration.processor.packet.receiver.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.exception.util.PlatformErrorConstants;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;

@RunWith(SpringRunner.class)
public class PacketNotValidExceptionTest {

	private static final Logger log = LoggerFactory.getLogger(PacketNotValidExceptionTest.class);
	
	@Mock
	private PacketReceiverService<MultipartFile, Boolean> packetHandlerService;

	@Test
	public void TestPacketNotValidException() {

		PacketNotValidException ex = new PacketNotValidException(
				PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage());

		Path path = Paths.get("src/test/resource/Client.zip");
		String name = "Client.zip";
		String originalFileName = "Client.zip";
		String contentType = "text/zip";
		byte[] content = null;
		try {
			content = Files.readAllBytes(path);
		} catch (IOException e1) {
			log.error(e1.getMessage());
		}
		MultipartFile file = new MockMultipartFile(name, originalFileName, contentType, content);

		Mockito.when(packetHandlerService.storePacket(file)).thenThrow(ex);
		try {

			packetHandlerService.storePacket(file);
			fail();

		} catch (PacketNotValidException e) {
			assertThat("Should throw PacketNotValid Exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getCode()));
			assertThat("Should throw PacketNotValid Exception  with correct messages",
					e.getErrorText().equalsIgnoreCase(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage()));

		}
	}
}
