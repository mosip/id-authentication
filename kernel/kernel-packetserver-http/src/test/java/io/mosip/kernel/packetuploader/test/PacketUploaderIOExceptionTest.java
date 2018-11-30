package io.mosip.kernel.packetuploader.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.kernel.packetuploader.http.PacketUploaderHttpBootApplication;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes=PacketUploaderHttpBootApplication.class)
@TestPropertySource("classpath:/test.properties")
public class PacketUploaderIOExceptionTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	public void uploadIOException() throws IOException, Exception {
		MockMultipartFile packet = new MockMultipartFile("packet", "packet.zip", "multipart/data",
				Files.readAllBytes(new ClassPathResource("/packet.zip").getFile().toPath()));
		mockMvc.perform(MockMvcRequestBuilders.multipart("/uploads").file(packet)).andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

}
