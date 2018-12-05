package io.mosip.authentication.service.impl.id.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.idrepo.ErrorDTO;
import io.mosip.authentication.core.dto.idrepo.IdRequestDTO;
import io.mosip.authentication.core.dto.idrepo.IdResponseDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@PropertySource("classpath:sample-output.properties")
@Service
public class IdRepoServiceImpl implements IdRepoService {

	private static Logger logger = IdaLogger.getLogger(OTPManager.class);

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/** The Constant AUTH_FACADE. */
	private static final String ID_REPO_SERVICE = "IDA - IdRepoService";

	@Autowired
	private RestHelper restHelper;

	@Autowired
	private RestRequestFactory restRequestFactory;

	@Value("${sample.demo.entity}")
	private String value;

	public IdResponseDTO getIdRepo(String uin) throws IdAuthenticationBusinessException {

		IdRequestDTO requestBody = new IdRequestDTO();
		requestBody.setUin(uin);

		RestRequestDTO buildRequest = null;
		IdResponseDTO requestSync = null;

		try {
			buildRequest = restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE, requestBody,
					IdResponseDTO.class);
			requestSync = restHelper.requestSync(buildRequest);

		} catch (RestServiceException e) {
			logger.error(SESSION_ID, ID_REPO_SERVICE, e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
		} catch (IDDataValidationException e) {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.KERNEL_OTP_GENERATION_REQUEST_FAILED, e);
		}

		//return String.valueOf(requestSync); 
		return requestSync;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, List<IdentityInfoDTO>> getIdInfo(String uinRefId) throws IdAuthenticationDaoException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			//Map<String, Object> outputMap = mapper.readValue(value, new TypeReference<Map>() {
			Map<String, Object> outputMap = mapper.readValue(uinRefId, new TypeReference<Map>() {
			});

			return outputMap.entrySet().parallelStream()
					.filter(entry -> entry.getKey().equals("response") && entry.getValue() instanceof Map)
					.flatMap(entry -> ((Map<String, Object>) entry.getValue()).entrySet().stream())
					.filter(entry -> entry.getKey().equals("identity") && entry.getValue() instanceof Map)
					.flatMap(entry -> ((Map<String, Object>) entry.getValue()).entrySet().stream())
					.collect(Collectors.toMap(Entry<String, Object>::getKey, entry -> {
						Object val = entry.getValue();
						if (val instanceof List) {
							List<Map> arrayList = (List) val;
							return arrayList.stream().filter(elem -> elem instanceof Map)
									.map(elem -> (Map<String, Object>) elem).map(map1 -> {
										IdentityInfoDTO idInfo = new IdentityInfoDTO();
										idInfo.setLanguage(String.valueOf(map1.get("language")));
										idInfo.setValue(String.valueOf(map1.get("value")));
										return idInfo;
									}).collect(Collectors.toList());

						}
						return Collections.emptyList();
					}));
		} catch (IOException e) {
			throw new IdAuthenticationDaoException();
		}

	}

	
	public static void main(String[] args) {
		ErrorDTO error = new ErrorDTO("errCode", "errMessage");
		List<ErrorDTO> list = new ArrayList<ErrorDTO>();
		list.add(error);
		
		IdResponseDTO res = new IdResponseDTO();
		res.setId("id");
		res.setErr(list);
		res.setRegistrationId("registrationId");
		res.setStatus("status");
		res.setTimestamp("timestamp");
		res.setVer("ver");
		System.out.println(res);
		System.out.println(res.toString());
		System.out.println(String.valueOf(res));
	}
}
