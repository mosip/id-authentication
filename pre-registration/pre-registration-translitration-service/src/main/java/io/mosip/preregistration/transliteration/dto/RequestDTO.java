package io.mosip.preregistration.transliteration.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestDTO<T> {
	
	String id;
	String ver;
    Date reqTime;
    T request;

}
