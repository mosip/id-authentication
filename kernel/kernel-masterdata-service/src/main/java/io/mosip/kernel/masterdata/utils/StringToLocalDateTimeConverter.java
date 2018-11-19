package io.mosip.kernel.masterdata.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * This util class to convert String format effective date and time to
 * LocalDateTime format.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

	/**
	 * This method to convert String format effective date and time to LocalDateTime
	 * format.
	 * 
	 * @param source
	 *            effective date and time in String format
	 * @return LocalDateTime> Return effective date and time in LocalDateTime format
	 * 
	 * @throws InValideDateTimeFormateException
	 *             throw exception not able to convert String to LocalDateTime.
	 */

	@Override
	public LocalDateTime convert(String source) {
		return LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

	}

}
