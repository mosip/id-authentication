package io.mosip.kernel.core.idgenerator.spi;

/**
 * Interface that provides methods for Token ID generation.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public interface TokenIdGenerator<T, D> {
	/**
	 * Method when called would create random token id.
	 * 
	 * @return tokenId
	 */
	T generateId(D tspID, D uin);
}
