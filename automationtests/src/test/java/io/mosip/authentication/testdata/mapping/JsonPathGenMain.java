package io.mosip.authentication.testdata.mapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class JsonPathGenMain {
	static JsonPathGenMain obj = new JsonPathGenMain();
	String parentpath="";
	public static void main(String arg[]) throws IOException
	{
		String inputFilePath="D:\\Git\\dev-ida-qa\\automation\\mosip-qa\\src\\test\\resources\\ida\\TestData\\Demo\\Name\\tittle\\nameTittle.json";
		//obj.generateJSONPath(new String(Files.readAllBytes(Paths.get(inputFilePath))));
		JsonPathGen o = new JsonPathGen(new String(Files.readAllBytes(Paths.get(inputFilePath))));
		o.generateJsonMappingDic("D:\\Git\\dev-ida-qa\\automation\\mosip-qa\\src\\test\\resources\\ida\\TestData\\Demo\\Name\\tittle\\nameTittle.properties");
		//logger.info(o.getPathList());
	}
}
