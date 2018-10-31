package io.mosip.authentication.core.dto.indauth;

/**
 * The Enum AuthUsageDataBit.
 */
public enum AuthUsageDataBit {

	// Used bits from 0 to 8 Hex digits

	/** The used otp. */
	USED_OTP(1, 0),

	/** The used pi name pri. */
	USED_PI_NAME_PRI(1, 1),

	/** The used pi name sec. */
	USED_PI_NAME_SEC(1, 2),

	/** The used pi dob. */
	USED_PI_DOB(1, 3),

	/** The used pi age. */
	USED_PI_AGE(2, 0),

	/** The used pi phone. */
	USED_PI_PHONE(2, 1),

	/** The used pi email. */
	USED_PI_EMAIL(2, 2),

	/** The used pi gender. */
	USED_PI_GENDER(2, 3),

	/** The used fad addr pri. */
	USED_FAD_ADDR_PRI(3, 0),

	/** The used fad addr sec. */
	USED_FAD_ADDR_SEC(3, 1),

	/** The used ad addr line1 pri. */
	USED_AD_ADDR_LINE1_PRI(3, 2),

	/** The used ad addr line2 pri. */
	USED_AD_ADDR_LINE2_PRI(3, 3),

	/** The used ad addr line3 pri. */
	USED_AD_ADDR_LINE3_PRI(4, 0),

	/** The used ad addr pincode pri. */
	USED_AD_ADDR_PINCODE_PRI(4, 1),

	/** The used ad addr line1 sec. */
	USED_AD_ADDR_LINE1_SEC(4, 2),

	/** The used ad addr line2 sec. */
	USED_AD_ADDR_LINE2_SEC(4, 3),

	/** The used ad addr line3 sec. */
	USED_AD_ADDR_LINE3_SEC(5, 0),

	/** The used ad addr city sec. */
	USED_AD_ADDR_CITY_SEC(5, 1),

	/** The used ad addr state sec. */
	USED_AD_ADDR_STATE_SEC(5, 2),

	/** The used ad addr country sec. */
	USED_AD_ADDR_COUNTRY_SEC(5, 3),

	/** The used ad addr pincode sec. */
	USED_AD_ADDR_PINCODE_SEC(6, 0),

	/** The used location pri. */
	USED_LOCATION1_PRI(6, 1),

	USED_LOCATION1_SEC(6, 2),

	USED_LOCATION2_PRI(6, 3),

	USED_LOCATION2_SEC(7, 0),

	USED_LOCATION3_PRI(7, 1),

	USED_LOCATION3_SEC(7, 2),

	USED_DOBTYPE(7, 3),

	USED_LOCATION_SEC(8, 0),

	// Matched bits from 9 to 16 Hex digits

	/** The matched otp. */
	MATCHED_OTP(9, 0),

	/** The matched pi name pri. */
	MATCHED_PI_NAME_PRI(9, 1),

	/** The matched pi name sec. */
	MATCHED_PI_NAME_SEC(9, 2),

	/** The matched pi dob. */
	MATCHED_PI_DOB(9, 3),

	/** The matched pi age. */
	MATCHED_PI_AGE(10, 0),

	/** The matched pi phone. */
	MATCHED_PI_PHONE(10, 1),

	/** The matched pi email. */
	MATCHED_PI_EMAIL(10, 2),

	/** The matched pi gender. */
	MATCHED_PI_GENDER(10, 3),

	/** The matched fad addr pri. */
	MATCHED_FAD_ADDR_PRI(11, 0),

	/** The matched fad addr sec. */
	MATCHED_FAD_ADDR_SEC(11, 1),

	/** The matched addr line1 pri. */
	MATCHED_AD_ADDR_LINE1_PRI(11, 2),

	/** The matched addr line2 pri. */
	MATCHED_AD_ADDR_LINE2_PRI(11, 3),

	/** The matched addr line3 pri. */
	MATCHED_AD_ADDR_LINE3_PRI(12, 0),

	/** The matched ad addr city pri. */
	MATCHED_AD_ADDR_CITY_PRI(12, 1),

	/** The matched ad addr state pri. */
	MATCHED_AD_ADDR_STATE_PRI(12, 2),

	/** The matched addr country pri. */
	MATCHED_AD_ADDR_COUNTRY_PRI(12, 3),

	/** The matched addr pincode pri. */
	MATCHED_AD_ADDR_PINCODE_PRI(13, 0),

	/** The matched addr line1 sec. */
	MATCHED_AD_ADDR_LINE1_SEC(13, 1),

	/** The matched addr line2 sec. */
	MATCHED_AD_ADDR_LINE2_SEC(13, 2),

	/** The matched addr line3 sec. */
	MATCHED_AD_ADDR_LINE3_SEC(13, 3),

	MATCHED_LOCATION1_PRI(14, 0),

	MATCHED_LOCATION1_SEC(14, 1),

	MATCHED_LOCATION2_PRI(14, 2),

	MATCHED_LOCATION2_SEC(14, 3),

	MATCHED_LOCATION3_PRI(15, 0),

	MATCHED_LOCATION3_SEC(15, 1),

	/** The matched addr pincode sec. */
	MATCHED_AD_ADDR_PINCODE_SEC(15, 2),

	MATCHED_DOB_TYPE(15, 3);

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
