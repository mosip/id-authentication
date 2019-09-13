package io.mosip.kernel.core.crypto.spi;

/**
 * This interface is specification for key generation. The user of this
 * interface will have methods for generation of both <b>Symmetric and
 * Asymmetric keys</b>.
 * 
 * Symmetric encryption is a type of encryption where only one key (a secret
 * key) is used to both encrypt and decrypt electronic information. The entities
 * communicating via symmetric encryption must exchange the key so that it can
 * be used in the decryption process. This encryption method differs from
 * asymmetric encryption where a pair of keys, one public and one private, is
 * used to encrypt and decrypt messages.
 * 
 * Symmetric Encryption is described in <a href=
 * "https://en.wikipedia.org/wiki/Symmetric-key_algorithm">Symmetric-key_algorithm</a>.
 * 
 * Asymmetric Encryption is described in <a href=
 * "https://en.wikipedia.org/wiki/Public-key_cryptography">Public-key_cryptography</a>.
 * 
 * @author Urvil Joshi
 *
 * @param <S> the type of Symmetric Key
 * @param <P> the type of Asymmetric Key
 */
public interface KeyGeneratorSpec<S, P> {

	/**
	 * This method is responsible for generating <>Symmetric Key</b>
	 * 
	 * @return generated Symmetric key
	 */
	S generateSymmetricKey();

	/**
	 * This method is responsible for generating <b>Asymmetric Key Pairs</b>
	 * 
	 * @return generated Asymmetric Key
	 */
	P generateAsymmetricKey();

}