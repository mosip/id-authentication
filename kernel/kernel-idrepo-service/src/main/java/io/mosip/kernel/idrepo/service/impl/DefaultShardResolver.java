package io.mosip.kernel.idrepo.service.impl;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.spi.ShardResolver;

/**
 * The Class DefaultShardResolver - to resolve which shard to use to store data.
 *
 * @author Manoj SP
 */
@Component
public class DefaultShardResolver implements ShardResolver {

	private static final String SHARD1 = "shard1";

	private static final String SHARD2 = "shard2";

	private static final Pattern PATTERN = Pattern.compile("[0-4].*");

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.kernel.core.idrepo.shard.ShardResolver#getShrad(java.lang.String)
	 */
	@Override
	public String getShard(String id) throws IdRepoAppException {
		if (PATTERN.matcher(id).matches()) {
			return SHARD1;
		} else {
			return SHARD2;
		}
	}
}
