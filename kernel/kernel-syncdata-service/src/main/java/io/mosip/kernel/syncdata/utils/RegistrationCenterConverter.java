package io.mosip.kernel.syncdata.utils;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.syncdata.dto.RegistrationCenterDto;
import io.mosip.kernel.syncdata.entity.RegistrationCenter;

/**
 * Registration Center Custom Converter
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public class RegistrationCenterConverter implements DataConverter<RegistrationCenter, RegistrationCenterDto> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.datamapper.spi.DataConverter#convert(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void convert(RegistrationCenter source, RegistrationCenterDto destination) {
		destination.setCenterEndTime(source.getCenterEndTime());
		destination.setCenterStartTime(source.getCenterStartTime());
		destination.setLunchEndTime(source.getLunchEndTime());
		destination.setLunchStartTime(source.getLunchStartTime());
		destination.setPerKioskProcessTime(source.getPerKioskProcessTime());
	}

}
