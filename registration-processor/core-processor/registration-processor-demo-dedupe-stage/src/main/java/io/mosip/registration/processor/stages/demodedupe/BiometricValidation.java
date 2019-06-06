package io.mosip.registration.processor.stages.demodedupe;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileSystemManager;
import io.mosip.registration.processor.core.util.JsonUtil;
//remove the class when auth is fixed
@Component
public class BiometricValidation {

	/** The adapter. */
	@Autowired
	private FileSystemManager adapter;

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BiometricValidation.class);

	/** The Constant ENCODING. */
	public static final String ENCODING = "UTF-8";

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = File.separator;

	public boolean validateBiometric(String duplicateRid, String regId) throws ApisResourceAccessException, IOException, ParseException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		/*
		 * authRequestDTO.setIdvId(duplicateUin);
		 * authRequestDTO.setAuthType(authTypeDTO); request.setIdentity(identityDTO);
		 * authRequestDTO.setRequest(request);
		 * 
		 * AuthResponseDTO authResponseDTO = (AuthResponseDTO)
		 * restClientService.postApi(ApiName.AUTHINTERNAL, "", "", authRequestDTO,
		 * AuthResponseDTO.class); return authResponseDTO != null &&
		 * authResponseDTO.getStatus() != null &&
		 * authResponseDTO.getStatus().equalsIgnoreCase("y");
		 */
		InputStream demographicInfoStream = adapter.getFile(regId,PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
		String demographicJsonString  = IOUtils.toString(demographicInfoStream, ENCODING);
		JSONObject demographicJson = (JSONObject) JsonUtil.objectMapperReadValue(demographicJsonString,
				JSONObject.class);
		Date date = null;
		boolean isValid = false;
		JSONObject dobJson = null;
		Object dob=null;
		Object identityJson = demographicJson.get("identity");
		
		if(identityJson!=null)
		 dobJson =new JSONObject((Map) identityJson);
		
		if(dobJson!=null)
		 dob = dobJson.get("dateOfBirth");

		if(dob != null && dob.toString().trim() != "" ) {
			try {
				date = new SimpleDateFormat("yyyy/mm/dd").parse(dob.toString());
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				int year = calendar.get(Calendar.YEAR);
				if(year%2==0) {
					isValid=true;
				}
			} catch (ParseException e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
						regId, "Date Parse Exception in BiometricValidation" + e.getMessage()
						+ ExceptionUtils.getStackTrace(e));
				throw new ParseException(demographicJson.toString(), 0);
			}
		}

		return isValid;
	}
}
