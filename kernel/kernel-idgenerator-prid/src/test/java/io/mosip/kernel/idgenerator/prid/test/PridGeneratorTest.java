package io.mosip.kernel.idgenerator.prid.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
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
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.idvalidator.spi.PridValidator;
import io.mosip.kernel.idgenerator.prid.entity.PridSeed;
import io.mosip.kernel.idgenerator.prid.entity.PridSequence;
import io.mosip.kernel.idgenerator.prid.exception.PridException;
import io.mosip.kernel.idgenerator.prid.repository.PridSeedRepository;
import io.mosip.kernel.idgenerator.prid.repository.PridSequenceRepository;

/**
 * Test class for PridGenerator class
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PridGeneratorBootApplication.class)
public class PridGeneratorTest {

	@Value("${mosip.kernel.prid.length}")
	private int pridLength;

	@Value("${mosip.kernel.prid.test.random-value-number}")
	private String random;

	@Value("${mosip.kernel.prid.test.random-counter-number}")
	private String key;

	@Autowired
	private PridGenerator<String> pridGenerator;

	@MockBean
	private PridSeedRepository seedRepository;

	@MockBean
	private PridSequenceRepository counterRepository;
	
	@MockBean
	private RestTemplate restTemplate;

	@Autowired
	private PridValidator<String> pridValidator;

	List<PridSeed> listOfSeed = null;
	PridSequence sequenceEntity = null;
	List<PridSeed> listOfEmptySeed = null;
	PridSequence nullSequenceEntity = null;

	@Before
	public void setUp() {
		listOfSeed = new ArrayList<>();
		PridSeed entity = new PridSeed();
		entity.setSeedNumber(random);
		listOfSeed.add(entity);
		sequenceEntity = new PridSequence();
		sequenceEntity.setSequenceNumber(key);
		listOfEmptySeed = new ArrayList<>();

	}

	@Test
	public void notNullTest() {
		when(seedRepository.findAll()).thenReturn(listOfSeed);
		when(counterRepository.findMaxSequence()).thenReturn(sequenceEntity);
		assertNotNull(pridGenerator.generateId());
	}

	@Test
	public void pridLengthTest() {
		when(seedRepository.findAll()).thenReturn(listOfSeed);
		when(counterRepository.findMaxSequence()).thenReturn(sequenceEntity);
		int tokenLength = pridGenerator.generateId().length();
		assertThat(tokenLength, is(pridLength));
	}

	@Test
	public void pridValidationTest() {
		when(seedRepository.findAll()).thenReturn(listOfSeed);
		when(counterRepository.findMaxSequence()).thenReturn(sequenceEntity);
		assertTrue(pridValidator.validateId(pridGenerator.generateId()));
	}

	@Test(expected = PridException.class)
	public void seedFetchExceptionTest() {
		when(seedRepository.findAll()).thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		pridGenerator.generateId();
	}

	@Test(expected = PridException.class)
	public void seedCreationExceptionTest() {
		when(seedRepository.findAll()).thenReturn(listOfEmptySeed);
		when(counterRepository.findMaxSequence()).thenReturn(sequenceEntity);
		when(seedRepository.saveAndFlush(Mockito.any()))
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", new RuntimeException()));
		pridGenerator.generateId();
	}

	@Test
	public void pridEmptySeedListTest() {
		when(seedRepository.findAll()).thenReturn(listOfEmptySeed);
		when(counterRepository.findMaxSequence()).thenReturn(sequenceEntity);
		assertThat(pridGenerator.generateId(), isA(String.class));
	}

	@Test
	public void pridNullSequenceTest() {
		when(seedRepository.findAll()).thenReturn(listOfSeed);
		when(counterRepository.findMaxSequence()).thenReturn(nullSequenceEntity);
		assertThat(pridGenerator.generateId(), isA(String.class));
	}
	
	

}
