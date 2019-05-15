package io.mosip.kernel.cryptosignature.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.cryptosignature.dto.SignResponseRequestDto;
import io.mosip.kernel.cryptosignature.service.CryptoSignatureService;

@Service
public class CryptoSignatureServiceImpl implements CryptoSignatureService {

	@Autowired
	SignatureUtil signatureUtil;

	@Override
	public SignatureResponse signResponse(SignResponseRequestDto signResponseRequestDto) {
		return (signatureUtil.signResponse(signResponseRequestDto.getResponse()));

	}

}
