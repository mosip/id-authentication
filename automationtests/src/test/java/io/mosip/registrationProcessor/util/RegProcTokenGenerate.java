package io.mosip.registrationProcessor.util;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.util.TokenGeneration;

public class RegProcTokenGenerate {
	TokenGeneration generateToken = new TokenGeneration();
	TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
	public String getRegProcAuthToken() {
	String tokenGenerationProperties = generateToken.readPropertyFile("syncTokenGenerationFilePath");
	tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
	String regProcAuthToken = generateToken.getToken(tokenEntity);
	return regProcAuthToken;
	}
	public String getAdminRegProcAuthToken() {
	TokenGenerationEntity adminTokenEntity = new TokenGenerationEntity();
	String adminTokenGenerationProperties = generateToken.readPropertyFile("getStatusTokenGenerationFilePath");
	adminTokenEntity = generateToken.createTokenGeneratorDto(adminTokenGenerationProperties);
	String adminRegProcAuthToken = generateToken.getToken(adminTokenEntity);
	return adminRegProcAuthToken;
	}
}
