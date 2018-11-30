package io.mosip.kernel.keymanagerservice.exception;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
/**
 * @author Dharmesh Khandelwal
 *
 * @since 1.0.0
 */
@Data
public class ErrorResponse<T> {
private List<T> errors = new ArrayList<>();
}
