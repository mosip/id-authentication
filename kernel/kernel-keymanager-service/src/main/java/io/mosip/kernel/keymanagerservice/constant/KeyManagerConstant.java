package io.mosip.kernel.keymanagerservice.constant;

/**
 * Constants for Keymanager
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class KeyManagerConstant {

	/**
	 * Private constructor for KeyManagerConstant
	 */
	private KeyManagerConstant() {
	}

	/**
	 * The constant Whitespace
	 */
	public static final String WHITESPACE = " ";

	/**
	 * The constant EMPTY
	 */
	public static final String EMPTY = "";

	/**
	 * The constant keyalias
	 */
	public static final String KEYALIAS = "keyAlias";

	/**
	 * The constant currentkeyalias
	 */
	public static final String CURRENTKEYALIAS = "currentKeyAlias";

	/**
	 * The constant timestamp
	 */
	public static final String TIMESTAMP = "timestamp";

	/**
	 * The constant sessionID
	 */
	public static final String SESSIONID = "sessionId";

	/**
	 * The constant applicationId
	 */
	public static final String APPLICATIONID = "applicationId";

	/**
	 * The constant referenceId
	 */
	public static final String REFERENCEID = "referenceId";

	/**
	 * The constant Request received to getPublicKey
	 */
	public static final String GETPUBLICKEY = "Request received to getPublicKey";

	/**
	 * The constant Getting public key from DB Store
	 */
	public static final String GETPUBLICKEYDB = "Getting public key from DB Store";

	/**
	 * The constant Getting public key from SoftHSM
	 */
	public static final String GETPUBLICKEYHSM = "Getting public key from SoftHSM";

	/**
	 * The constant Getting key alias
	 */
	public static final String GETALIAS = "Getting key alias";

	/**
	 * The constant Getting expiry policy
	 */
	public static final String GETEXPIRYPOLICY = "Getting expiry policy";

	/**
	 * The constant Request received to decryptSymmetricKey
	 */
	public static final String DECRYPTKEY = "Request received to decryptSymmetricKey";

	/**
	 * The constant Getting private key
	 */
	public static final String GETPRIVATEKEY = "Getting private key";

	/**
	 * The constant Storing key in KeyAlias
	 */
	public static final String STOREKEYALIAS = "Storing key in KeyAlias";

	/**
	 * The constant Storing key in dbKeyStore
	 */
	public static final String STOREDBKEY = "Storing key in dbKeyStore";

	/**
	 * The constant keyFromDBStore
	 */
	public static final String KEYFROMDB = "keyFromDBStore";

	/**
	 * The constant keyPolicy
	 */
	public static final String KEYPOLICY = "keyPolicy";

	/**
	 * The constant symmetricKeyRequestDto
	 */
	public static final String SYMMETRICKEYREQUEST = "symmetricKeyRequestDto";

	/**
	 * The constant fetchedKeyAlias
	 */
	public static final String FETCHEDKEYALIAS = "fetchedKeyAlias";

	/**
	 * The constant dbKeyStore
	 */
	public static final String DBKEYSTORE = "dbKeyStore";

	/**
	 * The constant RSA
	 */
	public static final String RSA = "RSA";
}
