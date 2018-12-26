package io.mosip.preregistration.translitration.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TranslitrationRequestDTO<T> {
	
	String id;
	String ver;
    Date reqTime;
    T request;

}
