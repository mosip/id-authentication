package io.mosip.authentication.internal.service.validator;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.autntxn.dto.AutnTxnRequestDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.indauth.dto.IdType;

/**
 * To validate Auth transaction inputs
 *
 * @author Dinesh Karuppiah.T
 */
@Component
public class AuthTxnValidator extends InternalAuthRequestValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return AutnTxnRequestDto.class.equals(clazz);
	}

	/*
	 * To Validate AuthTransaction request
	 */
	@Override
	public void validate(Object target, Errors errors) {
		if (Objects.nonNull(target)) {
			AutnTxnRequestDto autnTxnDto = (AutnTxnRequestDto) target;
			validateIdvId(autnTxnDto.getIndividualId(), autnTxnDto.getIndividualIdType(), errors,
					IdAuthCommonConstants.IDV_ID);
		}

	}

}
