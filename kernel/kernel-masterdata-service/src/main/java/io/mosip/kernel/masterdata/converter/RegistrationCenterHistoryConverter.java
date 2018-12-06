package io.mosip.kernel.masterdata.converter;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterHistory;

public class RegistrationCenterHistoryConverter implements  DataConverter<RegistrationCenterHistory, RegistrationCenterDto>{

	@Override
	public void convert(RegistrationCenterHistory source, RegistrationCenterDto destination) {
		destination.setCenterEndTime(source.getCenterEndTime());
		destination.setCenterStartTime(source.getCenterStartTime());
		destination.setLunchEndTime(source.getLunchEndTime());
		destination.setLunchStartTime(source.getLunchStartTime());
		destination.setPerKioskProcessTime(source.getPerKioskProcessTime());
		
	}

}
