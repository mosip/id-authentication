package io.mosip.authentication.fw.precon;

import java.util.Map;

public abstract class MessagePrecondtion {
	
	public abstract Map<String, String> parseAndWriteFile(String inputFilePath, Map<String, String> fieldvalue,
			String outputFilePath, String propFileName);

	public abstract Map<String,String> retrieveMappingAndItsValueToPerformOutputValidation(String filePath);
	
	public static MessagePrecondtion getPrecondtionObject(String filePath) {
		MessagePrecondtion msgPrecon = null;
		if (filePath.endsWith(".json"))
			msgPrecon = new JsonPrecondtion();
		else if (filePath.endsWith(".xml"))
			msgPrecon = new XmlPrecondtion();
		return msgPrecon;
	}
}
