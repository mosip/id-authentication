/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.security.cipher.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.security.cipher.constant.MosipSecurityExceptionCodeConstants;

/**
 * {@link Exception} to be thrown when data is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MissingProviderException extends BaseUncheckedException {

    /**
     * Unique id for serialization
     */
    private static final long serialVersionUID = -6206425923884046275L;

    /**
     * Constructor for this class
     * 
     * @param exceptionCodeConstants exception code constant
     */
    public MissingProviderException(MosipSecurityExceptionCodeConstants exceptionCodeConstants) {
	super(exceptionCodeConstants.getErrorCode(), exceptionCodeConstants.getErrorMessage());
    }
}
