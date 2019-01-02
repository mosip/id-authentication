/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.exception.util;

import java.time.DateTimeException;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.DocumentNotFoundException;
import io.mosip.preregistration.booking.exception.InvalidDateTimeFormatException;
import io.mosip.preregistration.booking.exception.RecordNotFoundException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;

/**
 * This class is used to catch the exceptions that occur while uploading the
 * document
 * 
 * @author Rajath KR
 * @since 1.0.0
 *
 */
public class BookingExceptionCatcher {
	public void handle(Exception ex) {
		if (ex instanceof RecordNotFoundException) {
			throw new RecordNotFoundException(ErrorCodes.PRG_BOOK_RCI_015.toString(),
					ErrorMessages.NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER.toString());
		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_BOOK_RCI_022.toString(),
					ErrorMessages.INVALID_REQUEST_PARAMETER.toString());
		} else if (ex instanceof DateTimeException) {
			throw new InvalidDateTimeFormatException(ErrorCodes.PRG_BOOK_RCI_009.toString(),
					ErrorMessages.INVALID_DATE_TIME_FORMAT.toString());
		} else if (ex instanceof DocumentNotFoundException) {
			throw new DocumentNotFoundException(((DocumentNotFoundException) ex).getErrorCode(),
					((DocumentNotFoundException) ex).getErrorText(), ex.getCause());
		} else if (ex instanceof DataAccessLayerException) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_BOOK_RCI_010.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString(), ex.getCause());

		}
	}

}