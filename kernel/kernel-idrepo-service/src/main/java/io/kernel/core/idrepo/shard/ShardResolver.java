package io.kernel.core.idrepo.shard;

import javax.sql.DataSource;

/**
 * @author Manoj SP
 *
 */
public interface ShardResolver {
	
	DataSource getShrad(String id);
}
