package io.mosip.kernel.pdfgenerator.itext.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
/**
 * 
 * 
 * @author M1046571
 * @since 1.0.0
 *
 */

public class PdfGeneratorException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6138841548758442351L;
   
	
	
	/**
	 * Constructor for PDFGeneratorGenericException
	 * 
	 * @param errorCode
	 *            The errorcode
	 * @param errorMessage
	 *            The errormessage
	 * @param cause
	 *            The cause
	 */
	public PdfGeneratorException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

	public PdfGeneratorException() {
		super();
	}

	public PdfGeneratorException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
