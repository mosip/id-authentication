package io.mosip.kernel.core.otpnotification.spi;

/**
 * This interface has methods to sendOtpNotification.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 * @param <T>
 */
public interface OtpNotification<T, D> {

	/**
	 * Method to send OTP notification.
	 * 
	 * @param request
	 *            the request dto.
	 * @return the response.
	 */
	public T sendOtpNotification(D request);
}
