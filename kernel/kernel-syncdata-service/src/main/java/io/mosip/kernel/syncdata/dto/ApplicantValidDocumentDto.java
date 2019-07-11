package io.mosip.kernel.syncdata.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Srinivasan
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicantValidDocumentDto extends BaseDto {

	private String appTypeCode;

	private String docTypeCode;

	private String docCatCode;

	private String langCode;

}
