package io.mosip.authentication.core.dto.idrepo;

import io.mosip.authentication.core.dto.indauth.BaseAuthRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class IdRequestDTO.
 *
 * @author Rakesh Roshan
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IdRequestDTO extends BaseAuthRequestDTO {

	/** The timestamp. */
	private String timestamp;

	/** The uin. */
	private String uin;

	/** The status. */
	private String status;

	/** The registration id. */
	private String registrationId;

	/** The request. */
	private Object request;
}