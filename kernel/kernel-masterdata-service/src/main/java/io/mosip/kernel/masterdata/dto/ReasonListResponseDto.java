package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;
import java.util.List;

import io.mosip.kernel.masterdata.entity.CodeLangCodeAndRsnCatCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReasonListResponseDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8029609332352937274L;
	
	List<CodeLangCodeAndRsnCatCode> successfully_created;
	

}
