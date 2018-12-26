package io.mosip.preregistration.translitration.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ResponseDTO<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6015563485338517440L;
	
	private ExceptionJSONInfoDTO err;

	private String status;

	private Date resTime;

	private T response;

}
