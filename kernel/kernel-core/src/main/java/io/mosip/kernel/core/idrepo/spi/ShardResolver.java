package io.mosip.kernel.core.idrepo.spi;

import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;

/**
 * The Interface ShardResolver.
 *
 * @author Manoj SP
 */
public interface ShardResolver {

	/**
	 * Gets the shrad.
	 *
	 * @param id
	 *            the id
	 * @return the shrad
	 * @throws IdRepoAppException
	 */
	String getShard(String id) throws IdRepoAppException;
}
