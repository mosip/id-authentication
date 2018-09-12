package org.mosip.kernel.core.utils.testEntities;

/**
 * @author Sidhant Agarwal
 *
 */
public class JsonUtilTestConstants {
	public static String EXPECTED_JSON = "{\r\n  \"color\" : \"Black\",\r\n  \"type\" : \"BMW\"\r\n}";
	public static String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
	public static String EXPECTED_JSON2 = "{\"color\":\"Black\",\"type\":\"BMW\"}";

	public static String jsonCarArray2 = "[{ \"color\" : \"Black\" \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
	public static String jsonParserError = "\"swagger: \\\"2.0\\\"\r\n" + "info: \r\n" + "  version: \\\"1.0.0\\\"\r\n"
			+ "  title: API which causes JsonParseException\r\n" + "paths: \r\n" + "  /pets: \r\n" + "    post: \r\n"
			+ "      parameters: \r\n" + "      - name: \\\"pet\\\"\r\n" + "        in: \\\"body\\\"\r\n"
			+ "        schema:\r\n" + "          type: object\r\n" + "        x-examples:\r\n"
			+ "          application/json:\r\n" + "            foo: bar\r\n" + "      responses:\r\n"
			+ "        \\\"200\\\":\r\n" + "          description: Success\r\n" + "          schema:\r\n"
			+ "            type: object\"";
	public static String jsonString = "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";
	public static String jsonCarArray = "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";

}
