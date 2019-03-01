package io.mosip.authentication.service.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;

@Component
public class MasterDataManager {

	/** The template manager. */
	private TemplateManager templateManager;

	/** UTF type. */
	private static final String ENCODE_TYPE = "UTF-8";

	/** Class path. */
	private static final String CLASSPATH = "classpath";

	/**
	 * The Template Manager Builder
	 */
	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	/**
	 * Environment
	 */
	@Autowired
	private Environment environment;

	/**
	 * The Rest Helper
	 */
	@Autowired
	private RestHelper restHelper;

	/**
	 * Id Info Helper
	 */
	@Autowired
	private IdInfoHelper idInfoHelper;

	/**
	 * The Rest request factory
	 */
	@Autowired
	private RestRequestFactory restFactory;

	private static final String SESSION_ID = "sessionId";

	/**
	 * IdTemplate Manager Logger
	 */
	private static Logger logger = IdaLogger.getLogger(MasterDataManager.class);

	@PostConstruct
	public void idTemplateManagerPostConstruct() {
		templateManager = templateManagerBuilder.encodingType(ENCODE_TYPE).enableCache(false).resourceLoader(CLASSPATH)
				.build();
	}

	public Map<String, List<String>> fetchGenderType() throws IdAuthenticationBusinessException {
		RestRequestDTO buildRequest = null;
		Map<String, List<Map<String, String>>> response = null;
		try {
			buildRequest = restFactory.buildRequest(RestServicesConstants.GENDER_TYPE_SERVICE, null, Map.class);
			response = restHelper.requestSync(buildRequest);
			List<Map<String, String>> value = response.get("genderType");
			Map<String, List<String>> genderTypes = new HashMap<>();
			for (Map<String, String> map : value) {
				String langCode = map.get("langCode");
				String genderName = map.get("genderName");
				List<String> list = genderTypes.computeIfAbsent(langCode, key -> new ArrayList<>());
				list.add(genderName);
			}
			return genderTypes;
		} catch (IDDataValidationException | RestServiceException e) {
			logger.error(SESSION_ID, this.getClass().getName(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}

	}

	public Map<String, List<String>> fetchTitles() throws IdAuthenticationBusinessException {
		RestRequestDTO buildRequest = null;
		Map<String, List<Map<String, String>>> response = null;
		try {
			buildRequest = restFactory.buildRequest(RestServicesConstants.TITLE_SERVICE, null, Map.class);
			response = restHelper.requestSync(buildRequest);
			List<Map<String, String>> value = response.get("titleList");
			Map<String, List<String>> titleList = new HashMap<>();
			for (Map<String, String> map : value) {
				String langCode = map.get("langCode");
				String genderName = map.get("titleName");
				List<String> list = titleList.computeIfAbsent(langCode, key -> new ArrayList<>());
				list.add(genderName);
			}
			return titleList;
		} catch (IDDataValidationException | RestServiceException e) {
			logger.error(SESSION_ID, this.getClass().getName(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}

	}

}
