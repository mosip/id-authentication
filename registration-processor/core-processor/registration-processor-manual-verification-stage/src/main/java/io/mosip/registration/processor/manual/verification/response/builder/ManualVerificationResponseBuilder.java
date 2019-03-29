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
	
	

	public static String buildManualVerificationSuccessResponse(Object classType,String id,String version,String dateTimePattern) {

		if(classType.getClass()==ManualVerificationDTO.class) {
			ManualVerificationAssignResponseDTO response = new ManualVerificationAssignResponseDTO();
			if (Objects.isNull(response.getId())) {
				response.setId(id);
			}
			response.setErrors(null);
			response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(dateTimePattern));
			response.setVersion(version);
			response.setResponse((ManualVerificationDTO)classType);
			Gson gson = new GsonBuilder().create();
			return gson.toJson(response);
		
		}else if(classType.getClass()==String.class) {
			ManualVerificationBioDemoResponseDTO response = new ManualVerificationBioDemoResponseDTO();
			if (Objects.isNull(response.getId())) {
				response.setId(id);
			} 
			response.setErrors(null);
			response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(dateTimePattern));
			response.setVersion(version);
			response.setFile((String)classType);
			Gson gson = new GsonBuilder().create();
			return gson.toJson(response);
		}else {

			ManualVerificationPacketResponseDTO response = new ManualVerificationPacketResponseDTO();
			if (Objects.isNull(response.getId())) {
				response.setId(id);
			}
			response.setErrors(null);
			response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(dateTimePattern));
			response.setVersion(version);
			response.setResponse((PacketMetaInfo)classType);
			Gson gson = new GsonBuilder().create();
			return gson.toJson(response);
		}
	}

}
