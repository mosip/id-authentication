package io.mosip.kernel.masterdata.validator;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomIntegerDeserializer extends JsonDeserializer<Object> {

	@Override
	public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		Integer value = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);

		value = mapper.readValue(p, Integer.class);

		return value;
	}

}
