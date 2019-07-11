package io.mosip.kernel.core.exception;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is the entity class for the BaseUncheckedException and
 * BaseCheckedException class.
 * 
 * @author Shashank Agrawal
 * @since 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
class InfoItem implements Serializable {

	private static final long serialVersionUID = -779695043380592601L;

	@Getter
	@Setter
	public String errorCode = null;

	@Getter
	@Setter
	public String errorText = null;

}
