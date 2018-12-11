package io.mosip.kernel.idrepo.test.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.idrepo.service.impl.DefaultShardResolver;

/**
 * The Class IdRepoDaoTest.
 *
 * @author Manoj SP
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
public class IdRepoDaoTest {

	/** The env. */
	@Autowired
	private Environment env;

	/** The shard resolver. */
	@Mock
	private DefaultShardResolver shardResolver;

	/** The dao. */
//	@InjectMocks
//	IdRepoDaoImpl dao;

	/**
	 * Setup.
	 */
	@Before
	public void setup() {
//		ReflectionTestUtils.setField(dao, "env", env);
	}

	/**
	 * Test jet jdbc template.
	 */
	@Test
	public void testJetJdbcTemplate() {
	}

	/**
	 * Test add identity exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityException() throws IdRepoAppException {
	}

	/**
	 * Test retrieve identity exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityException() throws IdRepoAppException {
	}

	/**
	 * Test update idenity info exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testUpdateIdenityInfoException() throws IdRepoAppException {
	}

	/**
	 * Test update idenity status exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testUpdateIdenityStatusException() throws IdRepoAppException {
	}
}
