package org.mosip.kernel.core.spi.otpmanager;

/**
 * This interface declares the methods required for OTP generation.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public interface OtpGenerator {
	/**
	 * This method generates the OTP.
	 * 
	 * @return 
	 * 		the generated OTP.
	 */
	public String generateOtp();
}
