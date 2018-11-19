package io.mosip.preregistration.application.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * View registration response DTO
 * 
 * @author M1037462
 
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ViewDto {
	//private Boolean status;
	//private List<ExceptionJSONInfo> err= new ArrayList<>();
	private String preId;
	private String firstname;
	private String appointmentDate;
	private String status_code;
	

}
