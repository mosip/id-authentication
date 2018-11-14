package io.mosip.registration.dto;

/**
 * This dto class is used for sms notification
 * @author Dinesh Ashokan
 *
 */
public class SMSDTO {
private String message;
private String number;

/**
 * @return the message
 */
public String getMessage() {
	return message;
}
/**
 * @param message the message to set
 */
public void setMessage(String message) {
	this.message = message;
}
/**
 * @return the number
 */
public String getNumber() {
	return number;
}
/**
 * @param number the number to set
 */
public void setNumber(String number) {
	this.number = number;
}

}
