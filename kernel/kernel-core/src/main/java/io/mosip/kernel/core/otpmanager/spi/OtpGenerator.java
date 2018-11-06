package io.mosip.kernel.core.otpmanager.spi;

/**
 * This interface provides the methods which can be used for OTP generation.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public interface OtpGenerator<S, D> {
	/**
	 * This method can be used to generate OTP against a particular key. OTP against
	 * a particular key is generated only if the key is not freezed.
	 * 
	 * @param otpDto
	 *            the OTP generation DTO.
	 * @return the generated OTP.
	 */
	public D getOtp(S otpDto);
}
