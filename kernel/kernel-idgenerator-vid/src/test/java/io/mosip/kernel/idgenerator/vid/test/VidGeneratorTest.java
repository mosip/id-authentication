package io.mosip.kernel.idgenerator.vid.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.idgenerator.vid.entity.Vid;
import io.mosip.kernel.idgenerator.vid.exception.VidException;
import io.mosip.kernel.idgenerator.vid.repository.VidRepository;

/**
 * Test class for vid generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
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

	@MockBean
	private VidRepository repository;

	List<Vid> listOfEntity = null;

	@Before
	public void setUp() {

		listOfEntity = new ArrayList<>();
		Vid entity = new Vid();
		entity.setRandomValue(random);
		entity.setSequenceCounter(key);
		listOfEntity.add(entity);
	}

	@Test
	public void notNullTest() {

		when(repository.findRandomValues()).thenReturn(listOfEntity);

		assertNotNull(vidGenerator.generateId());
	}

	@Test
	public void vidLengthTest() {

		when(repository.findRandomValues()).thenReturn(listOfEntity);

		assertEquals(vidLength, vidGenerator.generateId().length());
	}

	@Test
	public void vidValidationTest() {

		when(repository.findRandomValues()).thenReturn(listOfEntity);

		assertTrue(vidValidator.validateId(vidGenerator.generateId()));
	}

	@Test(expected = VidException.class)
	public void randomValueFetchExceptionTest() {

		when(repository.findRandomValues()).thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));

		vidGenerator.generateId();
	}

	@Test(expected = VidException.class)
	public void randomValuesUpdateExceptionTest() {

		when(repository.findRandomValues()).thenReturn(listOfEntity);

		when(repository.updateCounterValue(Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));

		vidGenerator.generateId();
	}

	@Test(expected = VidException.class)
	public void firstRandomNumberGenerationExceptionTest() {
		listOfEntity.clear();

		when(repository.findRandomValues()).thenReturn(listOfEntity);

		when(repository.save(Mockito.any())).thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));

		vidGenerator.generateId();

	}

}
