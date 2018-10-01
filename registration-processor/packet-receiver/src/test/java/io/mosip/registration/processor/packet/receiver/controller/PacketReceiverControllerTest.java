package io.mosip.registration.processor.packet.receiver.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.packet.receiver.controller.PacketReceiverController;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;

@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@WebMvcTest(PacketReceiverController.class)
public class PacketReceiverControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PacketReceiverService<MultipartFile, Boolean> packetReceiverService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private MockMultipartFile mockMultipartFile, duplicateFile, invalidPacket;

	@Before
	public void setup() {

		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource("0000.zip").getFile());

		File invalidFile = new File(classLoader.getResource("1111.txt").getFile());

		try {

			this.mockMultipartFile = new MockMultipartFile("file", "0000.zip", "mixed/multipart",
					new FileInputStream(file));

			this.duplicateFile = new MockMultipartFile("file", "0000.zip", "mixed/multipart",
					new FileInputStream(file));

			this.invalidPacket = new MockMultipartFile("file", "1111.txt", "text/plain",
					new FileInputStream(invalidFile));

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());

		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		Mockito.when(packetReceiverService.storePacket(this.mockMultipartFile)).thenReturn(true);

		Mockito.when(packetReceiverService.storePacket(this.duplicateFile)).thenReturn(false);

	}

	@Test
	public void uploadSuccessTest() throws Exception {

		logger.debug("File upload Success test case");

		this.mockMvc.perform(
				MockMvcRequestBuilders.multipart("/v0.1/registration-processor/packet-receiver/registrationpackets")
						.file(this.mockMultipartFile))
				.andExpect(status().isOk());
	}

	@Test
	public void uploadFailureTest() throws Exception {

		logger.debug("Packet upload failure test case");

		this.mockMvc.perform(MockMvcRequestBuilders
				.multipart("/v0.1/registration-processor/packet-receiver/registrationpackets").file(this.duplicateFile))
				.andExpect(status().isBadRequest());
	}

}
