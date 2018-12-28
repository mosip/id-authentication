package io.mosip.pregistration.datasync.dto;


import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MainRequestDTO<T> {
	
	private String id;
	private String ver;
	private Date reqTime;
	private T request;

}
