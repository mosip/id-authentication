package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Valid Document", description = "valid Document: Document category and Document type mapping")
public class ValidDocumentExtnDto extends BaseDto {

	@ApiModelProperty(value = "docTypeCode", required = true, dataType = "java.lang.String")
	private String docTypeCode;

	@ApiModelProperty(value = "docCategoryCode", required = true, dataType = "java.lang.String")
	private String docCategoryCode;

	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;
}