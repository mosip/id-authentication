package io.kernel.core.idrepo.shard;

import javax.sql.DataSource;

/**
 * The Interface ShardResolver.
 *
 * @author Manoj SP
 */
public interface ShardResolver {

	/**
	 * Gets the shrad.
	 *
	 * @param id the id
	 * @return the shrad
	 */
	DataSource getShrad(String id);
}
