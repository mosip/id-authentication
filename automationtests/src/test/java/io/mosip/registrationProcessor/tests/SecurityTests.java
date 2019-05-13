package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.util.StageValidationMethods;
import io.mosip.service.BaseTestCase;
import io.mosip.util.TokenGeneration;

public class SecurityTests extends BaseTestCase {
	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	StageValidationMethods apiRequest=new StageValidationMethods();
	public String getToken() {
		String tokenGenerationProperties=generateToken.readPropertyFile();
		tokenEntity=generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token=generateToken.getToken(tokenEntity);
		return token;
		}
	@BeforeClass
	public void getValidPacketPath() {
		Properties folderPath = new Properties();
		try {
			FileReader reader=new FileReader(new File(System.getProperty("user.dir") + "/src/config/folderPaths.properties"));
			folderPath.load(reader);
			reader.close();
			} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	@Test
	public void syncRequestWithValidToken() {
		String token=getToken();
		apiRequest.syncPacket("");
	}
	}
