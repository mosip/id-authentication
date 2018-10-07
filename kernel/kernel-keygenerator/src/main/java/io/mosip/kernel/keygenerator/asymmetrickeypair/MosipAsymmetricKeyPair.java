package io.mosip.kernel.keygenerator.asymmetrickeypair;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class has {@link #publicKey} and {@link #privateKey}
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MosipAsymmetricKeyPair {
	/**
	 * Private key
	 */
	private byte[] publicKey;
	/**
	 * Public Key
	 */
	private byte[] privateKey;
}
