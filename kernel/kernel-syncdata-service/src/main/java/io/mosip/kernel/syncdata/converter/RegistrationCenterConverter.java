package io.mosip.kernel.syncdata.converter;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.syncdata.dto.RegistrationCenterDto;
import io.mosip.kernel.syncdata.entity.RegistrationCenter;

public class RegistrationCenterConverter implements DataConverter<RegistrationCenter, RegistrationCenterDto>{

	@Override
	public void convert(RegistrationCenter source, RegistrationCenterDto destination) {
		destination.setCenterEndTime(source.getCenterEndTime());
		destination.setCenterStartTime(source.getCenterStartTime());
		destination.setLunchEndTime(source.getLunchEndTime());
		destination.setLunchStartTime(source.getLunchStartTime());
		destination.setPerKioskProcessTime(source.getPerKioskProcessTime());
	}

}
