package io.mosip.authentication.core.dto.indauth;

/**
 * The Enum AuthUsageDataBit.
 */
public enum AuthUsageDataBit {

	// Used bits from 0 to 8 Hex digits

	/** The used otp. */
	USED_OTP(1, 3),

	/** The used pi name pri. */
	USED_PI_NAME(1, 2),

	/** The used pi dob. */
	USED_PI_DOB(1, 1),

	/** The used pi dob type. */
	USED_PI_DOBTYPE(1,0),

	/** The used pi age. */
	USED_PI_AGE(2, 3),

	/** The used pi phone. */
	USED_PI_PHONE(2, 2),

	/** The used pi email. */
	USED_PI_EMAIL(2, 1),

	/** The used pi gender. */
	USED_PI_GENDER(2,0),

	/** The used fad addr pri. */
	USED_FAD_ADDR(3, 3),

	/** The used ad addr line1 pri. */
	USED_AD_ADDR_LINE1(3, 2),

	/** The used ad addr line2 pri. */
	USED_AD_ADDR_LINE2(3, 1),

	/** The used ad addr line3 pri. */
	USED_AD_ADDR_LINE3(3, 0),

	/** The used ad location1 pri. */
	USED_AD_LOCATION1(4, 3 ),

	/** The used ad location2 pri. */
	USED_AD_LOCATION2(4,2),

	/** The used ad location3 pri. */
	USED_AD_LOCATION3(4, 1),

	/** The used ad addr pincode pri. */
	USED_AD_ADDR_PINCODE(4, 0),

	USED_BIO_FINGERPRINT_MINUTIAE(5, 3),

	USED_BIO_FINGERPRINT_IMAGE(5, 2),

	USED_BIO_IRIS(5, 1),

	USED_BIO_FACE(5, 0),
	
	USED_STATIC_PIN(6, 3),

	// Matched bits from 9 to 16 Hex digits

	/** The matched otp. */
	MATCHED_OTP(9, 3),

	/** The matched pi name pri. */
	MATCHED_PI_NAME(9, 2),

	/** The matched pi dob. */
	MATCHED_PI_DOB(9, 1),

	/** The matched pi dob type. */
	MATCHED_PI_DOB_TYPE(9, 0),

	/** The matched pi age. */
	MATCHED_PI_AGE(10, 3),

	/** The matched pi phone. */
	MATCHED_PI_PHONE(10, 2),

	/** The matched pi email. */
	MATCHED_PI_EMAIL(10, 1),

	/** The matched pi gender. */
	MATCHED_PI_GENDER(10, 0),

	/** The matched fad addr pri. */
	MATCHED_FAD_ADDR(11, 3),

	/** The matched addr line1 pri. */
	MATCHED_AD_ADDR_LINE1(11, 2),

	/** The matched addr line2 pri. */
	MATCHED_AD_ADDR_LINE2(11, 1),

	/** The matched addr line3 pri. */
	MATCHED_AD_ADDR_LINE3(11, 0),

	/** The matched location1 pri. */
	MATCHED_AD_LOCATION1(12, 3),

	/** The matched location2 pri. */
	MATCHED_AD_LOCATION2(12, 2),

	/** The matched location3 pri. */
	MATCHED_AD_LOCATION3(12, 1),

	/** The matched addr pincode pri. */
	MATCHED_AD_ADDR_PINCODE(12, 0),

	MATCHED_BIO_FINGERPRINT_MINUTIAE(13, 3),

	MATCHED_BIO_FINGERPRINT_IMAGE(13, 2),

	MATCHED_BIO_IRIS(13, 1),

	MATCHED_BIO_FACE(13, 0),
	
	MATCHED_STATIC_PIN(14, 3)

	;

	private int hexNum;

	/** The bit index. */
	private int bitIndex;

	/**
	 * Instantiates a new auth usage data bit.
	 *
	 * @param hexNum   the hex num
	 * @param bitIndex the bit index
	 */
	private AuthUsageDataBit(int hexNum, int bitIndex) {
		this.hexNum = hexNum;
		this.bitIndex = bitIndex;
	}

	/**
	 * Gets the hex num.
	 *
	 * @return the hex num
	 */
	public int getHexNum() {
		return hexNum;
	}

	/**
	 * Gets the bit index.
	 *
	 * @return the bit index
	 */
	public int getBitIndex() {
		return bitIndex;
	}
}
