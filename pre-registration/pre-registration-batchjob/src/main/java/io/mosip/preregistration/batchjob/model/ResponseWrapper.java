/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjob.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.mosip.kernel.core.exception.ServiceError;
import lombok.Data;

/**
 * This is a ResponseWrapper class used in Rest call to kernel authmanager. 
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 * @param <T>
 */
@Data
public class ResponseWrapper<T> {
	private String id;
	private String version;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime responsetime = LocalDateTime.now(ZoneId.of("UTC"));
	private Object metadata;
	@NotNull
	@Valid
	private T response;

	private List<ServiceError> errors = new ArrayList<>();

}
