package io.mosip.resident.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.mosip.kernel.core.exception.ServiceError;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseWrapper<T> {
	private String id;
	private String version;
	private String responsetime;
	@NotNull
	@Valid
	private T response;

	private List<ServiceError> errors = new ArrayList<>();

}
