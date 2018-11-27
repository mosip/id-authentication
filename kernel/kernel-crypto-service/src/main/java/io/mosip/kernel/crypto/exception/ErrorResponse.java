package io.mosip.kernel.crypto.exception;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
@Data
public class ErrorResponse<T> {
private List<T> errors = new ArrayList<>();
}
