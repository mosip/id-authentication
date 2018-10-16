package io.mosip.kernel.otpmanager.constant;

/**
 * This enum defines the constants that holds the crypto properties.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since version 1.0.0
 *
 */
public enum OtpCryptoConstants {
	OTP_CRYPTO_SHA512("HmacSHA512"), 
	OTP_CRYPTO_SHA256("HmacSHA256"), 
	OTP_CRYPTO_SHA1("HmacSHA1");

	/**
	 * This variable holds the crypto function.
	 */
	private String cryptoType;

	/**
	 * Constructor for OtpCryptoConstants ENUM.
	 * 
	 * @param crypto
	 *            crypto function
	 */
	OtpCryptoConstants(final String crypto) {
		this.cryptoType = crypto;
	}

	/**
	 * Getter for cryptoType.
	 * 
	 * @return crypto function used.
	 */
	public String getCryptoType() {
		return cryptoType;
	}
}
