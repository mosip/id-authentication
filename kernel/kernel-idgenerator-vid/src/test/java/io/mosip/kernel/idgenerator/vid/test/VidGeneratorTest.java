package io.mosip.kernel.idgenerator.vid.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
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
import io.mosip.kernel.idgenerator.vid.entity.VidSeed;
import io.mosip.kernel.idgenerator.vid.entity.VidSequence;
import io.mosip.kernel.idgenerator.vid.exception.VidException;
import io.mosip.kernel.idgenerator.vid.repository.VidSeedRepository;
import io.mosip.kernel.idgenerator.vid.repository.VidSequenceRepository;

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

	@MockBean
	private VidSeedRepository seedRepository;

	@MockBean
	private VidSequenceRepository sequenceRepository;

	List<VidSeed> listOfSeed = null;
	VidSequence sequenceEntity = null;
	List<VidSeed> listOfEmptySeed = null;
	VidSequence nullSequenceEntity = null;

	@Before
	public void setUp() {
		listOfSeed = new ArrayList<>();
		VidSeed entity = new VidSeed();
		entity.setSeedNumber(random);
		listOfSeed.add(entity);
		sequenceEntity = new VidSequence();
		sequenceEntity.setSequenceNumber(key);
		listOfEmptySeed = new ArrayList<>();

	}

	@Test
	public void notNullTest() {
		when(seedRepository.findAll()).thenReturn(listOfSeed);
		when(sequenceRepository.findMaxSequence()).thenReturn(sequenceEntity);
		assertNotNull(vidGenerator.generateId());
	}

	@Test
	public void vidLengthTest() {

		when(seedRepository.findAll()).thenReturn(listOfSeed);
		when(sequenceRepository.findMaxSequence()).thenReturn(sequenceEntity);
		int vidLength = vidGenerator.generateId().length();
		assertEquals(vidLength, vidGenerator.generateId().length());
	}

	@Test
	public void vidValidationTest() {
		when(seedRepository.findAll()).thenReturn(listOfSeed);
		when(sequenceRepository.findMaxSequence()).thenReturn(sequenceEntity);
		assertTrue(vidValidator.validateId(vidGenerator.generateId()));
	}

	@Test(expected = VidException.class)
	public void seedFetchExceptionTest() {
		when(seedRepository.findAll()).thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		vidGenerator.generateId();
	}

	@Test(expected = VidException.class)
	public void seedCreationExceptionTest() {
		when(seedRepository.findAll()).thenReturn(listOfEmptySeed);
		when(sequenceRepository.findMaxSequence()).thenReturn(sequenceEntity);
		when(seedRepository.saveAndFlush(Mockito.any()))
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", new RuntimeException()));
		vidGenerator.generateId();
	}

	@Test
	public void vidEmptySeedListTest() {
		when(seedRepository.findAll()).thenReturn(listOfEmptySeed);
		when(sequenceRepository.findMaxSequence()).thenReturn(sequenceEntity);
		assertThat(vidGenerator.generateId(), isA(String.class));
	}

	@Test
	public void vidNullSequenceTest() {
		when(seedRepository.findAll()).thenReturn(listOfSeed);
		when(sequenceRepository.findMaxSequence()).thenReturn(nullSequenceEntity);
		assertThat(vidGenerator.generateId(), isA(String.class));
	}

}
