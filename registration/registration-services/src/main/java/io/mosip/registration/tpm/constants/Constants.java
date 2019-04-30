package io.mosip.registration.tpm.constants;

/**
 * The constants used by TPM classes
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Constants {

	private Constants() {
	}

	public static final byte[] NULL_VECTOR = new byte[0];

	// Sign
	public static final String PUBLIC_KEY_FILE_NAME = "publicKey.dat";
	public static final String SIGNED_DATA_FILE_NAME = "signedData.dat";
	public static final String PUBLIC_PART_FILE_NAME = "publicPart.dat";

	public static final byte[] DATA_TO_SIGN = "abc".getBytes();

	// Asymmetric Encryption or Decryption
	public static final String ASYMMETRIC_ENCRYPTED_DATA_FILE_NAME = "asymmetricEncryptedData.dat";

	// Symmetric Encryption or Decryption
	public static final String SYMMETRIC_ENCRYPTED_DATA_FILE_NAME = "symmetricEncryptedData.dat";
	public static final byte[] INITIAL_VECTOR = new byte[16];

	public static final byte[] DATA_TO_ENCRYPT = "samplestring".getBytes();

}
