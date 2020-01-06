package io.mosip.registration.util.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;

/**
 * This class will give the Page Flow
 * 
 * @author Sravya Surampalli
 *
 */
@Component
public class PageFlow {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(PageFlow.class);

	/**
	 * This method sets the initial page flow for all the functionalities like New
	 * Registration, On-boarding, UIN Update.
	 * 
	 * <p>
	 * The page flow will be stored in a map, the page name as key and its
	 * visibility status (true/false) for a particular functionality as value.
	 * </p>
	 * 
	 * <p>
	 * After updating the maps with the page names and their visibility statuses,
	 * these maps will be stored in {@link ApplicationContext} so that they can be
	 * accessed from anywhere.
	 * </p>
	 */
	public void getInitialPageDetails() {

		LOGGER.info(LoggerConstants.LOG_REG_PAGE_FLOW, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Preparing Page flow map for New Registration, Onboard, UIN Update");

		Map<String, Map<String, Boolean>> registrationMap = new LinkedHashMap<>();
		Map<String, Map<String, Boolean>> onboardMap = new LinkedHashMap<>();

		Map<String, Boolean> onboardUserParent = new LinkedHashMap<>();
		onboardUserParent.put(RegistrationConstants.VISIBILITY, true);
		onboardMap.put(RegistrationConstants.ONBOARD_USER_PARENT, onboardUserParent);

		Map<String, Boolean> demographicMap = new LinkedHashMap<>();
		demographicMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.DEMOGRAPHIC_DETAIL, demographicMap);

		Map<String, Boolean> docMap = new LinkedHashMap<>();
		docMap.put(RegistrationConstants.VISIBILITY, true);
		docMap.put(RegistrationConstants.DOCUMENT_PANE, true);
		docMap.put(RegistrationConstants.EXCEPTION_PANE, true);
		registrationMap.put(RegistrationConstants.DOCUMENT_SCAN, docMap);

		Map<String, Boolean> exceptionMap = new LinkedHashMap<>();
		exceptionMap.put(RegistrationConstants.VISIBILITY, true);
		exceptionMap.put(RegistrationConstants.FINGER_PANE, true);
		exceptionMap.put(RegistrationConstants.IRIS_PANE, true);
		registrationMap.put(RegistrationConstants.BIOMETRIC_EXCEPTION, exceptionMap);
		onboardMap.put(RegistrationConstants.BIOMETRIC_EXCEPTION, exceptionMap);

		Map<String, Boolean> guardianBioMap = new LinkedHashMap<>();
		guardianBioMap.put(RegistrationConstants.VISIBILITY, false);
		registrationMap.put(RegistrationConstants.GUARDIAN_BIOMETRIC, guardianBioMap);

		Map<String, Boolean> fingerPrintMap = new LinkedHashMap<>();
		fingerPrintMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.FINGERPRINT_CAPTURE, fingerPrintMap);
		onboardMap.put(RegistrationConstants.FINGERPRINT_CAPTURE, fingerPrintMap);

		Map<String, Boolean> irisMap = new LinkedHashMap<>();
		irisMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.IRIS_CAPTURE, irisMap);
		onboardMap.put(RegistrationConstants.IRIS_CAPTURE, irisMap);

		Map<String, Boolean> faceMap = new LinkedHashMap<>();
		faceMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.FACE_CAPTURE, faceMap);
		onboardMap.put(RegistrationConstants.FACE_CAPTURE, faceMap);

		Map<String, Boolean> previewMap = new LinkedHashMap<>();
		previewMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.REGISTRATION_PREVIEW, previewMap);

		Map<String, Boolean> authMap = new LinkedHashMap<>();
		authMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.OPERATOR_AUTHENTICATION, authMap);

		Map<String, Boolean> onBoardSuccessMap = new LinkedHashMap<>();
		onBoardSuccessMap.put(RegistrationConstants.VISIBILITY, true);
		onboardMap.put(RegistrationConstants.ONBOARD_USER_SUCCESS, onBoardSuccessMap);

		updateRegMap(registrationMap, RegistrationConstants.APPLICATION_NAME);
		updateRegMap(onboardMap, RegistrationConstants.ONBOARD);

		ApplicationContext.map().put(RegistrationConstants.ONBOARD_LIST, getOnboardPageList(onboardMap));
		ApplicationContext.map().put(RegistrationConstants.ONBOARD_MAP, onboardMap);
		ApplicationContext.map().put(RegistrationConstants.REGISTRATION_MAP, registrationMap);
		ApplicationContext.map().put(RegistrationConstants.BIOMETRIC_EXCEPTION_FLOW,
				registrationMap.get(RegistrationConstants.BIOMETRIC_EXCEPTION).get(RegistrationConstants.VISIBILITY));

		LOGGER.info(LoggerConstants.LOG_REG_PAGE_FLOW, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Updating Map and storing in Application Context");

	}

	private void updateRegMap(Map<String, Map<String, Boolean>> registrationMap, String page) {

		LOGGER.info(LoggerConstants.LOG_REG_PAGE_FLOW, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Updating Map values based on Configuration ");

		updateDetailMap(registrationMap,
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.FINGERPRINT_DISABLE_FLAG)),
				RegistrationConstants.FINGERPRINT_CAPTURE, RegistrationConstants.BIOMETRIC_EXCEPTION,
				RegistrationConstants.FINGER_PANE);
		updateDetailMap(registrationMap,
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.IRIS_DISABLE_FLAG)),
				RegistrationConstants.IRIS_CAPTURE, RegistrationConstants.BIOMETRIC_EXCEPTION,
				RegistrationConstants.IRIS_PANE);
		updateDetailMap(registrationMap,
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.FACE_DISABLE_FLAG)),
				RegistrationConstants.FACE_CAPTURE, "", "");

		LOGGER.info(LoggerConstants.LOG_REG_PAGE_FLOW, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Updating Child values of Map based on Configuration");

		if (page.equalsIgnoreCase(RegistrationConstants.APPLICATION_NAME)) {
			updateDetailMap(registrationMap,
					String.valueOf(ApplicationContext.map().get(RegistrationConstants.DOC_DISABLE_FLAG)),
					RegistrationConstants.DOCUMENT_PANE, RegistrationConstants.DOCUMENT_SCAN, "");

			if (!registrationMap.get(RegistrationConstants.BIOMETRIC_EXCEPTION).get(RegistrationConstants.FINGER_PANE)
					&& !registrationMap.get(RegistrationConstants.BIOMETRIC_EXCEPTION)
							.get(RegistrationConstants.IRIS_PANE)) {
				registrationMap.get(RegistrationConstants.BIOMETRIC_EXCEPTION).put(RegistrationConstants.VISIBILITY,
						false);
				registrationMap.get(RegistrationConstants.DOCUMENT_SCAN).put(RegistrationConstants.EXCEPTION_PANE,
						false);
			}

			if (!registrationMap.get(RegistrationConstants.BIOMETRIC_EXCEPTION).get(RegistrationConstants.VISIBILITY)
					&& registrationMap.containsKey(RegistrationConstants.DOCUMENT_SCAN) && !registrationMap
							.get(RegistrationConstants.DOCUMENT_SCAN).get(RegistrationConstants.DOCUMENT_PANE)) {
				registrationMap.get(RegistrationConstants.DOCUMENT_SCAN).put(RegistrationConstants.VISIBILITY, false);
			}
		}

		LOGGER.info(LoggerConstants.LOG_REG_PAGE_FLOW, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Map values are updated based on Configuration");
	}

	private void updateDetailMap(Map<String, Map<String, Boolean>> detailMap, String flagVal, String pageId,
			String subPane, String childId) {

		LOGGER.info(LoggerConstants.LOG_REG_PAGE_FLOW, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Updating Visibility values based on Configuration");

		if (RegistrationConstants.DISABLE.equalsIgnoreCase(flagVal)) {

			if (pageId.equals(RegistrationConstants.DOCUMENT_PANE)) {
				detailMap.get(subPane).put(pageId, false);
			} else {
				detailMap.get(pageId).put(RegistrationConstants.VISIBILITY, false);

				if (!subPane.isEmpty()) {
					detailMap.get(subPane).put(childId, false);
				}
			}
		}

		LOGGER.info(LoggerConstants.LOG_REG_PAGE_FLOW, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Visibility values updated based on Configuration");
	}

	private List<String> getOnboardPageList(Map<String, Map<String, Boolean>> onboardMap) {
		List<String> onboardPageList = new ArrayList<>();

		LOGGER.info(LoggerConstants.LOG_REG_PAGE_FLOW, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Retrieving page ids from map to list based on visibility");

		for (Map.Entry<String, Map<String, Boolean>> entry : onboardMap.entrySet()) {
			if (entry.getValue().get(RegistrationConstants.VISIBILITY)) {
				onboardPageList.add(entry.getKey());
			}
		}
		return onboardPageList;
	}

}
