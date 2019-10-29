package io.mosip.kernel.core.crypto.spi;

/**
 * This interface is specification for <b> Core Cryptographic Operations</b>.
 * 
 * The user of this interface will have all JWS basic operations like
 * {@link #jwssign(Object, Object)} ,
 * {@link #verifySignature(Object, Object)}, {@link #random()}.
 * 
 * @author Rajath
 * 
 * @since 1.0.0
 *
 * @param <R> the return type of data
 * @param <D> the type of input data
 * @param <C> the type of Certificate key
 * @param <P> the type of private key
 */
public interface JwsSpec<R, D, C, P> {
	
	/**
	 * This method is for signing data.
	 * 
	 * @param payload
	 * @param pKey
	 * @return signed string
	 */
	R jwsSign(D payload, P pKey);
	
	/**
	 * This method verifies signature.
	 * 
	 * @param sign
	 * @param cert
	 * @return boolean
	 */
	boolean verifySignature(D sign,C cert);

}
