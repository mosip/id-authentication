package io.mosip.kernel.otpnotification.exception;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class ErrorResponse<T> {

	private List<T> errors = new ArrayList<>();

}
