package io.mosip.registration.dto.mastersync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * The Class AppDetailDto.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class AppDetailDto extends MasterSyncBaseDto {

	/** The id. */
	private String id;

	/** The name. */
	private String name;

	/** The descr. */
	private String descr;

	/** The lang code. */
	private String langCode;
	
	/** The is Active. */
	private Boolean isActive;

}
