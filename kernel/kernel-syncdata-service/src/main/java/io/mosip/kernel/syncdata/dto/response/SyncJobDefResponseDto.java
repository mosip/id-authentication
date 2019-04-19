package io.mosip.kernel.syncdata.dto.response;

import java.util.List;

import io.mosip.kernel.syncdata.dto.SyncJobDefDto;
import lombok.Data;

@Data
public class SyncJobDefResponseDto {

	private List<SyncJobDefDto> syncJobDefinitions;
}
