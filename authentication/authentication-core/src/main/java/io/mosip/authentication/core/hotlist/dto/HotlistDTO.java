package io.mosip.authentication.core.hotlist.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HotlistDTO {

	private String status;
	private LocalDateTime startDTimes;
	private LocalDateTime expiryDTimes;

}
