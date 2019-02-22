package io.mosip.registration.processor.manual.verification.response.builder;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationAssignResponseDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationBioDemoResponseDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationPacketResponseDTO;

@Component
public class ManualVerificationResponseBuilder{
	
	@Autowired
	private static Environment env;
	private static final String MVS_APPLICATION_VERSION = "mosip.registration.processor.application.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";


	public static String buildManualVerificationSuccessResponse(Object classType,String id) {

		if(classType.getClass()==ManualVerificationDTO.class) {
			ManualVerificationAssignResponseDTO response = new ManualVerificationAssignResponseDTO();
			if (Objects.isNull(response.getId())) {
				response.setId(id);
			}
			response.setError(null);
			response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
			response.setVersion(env.getProperty(MVS_APPLICATION_VERSION));
			response.setResponse((ManualVerificationDTO)classType);
			Gson gson = new GsonBuilder().serializeNulls().create();
			return gson.toJson(response);
		
		}else if(classType.getClass()==String.class) {
			ManualVerificationBioDemoResponseDTO response = new ManualVerificationBioDemoResponseDTO();
			if (Objects.isNull(response.getId())) {
				response.setId(id);
			} 
			response.setError(null);
			response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
			response.setVersion(env.getProperty(MVS_APPLICATION_VERSION));
			response.setFile((String)classType);
			Gson gson = new GsonBuilder().serializeNulls().create();
			return gson.toJson(response);
		}else {

			ManualVerificationPacketResponseDTO response = new ManualVerificationPacketResponseDTO();
			if (Objects.isNull(response.getId())) {
				response.setId(id);
			}
			response.setError(null);
			response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
			response.setVersion(env.getProperty(MVS_APPLICATION_VERSION));
			response.setResponse((PacketMetaInfo)classType);
			Gson gson = new GsonBuilder().serializeNulls().create();
			return gson.toJson(response);
		}
	}

}
