package io.mosip.kernel.core.crypto.spi;

/**
 * This interface is for <b> Core Cryptographic </b> operations for MOSIP.
 * 
 * The user of this interface will have all cryptographic basic operations like
 * {@link #asymmetricEncrypt(Object, Object)} ,
 * {@link #asymmetricDecrypt(Object, Object)} ,
 * {@link #symmetricEncrypt(Object, Object)} ,
 * {@link #symmetricEncrypt(Object, Object)} , {@link #hash(Object, Object)} ,
 * {@link #sign(Object, Object)} ,
 * {@link #verifySignature(Object, Object, Object)}, {@link #random()}.
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

	/**
	 * This method is used for core <b> Symmetric Encryption </b>. Symmetric
	 * Encryption is described in <a href=
	 * "https://en.wikipedia.org/wiki/Symmetric-key_algorithm">Symmetric-key_algorithm</a>
	 * 
	 * @param key  Symmetric Key as key
	 * @param data data to encrypt
	 * @return encrypted data
	 */
	R symmetricEncrypt(S key, D data);

	/**
	 * This method is used for core <b> Symmetric Decryption </b>. Symmetric
	 * Encryption is described in <a href=
	 * "https://en.wikipedia.org/wiki/Symmetric-key_algorithm">Symmetric-key_algorithm</a>
	 * 
	 * @param key  Symmetric Key as key
	 * @param data data to decrypt
	 * @return decrypted data
	 */
	R symmetricDecrypt(S key, D data);

	/**
	 * This method is used for core <b> Asymmetric Encryption </b>.
	 * 
	 * Asymmetric Encryption is described in <a href=
	 * "https://en.wikipedia.org/wiki/Public-key_cryptography">Public-key_cryptography</a>
	 * 
	 * @param key  Public Key as key
	 * @param data data to encrypt
	 * @return encrypted data
	 */
	R asymmetricEncrypt(P key, D data);

	/**
	 * This method is used for core <b> Asymmetric Decryption </b>.
	 * 
	 * Asymmetric Encryption is described in <a href=
	 * "https://en.wikipedia.org/wiki/Public-key_cryptography">Public-key_cryptography</a>
	 * 
	 * @param key  Private Key as key
	 * @param data data to decrypt
	 * @return decrypted data
	 */
	R asymmetricDecrypt(K key, D data);

	/**
	 * This method is used as core <b> Cryptographic hash </b>
	 * 
	 * Cryptographic hashing is described in <a href=
	 * "https://en.wikipedia.org/wiki/Cryptographic_hash_function">Cryptographic_hash_function</a>
	 * 
	 * There are some Standards for password hashing can be found at <a href =
	 * "https://www.owasp.org/index.php/Password_Storage_Cheat_Sheet">OWASP Password
	 * Storage Sheet</a>
	 * 
	 * Iterations should be included by implementation.
	 * 
	 * The iterations specifies how many times the hash executes its underlying
	 * algorithm. A higher value is safer. You need to experiment on hardware
	 * equivalent to your production systems. As a starting point, find a value that
	 * requires one half second to execute. Scaling to huge number of users is
	 * beyond the scope of this document. Remember to save the value of iterations
	 * with the hashed password!
	 * 
	 * @param data data to hash
	 * @param salt salt argument should be random data and vary for each user. It
	 *             should be at least 32 bytes long.
	 * @return hashed data
	 */
	R hash(W data, D salt);

	/**
	 * This method is responsible for core <b> Digital Signature </b>
	 * 
	 * A digital signature is a mathematical technique used to validate the
	 * authenticity and integrity of a message, software or digital document. As the
	 * digital equivalent of a handwritten signature or stamped seal, a digital
	 * signature offers far more inherent security, and it is intended to solve the
	 * problem of tampering and impersonation in digital communications.
	 * 
	 * Digital signatures can provide the added assurances of evidence of origin,
	 * identity and status of an electronic document, transaction or message and can
	 * acknowledge informed consent by the signer.
	 * 
	 * In many countries, including the United States, digital signatures are
	 * considered legally binding in the same way as traditional document
	 * signatures.
	 * 
	 * Digital Signature is described in <a href=
	 * "https://en.wikipedia.org/wiki/Digital_signature">Digital_signature</a>
	 * 
	 * @param data       data to sign
	 * @param privateKey privateKey of owner
	 * @return signed data
	 */
	T sign(D data, K privateKey);

	/**
	 * @param data
	 * @param signature
	 * @param publicKey
	 * @return
	 */
	boolean verifySignature(D data, T signature, P publicKey);

	/**
	 * @return
	 */
	U random();

}
