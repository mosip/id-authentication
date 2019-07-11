package io.mosip.service;

import java.io.IOException;
import org.apache.log4j.Logger;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import io.restassured.response.Response;

public class AssertPreReg {
	protected static Logger logger = Logger.getLogger(AssertPreReg.class);
	ObjectMapper oMapper = new ObjectMapper();
	static JSONObject jsonObject = new JSONObject();
	static JSONArray jsonArray = new JSONArray();

	public boolean assertPreRegistration(Response response, JSONObject object, List<String> outerKeys,
			List<String> innerKeys) throws JsonProcessingException, IOException, ParseException {

		JSONObject obj1 = AssertPreReg.getComparableBody(response.asString(), outerKeys, innerKeys);
		JSONObject obj2 = AssertPreReg.getComparableBody(object.toString(), outerKeys, innerKeys);
		logger.info(obj1);
		logger.info(obj2);
		Gson g = new Gson();
		Type mapType = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> firstMap = g.fromJson(obj1.toJSONString(), mapType);
		Map<String, Object> secondMap = g.fromJson(obj2.toJSONString(), mapType);
		logger.info(com.google.common.collect.Maps.difference(firstMap, secondMap));
		try {
			if (obj1.hashCode() == obj2.hashCode()) {
				Assert.assertEquals(obj1, obj2);
				return true;
			} else {
				return false;
			}
		} catch (AssertionError e) {
			logger.info("Assertion fails");
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
