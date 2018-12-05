package io.mosip.kernel.masterdata.dto.postresponse;

import java.util.List;

import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data


public class PostResponseDto {
	private List<CodeAndLanguageCodeID> results;
}
