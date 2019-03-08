package io.mosip.kernel.idgenerator.prid.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.idvalidator.spi.PridValidator;
import io.mosip.kernel.idgenerator.prid.entity.Prid;
import io.mosip.kernel.idgenerator.prid.exception.PridException;
import io.mosip.kernel.idgenerator.prid.repository.PridRepository;

/**
 * Test class for PridGenerator class
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PridGeneratorTest {

	@Value("${mosip.kernel.prid.length}")
	private int pridLength;

	@Value("${mosip.kernel.prid.test.random-value-number}")
	private String random;

	@Value("${mosip.kernel.prid.test.random-counter-number}")
	private String key;

	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	@Autowired
	private PridGenerator<String> pridGenerator;

	@MockBean
	private PridRepository repository;

	@Autowired
	private PridValidator<String> pridValidator;

	List<Prid> listOfEntity = null;

	@Before
	public void setUp() {

		listOfEntity = new ArrayList<>();
		Prid entity = new Prid();
		entity.setRandomValue(random);
		entity.setSequenceCounter(key);
		listOfEntity.add(entity);
	}

	@Test
	public void notNullTest() {
		when(repository.findRandomValues()).thenReturn(listOfEntity);

		assertNotNull(pridGenerator.generateId());
	}

	@Test
	public void pridLengthTest() {

		when(repository.findRandomValues()).thenReturn(listOfEntity);

		assertEquals(pridLength, pridGenerator.generateId().length());
	}

	@Test
	public void pridValidationTest() {

		when(repository.findRandomValues()).thenReturn(listOfEntity);

		assertTrue(pridValidator.validateId(pridGenerator.generateId()));
	}

	@Test(expected = PridException.class)
	public void randomValueFetchExceptionTest() {

		when(repository.findRandomValues()).thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));

		pridGenerator.generateId();
	}

	@Test(expected = PridException.class)
	public void randomValuesUpdateExceptionTest() {

		when(repository.findRandomValues()).thenReturn(listOfEntity);

		when(repository.updateCounterValue(Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));

		pridGenerator.generateId();
	}

	@Test(expected = PridException.class)
	public void firstRandomNumberGenerationExceptionTest() {
		listOfEntity.clear();

		when(repository.findRandomValues()).thenReturn(listOfEntity);

		when(repository.save(Mockito.any())).thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));

		pridGenerator.generateId();

	}

}
