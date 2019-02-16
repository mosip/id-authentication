package io.mosip.registration.processor.status.sync.response.dto;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * The Class PacketReceiverReqRespJsonSerializer.
 * @author Rishabh Keshari
 */
public class RegStatusReqRespJsonSerializer implements JsonSerializer<RegStatusResponseDTO> {

	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(RegStatusResponseDTO src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
	        object.add("id", context.serialize(src.getId()));
	        object.add("version", context.serialize(src.getVersion()));
	        object.add("timestamp", context.serialize(src.getTimestamp()));
	        object.add("response", context.serialize(src.getResponse()));
	        object.add("error", context.serialize(src.getError()));
	        return object;
	}

}