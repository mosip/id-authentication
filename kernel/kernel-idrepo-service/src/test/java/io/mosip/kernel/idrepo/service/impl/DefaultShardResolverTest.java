package io.mosip.kernel.idrepo.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.idrepo.service.impl.DefaultShardResolver;

/**
 * The Class DefaultShardResolverTest.
 *
 * @author Manoj SP
 */
public class DefaultShardResolverTest {

	/** The resolver. */
	DefaultShardResolver resolver = new DefaultShardResolver();

	/**
	 * Test get shrad.
	 * @throws IdRepoAppException 
	 */
	@Test
	public void testGetShrad() throws IdRepoAppException {
		assertEquals("shard1", resolver.getShard("1234"));
		assertEquals("shard2", resolver.getShard("5678"));
	}
}
