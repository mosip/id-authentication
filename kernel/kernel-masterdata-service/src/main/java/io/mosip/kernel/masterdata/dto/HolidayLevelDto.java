package io.mosip.kernel.masterdata.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "HolidayList", description = "Holiday list for the country level")
public class HolidayLevelDto {
	
	@ApiModelProperty(value = "locationCode", required = true, dataType = "java.lang.String")
	private String locationCode;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate holidayDate;
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;
	@ApiModelProperty(value = "holidayName", required = true, dataType = "java.lang.String")
	private String holidayName;
	@ApiModelProperty(value = "holidayDesc", required = true, dataType = "java.lang.String")
	private String holidayDesc;

}
