package io.mosip.authentication.core.util;

import java.util.Map;
import java.util.Objects;

import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;

/**
 * @author Manoj SP
 * @author Nagarjuna
 *
 */
@Component
public class IdTypeUtil {

	private static Logger mosipLogger = IdaLogger.getLogger(IdTypeUtil.class);

	@Autowired
	IdValidationUtil idValidator;

	@Value("#{${mosip.ida.handle-types.regex}}")
	private Map<String, String> handleTypesRegex;

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

	public boolean validateHandle(String handle) {
		try {
			if(Objects.nonNull(handleTypesRegex)) {
				if(StringUtils.isEmpty(handle))
					return false;

				int index = handle.lastIndexOf("@");
				if(index <= 0)
					return false;

				String handleType = handle.substring(index);
				if(!handleTypesRegex.containsKey(handleType))
					return false;

				return handle.matches(handleTypesRegex.get(handleType));
			}
		} catch (BaseUncheckedException e) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "VALIDATE_HANDLE",
					"Failed to validate handle >> "+ e.getMessage());
		}
		return false;
	}

	public IdType getIdType(String id) throws IdAuthenticationBusinessException {
		if (this.validateUin(id))
			return IdType.UIN;
		if (this.validateVid(id))
			return IdType.VID;
		if (this.validateHandle(id))
			return IdType.HANDLE;
		throw new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
				String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
						IdAuthCommonConstants.INDIVIDUAL_ID));
	}
}
