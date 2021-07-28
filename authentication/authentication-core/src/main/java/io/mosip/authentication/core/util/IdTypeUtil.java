package io.mosip.authentication.core.util;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;

/**
 * @author Manoj SP
 * @author Nagarjuna
 *
 */
@Component
public class IdTypeUtil {

	@Autowired
	IdValidationUtil idValidator;

	public boolean validateUin(String uin) {
		try {
			if (Objects.nonNull(idValidator))
				return idValidator.validateUIN(uin);
			else
				return false;
		} catch (InvalidIDException  | IdAuthenticationBusinessException e) {
			return false;
		}
	}

	public boolean validateVid(String vid) {
		try {
			if (Objects.nonNull(idValidator))
				return idValidator.validateVID(vid);
			else
				return false;
		} catch (InvalidIDException | IdAuthenticationBusinessException e) {
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
