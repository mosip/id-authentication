package io.mosip.kernel.tokenidgenerator.generator;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.HMACUtils;

@Component
public class TokenIDGenerator {

	@Value("${mosip.kernel.tokenid.uin.salt}")
	private String uinSalt;

	@Value("${mosip.kernel.tokenid.length}")
	private int tokenIDLength;

	@Value("${mosip.kernel.tokenid.partnercode.salt}")
	private String partnerCodeSalt;

	public String generateTokenID(String uin, String partnerCode) {
		String uinHash = HMACUtils.digestAsPlainText(HMACUtils.generateHash((uin + uinSalt).getBytes()));
		String hash = HMACUtils
				.digestAsPlainText(HMACUtils.generateHash((partnerCodeSalt + partnerCode + uinHash).getBytes()));
		return new BigInteger(hash.getBytes()).toString().substring(0, tokenIDLength);
	}

}
