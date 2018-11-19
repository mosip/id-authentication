package io.mosip.kernel.packetuploader.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.packetuploader.http.config.PacketFileStorageProperties;
import io.mosip.kernel.packetuploader.http.dto.PacketUploaderResponceDTO;
import io.mosip.kernel.packetuploader.http.service.impl.PacketUploaderServiceImpl;
import io.mosip.kernel.packetuploader.http.util.PacketUploaderUtils;

@RunWith(SpringRunner.class)
public class PacketUploaderServiceImplTest {

	@Mock
	private PacketFileStorageProperties packetFileStorageProperties;
	@Mock
	private PacketUploaderUtils packetUploaderUtils;
	@InjectMocks
	private PacketUploaderServiceImpl packetUploaderServiceImpl;

	@Test
	public void testStorePacket() throws IOException, io.mosip.kernel.core.exception.IOException {
		MultipartFile file = new MockMultipartFile("testFile.zip", "testFile.zip", null, new byte[1100]);
		doNothing().when(packetUploaderUtils).check(file);
		doReturn(new ClassPathResource("/").getFile().toPath().toString()).when(packetFileStorageProperties)
				.getUploadDir();
		assertThat(packetUploaderServiceImpl.upload(file), isA(PacketUploaderResponceDTO.class));
	}

}
