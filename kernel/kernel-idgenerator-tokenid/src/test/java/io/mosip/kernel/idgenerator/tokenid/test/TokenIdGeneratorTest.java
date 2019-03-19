package io.mosip.kernel.idgenerator.tokenid.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
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
import io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator;
import io.mosip.kernel.idgenerator.tokenid.entity.TokenIdSeed;
import io.mosip.kernel.idgenerator.tokenid.entity.TokenIdSequence;
import io.mosip.kernel.idgenerator.tokenid.exception.TokenIdGeneratorException;
import io.mosip.kernel.idgenerator.tokenid.repository.TokenIdSeedRepository;
import io.mosip.kernel.idgenerator.tokenid.repository.TokenIdSequenceRepository;

/**
 * Test class for tokenid.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TokenIdGeneratorBootApplication.class)
public class TokenIdGeneratorTest {

	@Autowired
	private TokenIdGenerator<String> tokenIdGenerator;

	@Value("${mosip.kernel.tokenid.length}")
	private Integer tokenIdLength;

	@MockBean
	private TokenIdSeedRepository seedRepository;

	@MockBean
	private TokenIdSequenceRepository counterRepository;

	@Value("${mosip.kernel.vid.test.random-value-number}")
	private String random;

	@Value("${mosip.kernel.vid.test.random-counter-number}")
	private String key;

	List<TokenIdSeed> listOfSeed = null;
	TokenIdSequence sequenceEntity = null;
	List<TokenIdSeed> listOfEmptySeed = null;

	@Before
	public void setUp() {

		listOfSeed = new ArrayList<>();
		TokenIdSeed entity = new TokenIdSeed();
		entity.setSeedNumber(random);
		listOfSeed.add(entity);
		sequenceEntity = new TokenIdSequence();
		sequenceEntity.setSequenceNumber(key);
		listOfEmptySeed = new ArrayList<>();
	}

	@Test
	public void notNullTest() {
		when(seedRepository.findAll()).thenReturn(listOfSeed);
		when(counterRepository.findMaxSequence()).thenReturn(sequenceEntity);
		assertNotNull(tokenIdGenerator.generateId());
	}

	@Test
	public void tokenIdLengthTest() {
		when(seedRepository.findAll()).thenReturn(listOfSeed);
		when(counterRepository.findMaxSequence()).thenReturn(sequenceEntity);
		int tokenLength = tokenIdGenerator.generateId().length();
		assertThat(tokenLength, is(tokenIdLength));
	}

	@Test(expected = TokenIdGeneratorException.class)
	public void tokenIdSeedFetchExceptionTest() {
		when(seedRepository.findAll()).thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		tokenIdGenerator.generateId();
	}

	@Test(expected = TokenIdGeneratorException.class)
	public void tokenIdSequnceFetchExceptionTest() {
		when(counterRepository.findMaxSequence())
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		tokenIdGenerator.generateId();
	}

	@Test(expected = TokenIdGeneratorException.class)
	public void tokenIdSequenceAdditionExceptionTest() {
		when(counterRepository.saveAndFlush(Mockito.any()))
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", new RuntimeException()));
		tokenIdGenerator.generateId();
	}

	@Test
	public void tokenIdEmptySeedListTest() {
		when(seedRepository.findAll()).thenReturn(listOfEmptySeed);
		when(counterRepository.findMaxSequence()).thenReturn(sequenceEntity);
		assertThat(tokenIdGenerator.generateId(), isA(String.class));
	}

}
