package io.mosip.admin.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * masterdata card response dto
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataCardDto {
	private String dataCode;
	private String displayName;
}
