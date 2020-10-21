package io.mosip.authentication.core.util;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;

/**
 * @author Manoj SP
 *
 */
@Component
public class IdTypeUtil {

	@Autowired(required = false)
	private RidValidator<String> ridValidator;

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

	public boolean validateRid(String registrationId) {
		try {
			if (Objects.nonNull(ridValidator))
				return ridValidator.validateId(registrationId);
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

	public IdType getIdType(String id) {
		if (this.validateUin(id))
			return IdType.UIN;
		if (this.validateVid(id))
			return IdType.VID;
		if (this.validateRid(id))
			return IdType.RID;
		return IdType.USER_ID;
	}
}
