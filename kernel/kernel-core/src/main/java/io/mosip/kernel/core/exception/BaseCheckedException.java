package io.mosip.kernel.core.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the base class for all MOSIP checked exceptions.
 * 
 * The class and its subclasses are a form that indicates conditions that a
 * reasonable application might want to catch.
 *
 * <p>
 * The class {@code BaseCheckedException} and any subclasses that are not also
 * subclasses of {@link BaseUncheckedException} are <em>checked exceptions</em>.
 * Checked exceptions need to be declared in a method or constructor's
 * {@code throws} clause if they can be thrown by the execution of the method or
 * constructor and propagate outside the method or constructor boundary.
 *
 * @author Shashank Agrawal
 * @since 1.0
 *
 */
public class BaseCheckedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -924722202100630614L;

	private final List<InfoItem> infoItems = new ArrayList<>();

	/**
	 * Constructs a new checked exception
	 */
	public BaseCheckedException() {
		super();
	}

	/**
	 * Constructs a new checked exception with errorMessage
	 * 
	 * @param errorMessage
	 *            the detail message.
	 */
	public BaseCheckedException(String errorMessage) {
		super(errorMessage);
	}

	/**
	 * Constructs a new checked exception with the specified detail message and
	 * error code.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the detail message.
	 */
	public BaseCheckedException(String errorCode, String errorMessage) {
		super(errorCode + " --> " + errorMessage);
		addInfo(errorCode, errorMessage);
	}

	/**
	 * Constructs a new checked exception with the specified detail message and
	 * error code and specified cause.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the detail message.
	 * @param rootCause
	 *            the specified cause
	 * 
	 */
	public BaseCheckedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode + " --> " + errorMessage, rootCause);
		addInfo(errorCode, errorMessage);
		if (rootCause instanceof BaseCheckedException) {
			BaseCheckedException bce = (BaseCheckedException) rootCause;
			infoItems.addAll(bce.infoItems);
		}
	}

	/**
	 * This method add error code and error message.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorText
	 *            the detail message.
	 * 
	 * @return the current instance of BaseCheckedException
	 */
	public BaseCheckedException addInfo(String errorCode, String errorText) {
		this.infoItems.add(new InfoItem(errorCode, errorText));
		return this;
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
	 * Returns the list of exception codes.
	 * 
	 * @return list of exception codes
	 */
	public List<String> getCodes() {
		List<String> codes = new ArrayList<>();
		for (int i = this.infoItems.size() - 1; i >= 0; i--)
			codes.add(this.infoItems.get(i).errorCode);
		return codes;
	}

	/**
	 * Returns the list of exception messages.
	 * 
	 * @return list of exception messages
	 */
	public List<String> getErrorTexts() {
		List<String> errorTexts = new ArrayList<>();
		for (int i = this.infoItems.size() - 1; i >= 0; i--)
			errorTexts.add(this.infoItems.get(i).errorText);
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