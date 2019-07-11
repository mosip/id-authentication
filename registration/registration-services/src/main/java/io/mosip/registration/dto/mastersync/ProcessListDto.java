package io.mosip.registration.dto.mastersync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ProcessListDto extends MasterSyncBaseDto {

	private String id;

	private String name;

	private String descr;

	private String langCode;

	/** The is Active. */
	private Boolean isActive;

}
