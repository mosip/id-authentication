package io.mosip.kernel.core.test.idrepo.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.idrepo.spi.ShardDataSourceResolver;

/**
 * @author Manoj SP
 *
 */
public class ShardDataSourceResolverTest {

	ShardDataSourceResolver shardResolver = new ShardDataSourceResolver();

	@Test
	public void testDetermineCurrentLookupKey() {
		ShardDataSourceResolver.setCurrentShard("shard1");
		assertEquals("shard1", ReflectionTestUtils.invokeMethod(shardResolver, "determineCurrentLookupKey"));
	}
	
	@Test
	public void testResetShardConfig() {
		ShardDataSourceResolver.setCurrentShard("shard1");
		ShardDataSourceResolver.resetShardConfig();
		assertTrue(Objects.isNull(ReflectionTestUtils.invokeMethod(shardResolver, "determineCurrentLookupKey")));
		ShardDataSourceResolver.setCurrentShard("shard2");
		assertEquals("shard2", ReflectionTestUtils.invokeMethod(shardResolver, "determineCurrentLookupKey"));
	}
}
