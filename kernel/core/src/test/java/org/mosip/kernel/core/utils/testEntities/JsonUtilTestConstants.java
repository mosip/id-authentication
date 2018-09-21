package org.mosip.kernel.core.utils.testEntities;

/**
 * This class contains constants used to test Json util class
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class JsonUtilTestConstants {

	public static String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
	public static String jsonCarArray2 = "[{ \"color\" : \"Black\" \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
	public static String jsonParserError = "{'id':1,'name':'John'}";
	public static String jsonParserError2 = "\"swagger: \\\"2.0\\\"" + "info: " + "  version: \\\"1.0.0\\\""
			+ "  title: API which causes JsonParseException" + "paths: " + "  /pets: " + "    post: "
			+ "      parameters: " + "      - name: \\\"pet\\\"" + "        in: \\\"body\\\"" + "        schema:\r\n"
			+ "          type: object\r\n" + "        x-examples:" + "          application/json:\r\n"
			+ "            foo: bar\r\n" + "      responses:" + "        \\\"200\\\":"
			+ "          description: Success\r\n" + "          schema:\r\n" + "            type: object\"";
	public static String jsonString = "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";
	public static String jsonCarArray = "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";

}
