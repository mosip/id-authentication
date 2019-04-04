package io.mosip.kernel.idrepo.test.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
	 * 
	 * @throws IdRepoAppException
	 */
	@Test
	public void testGetShrad() throws IdRepoAppException {
		assertEquals("shard1", resolver.getShard("1234"));
		assertEquals("shard2", resolver.getShard("5678"));
	}
}
