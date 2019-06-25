package io.mosip.registration.processor.stages.demodedupe;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
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


}
