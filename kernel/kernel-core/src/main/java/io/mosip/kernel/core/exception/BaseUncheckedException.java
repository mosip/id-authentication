package io.mosip.kernel.core.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the base class for all MOSIP unchecked exceptions.
 * 
 * {@code BaseUncheckedException} is the superclass of those exceptions that can
 * be thrown during the normal operation of the Java Virtual Machine.
 *
 * <p>
 * {@code RuntimeException} and its subclasses are <em>unchecked
 * exceptions</em>. Unchecked exceptions do <em>not</em> need to be declared in
 * a method or constructor's {@code throws} clause if they can be thrown by the
 * execution of the method or constructor and propagate outside the method or
 * constructor boundary.
 *
 * @author Shashank Agrawal Compile-Time Checking of Exceptions
 * @since 1.0
 */
public class BaseUncheckedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -875003872780128394L;
	public static final String EMPTY_SPACE = " ";
	private final List<InfoItem> infoItems = new ArrayList<>();

	/**
	 * Constructs a new unchecked exception
	 */
	public BaseUncheckedException() {
		super();
	}

	/**
	 * Constructs a new checked exception with errorMessage
	 * 
	 * @param errorMessage
	 *            the detail message.
	 */
	public BaseUncheckedException(String errorMessage) {
		super(errorMessage);
	}

	/**
	 * Constructs a new unchecked exception with the specified detail message and
	 * error code.
	 *
	 * @param errorMessage
	 *            the detail message.
	 * @param errorCode
	 *            the error code.
	 * 
	 */
	public BaseUncheckedException(String errorCode, String errorMessage) {
		super(errorCode + " --> " + errorMessage);
		addInfo(errorCode, errorMessage);
	}

	/**
	 * Constructs a new unchecked exception with the specified detail message and
	 * error code and error cause.
	 *
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the detail message.
	 * @param rootCause
	 *            the specified cause
	 */
	public BaseUncheckedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode + " --> " + errorMessage, rootCause);
		addInfo(errorCode, errorMessage);
		if (rootCause instanceof BaseUncheckedException) {
			BaseUncheckedException bue = (BaseUncheckedException) rootCause;
			infoItems.addAll(bue.infoItems);
		}
	}

	/*
	 * Returns a String object that can be used to get the exception message.
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return ExceptionUtils.buildMessage(super.getMessage(), getCause());
	}

	/**
	 * This method add the information of error code and error message.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorText
	 *            the detail message.
	 * @return the instance of current BaseCheckedException
	 */
	public BaseUncheckedException addInfo(String errorCode, String errorText) {
		this.infoItems.add(new InfoItem(errorCode, errorText));
		return this;
	}

	/**
	 * Returns the list of error codes.
	 * 
	 * @return the list of error codes
	 */
	public List<String> getCodes() {
		List<String> codes = new ArrayList<>();

		for (int i = this.infoItems.size() - 1; i >= 0; i--) {
			codes.add(this.infoItems.get(i).errorCode);
		}

		return codes;
	}

	/**
	 * Returns the list of exception messages.
	 * 
	 * @return the list of exception messages
	 */
	public List<String> getErrorTexts() {
		List<String> errorTexts = new ArrayList<>();

		for (int i = this.infoItems.size() - 1; i >= 0; i--) {
			errorTexts.add(this.infoItems.get(i).errorText);
		}

		return errorTexts;
	}

	/**
	 * Return the last error code.
	 * 
	 * @return the last error code
	 */
	public String getErrorCode() {
		return infoItems.get(0).errorCode;
	}

	/**
	 * Return the last exception message.
	 * 
	 * @return the last exception message
	 */
	public String getErrorText() {
		return infoItems.get(0).errorText;
	}

}
