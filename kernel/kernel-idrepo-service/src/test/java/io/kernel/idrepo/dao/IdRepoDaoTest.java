package io.kernel.idrepo.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.kernel.idrepo.dao.impl.IdRepoDaoImpl;
import io.kernel.idrepo.exception.IdRepoAppException;
import io.kernel.idrepo.shard.impl.DefaultShardResolver;

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
	@InjectMocks
	IdRepoDaoImpl dao;

	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		ReflectionTestUtils.setField(dao, "env", env);
	}

	/**
	 * Test jet jdbc template.
	 */
	@Test
	public void testJetJdbcTemplate() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		when(shardResolver.getShrad(any())).thenReturn(dataSource);
		assertEquals(new JdbcTemplate(dataSource).getDataSource(), dao.getJdbcTemplate("1234").getDataSource());
	}

	/**
	 * Test add identity exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityException() throws IdRepoAppException {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		when(shardResolver.getShrad(any())).thenReturn(dataSource);
		dao.addIdentity("1234", "4321", new byte[] {});
	}

	/**
	 * Test retrieve identity exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityException() throws IdRepoAppException {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		when(shardResolver.getShrad(any())).thenReturn(dataSource);
		dao.retrieveIdentity("1234");
	}

	/**
	 * Test update idenity info exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testUpdateIdenityInfoException() throws IdRepoAppException {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		when(shardResolver.getShrad(any())).thenReturn(dataSource);
		dao.updateIdenityInfo("1234", new byte[] {});
	}

	/**
	 * Test update idenity status exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testUpdateIdenityStatusException() throws IdRepoAppException {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		when(shardResolver.getShrad(any())).thenReturn(dataSource);
		dao.updateUinStatus("1234", "status");
	}
}
