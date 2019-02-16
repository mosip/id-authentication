package io.mosip.registration.processor.status.dto;

import java.util.List;
import io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationSyncRequestDTO extends BaseRequestResponseDTO {
	
	private List<SyncRegistrationDto> request;
	
}
