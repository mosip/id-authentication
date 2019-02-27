package io.mosip.testdata.mapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class JsonPathGenMain {
	static JsonPathGenMain obj = new JsonPathGenMain();
	String parentpath="";
	public static void main(String arg[]) throws IOException
	{
		String inputFilePath="D:\\MosipTestJavaWokspace\\IDAAutomation\\Resources\\masterdataIDA.json";
		//obj.generateJSONPath(new String(Files.readAllBytes(Paths.get(inputFilePath))));
		JsonPathGen o = new JsonPathGen(new String(Files.readAllBytes(Paths.get(inputFilePath))));
		o.generateJsonMappingDic("D:\\MosipTestJavaWokspace\\IDAAutomation\\Resources\\sample12.properties");
		//System.out.println(o.getPathList());
	}
}
