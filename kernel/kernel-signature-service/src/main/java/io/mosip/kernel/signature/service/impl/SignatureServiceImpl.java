package io.mosip.kernel.signature.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.signature.constant.SignatureErrorCode;
import io.mosip.kernel.signature.dto.PublicKeyRequestDto;
import io.mosip.kernel.signature.dto.SignRequestDto;
import io.mosip.kernel.signature.dto.TimestampRequestDto;
import io.mosip.kernel.signature.dto.ValidatorResponseDto;
import io.mosip.kernel.signature.exception.SignatureFailureException;
import io.mosip.kernel.signature.service.SignatureService;

@Service
public class SignatureServiceImpl implements SignatureService {

	@Autowired
	SignatureUtil signatureUtil;

	@Override
	public SignatureResponse signResponse(SignRequestDto signResponseRequestDto) {
		return (signatureUtil.signResponse(signResponseRequestDto.getData()));

	}

	@Override
	public ValidatorResponseDto validateWithPublicKey(PublicKeyRequestDto validateWithPublicKeyRequestDto)
			throws InvalidKeySpecException, NoSuchAlgorithmException {

		boolean status = signatureUtil.validateWithPublicKey(validateWithPublicKeyRequestDto.getSignature(),
				validateWithPublicKeyRequestDto.getData(), validateWithPublicKeyRequestDto.getPublickey());

		if (status) {
			ValidatorResponseDto response = new ValidatorResponseDto();
			response.setMessage("VALIDATION_SUCCESSFUL");
			response.setStatus("success");
			return response;
		} else {
			throw new SignatureFailureException(SignatureErrorCode.NOT_VALID.getErrorCode(),
					SignatureErrorCode.NOT_VALID.getErrorMessage(), null);
		}
	}

	@Override
	public ValidatorResponseDto validate(TimestampRequestDto timestampRequestDto)
			throws InvalidKeySpecException, NoSuchAlgorithmException {

		boolean status = signatureUtil.validate(timestampRequestDto.getSignature(),
				timestampRequestDto.getData(), DateUtils.formatToISOString(timestampRequestDto.getTimestamp()));

		if (status) {
			ValidatorResponseDto response = new ValidatorResponseDto();
			response.setMessage("VALIDATION_SUCCESSFUL");
			response.setStatus("success");
			return response;
		} else {
			throw new SignatureFailureException(SignatureErrorCode.NOT_VALID.getErrorCode(),
					SignatureErrorCode.NOT_VALID.getErrorMessage(), null);
		}

	}

}
