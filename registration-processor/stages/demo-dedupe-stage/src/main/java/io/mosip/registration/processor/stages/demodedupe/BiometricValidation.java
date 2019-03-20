package io.mosip.registration.processor.stages.demodedupe;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
//remove the class when auth is fixed
@Component
public class BiometricValidation {

	/** The adapter. */
	@Autowired
	private FileSystemAdapter adapter;
	
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BiometricValidation.class);

	@Autowired
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	/** The Constant ENCODING. */
	public static final String ENCODING = "UTF-8";

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = File.separator;

	public boolean validateBiometric(String duplicateUin, String regId) throws ApisResourceAccessException, IOException {
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

		boolean isValid= false;
		InputStream demographicInfoStream = null;
		demographicInfoStream = adapter.getFile(regId,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
		String demographicInfo = IOUtils.toString(demographicInfoStream, ENCODING);
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		regProcessorIdentityJson = mapIdentityJsonStringToObject.readValue(demographicInfo,
				RegistrationProcessorIdentity.class);
		String doBValue = regProcessorIdentityJson.getIdentity().getDob().getValue();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy/mm/dd").parse(doBValue);
		} catch (ParseException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, "Date Parse Exception in BiometricValidation" + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
		}
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		if(year%2==0) {
			isValid=true;
		}				
		return isValid;
	}
}
