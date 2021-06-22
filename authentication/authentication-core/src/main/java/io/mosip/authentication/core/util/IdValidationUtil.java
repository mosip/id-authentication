package io.mosip.authentication.core.util;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;

/**
 * Validates the UIN and VID
 * 
 * @author Nagarjuna
 *
 * @since 1.2.0
 */

@Component
public class IdValidationUtil {

	/**
	 * IdValidationUtil logger
	 */
	private static Logger mosipLogger = IdaLogger.getLogger(IdValidationUtil.class);

	/**
	 * The multiplication table.
	 */
	private static int[][] d = new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 },
			{ 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 }, { 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 }, { 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 },
			{ 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 }, { 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 }, { 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 },
			{ 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 }, { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 } };

	/**
	 * The permutation table.
	 */
	private static int[][] p = new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 },
			{ 5, 8, 0, 3, 7, 9, 6, 1, 4, 2 }, { 8, 9, 1, 6, 0, 4, 3, 5, 2, 7 }, { 9, 4, 5, 3, 1, 2, 6, 8, 7, 0 },
			{ 4, 2, 8, 6, 5, 7, 3, 9, 0, 1 }, { 2, 7, 9, 3, 8, 0, 6, 4, 1, 5 }, { 7, 0, 4, 6, 9, 1, 3, 2, 5, 8 } };

	/**
	 * The length of the UIN is reading from property file
	 */
	@Value("${mosip.kernel.uin.length:-1}")
	private int uinLength;

	/**
	 * The length of the VID is reading from property file
	 */
	@Value("${mosip.kernel.vid.length}")
	private int vidLength;

	/**
	 * Validates the UIN length and checksum
	 * 
	 * @param id
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public boolean validateUIN(String id) throws IdAuthenticationBusinessException {
		

		/**
		 * 
		 * Check UIN, It Shouldn't be Null or empty
		 * 
		 */
		if (StringUtils.isEmpty(id)) {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.UIN_VAL_INVALID_NULL.getErrorCode(),
					IdAuthenticationErrorConstants.UIN_VAL_INVALID_NULL.getErrorMessage());			
		}

		/**
		 * 
		 * Check the Length of the UIN, It Should be specified number of digits
		 * 
		 */
		if (id.length() != uinLength) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "VALIDATE",
					"UIN length should be as per configuration");
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.UIN_VAL_ILLEGAL_LENGTH.getErrorCode(),
					IdAuthenticationErrorConstants.UIN_VAL_ILLEGAL_LENGTH.getErrorMessage());
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate
		 * 
		 * Validate the UIN by verifying the checksum
		 * 
		 */
		if (!validateChecksum(id)) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "VALIDATE",
					"UIN should match checksum.");
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.UIN_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					IdAuthenticationErrorConstants.UIN_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
		}

		/**
		 * 
		 * once the above validation are passed then the method will going to return
		 * True That is its Valid UIN Number
		 * 
		 * 
		 */
		return true;
	}

	/**
	 * Validates the VID length and checksum
	 * 
	 * @param id
	 * @return
	 * @throws IdAuthenticationBusinessException 
	 */
	public boolean validateVID(String id) throws IdAuthenticationBusinessException {

		/**
		 * 
		 * Check UIN, It Shouldn't be Null or empty
		 * 
		 */
		if (StringUtils.isEmpty(id)) {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.VID_VAL_INVALID_NULL.getErrorCode(),
					IdAuthenticationErrorConstants.VID_VAL_INVALID_NULL.getErrorMessage());			
		}
		
		/**
		 * 
		 * Check the Length of the VID, It Should be specified number of digits
		 * 
		 */
		if (id.length() != vidLength) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "VALIDATE",
					"VID length should be as per configuration");
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.VID_VAL_ILLEGAL_LENGTH.getErrorCode(),
					IdAuthenticationErrorConstants.VID_VAL_ILLEGAL_LENGTH.getErrorMessage());
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate
		 * 
		 * Validate the UIN by verifying the checksum
		 * 
		 */
		if (!validateChecksum(id)) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "VALIDATE",
					"VID should match checksum.");
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.VID_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					IdAuthenticationErrorConstants.VID_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
		}

		/**
		 * 
		 * once the above validation are passed then the method will going to return
		 * True That is its Valid UIN Number
		 * 
		 * 
		 */
		return true;
	}

	/**
	 * Validates that an entered number is Verhoeff checksum compliant. Make sure
	 * the check digit is the last one.
	 * 
	 * @param num The numeric string data for Verhoeff checksum compliance check.
	 * @return true if the provided number is Verhoeff checksum compliant.
	 */
	public static boolean validateChecksum(String num) {
		int c = 0;
		int[] myArray = stringToReversedIntArray(num);
		for (int i = 0; i < myArray.length; i++) {
			c = d[c][p[(i % 8)][myArray[i]]];
		}
		return (c == 0);
	}

	/**
	 * Converts a string to a reversed integer array.
	 * 
	 * @param num The numeric string data converted to reversed int array.
	 * @return Integer array containing the digits in the numeric string provided in
	 *         reverse.
	 */
	private static int[] stringToReversedIntArray(String num) {
		int[] myArray = new int[num.length()];
		for (int i = 0; i < num.length(); i++) {
			myArray[i] = Integer.parseInt(num.substring(i, i + 1));
		}
		myArray = reverse(myArray);
		return myArray;
	}

	/**
	 * Reverses an int array.
	 * 
	 * @param myArray The input array which needs to be reversed
	 * @return The array provided in reverse order.
	 */
	private static int[] reverse(int[] myArray) {
		int[] reversed = new int[myArray.length];
		for (int i = 0; i < myArray.length; i++) {
			reversed[i] = myArray[myArray.length - (i + 1)];
		}
		return reversed;
	}
}