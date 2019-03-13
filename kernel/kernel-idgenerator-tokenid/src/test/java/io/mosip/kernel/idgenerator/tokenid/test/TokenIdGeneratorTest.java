package io.mosip.kernel.idgenerator.tokenid.test;

import static org.hamcrest.CoreMatchers.is;
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
import io.mosip.kernel.idgenerator.tokenid.entity.TokenId;
import io.mosip.kernel.idgenerator.tokenid.exception.TokenIdGeneratorException;
import io.mosip.kernel.idgenerator.tokenid.repository.TokenIdRepository;

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
	private TokenIdRepository repository;

	@Value("${mosip.kernel.vid.test.random-value-number}")
	private String random;

	@Value("${mosip.kernel.vid.test.random-counter-number}")
	private String key;

	List<TokenId> listOfEntity = null;

	@Before
	public void setUp() {

		listOfEntity = new ArrayList<>();
		TokenId entity = new TokenId();
		entity.setRandomValue(random);
		entity.setSequenceCounter(key);
		listOfEntity.add(entity);
	}

	@Test
	public void notNullTest() {
		when(repository.findRandomValues()).thenReturn(listOfEntity);

		assertNotNull(tokenIdGenerator.generateId());
	}

	@Test
	public void tokenIdLengthTest() {
		when(repository.findRandomValues()).thenReturn(listOfEntity);
		int tokenLength = tokenIdGenerator.generateId().length();
		assertThat(tokenLength, is(tokenIdLength));
	}

	@Test(expected = TokenIdGeneratorException.class)
	public void tokenIdApiExceptionTestWhenTSPIsEmpty() {
		when(repository.findRandomValues()).thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		tokenIdGenerator.generateId();
	}

	@Test(expected = TokenIdGeneratorException.class)
	public void tokenIdApiExceptionTestWhenUINIsEmpty() {
		when(repository.findRandomValues()).thenReturn(listOfEntity);
		when(repository.updateCounterValue(Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		tokenIdGenerator.generateId();
	}

}
