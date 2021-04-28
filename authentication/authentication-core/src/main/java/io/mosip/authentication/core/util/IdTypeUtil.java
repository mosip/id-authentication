package io.mosip.authentication.core.util;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;

/**
 * @author Manoj SP
 *
 */
@Component
public class IdTypeUtil {

	@Autowired(required = false)
	private UinValidator<String> uinValidator;

	@Autowired(required = false)
	private VidValidator<String> vidValidator;

	public boolean validateUin(String uin) {
		try {
			if (Objects.nonNull(uinValidator))
				return uinValidator.validateId(uin);
			else
				return false;
		} catch (InvalidIDException e) {
			return false;
		}
	}

	public boolean validateVid(String vid) {
		try {
			if (Objects.nonNull(vidValidator))
				return vidValidator.validateId(vid);
			else
				return false;
		} catch (InvalidIDException e) {
			return false;
		}
	}

	public IdType getIdType(String id) throws IdAuthenticationBusinessException {
		if (this.validateUin(id))
			return IdType.UIN;
		if (this.validateVid(id))
			return IdType.VID;
		throw new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
				String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
						IdAuthCommonConstants.INDIVIDUAL_ID));
	}
}
