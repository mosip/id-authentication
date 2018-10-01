package org.mosip.kernel.core.util.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mosip.kernel.core.util.JsonUtils;
import org.mosip.kernel.core.util.exception.MosipIOException;
import org.mosip.kernel.core.util.exception.MosipJsonGenerationException;
import org.mosip.kernel.core.util.exception.MosipJsonMappingException;
import org.mosip.kernel.core.util.exception.MosipJsonParseException;
import org.mosip.kernel.core.util.exception.MosipJsonProcessingException;
import org.mosip.kernel.core.util.testEntities.Car;
import org.mosip.kernel.core.util.testEntities.JsonUtilTestConstants;
import org.mosip.kernel.core.util.testEntities.ParentCar2;
import org.mosip.kernel.core.util.testEntities.SampleClass;

/**
 * Unit test for JsonUtil class
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 */

public class JsonUtilTest {

	Car car = new Car("Black", "BMW");
	Car car2;
	ParentCar2 parentCar2;

	@Test
	public void testJavaObjectToJsonFile()
			throws MosipJsonGenerationException, MosipJsonMappingException, MosipIOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("sample.json").getFile());
		assertThat(JsonUtils.javaObjectToJsonFile(car, file.getAbsolutePath()), is(true));
	}

	@Test
	public void testJavaObjectToJsonString() throws MosipJsonProcessingException {
		String jsonString = JsonUtils.javaObjectToJsonString(car);

		jsonString = jsonString.replaceAll("\r", "");// \r and \n
		assertThat(jsonString.contains("Black"), is(true));

	}

	@Test
	public void testJsonStringToJavaObject()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		Car car2 = (Car) JsonUtils.jsonStringToJavaObject(Car.class, JsonUtilTestConstants.json);
		assertNotNull(car2);
		assertThat(car2.getColor(), is("Black"));
		assertThat(car2.getType(), is("BMW"));

	}

	@Test
	public void testJsonFileToJavaObject() throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("sample2.json").getFile());
		Car car2 = (Car) JsonUtils.jsonFileToJavaObject(Car.class, file.getAbsolutePath());
		assertNotNull(car2);
		assertThat(car2.getColor(), is("Blue"));
		assertThat(car2.getType(), is("Audi"));
	}

	@Test
	public void testJsonToJacksonJsonNode() throws MosipIOException {

		assertThat(JsonUtils.jsonToJacksonJson(JsonUtilTestConstants.jsonString, "type"), is("FIAT"));
	}

	@Test
	public void testJsonStringToJavaList() throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		List<Object> listElements = JsonUtils.jsonStringToJavaList(JsonUtilTestConstants.jsonCarArray);
		assertThat(listElements.toString(), is("[{color=Black, type=BMW}, {color=Red, type=FIAT}]"));
	}

	@Test
	public void testJsonStringToJavaMap() throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		Map<String, Object> mapElements = JsonUtils.jsonStringToJavaMap(JsonUtilTestConstants.jsonString);
		assertThat(mapElements.toString(), is("{color=Black, type=FIAT}"));
	}

	@Test(expected = MosipIOException.class)
	public void testJavaObjectToJsonFileWithIOException()
			throws MosipJsonGenerationException, MosipJsonMappingException, MosipIOException {

		JsonUtils.javaObjectToJsonFile("", "C:/InvalidLocation");
	}

	@Test(expected = MosipJsonParseException.class)
	public void testjsonStringtoJavaObjectWithParseException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtils.jsonStringToJavaObject(Car.class, JsonUtilTestConstants.jsonParserError);
	}

	@Test(expected = MosipJsonMappingException.class)
	public void testjsonStringtoJavaObjectWithMappingException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtils.jsonStringToJavaObject(Car.class, JsonUtilTestConstants.jsonCarArray2);
	}

	@Test(expected = MosipJsonParseException.class)
	public void testjsonFiletoJavaObjectWithParseException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("sampleParse.json").getFile());

		JsonUtils.jsonFileToJavaObject(SampleClass.class, file.getAbsolutePath());
	}

	@Test(expected = MosipJsonMappingException.class)
	public void testjsonFiletoJavaObjectWithMappingException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("samplex.json").getFile());

		JsonUtils.jsonFileToJavaObject(ParentCar2.class, file.getAbsolutePath());
	}

	@Test(expected = MosipIOException.class)
	public void testjsonFiletoJavaObjectWithIOException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtils.jsonFileToJavaObject(ParentCar2.class, "C:/InvalidLocation");
	}

	@Test(expected = MosipIOException.class)
	public void testJsonToJacksonJsonWithIOException() throws MosipIOException {
		JsonUtils.jsonToJacksonJson(JsonUtilTestConstants.jsonCarArray2, "");
	}

	@Test(expected = MosipJsonParseException.class)
	public void testjsonStringToJavaListWithParseException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtils.jsonStringToJavaList(JsonUtilTestConstants.jsonParserError2);
	}

	@Test(expected = MosipJsonMappingException.class)
	public void testjsonStringToJavaListWithMappingException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtils.jsonStringToJavaList(JsonUtilTestConstants.jsonCarArray2);
	}

	@Test(expected = MosipJsonParseException.class)
	public void testjsonStringToJavaMapWithParseException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtils.jsonStringToJavaMap(JsonUtilTestConstants.jsonParserError);
	}

	@Test(expected = MosipJsonMappingException.class)
	public void testjsonStringToJavaMapWithMappingException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtils.jsonStringToJavaMap(JsonUtilTestConstants.jsonCarArray2);
	}

}
