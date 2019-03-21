package io.mosip.kernel.core.idgenerator.spi;

/**
 * Interface that provides methods for Token ID generation.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public interface TokenIdGenerator<T> {
	/**
	 * Method when called would create random token id.
	 * 
	 * @return tokenId
	 */
	T generateId();
}
