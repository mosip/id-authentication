package io.mosip.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
			ArrayList<String> listOfElementToRemove) throws JsonProcessingException, IOException, ParseException {
		JSONObject expectedResponseBody = (JSONObject) new JSONParser().parse(expectedResponse.asString());
		JSONObject actualResponseBody = actualResponse;
		
			expectedResponseBody = AssertKernel.removeElementFromBody(expectedResponseBody, listOfElementToRemove);
			actualResponseBody = AssertKernel.removeElementFromBody(actualResponse, listOfElementToRemove);
		
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

			System.err.println("======" + diffJson + "==========");
			if (diffJson.toString().equals("[]")) {
				System.out.println("equal");
				return true;
			}

			for (int i = 0; i < diffJson.size(); i++) {
				JsonNode operation = diffJson.get(i);
				if (!operation.get("op").toString().equals("\"move\"")) {
					System.out.println("not equal");
					return false;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("equal");
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
	public static JSONObject removeElementFromBody(JSONObject responce, ArrayList<String> listOfElementToRemove) throws ParseException {
		for (String elementToRemove : listOfElementToRemove) {
			responce.remove(elementToRemove);
		}
		
		return responce;
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
							 System.err.println("passed data to fetch does not match with Reponse data");
							 return false;
						 }
					 }
				 }
				 for(int j=0;j<attributesToValidate.size();j++) {
					 String key = attributesToValidate.get(j);
				 if (!object.containsKey(key) || object.get(key)==null) 
				        {
					 System.err.println(key+"  is not present in Response data");
					 		return false;
				        }
				 }
				      
		    }
		return true;
	}

}
