package io.mosip.kernel.core.crypto.spi;

/**
 * This interface is base for <b> Core Cryptographic </b> operations for MOSIP.
 * 
 * The user of this interface will have all cryptographic basic operations 
 * like {@link #asymmetricEncrypt(Object, Object)} , {@link #asymmetricDecrypt(Object, Object)} ,
 * {@link #symmetricEncrypt(Object, Object)} , {@link #symmetricEncrypt(Object, Object)} ,
 * {@link #hash(Object, Object)} , {@link #sign(Object, Object)} , {@link #verifySignature(Object, Object, Object)},
 * {@link #random()}.
 *  
 * @author Urvil Joshi
 *
 * @param <R> the return type of data
 * @param <D> the type of input data
 * @param <S> the type of symmetric key
 * @param <P> the type of public key
 * @param <K> the type of private key
 * @param <T> the type of signature
 * @param <U> the type of random
 * @param <W> the input type of hash
 * 
 */
public interface CryptoCore<R, D, S, P, K, T, U, W> {

	/** This method is used for core <b> Symmetric Encryption </b>.
	 * Symmetric Encryption is described in <a href="https://en.wikipedia.org/wiki/Public-key_cryptography">
	 * 
	 * @param key Symmetric Key as key
	 * @param data data to encrypt
	 * @return encrypted data
	 */
	R symmetricEncrypt(S key, D data);

	R symmetricDecrypt(S key, D data);

	R asymmetricEncrypt(P key, D data);

	R asymmetricDecrypt(K key, D data);

	R hash(W data, D salt);

	T sign(D data, K privateKey);

	boolean verifySignature(D data, T signature, P publicKey);

	U random();

}
