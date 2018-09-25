package org.mosip.kernel.core.utils.testEntities;

import java.io.IOException;
import java.util.List;

import org.mosip.kernel.core.utils.JsonUtil;
import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.kernel.core.utils.exception.MosipJsonGenerationException;
import org.mosip.kernel.core.utils.exception.MosipJsonMappingException;
import org.mosip.kernel.core.utils.exception.MosipJsonParseException;
import org.mosip.kernel.core.utils.exception.MosipJsonProcessingException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 *
 * @author Sidhant Agarwal
 *
 */
public class App {
	static ParentCar parentCar;
	static ChildCar childCar;
	static ParentCar2 parentCar2;
	static ChildCar2 childCar2;

	/**
	 * @param args
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws MosipIOException
	 * @throws MosipJsonParseException
	 * @throws MosipJsonMappingException
	 * @throws MosipJsonGenerationException
	 * @throws MosipJsonProcessingException
	 */
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException,
			MosipIOException, MosipJsonParseException, MosipJsonMappingException, MosipJsonGenerationException,
			MosipJsonProcessingException {
		String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
		String jsonCarArray = "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
		parentCar = new ParentCar();
		childCar = parentCar.getChildCar();
		childCar.setCompanyName("Mercedes");
		childCar.setModelName("X class");
		parentCar.setColor("yellow");
		JsonUtil.jsonToJacksonJson(json, "type");
		// System.out.println(value);
		List<Object> lisT = JsonUtil.jsonStringToJavaList(jsonCarArray);
		System.out.println(lisT.toString());
		JsonUtil.jsonStringToJavaMap(json);
		// System.out.println(javaMap.toString());
		Car car = new Car();
		car.setColor("Black");
		car.setType("BMW");
		JsonUtil.jsonStringToJavaObject(Car.class, json);
		System.out.println("Hello:" + car.getType());
		// ObjectMapper objectMapper = new ObjectMapper();
		// Car car = objectMapper.readValue(json, Car.class);
		parentCar = new ParentCar();
		parentCar.getChildCar();
		car = (Car) JsonUtil.jsonStringToJavaObject(Car.class, json);
		// boolean value1=JsonUtil.javaObjectToJsonFile(car, "target/samplexx.json");
		// System.out.println(value1);
		// String value2=JsonUtil.javaObjectToJsonString(car);
		// System.out.println(value2);

		// parentCar.getChildCar();
		// car=(Car) JsonUtil.jsonToJavaObject(Car.class, json);
		// String res=JsonUtil.javaObjectToJson(car,"target/sample.json");
		// System.out.println(res);
		// System.out.println(parentCar.getColor()+"@@@@@@@@@@@@@"+childCar.getCompanyName()+"@@@@"+childCar.getModelName());

		/*
		 * parentCar2 = new ParentCar2(); //childCar2=new ChildCar2();
		 * childCar2.setCompanyName("Mercedes"); childCar2.setModelName("X class");
		 * parentCar2.setColor("yellow");
		 */

	}

	// Working code for a single class
	/*
	 * static Car car; public static void main(String[] args) throws
	 * JsonGenerationException, JsonMappingException, IOException { car = new Car();
	 * car.setColor("yellow"); car.setType("renault");
	 * JsonUtil.JavaObjectToJson(car,"target/car2.json"); }
	 */

}
