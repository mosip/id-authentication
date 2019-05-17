package io.mosip.kernel.signature.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.signature.dto.SignResponseRequestDto;
import io.mosip.kernel.signature.dto.ValidateWithPublicKeyRequestDto;
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
	public boolean validateWithPublicKey(ValidateWithPublicKeyRequestDto validateWithPublicKeyRequestDto)
			throws InvalidKeySpecException, NoSuchAlgorithmException {

		return (signatureUtil.validateWithPublicKey(validateWithPublicKeyRequestDto.getResponseSignature(),
				validateWithPublicKeyRequestDto.getResponseBody(), validateWithPublicKeyRequestDto.getPublicKey()));
	}

}
