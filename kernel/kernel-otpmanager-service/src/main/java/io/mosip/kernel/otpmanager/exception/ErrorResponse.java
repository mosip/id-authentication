package io.mosip.kernel.otpmanager.exception;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class ErrorResponse<T> {

	/**
	 * The errors list.
	 */
	private List<T> errors = new ArrayList<>();

}
