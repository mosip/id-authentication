package io.mosip.kernel.idgenerator.uin.exception;

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

	private List<T> errors = new ArrayList<>();

}
