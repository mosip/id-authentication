package io.mosip.kernel.packetuploader.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import io.mosip.kernel.core.exception.DirectoryNotEmptyException;
import io.mosip.kernel.packetuploader.http.PacketUploaderHttpBootApplication;
import io.mosip.kernel.packetuploader.http.constant.PacketUploaderExceptionConstant;
import io.mosip.kernel.packetuploader.http.exception.PacketLocationSecurityException;
import io.mosip.kernel.packetuploader.http.service.PacketUploaderService;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes=PacketUploaderHttpBootApplication.class)
@TestPropertySource("classpath:/application.properties")
public class PacketUploaderExceptionTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	PacketUploaderService service;

	@Test
	public void uploadFilSizeException() throws IOException, Exception {
		MockMultipartFile packet = new MockMultipartFile("packet", "packet4.zip", "multipart/data",
				Files.readAllBytes(new ClassPathResource("/packet4.zip").getFile().toPath()));
		when(service.upload(packet)).thenThrow(MaxUploadSizeExceededException.class);
		mockMvc.perform(MockMvcRequestBuilders.multipart("/uploads").file(packet)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].code", isA(String.class)));
	}

	@Test
	public void uploadFilSizeMinException() throws IOException, Exception {
		MockMultipartFile packet = new MockMultipartFile("packet", "packet4.zip", "multipart/data",
				Files.readAllBytes(new ClassPathResource("/aa.txt").getFile().toPath()));
		when(service.upload(packet)).thenThrow(MaxUploadSizeExceededException.class);
		mockMvc.perform(MockMvcRequestBuilders.multipart("/uploads").file(packet)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].code", isA(String.class)));
	}

	@Test
	public void uploadFileDirectoryException() throws IOException, Exception {
		MockMultipartFile packet = new MockMultipartFile("packet", "packet.zip", "multipart/data",
				Files.readAllBytes(new ClassPathResource("/packet.zip").getFile().toPath()));
		when(service.upload(packet)).thenThrow(new DirectoryNotEmptyException(PacketUploaderExceptionConstant.MOSIP_DIRECTORY_NOT_EMPTY_FILE_LOCATION_EXCEPTION.getErrorCode(),
				PacketUploaderExceptionConstant.MOSIP_DIRECTORY_NOT_EMPTY_FILE_LOCATION_EXCEPTION.getErrorMessage(),null));
		mockMvc.perform(MockMvcRequestBuilders.multipart("/uploads").file(packet)).andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.errors[0].code", isA(String.class)));
	}

	@Test
	public void uploadSecurityException() throws IOException, Exception {
		MockMultipartFile packet = new MockMultipartFile("packet", "packet.zip", "multipart/data",
				Files.readAllBytes(new ClassPathResource("/packet.zip").getFile().toPath()));
		when(service.upload(packet)).thenThrow( new PacketLocationSecurityException(PacketUploaderExceptionConstant.MOSIP_SECURITY_FILE_LOCATION_EXCEPTION, null));
		mockMvc.perform(MockMvcRequestBuilders.multipart("/uploads").file(packet)).andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.errors[0].code", isA(String.class)));
	}

}
