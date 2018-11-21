package io.mosip.authentication.core.util;

import java.util.stream.IntStream;

import io.mosip.kernel.core.util.StringUtils;

/**
 * Method to generate masking the values
 * 
 * @author Sanjay Murali
 */
public class MaskUtil {

	private MaskUtil() {

	}

	/**
	 * masked the email/mobile with mask number.
	 * 
	 * @param maskValue
	 * @param maskNo
	 * @return
	 */
	public static String generateMaskValue(String maskValue, int maskNo) {
		char[] maskedDetail = maskValue.toCharArray();
		for (int i = 0; i < maskNo && i < maskedDetail.length; i++) {
			maskedDetail[i] = 'X';
		}
		return String.valueOf(maskedDetail);
	}

	/**
	 * Mask email.
	 *
	 * @param email the email
	 * @return the string
	 */
	public static String maskEmail(String email) {
		StringBuilder maskedEmail = new StringBuilder(email);
		IntStream.range(1, StringUtils.split(email, '@')[0].length() + 1).filter(i -> i % 3 != 0)
				.forEach(i -> maskedEmail.setCharAt(i - 1, 'X'));
		return maskedEmail.toString();
	}

	/**
	 * Mask mobile number.
	 *
	 * @param email the email
	 * @return the string
	 */
	public static String maskMobile(String mobileNumber) {
		StringBuilder maskedMobile = new StringBuilder(mobileNumber);
		IntStream.range(0, (maskedMobile.length() / 2) + 1).forEach(i -> maskedMobile.setCharAt(i, 'X'));
		return maskedMobile.toString();
	}

}
