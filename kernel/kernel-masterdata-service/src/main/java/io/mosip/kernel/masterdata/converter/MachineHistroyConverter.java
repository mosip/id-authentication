package io.mosip.kernel.masterdata.converter;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.masterdata.dto.MachineHistoryDto;
import io.mosip.kernel.masterdata.entity.MachineHistory;

public class MachineHistroyConverter implements  DataConverter<MachineHistory, MachineHistoryDto>{

	@Override
	public void convert(MachineHistory source, MachineHistoryDto destination) {	
		destination.setCreatedtimes(source.getCreatedtimes());
		destination.setUpdatedtimes(source.getUpdatedtimes());
		destination.setEffectDtimes(source.getEffectDtimes());
		destination.setValEndDtimes(source.getValEndDtimes());	
		destination.setDeletedtimes(source.getDeletedtimes());
	}

}
