package io.mosip.authentication.testdata.mapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The class to generate json mapping in property file
 * 
 * @author Vignesh
 *
 */

public class JsonPathGenMain {

	public static void main(String arg[]) throws IOException {
		String inputFilePath = "D:\\Git\\QA_IDA\\automationtests\\src\\test\\resources\\ida\\TestData\\Otp\\OtpGeneration\\input\\otp-generate-request.json";
		JsonPathGen o = new JsonPathGen(new String(Files.readAllBytes(Paths.get(inputFilePath))));
		o.generateJsonMappingDic(
				"D:\\Git\\QA_IDA\\automationtests\\src\\test\\resources\\ida\\TestData\\Otp\\OtpGeneration\\map.properties");
	}
}
