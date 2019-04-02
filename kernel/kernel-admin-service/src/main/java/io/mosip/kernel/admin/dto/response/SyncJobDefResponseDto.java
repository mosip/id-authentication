package io.mosip.kernel.admin.dto.response;

import java.util.List;

import io.mosip.kernel.admin.dto.SyncJobDefDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncJobDefResponseDto {

	private List<SyncJobDefDto> syncJobDefinitions;
}
