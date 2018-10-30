package io.mosip.kernel.vidgenerator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idgenerator.exception.InValidUinException;
import io.mosip.kernel.core.idgenerator.spi.VidGenerator;

/**
 * @author M1043226
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class VidGeneratorTest {
	@Value("${mosip.kernel.vid.length}")
	private int vidLength;
	@Autowired
	VidGenerator<String> vidGenerator;

	@Test(expected = InValidUinException.class)
	public void UinNotValidExceptionTest() {
		String uin = null;
		vidGenerator.generateId(uin);
	}

	@Test
	public void generateIdNullTest() {
		String result = vidGenerator.generateId("89078997897893");
		assertNotNull(result);
	}

	@Test
	public void generateIdLengthTest() {
		String result = vidGenerator.generateId("89078997897893");
		assertEquals(vidLength, result.length());
	}

	@Test
	public void existingVidTest() {
		String result = vidGenerator.generateId("89078997897884");
		assertNotNull(result);
	}
}
