package io.mosip.registration.dto.mastersync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * Class AppAuthenticationMethodDto.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)

/**
 * Instantiates a new app authentication method dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new app authentication method dto.
 *
 * @param appId the app id
 * @param processId the process id
 * @param roleCode the role code
 * @param authMethodCode the auth method code
 * @param methodSequence the method sequence
 * @param langCode the lang code
 */
@AllArgsConstructor
public class AppAuthenticationMethodDto extends MasterSyncBaseDto {

	/** The app id. */

	private String appId;

	/** The process id. */

	private String processId;

	/** The role code. */

	private String roleCode;

	/** The auth method code. */

	private String authMethodCode;

	/** The method sequence. */

	private Integer methodSequence;

	/** The lang code. */

	private String langCode;

	/** The is Active. */
	private Boolean isActive;
}
