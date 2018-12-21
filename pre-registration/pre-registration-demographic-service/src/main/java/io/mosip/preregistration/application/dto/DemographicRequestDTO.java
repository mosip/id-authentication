package io.mosip.preregistration.application.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author M1046129
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
