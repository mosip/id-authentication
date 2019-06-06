package io.mosip.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import io.restassured.response.Response;

public class AssertResponses {


	private static Logger logger = Logger.getLogger(AssertResponses.class);
	static JSONObject jsonObject = new JSONObject();
	static JSONArray jsonArray = new JSONArray();
	static SoftAssert softAssert=new SoftAssert();
	@SuppressWarnings("serial")
	public static boolean assertResponses(Response response, JSONObject object, List<String> outerKeys, List<String> innerKeys)
			throws JsonProcessingException, IOException, ParseException {
		JSONObject obj1 = AssertResponses.getComparableBody(response.asString(), outerKeys, innerKeys);
		JSONObject obj2 = AssertResponses.getComparableBody(object.toString(), outerKeys, innerKeys);
		Gson g = new Gson(); 
		Type mapType = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> firstMap = g.fromJson(obj1.toJSONString(), mapType);
		Map<String, Object> secondMap = g.fromJson(obj2.toJSONString(), mapType);
		logger.info(com.google.common.collect.Maps.difference(firstMap, secondMap));
			try {
				Assert.assertTrue(obj1.equals(obj2));
				logger.info("both object are equal");
			} catch (AssertionError e) {
				Assert.assertTrue(false, "Response Data Mismatch Failure  : difference is: "+com.google.common.collect.Maps.difference(firstMap, secondMap));
				return false;
			}
			softAssert.assertAll();
			return true;
	}

	@SuppressWarnings("serial")
	public static boolean assertArrayResponses(Response response, JSONArray objectArray, List<String> outerKeys, List<String> innerKeys)
			throws JsonProcessingException, IOException, ParseException {
		
		String empty = "";
		JSONObject obj1 = new JSONObject();
		JSONObject obj2 = new JSONObject();
		String newResponse = response.asString();

		newResponse = newResponse.replace("[", empty).replace("]",empty);

		if(newResponse.contains("},{")){
			newResponse = newResponse.replace("},{", "}@{");
			logger.info("newResponse: "+newResponse);
			String[] newResponse1 = newResponse.split("@");

			for(int i=1; i<=objectArray.size();i++){
				for(String res : newResponse1){
					obj1 = AssertResponses.getComparableBody(res, outerKeys, innerKeys);
				}
			}
		}else 
			obj1 =  AssertResponses.getComparableBody(newResponse, outerKeys, innerKeys);
		//			JSONObject obj1 = AssertResponses.getComparableBody(response.getBody().asString(), outerKeys, innerKeys);


			for(int i=1; i<=objectArray.size();i++){
				obj2 = AssertResponses.getComparableBody(objectArray.get(i-1).toString(), outerKeys, innerKeys);
			}		
			
			logger.info(obj1);
			logger.info(obj2);

		Gson g = new Gson(); 
		Type mapType = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> firstMap = g.fromJson(obj1.toJSONString(), mapType);
		Map<String, Object> secondMap = g.fromJson(obj2.toJSONString(), mapType);
		logger.info(com.google.common.collect.Maps.difference(firstMap, secondMap));
		if (obj1.hashCode() == obj2.hashCode()) {
			softAssert.assertTrue(obj1.equals(obj2));
			softAssert.assertAll();
			return true;

		} else { 
			return false;
		}

	}


	public static JSONObject getComparableBody(String input, List<String> outerKeys, List<String> innerKeys)
			throws ParseException {

		JSONObject object = (JSONObject) new JSONParser().parse(input);
		Iterator<String> itr = outerKeys.iterator();
		while (itr.hasNext()) {
			object.remove(itr.next());
		}
		for (Object keys : object.keySet()) {

			try {
				jsonObject = (JSONObject) object.get(keys.toString());

				recursiveObject(jsonObject, innerKeys);

			} catch (ClassCastException exp) {
			}

			try {
				jsonArray = (JSONArray) object.get(keys);
				if (jsonArray != null)
					recursiveArray(jsonArray, innerKeys);
			} catch (ClassCastException exp1) {
			}
		}
		return object;
	}

	private static void recursiveObject(JSONObject parsebleObject, List<String> innerKeys) {
		if (parsebleObject != null) {
			for (String keys : innerKeys) {
				parsebleObject.remove(keys);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	private static void recursiveArray(JSONArray parsebleArray, List innerKeys) {
		Iterator itr = innerKeys.iterator();
		Iterator arrayItr = parsebleArray.iterator();
		if (parsebleArray != null) {
			while (arrayItr.hasNext()) {
				JSONObject obj = (JSONObject) arrayItr.next();
				recursiveObject(obj, innerKeys);
			}
		}
	}

}
