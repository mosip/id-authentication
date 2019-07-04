package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class PacketReceiverAppException.
 * 
 * @author Rishabh Keshari
 */
public class PacketReceiverAppException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6748760277721155095L;

	/** The id. */
	private String id;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Instantiates a new id repo app exception.
	 */
	public PacketReceiverAppException() {
		super();
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public PacketReceiverAppException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 * @param rootCause
	 *            the root cause
	 */
	public PacketReceiverAppException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 * @param rootCause
	 *            the root cause
	 * @param id
	 *            the id
	 */
	public PacketReceiverAppException(String errorCode, String errorMessage, Throwable rootCause, String id) {
		super(errorCode, errorMessage, rootCause);
		this.id = id;
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant
	 *            the exception constant
	 */
	public PacketReceiverAppException(PlatformErrorMessages exceptionConstant) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage());
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant
	 *            the exception constant
	 * @param rootCause
	 *            the root cause
	 */
	public PacketReceiverAppException(PlatformErrorMessages exceptionConstant, Throwable rootCause) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage(), rootCause);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant
	 *            the exception constant
	 * @param id
	 *            the id
	 */
	public PacketReceiverAppException(PlatformErrorMessages exceptionConstant, String id) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage());
		this.id = id;
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant
	 *            the exception constant
	 * @param rootCause
	 *            the root cause
	 * @param id
	 *            the id
	 */
	public PacketReceiverAppException(PlatformErrorMessages exceptionConstant, Throwable rootCause, String id) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage(), rootCause);
		this.id = id;
	}

}
