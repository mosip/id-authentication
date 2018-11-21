package io.mosip.kernel.masterdata.exceptionhandler;

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

	private List<T> errorList = new ArrayList<>();

}
