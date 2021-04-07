package io.mosip.authentication.core.hotlist.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author Mamta A
 *
 */
@Data
public class HotlistDTO {

	/** The status. */
	private String status;
	
	/** The startDTimes. */
	private LocalDateTime startDTimes;
	
	/** The expiryDTimes. */
	private LocalDateTime expiryDTimes;

}
