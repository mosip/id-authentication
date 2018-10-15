package io.mosip.kernel.core.spi.smsnotifier;

public interface SmsNotifier<T> {

	/**
	 * This method sends contentMessage as sms on contactNumber provided.
	 * 
	 * @param contactNumber
	 *            the Recipient contact number.
	 * @param contentMessage
	 *            the message need to be send.
	 * @return the ResponseDto.
	 */
	public T sendSmsNotification(String contactNumber, String contentMessage);

}
