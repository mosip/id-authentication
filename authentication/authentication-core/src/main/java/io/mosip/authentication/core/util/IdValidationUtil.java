package io.mosip.authentication.core.util;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.ChecksumUtils;
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
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
							"individualId"));		
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
					String.format(IdAuthenticationErrorConstants.UIN_VAL_ILLEGAL_LENGTH.getErrorMessage(), uinLength));
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate
		 * 
		 * Validate the UIN by verifying the checksum
		 * 
		 */
		if (!ChecksumUtils.validateChecksum(id)) {
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
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
							"individualId"));			
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
					String.format(IdAuthenticationErrorConstants.VID_VAL_ILLEGAL_LENGTH.getErrorMessage(), vidLength));
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate
		 * 
		 * Validate the UIN by verifying the checksum
		 * 
		 */
		if (!ChecksumUtils.validateChecksum(id)) {
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
}