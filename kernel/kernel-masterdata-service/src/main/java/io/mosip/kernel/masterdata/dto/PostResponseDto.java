package io.mosip.kernel.masterdata.dto;

import java.util.List;

import io.mosip.kernel.masterdata.entity.CodeLangCodeId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
	private List<CodeLangCodeId> successfully_created;
}
