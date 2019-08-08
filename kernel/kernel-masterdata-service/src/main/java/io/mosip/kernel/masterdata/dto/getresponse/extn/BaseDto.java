package io.mosip.kernel.masterdata.dto.getresponse.extn;

import java.time.LocalDateTime;

import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * this class will contains metadata
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@ApiModel(value = "base dto", description = "this class will contains metadata")
public class BaseDto {
	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "isActive", required = true, dataType = "java.lang.Boolean")
	private Boolean isActive;

	@ApiModelProperty(value = "createdBy", required = true, dataType = "java.lang.String")
	private String createdBy;

	@FilterType(types = { FilterTypeEnum.BETWEEN })
	@ApiModelProperty(value = "createdBy", required = true, dataType = "java.time.LocalDateTime")
	private LocalDateTime createdDateTime;

	@ApiModelProperty(value = "updatedBy", required = false, dataType = "java.lang.String")
	private String updatedBy;

	@ApiModelProperty(value = "updatedDateTime", required = false, dataType = "java.time.LocalDateTime")
	private LocalDateTime updatedDateTime;

	@ApiModelProperty(value = "isDeleted", required = false, dataType = "java.lang.Boolean")
	private Boolean isDeleted;

	@ApiModelProperty(value = "deletedDateTime", required = false, dataType = "java.time.LocalDateTime")
	private LocalDateTime deletedDateTime;

}
