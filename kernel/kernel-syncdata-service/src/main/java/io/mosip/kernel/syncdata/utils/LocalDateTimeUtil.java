package io.mosip.kernel.syncdata.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

import io.mosip.kernel.syncdata.constant.MasterDataErrorCode;
import io.mosip.kernel.syncdata.exception.DataNotFoundException;
import io.mosip.kernel.syncdata.exception.DateParsingException;

@Component
public class LocalDateTimeUtil {

	/**
	 * It will parse string timestamp to localdatetime. It also validates if the
	 * lastupdatedtime is not future date.
	 * 
	 * @param currentTimeStamp - current time stamp
	 * @param lastUpdated      - last updated time stamp
	 * @return {@link LocalDateTime}
	 */
	public LocalDateTime getLocalDateTimeFromTimeStamp(LocalDateTime currentTimeStamp, String lastUpdated) {
		LocalDateTime timeStamp = null;
		if (lastUpdated != null) {
			try {
				timeStamp = MapperUtils.parseToLocalDateTime(lastUpdated);
				if (timeStamp.isAfter(currentTimeStamp)) {
					throw new DataNotFoundException(MasterDataErrorCode.INVALID_TIMESTAMP_EXCEPTION.getErrorCode(),
							MasterDataErrorCode.INVALID_TIMESTAMP_EXCEPTION.getErrorMessage());
				}
			} catch (DateTimeParseException e) {
				throw new DateParsingException(MasterDataErrorCode.LAST_UPDATED_PARSE_EXCEPTION.getErrorCode(),
						e.getMessage());
			}
		}

		return timeStamp;
	}
}
