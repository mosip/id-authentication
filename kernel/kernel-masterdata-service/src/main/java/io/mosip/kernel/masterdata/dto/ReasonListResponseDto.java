package io.mosip.kernel.masterdata.dto;

import java.util.List;

import io.mosip.kernel.masterdata.entity.id.CodeLangCodeAndRsnCatCodeID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReasonListResponseDto  {
	
	
	private List<CodeLangCodeAndRsnCatCodeID> reasonList;
	

}
