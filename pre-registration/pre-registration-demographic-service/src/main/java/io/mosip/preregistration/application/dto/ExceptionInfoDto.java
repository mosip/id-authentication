package io.mosip.preregistration.application.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class ExceptionInfoDto {
	private Boolean status;
	private List<ExceptionJSONInfo> err= new ArrayList<>();
	private List<ViewDto> response=new ArrayList<>();

}
