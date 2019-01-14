package io.mosip.kernel.idgenerator.machineid.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.MachineIdGenerator;
import io.mosip.kernel.core.util.MathUtils;
import io.mosip.kernel.idgenerator.machineid.constant.MachineIdConstant;
import io.mosip.kernel.idgenerator.machineid.entity.MachineId;
import io.mosip.kernel.idgenerator.machineid.exception.MachineIdServiceException;
import io.mosip.kernel.idgenerator.machineid.repository.MachineIdRepository;

/**
 * Implementation class for {@link MachineIdGenerator}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Component
public class MachineIdGeneratorImpl implements MachineIdGenerator<Integer> {
	/**
	 * The length of machine ID.
	 */
	@Value("${mosip.kernel.machineid.length}")
	private int machineIdLength;

	/**
	 * Autowired reference for {@link MachineIdRepository}.
	 */
	@Autowired
	private MachineIdRepository machineIdRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idgenerator.spi.MachineIdGenerator#generateMachineId()
	 */
	@Override
	public Integer generateMachineId() {
		final int initialValue = MathUtils.getPow(MachineIdConstant.ID_BASE.getValue(), machineIdLength - 1);
		MachineId machineId = null;
		try {
			machineId = machineIdRepository.findMaxMachineId();
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new MachineIdServiceException(MachineIdConstant.MID_FETCH_EXCEPTION.getErrorCode(),
					MachineIdConstant.MID_FETCH_EXCEPTION.getErrorMessage(), dataAccessLayerException.getCause());
		}
		if (machineId == null) {
			machineId = new MachineId();
			machineId.setMId(initialValue);

		} else {
			int lastGeneratedId = machineId.getMId();
			machineId = new MachineId();
			machineId.setMId(lastGeneratedId + 1);
		}
		try {
			machineIdRepository.save(machineId);
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new MachineIdServiceException(MachineIdConstant.MID_INSERT_EXCEPTION.getErrorCode(),
					MachineIdConstant.MID_INSERT_EXCEPTION.getErrorMessage(), dataAccessLayerException.getCause());
		}
		return machineId.getMId();
	}
}
