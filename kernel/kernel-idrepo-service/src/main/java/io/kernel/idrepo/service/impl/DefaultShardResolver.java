package io.kernel.idrepo.service.impl;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idrepo.spi.ShardResolver;

/**
 * The Class DefaultShardResolver.
 *
 * @author Manoj SP
 */
@Component
public class DefaultShardResolver implements ShardResolver {

	/** The Constant pattern. */
	private static final Pattern pattern = Pattern.compile("[0-4].*");

	/* (non-Javadoc)
	 * @see io.kernel.core.idrepo.shard.ShardResolver#getShrad(java.lang.String)
	 */
	@Override
	public String getShard(String id) {
		if (pattern.matcher(id).matches()) {
			return "shard1";
		} else {
			return "shard2";
		}
	}
}
