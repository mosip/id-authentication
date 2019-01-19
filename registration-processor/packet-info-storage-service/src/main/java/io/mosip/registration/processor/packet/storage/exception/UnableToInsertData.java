package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class UnableToInsertData.
 */
public class UnableToInsertData extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new unable to insert data.
	 */
	public UnableToInsertData() {
		super();
	}
	
	/**
	 * Instantiates a new unable to insert data.
	 *
	 * @param errorMessage the error message
	 */
	public UnableToInsertData(String errorMessage) {
		super(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getCode() + EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new unable to insert data.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public UnableToInsertData(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getCode() + EMPTY_SPACE, message, cause);
	}

}
