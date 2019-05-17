package io.mosip.kernel.signature.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.signature.constant.CryptoSignatureErrorCode;
import io.mosip.kernel.signature.dto.PublicKeyRequestDto;
import io.mosip.kernel.signature.dto.SignResponseRequestDto;
import io.mosip.kernel.signature.dto.TimestampRequestDto;
import io.mosip.kernel.signature.dto.ValidatorResponseDto;
import io.mosip.kernel.signature.exception.CryptoFailureException;
import io.mosip.kernel.signature.service.CryptoSignatureService;

@Service
public class CryptoSignatureServiceImpl implements CryptoSignatureService {

	@Autowired
	SignatureUtil signatureUtil;

	@Override
	public SignatureResponse signResponse(SignResponseRequestDto signResponseRequestDto) {
		return (signatureUtil.signResponse(signResponseRequestDto.getResponse()));

	}

	@Override
	public ValidatorResponseDto validateWithPublicKey(PublicKeyRequestDto validateWithPublicKeyRequestDto)
			throws InvalidKeySpecException, NoSuchAlgorithmException {

		boolean status = signatureUtil.validateWithPublicKey(validateWithPublicKeyRequestDto.getResponseSignature(),
				validateWithPublicKeyRequestDto.getResponseBody(), validateWithPublicKeyRequestDto.getPublicKey());

		if (status) {
			ValidatorResponseDto response = new ValidatorResponseDto();
			response.setMessage("VALIDATION_SUCCESSFUL");
			response.setStatus("success");
			return response;
		} else {
			throw new CryptoFailureException(CryptoSignatureErrorCode.NOT_VALID.getErrorCode(),
					CryptoSignatureErrorCode.NOT_VALID.getErrorMessage(), null);
		}
	}

	@Override
	public ValidatorResponseDto validate(TimestampRequestDto timestampRequestDto)
			throws InvalidKeySpecException, NoSuchAlgorithmException {

		boolean status = signatureUtil.validate(timestampRequestDto.getResponseSignature(),
				timestampRequestDto.getResponseBody(), DateUtils.formatToISOString(timestampRequestDto.getTimestamp()));

		if (status) {
			ValidatorResponseDto response = new ValidatorResponseDto();
			response.setMessage("VALIDATION_SUCCESSFUL");
			response.setStatus("success");
			return response;
		} else {
			throw new CryptoFailureException(CryptoSignatureErrorCode.NOT_VALID.getErrorCode(),
					CryptoSignatureErrorCode.NOT_VALID.getErrorMessage(), null);
		}

	}

}
