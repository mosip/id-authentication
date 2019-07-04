package io.mosip.registration.dto.mastersync;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Sreekar Chukka
 * @since 1.0.0 Instantiates a new screen detail dto.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ScreenDetailDto extends MasterSyncBaseDto {

	/** The id. */
	private String id;

	/** The app id. */
	private String appId;

	/** The name. */
	private String name;

	/** The descr. */
	private String descr;

	/** The isActive. */
	private Boolean isActive;

	/** The langCode. */
	private String langCode;

}