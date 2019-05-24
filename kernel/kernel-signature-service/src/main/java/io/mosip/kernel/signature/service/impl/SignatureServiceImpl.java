package io.mosip.kernel.signature.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.signature.constant.SignatureConstant;
import io.mosip.kernel.signature.constant.SignatureErrorCode;
import io.mosip.kernel.signature.dto.PublicKeyRequestDto;
import io.mosip.kernel.signature.dto.SignRequestDto;
import io.mosip.kernel.signature.dto.TimestampRequestDto;
import io.mosip.kernel.signature.dto.ValidatorResponseDto;
import io.mosip.kernel.signature.exception.PublicKeyParseException;
import io.mosip.kernel.signature.exception.SignatureFailureException;
import io.mosip.kernel.signature.service.SignatureService;

/**
 * @author Uday Kumar
 * @author Urvil
 *
 */
@Service
public class SignatureServiceImpl implements SignatureService {

	@Autowired
	private SignatureUtil signatureUtil;

	@Override
	public SignatureResponse sign(SignRequestDto signRequestDto) {
		return signatureUtil.sign(signRequestDto.getData(),DateUtils.getUTCCurrentDateTimeString());
	}

	@Override
	public ValidatorResponseDto validateWithPublicKey(PublicKeyRequestDto validateWithPublicKeyRequestDto)
			throws InvalidKeySpecException, NoSuchAlgorithmException {

		boolean status = signatureUtil.validateWithPublicKey(validateWithPublicKeyRequestDto.getSignature(),
				validateWithPublicKeyRequestDto.getData(), validateWithPublicKeyRequestDto.getPublickey());

		if (status) {
			ValidatorResponseDto response = new ValidatorResponseDto();
			response.setMessage(SignatureConstant.VALIDATION_SUCCESSFUL);
			response.setStatus(SignatureConstant.SUCCESS);
			return response;
		} else {
			throw new SignatureFailureException(SignatureErrorCode.NOT_VALID.getErrorCode(),
					SignatureErrorCode.NOT_VALID.getErrorMessage(), null);
		}
	}

	@Override
	public ValidatorResponseDto validate(TimestampRequestDto timestampRequestDto) {

		boolean status;
		try {
			status = signatureUtil.validate(timestampRequestDto.getSignature(),
					timestampRequestDto.getData(), DateUtils.formatToISOString(timestampRequestDto.getTimestamp()));
		} catch (InvalidKeySpecException| NoSuchAlgorithmException exception) {
			throw new  PublicKeyParseException(SignatureErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(), exception.getMessage(), exception);
		}

		if (status) {
			ValidatorResponseDto response = new ValidatorResponseDto();
			response.setMessage(SignatureConstant.VALIDATION_SUCCESSFUL);
			response.setStatus(SignatureConstant.SUCCESS);
			return response;
		} else {
			throw new SignatureFailureException(SignatureErrorCode.NOT_VALID.getErrorCode(),
					SignatureErrorCode.NOT_VALID.getErrorMessage(), null);
		}

	}


}
