package io.mosip.resident.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.resident.dto.JsonValue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class JsonUtilTest {
	private String jsonString;
	private JSONObject jsonObject;

	@Before
	public void setUp() throws IOException {
		jsonString = "{\"identity\":{\"fullName\":[{\"language\":\"eng\",\"value\":\"firstName\"},{\"language\":\"ara\",\"value\":\"lastName\"}],\"dateOfBirth\":\"1996/01/01\",\"referenceIdentityNumber\":\"2323232323232323\",\"proofOfIdentity\":{\"value\":\"POI_Passport\",\"type\":\"DOC001\",\"format\":\"jpg\"},\"IDSchemaVersion\":1,\"phone\":\"9898989899\",\"age\":23,\"email\":\"sdf@sdf.co\"}}\r\n"
				+ "";

		jsonObject = JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
	}

	@Test
	public void getJSONObjectTest() throws IOException {

		JSONObject result = JsonUtil.getJSONObject(jsonObject, "identity");
		assertEquals("9898989899", result.get("phone"));

	}

	@Test
	public void getJSONArrayTest() {
		JSONArray jsonArray = JsonUtil.getJSONArray(JsonUtil.getJSONObject(jsonObject, "identity"), "fullName");
		JSONObject result = JsonUtil.getJSONObjectFromArray(jsonArray, 0);
		String firstName = JsonUtil.getJSONValue(result, "value");
		assertEquals("firstName", firstName);
	}

	@Test
	public void testJsonUtilWriteValue() throws IOException {
		String result = JsonUtil.objectMapperObjectToJson(jsonObject);
		System.out.println(result);
		assertTrue(jsonString.trim().equals(result));
	}

	@Test
	public void getJsonValuesTest() throws ReflectiveOperationException {
		JsonValue[] jsonvalues = JsonUtil.getJsonValues(JsonUtil.getJSONObject(jsonObject, "identity"), "fullName");
		assertEquals(2, jsonvalues.length);

	}

}
