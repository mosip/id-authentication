package io.mosip.authentication.core.dto;

import java.util.stream.IntStream;

import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class MaskUtil is used to mask a value
 * to a particular char set as configured
 *
 * @author Sanjay Murali
 */
public class MaskUtil {

	/**
	 * Instantiates a new mask util.
	 */
	private MaskUtil() {

	}

	/**
	 * generateMaskValue method is used to mask the email/mobile with mask number.
	 *
	 * @param maskValue the value to be masked
	 * @param maskNo the number of char set to the masked
	 * @return the string
	 */
	public static String generateMaskValue(String maskValue, int maskNo) {
		char[] maskedDetail = maskValue.toCharArray();
		for (int i = 0; i < maskNo && i < maskedDetail.length; i++) {
			maskedDetail[i] = 'X';
		}
		return String.valueOf(maskedDetail);
	}

	/**
	 * maskEmail method used to mask email
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
	 * maskMobile method used to mask mobile number
	 *
	 * @param mobileNumber the mobile number
	 * @return the string
	 */
	public static String maskMobile(String mobileNumber) {
		StringBuilder maskedMobile = new StringBuilder(mobileNumber);
		IntStream.range(0, (maskedMobile.length() / 2) + 1).forEach(i -> maskedMobile.setCharAt(i, 'X'));
		return maskedMobile.toString();
	}

}
