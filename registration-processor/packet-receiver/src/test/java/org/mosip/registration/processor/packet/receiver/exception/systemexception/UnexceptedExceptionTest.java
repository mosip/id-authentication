package org.mosip.registration.processor.packet.receiver.exception.systemexception;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.registration.processor.packet.receiver.exception.systemexception.UnexpectedException;
import org.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;
import org.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

@RunWith(SpringRunner.class)
public class UnexceptedExceptionTest {
	private static final String UNEXCEPTED_EXCEPTION = "This is unexcepted exception";
	private static final Logger log = LoggerFactory.getLogger(UnexceptedExceptionTest.class);

	@Mock
	private PacketReceiverService<MultipartFile, Boolean> packetHandlerService;

	@Test
	public void TestUnexceptedException() {

		UnexpectedException ex = new UnexpectedException(UNEXCEPTED_EXCEPTION);

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
		} catch (UnexpectedException e) {
			assertThat("Should throw Unexpected Exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(IISPlatformErrorCodes.IIS_EPU_ATU_UNEXCEPTED_ERROR));
			assertThat("Should throw Unexpected Exception with correct messages",
					e.getErrorText().equalsIgnoreCase(UNEXCEPTED_EXCEPTION));
		}
	}
}
