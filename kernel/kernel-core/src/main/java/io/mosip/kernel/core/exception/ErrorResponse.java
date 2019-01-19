package io.mosip.kernel.core.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
@Data
public class ErrorResponse<T> {
	private long timestamp = Instant.now().toEpochMilli();
	private int status;
	private List<T> errors = new ArrayList<>();

}
