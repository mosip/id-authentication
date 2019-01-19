package io.mosip.kernel.core.idgenerator.spi;

/**
 * This interface is to generate random tokenId.
 * @author Srinivasan
 *
 */
public interface TokenIdGenerator<T> {

	/**
	 * Method when called would create random token id.
	 * @return tokenId
	 */
	T generateId();
}
