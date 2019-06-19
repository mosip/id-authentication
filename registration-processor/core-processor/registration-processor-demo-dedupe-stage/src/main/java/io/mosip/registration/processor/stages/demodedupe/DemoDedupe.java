package io.mosip.registration.processor.stages.demodedupe;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.auth.dto.IdentityDTO;
import io.mosip.registration.processor.core.auth.dto.IdentityInfoDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.stages.app.constants.DemoDedupeConstants;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class DemoDedupe.
 *
 * @author M1048358 Alok Ranjan
 * @author M1048860 Kiran Raj
 */
@Component
public class DemoDedupe {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(DemoDedupe.class);

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private RegistrationStatusService registrationStatusService;

	/** The packet info dao. */
	@Autowired
	private PacketInfoDao packetInfoDao;

	/**
	 * Perform dedupe.
	 *
	 * @param refId
	 *            the ref id
	 * @return the list
	 */
	public List<DemographicInfoDto> performDedupe(String refId) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REFFERENCEID.toString(), refId,
				"DemoDedupe::performDedupe()::entry");

		List<DemographicInfoDto> applicantDemoDto = packetInfoDao.findDemoById(refId);
		List<DemographicInfoDto> demographicInfoDtos = new ArrayList<>();
		List<DemographicInfoDto> infoDtos = new ArrayList<>();
		for (DemographicInfoDto demoDto : applicantDemoDto) {
			infoDtos.addAll(packetInfoDao.getAllDemographicInfoDtos(demoDto.getName(), demoDto.getGenderCode(),
					demoDto.getDob(), demoDto.getLangCode()));
		}
		demographicInfoDtos = getAllDemographicInfoDtosWithUin(infoDtos);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REFFERENCEID.toString(), refId,
				"DemoDedupe::performDedupe()::exit");
		return demographicInfoDtos;
	}

	private List<DemographicInfoDto> getAllDemographicInfoDtosWithUin(
			List<DemographicInfoDto> duplicateDemographicDtos) {
		List<DemographicInfoDto> demographicInfoDtosWithUin = new ArrayList<>();
		for (DemographicInfoDto demographicDto : duplicateDemographicDtos) {
			if (registrationStatusService.checkUinAvailabilityForRid(demographicDto.getRegId())) {
				demographicInfoDtosWithUin.add(demographicDto);
			}

		}
		return demographicInfoDtosWithUin;
	}

	/**
	 * Sets the finger biometric dto.
	 *
	 * @param obj
	 *            the obj
	 * @param fieldName
	 *            the field name
	 * @param value
	 *            the value
	 * @throws IntrospectionException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void setFingerBiometricDto(Object obj, String fieldName, Object value)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
		PropertyDescriptor pd;
		if (fieldName != null) {
			pd = new PropertyDescriptor(fieldName, obj.getClass());
			pd.getWriteMethod().invoke(obj, value);
		}
	}

	/**
	 * Sets the finger biometric.
	 *
	 * @param biometricData
	 *            the biometric data
	 * @param type
	 *            the type
	 * @throws IntrospectionException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	void setFingerBiometric(List<IdentityInfoDTO> biometricData, String type)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
		String finger = null;
		String[] fingerType = env.getProperty(DemoDedupeConstants.FINGERTYPE).split(",");
		List<String> list = new ArrayList<>(Arrays.asList(fingerType));
		IdentityDTO identityDTO = new IdentityDTO();
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String ftype = it.next();
			if (ftype.equalsIgnoreCase(type)) {
				finger = ftype;
				break;
			}
		}
		this.setFingerBiometricDto(identityDTO, finger, biometricData);
	}

}
