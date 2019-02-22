package io.mosip.registration.processor.packet.receiver.builder;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.packet.receiver.dto.PacketReceiverResponseDTO;
import io.mosip.registration.processor.packet.receiver.dto.ResponseDTO;

@Component
public class PacketReceiverResponseBuilder{
	
	@Autowired
	private static Environment env;
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	private static final String APPLICATION_VERSION = "mosip.registration.processor.application.version";
	private static final String MODULE_ID = "mosip.registration.processor.packet.id";

	/**
	 * Builds the packet receiver exception response.
	 *
	 * @param ex the ex
	 * @return the string
	 */
	public static String buildPacketReceiverResponse(String statusCode) {

		PacketReceiverResponseDTO response = new PacketReceiverResponseDTO();
		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(MODULE_ID));
		}
		response.setError(null);
		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		response.setVersion(env.getProperty(APPLICATION_VERSION));
		ResponseDTO responseDTO=new ResponseDTO();
		responseDTO.setStatus(statusCode);
		response.setResponse(responseDTO);
		Gson gson = new GsonBuilder().serializeNulls().create();
		return gson.toJson(response);
	}

}
