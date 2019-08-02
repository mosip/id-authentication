package io.mosip.registration.processor.core.token.validation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.APIAuthorityList;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.token.validation.dto.TokenResponseDTO;
import io.mosip.registration.processor.core.token.validation.exception.AccessDeniedException;
import io.mosip.registration.processor.core.token.validation.exception.InvalidTokenException;
import io.mosip.registration.processor.core.util.JsonUtil;

@Service
public class TokenValidator {
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(TokenValidator.class);

	/** If token is not valid */
	private static final String INVALIDTOKENMESSAGE = "No Token Available In The Header";

	/** If token is valid */
	private static final String VALIDATEDMESSAGE = "Token Validation Successful For Role: ";

	/**  */
	private static final String ACCESSDENIEDMESSAGE = "Access Denied For Role: ";

	@Autowired
	Environment env;

	public void validate(String token, String url) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"TokenValidator::validate()::entry");

		if (token == null)
			throw new InvalidTokenException(INVALIDTOKENMESSAGE);
		try {
			URL obj = new URL(env.getProperty("TOKENVALIDATE"));
			URLConnection urlConnection = obj.openConnection();
			HttpsURLConnection con = (HttpsURLConnection) urlConnection;

			con.setRequestProperty("Cookie", token);
			con.setRequestMethod("POST");

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			InputStream responseStream = new ByteArrayInputStream(response.toString().getBytes());
			TokenResponseDTO tokenResponseDTO = (TokenResponseDTO) JsonUtil.inputStreamtoJavaObject(responseStream,
					TokenResponseDTO.class);

			if (tokenResponseDTO.getErrors() != null) {
				throw new InvalidTokenException(tokenResponseDTO.getErrors()[0].getMessage());
			} else {
				if (!validateAccess(url, tokenResponseDTO.getResponse().getRole())) {
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), "",
							ACCESSDENIEDMESSAGE + tokenResponseDTO.getResponse().getRole());
					throw new AccessDeniedException(ACCESSDENIEDMESSAGE + tokenResponseDTO.getResponse().getRole());
				}
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), VALIDATEDMESSAGE,
						tokenResponseDTO.getResponse().getRole());
			}

		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));
		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"TokenValidator::validate()::exit");

	}

	public boolean validateAccess(String url, String role) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"TokenValidator::validateAccess()::entry");
		if (url.contains("receiver")) {
			for (String assignedRole : APIAuthorityList.PACKETRECEIVER.getList()) {
				if (role.contains(assignedRole))
					return true;
			}
		} else if (url.contains("sync")) {
			for (String assignedRole : APIAuthorityList.PACKETSYNC.getList()) {
				if (role.contains(assignedRole))
					return true;
			}
		} else if (url.contains("status")) {
			for (String assignedRole : APIAuthorityList.REGISTRATIONSTATUS.getList()) {
				if (role.contains(assignedRole))
					return true;
			}
		} else if (url.contains("manual")) {
			for (String assignedRole : APIAuthorityList.MANUALVERIFICTION.getList()) {
				if (role.contains(assignedRole))
					return true;
			}
		} else if (url.contains("print")) {
			for (String assignedRole : APIAuthorityList.PRINTSTAGE.getList()) {
				if (role.contains(assignedRole))
					return true;
			}
		} else if (url.contains("biodedupe")) {
			for (String assignedRole : APIAuthorityList.BIODEDUPE.getList()) {
				if (role.contains(assignedRole))
					return true;
			}
		} else if (url.contains("abis")) {
			for (String assignedRole : APIAuthorityList.ABIS.getList()) {
				if (role.compareToIgnoreCase(assignedRole) == 0)
					return true;
			}
		} else if (url.contains("bio")) {
			for (String assignedRole : APIAuthorityList.BIO.getList()) {
				if (role.compareToIgnoreCase(assignedRole) == 0)
					return true;
			}
		} else if (url.contains("uploader")) {
			for (String assignedRole : APIAuthorityList.PACKETUPLOADER.getList()) {
				if (role.compareToIgnoreCase(assignedRole) == 0)
					return true;
			}
		} else if (url.contains("requesthandler")) {
			for (String assignedRole : APIAuthorityList.REQUESTHANDLER.getList()) {
				if (role.contains(assignedRole))
					return true;
			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"TokenValidator::validateAccess()::exit");
	
		return false;
	}
}
