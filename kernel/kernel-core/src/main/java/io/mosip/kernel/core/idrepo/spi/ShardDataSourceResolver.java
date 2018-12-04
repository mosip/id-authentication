package io.mosip.kernel.core.idrepo.spi;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * The Class ShardDataSourceResolver.
 *
 * @author Manoj SP
 */
public class ShardDataSourceResolver extends AbstractRoutingDataSource {
	
	/** The current shard. */
	private static ThreadLocal<Object> currentShard = new ThreadLocal<>();
	
	/* (non-Javadoc)
	 * @see org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource#determineCurrentLookupKey()
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		return ShardDataSourceResolver.getCurrentShard();
	}

	/**
	 * Sets the current shard.
	 *
	 * @param shard the new current shard
	 */
	public static void setCurrentShard(String shard) {
		currentShard.set(shard);
	}
	
	/**
	 * Gets the current shard.
	 *
	 * @return the current shard
	 */
	public static Object getCurrentShard() {
		return currentShard.get();
	}
	
	/**
	 * Reset shard config.
	 */
	public static void resetShardConfig() {
		currentShard.remove();
	}
}
