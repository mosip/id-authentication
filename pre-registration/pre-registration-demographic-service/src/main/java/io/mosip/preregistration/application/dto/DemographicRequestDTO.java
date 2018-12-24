/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This DTO class is used to define the initial request parameters.
 * 
 * @author Rajath KR
 * @since 1.0.0
 *
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
public class DemographicRequestDTO<T> {
	String id;
	String ver;
	Date reqTime;
	T request;
}
