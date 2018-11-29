package io.mosip.kernel.masterdata.converter;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.masterdata.dto.RegistrationCenterHierarchyLevelDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;

public class RegistrationCenterHierarchyLevelConverter implements DataConverter<RegistrationCenter, RegistrationCenterHierarchyLevelDto>{

	@Override
	public void convert(RegistrationCenter source, RegistrationCenterHierarchyLevelDto destination) {
		destination.setCenterEndTime(source.getCenterEndTime());
		destination.setCenterStartTime(source.getCenterStartTime());
		destination.setLunchEndTime(source.getLunchEndTime());
		destination.setLunchStartTime(source.getLunchStartTime());
		destination.setPerKioskProcessTime(source.getPerKioskProcessTime());
		
		
	}

}
