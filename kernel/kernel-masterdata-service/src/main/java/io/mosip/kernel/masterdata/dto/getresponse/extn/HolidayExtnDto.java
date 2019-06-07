package io.mosip.kernel.masterdata.dto.getresponse.extn;

import java.time.LocalDate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO class for Holiday Data
 * 
 * @author Abhishek Kumar
 * @author Sidhant Agarwal
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Holiday", description = "Holiday details")
public class HolidayExtnDto extends BaseDto {

	@ApiModelProperty(value = "id", required = true, dataType = "java.lang.Integer")
	private int id;

	@ApiModelProperty(value = "locationCode", required = true, dataType = "java.lang.String")
	private String locationCode;

	@ApiModelProperty(value = "holidayDate", required = true, dataType = "java.lang.Integer")
	private LocalDate holidayDate;
	/**
	 * Holiday day is day of week as integer value, week start from Monday , Monday
	 * is 1 and Sunday is 7
	 */
	@ApiModelProperty(value = "holidayDay", required = true, dataType = "java.lang.String")
	private String holidayDay;
	/**
	 * Holiday month is month of the year as integer value.
	 */
	@ApiModelProperty(value = "holidayMonth", required = true, dataType = "java.lang.String")
	private String holidayMonth;
	@ApiModelProperty(value = "holidayYear", required = true, dataType = "java.lang.String")
	private String holidayYear;

	@ApiModelProperty(value = "holidayName", required = true, dataType = "java.lang.String")
	private String holidayName;

	@ApiModelProperty(value = "holidayDesc", required = true, dataType = "java.lang.String")
	private String holidayDesc;

	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

}