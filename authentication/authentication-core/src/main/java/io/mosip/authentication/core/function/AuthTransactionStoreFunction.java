package io.mosip.authentication.core.function;

import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Interface AuthTransactionStoreFunction.
 * 
 * @author Loganathan S
 */
@FunctionalInterface
public interface AuthTransactionStoreFunction {
	
	/**
	 * Store auth transaction.
	 *
	 * @param metadata the metadata
	 * @param requestSignature the request signature
	 * @param responseSignature the response signature
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	void storeAuthTransaction(Map<String, Object> metadata, String requestSignature,
			String responseSignature) throws IdAuthenticationAppException;

}
