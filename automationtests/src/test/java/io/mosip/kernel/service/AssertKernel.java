package io.mosip.kernel.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;

import io.restassured.response.Response;

/**
 * @author Arjun and Ravikant
 *
 */
public class AssertKernel {
	
	private static Logger logger = Logger.getLogger(AssertKernel.class);
	/**
	 * this method accepts expected and actual response and return boolean value
	 * 
	 * @param expectedResponse
	 * @param actualResponse
	 * @param listOfElementToRemove
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws ParseException
	 */
	public boolean assertKernel(Response expectedResponse, JSONObject actualResponse,
			ArrayList<String> listOfElementToRemove){
		JSONObject expectedResponseBody = null;
		JSONObject actualResponseBody = actualResponse;
		try {
			expectedResponseBody = (JSONObject) new JSONParser().parse(expectedResponse.asString());
			expectedResponseBody = AssertKernel.removeElementFromBody(expectedResponseBody, listOfElementToRemove);
			actualResponseBody = AssertKernel.removeElementFromBody(actualResponse, listOfElementToRemove);
			} catch (ParseException e) {
				logger.info(e.getMessage());
			}
		
		return jsonComparison(expectedResponseBody, actualResponseBody);

	}
	
	
	/**
	 * @author Arjun chandramohan
	 * Created for id repo assertion
	 * @param expectedResponse
	 * @param actualResponse
	 * @param listOfElementToRemove
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws ParseException
	 */
		public boolean assertIdRepo(Object expectedResponse, Object actualResponse, ArrayList<String> listOfElementToRemove)
				throws JsonProcessingException, IOException, ParseException {
			JSONObject expectedResponseBody = (JSONObject) new JSONParser().parse(expectedResponse.toString());
			JSONObject actualResponseBody = (JSONObject) new JSONParser().parse(actualResponse.toString());

			expectedResponseBody = AssertKernel.removeElementFromBody(expectedResponseBody, listOfElementToRemove);
			actualResponseBody = AssertKernel.removeElementFromBody(actualResponseBody, listOfElementToRemove);

			return jsonComparison(expectedResponseBody, actualResponseBody);

		}
	/**
	 * this function compare the request and response json and return the boolean
	 * value
	 * 
	 * @param expectedResponseBody
	 * @param actualResponseBody
	 * @return boolean value
	 */
	public boolean jsonComparison(Object expectedResponseBody, Object actualResponseBody) {
		JSONObject reqObj = (JSONObject) expectedResponseBody;
		JSONObject resObj = (JSONObject) actualResponseBody;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode requestJson = mapper.readTree(reqObj.toString());
			JsonNode responseJson = mapper.readTree(resObj.toString());
			JsonNode diffJson = JsonDiff.asJson(requestJson, responseJson);

			//logger.error("======" + diffJson + "==========");
			if (diffJson.toString().equals("[]")) {
				logger.info("equal");
				return true;
			}

			for (int i = 0; i < diffJson.size(); i++) {
				JsonNode operation = diffJson.get(i);
				if (!operation.get("op").toString().equals("\"move\"")) {
					logger.error("not equal");
					return false;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("equal");
		return true;

	}

	/**
	 * this method accept json as string and remove the element to remove
	 * 
	 * @param input
	 * @param removekey
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject removeElementFromBody(JSONObject response, ArrayList<String> listOfElementToRemove) throws ParseException {
		for (String elementToRemove : listOfElementToRemove) {
			if(response.containsKey(elementToRemove))
				response.remove(elementToRemove);
			else {
				JSONObject responseJson = null;

				if(response.containsKey("response") && response.get("response")!=null)
					{
					responseJson = (JSONObject)response.get("response");
					if(responseJson.containsKey(elementToRemove))responseJson.remove(elementToRemove);
					response.put("response", responseJson);
					}
				else if(response.containsKey("request") && response.get("request")!=null)
					{
					responseJson = (JSONObject)response.get("request");
					if(responseJson.containsKey(elementToRemove))responseJson.remove(elementToRemove);
					response.put("request", responseJson);
					}

			}
		}
		
		return response;
	}
	
	/**
	 * this method takes Json array of objects obtained in response, and key value of field send to fetch data and  
	 * list of attributes which should be present in response objects.
	 * @param responseArray
	 * @param attributesToValidate
	 * @param passedAttributes
	 * @return
	 */
	public static boolean validator(JSONArray responseArray, List<String> attributesToValidate, HashMap<String, String> passedAttributes)
	{
		    for(int i=0;i<responseArray.size();i++) {
		    	 JSONObject object = (JSONObject) responseArray.get(0);
				 if(passedAttributes.size()>0)
				 {
					 String[] keys = passedAttributes.keySet().toArray(new String[passedAttributes.size()]);
					 for(int j=0;j<keys.length;j++) {
						 if(!passedAttributes.get(keys[j]).equals(object.get(keys[j]).toString()))
						 {
							 logger.error("passed data to fetch does not match with Reponse data");
							 return false;
						 }
					 }
				 }
				 for(int j=0;j<attributesToValidate.size();j++) {
					 String key = attributesToValidate.get(j);
				 if (!object.containsKey(key) || object.get(key)==null) 
				        {
					 logger.error(key+"  is not present in Response data");
					 		return false;
				        }
				 }
				      
		    }
		return true;
	}

}
