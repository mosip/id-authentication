package org.mosip.kernel.core.utils.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mosip.kernel.core.utils.JsonUtil;
import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.kernel.core.utils.exception.MosipJsonGenerationException;
import org.mosip.kernel.core.utils.exception.MosipJsonMappingException;
import org.mosip.kernel.core.utils.exception.MosipJsonParseException;
import org.mosip.kernel.core.utils.exception.MosipJsonProcessingException;
import org.mosip.kernel.core.utils.testEntities.Car;
import org.mosip.kernel.core.utils.testEntities.JsonUtilTestConstants;
import org.mosip.kernel.core.utils.testEntities.ParentCar2;
import org.mosip.kernel.core.utils.testEntities.SampleClass;


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
		assertThat(JsonUtil.javaObjectToJsonFile(car, file.getAbsolutePath()), is(true));
	}

	@Test
	public void testJavaObjectToJsonString() throws MosipJsonProcessingException {
		assertThat(JsonUtil.javaObjectToJsonString(car), is(JsonUtilTestConstants.EXPECTED_JSON));
	}

	@Test
	public void testJsonStringToJavaObject()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		Car car2 = (Car) JsonUtil.jsonStringToJavaObject(Car.class, JsonUtilTestConstants.EXPECTED_JSON);
		assertNotNull(car2);
		assertThat(car2.getColor(), is("Black"));
		assertThat(car2.getType(), is("BMW"));

	}

	@Test
	public void testJsonFileToJavaObject() throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("sample2.json").getFile());
		Car car2 = (Car) JsonUtil.jsonFileToJavaObject(Car.class, file.getAbsolutePath());
		assertNotNull(car2);
		assertThat(car2.getColor(), is("Blue"));
		assertThat(car2.getType(), is("Audi"));
	}

	@Test
	public void testJsonToJacksonJsonNode() throws MosipIOException {

		assertThat(JsonUtil.jsonToJacksonJson(JsonUtilTestConstants.jsonString, "type"), is("FIAT"));
	}

	@Test
	public void testJsonStringToJavaList() throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		List<Object> listElements = JsonUtil.jsonStringToJavaList(JsonUtilTestConstants.jsonCarArray);
		assertThat(listElements.toString(), is("[{color=Black, type=BMW}, {color=Red, type=FIAT}]"));
	}

	@Test
	public void testJsonStringToJavaMap() throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		Map<String, Object> mapElements = JsonUtil.jsonStringToJavaMap(JsonUtilTestConstants.jsonString);
		assertThat(mapElements.toString(), is("{color=Black, type=FIAT}"));
	}

	@Test(expected = MosipIOException.class)
	public void testJavaObjectToJsonFileWithIOException()
			throws MosipJsonGenerationException, MosipJsonMappingException, MosipIOException {

		JsonUtil.javaObjectToJsonFile("", "C:/InvalidLocation");
	}

	@Test(expected = MosipJsonParseException.class)
	public void testjsonStringtoJavaObjectWithParseException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtil.jsonStringToJavaObject(Car.class, JsonUtilTestConstants.jsonParserError);
	}

	@Test(expected = MosipJsonMappingException.class)
	public void testjsonStringtoJavaObjectWithMappingException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtil.jsonStringToJavaObject(Car.class, JsonUtilTestConstants.jsonCarArray2);
	}

	@Test(expected = MosipJsonParseException.class)
	public void testjsonFiletoJavaObjectWithParseException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("sampleParse.json").getFile());

		JsonUtil.jsonFileToJavaObject(SampleClass.class, file.getAbsolutePath());
	}

	@Test(expected = MosipJsonMappingException.class)
	public void testjsonFiletoJavaObjectWithMappingException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("samplex.json").getFile());

		JsonUtil.jsonFileToJavaObject(ParentCar2.class, file.getAbsolutePath());
	}

	@Test(expected = MosipIOException.class)
	public void testjsonFiletoJavaObjectWithIOException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtil.jsonFileToJavaObject(ParentCar2.class, "C:/InvalidLocation");
	}

	@Test(expected = MosipIOException.class)
	public void testJsonToJacksonJsonWithIOException() throws MosipIOException {
		JsonUtil.jsonToJacksonJson(JsonUtilTestConstants.jsonCarArray2, "");
	}

	@Test(expected = MosipJsonParseException.class)
	public void testjsonStringToJavaListWithParseException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtil.jsonStringToJavaList(JsonUtilTestConstants.jsonParserError);
	}

	@Test(expected = MosipJsonMappingException.class)
	public void testjsonStringToJavaListWithMappingException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtil.jsonStringToJavaList(JsonUtilTestConstants.jsonCarArray2);
	}

	@Test(expected = MosipJsonParseException.class)
	public void testjsonStringToJavaMapWithParseException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtil.jsonStringToJavaMap(JsonUtilTestConstants.jsonParserError);
	}

	@Test(expected = MosipJsonMappingException.class)
	public void testjsonStringToJavaMapWithMappingException()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		JsonUtil.jsonStringToJavaMap(JsonUtilTestConstants.jsonCarArray2);
	}

}
