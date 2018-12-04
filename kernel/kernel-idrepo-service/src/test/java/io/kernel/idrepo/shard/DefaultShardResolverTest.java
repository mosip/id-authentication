package io.kernel.idrepo.shard;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.util.ReflectionTestUtils;

import io.kernel.idrepo.service.impl.DefaultShardResolver;

/**
 * The Class DefaultShardResolverTest.
 *
 * @author Manoj SP
 */
public class DefaultShardResolverTest {

	/** The resolver. */
	DefaultShardResolver resolver = new DefaultShardResolver();

	/** The data sources. */
	private Map<String, DataSource> dataSources = new HashMap<>();

	/** The driver manager data source 1. */
	DriverManagerDataSource driverManagerDataSource1 = new DriverManagerDataSource();

	/** The driver manager data source 2. */
	DriverManagerDataSource driverManagerDataSource2 = new DriverManagerDataSource();

	/**
	 * Before.
	 */
	@Before
	public void before() {
		dataSources.put("shard1", driverManagerDataSource1);
		dataSources.put("shard2", driverManagerDataSource2);
		ReflectionTestUtils.setField(resolver, "dataSources", dataSources);
	}

	/**
	 * Test get shrad.
	 */
	@Test
	public void testGetShrad() {
		assertEquals(driverManagerDataSource1, resolver.getShrad("1234"));
		assertEquals(driverManagerDataSource2, resolver.getShrad("5678"));
	}
}
