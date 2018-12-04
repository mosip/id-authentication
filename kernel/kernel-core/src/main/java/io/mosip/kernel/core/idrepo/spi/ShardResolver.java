package io.mosip.kernel.core.idrepo.spi;

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
	String getShrad(String id);
}
