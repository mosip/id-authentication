package io.mosip.kernel.core.test.model;

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
	public static String jsonParserError4 = "{\"page_attr\":{\"query\":null,\"page_type\":\"retailer\"},\"sales\":,\"request_attr\":{\"search_referer_engine\":null,\"user_id\":false,\"user_agent\":\"Mozilla/5.0 (en-us) AppleWebKit/525.13 (KHTML, like Gecko; Google Web Preview) Version/3.1 Safari/525.13\",\"is_ajax\":false,\"path\":\"/retailer/johnston-murphy-outlet-1433538\",\"ip\":\"74.125.44.85\",\"user_is_staff\":false,\"geo_info\":{\"city\":\"Mountain View\",\"region\":\"CA\",\"area_code\":650,\"longitude\":-122.05740356445312,\"country_code3\":\"USA\",\"latitude\":37.4192008972168,\"postal_code\":\"55555,\"dma_code\":807,\"country_code\":\"US\",\"country_name\":\"United States\",\"location\":\"Mountain View, CA 94043\"},\"search_referer_term\":null,\"method\":\"GET\"}}";
	public static String jsonParserError3 = "";
	public static String jsonParserError2 = "\"swagger: \\\"2.0\\\"\r\n" + "info: \r\n" + "  version: \\\"1.0.0\\\"\r\n"
			+ "  title: API which causes JsonParseException\r\n" + "paths: \r\n" + "  /pets: \r\n" + "    post: \r\n"
			+ "      parameters: \r\n" + "      - name: \\\"pet\\\"\r\n" + "        in: \\\"body\\\"\r\n"
			+ "        schema:\r\n" + "          type: object\r\n" + "        x-examples:\r\n"
			+ "          application/json:\r\n" + "            foo: bar\r\n" + "      responses:\r\n"
			+ "        \\\"200\\\":\r\n" + "          description: Success\r\n" + "          schema:\r\n"
			+ "            type: object\"";
	public static String jsonString = "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";
	public static String jsonCarArray = "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";

}
