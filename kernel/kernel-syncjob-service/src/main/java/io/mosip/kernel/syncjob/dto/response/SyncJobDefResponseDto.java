package io.mosip.kernel.syncjob.dto.response;

import java.util.List;

import io.mosip.kernel.syncjob.dto.SyncJobDefDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncJobDefResponseDto {

	private List<SyncJobDefDto> syncJobDefinitions;
}
