package io.kernel.core.idrepo.shard.impl;

import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import io.kernel.core.idrepo.shard.ShardResolver;

/**
 * The Class DefaultShardResolver.
 *
 * @author Manoj SP
 */
@Component
public class DefaultShardResolver implements ShardResolver {

	/** The Constant pattern. */
	private static final Pattern pattern = Pattern.compile("[0-4].*");

	/** The data sources. */
	@Resource
	private Map<String, DataSource> dataSources;

	/* (non-Javadoc)
	 * @see io.kernel.core.idrepo.shard.ShardResolver#getShrad(java.lang.String)
	 */
	@Override
	public DataSource getShrad(String id) {
		if (pattern.matcher(id).matches()) {
			return dataSources.get("shard1");
		} else {
			return dataSources.get("shard2");
		}
	}
}
