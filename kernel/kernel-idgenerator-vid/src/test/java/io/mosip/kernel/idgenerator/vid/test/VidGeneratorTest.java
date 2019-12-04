package io.mosip.kernel.idgenerator.vid.test;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.idgenerator.vid.entity.VidSeed;
import io.mosip.kernel.idgenerator.vid.entity.VidSequence;

/**
 * Test class for vid generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = VidGeneratorBootApplication.class)
public class VidGeneratorTest {

	@Value("${mosip.kernel.vid.length}")
	private int vidLength;

	@Value("${mosip.kernel.vid.test.random-value-number}")
	private String random;

	@Value("${mosip.kernel.vid.test.random-counter-number}")
	private String key;

	@Autowired
	VidGenerator<String> vidGenerator;

	@Autowired
	private VidValidator<String> vidValidator;

	@Test
	public void vidSequenceTest() {
		assertThat(vidGenerator.generateId(), isA(String.class));
	}

}