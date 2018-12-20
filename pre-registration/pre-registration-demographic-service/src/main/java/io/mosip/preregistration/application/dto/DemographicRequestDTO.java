package io.mosip.preregistration.application.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DemographicRequestDTO<T> {
	String id;
	String ver;
    Date reqTime;
    T request;
}
