package io.mosip.kernel.idgenerator.machineid.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.transaction.Transactional;

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
@Transactional
public class MachineIdGeneratorImpl implements MachineIdGenerator<String> {
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
	public String generateMachineId() {
		int generatedMID;

		final int initialValue = MathUtils.getPow(MachineIdConstant.ID_BASE.getValue(), machineIdLength - 1);

		MachineId machineId = null;

		try {
			machineId = machineIdRepository.findLastMID();
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new MachineIdServiceException(MachineIdConstant.MID_FETCH_EXCEPTION.getErrorCode(),
					MachineIdConstant.MID_FETCH_EXCEPTION.getErrorMessage(), dataAccessLayerException.getCause());
		}
		if (machineId == null) {
			machineId = new MachineId();
			machineId.setMId(initialValue);
			machineId.setCreatedBy("default@user");
			machineId.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
			machineId.setUpdatedBy("default@user");
			machineId.setUpdatedDateTime(null);
			generatedMID = initialValue;
			machineIdRepository.save(machineId);
		} else {
			try {
				machineIdRepository.updateMID(machineId.getMId() + 1, machineId.getMId(),
						LocalDateTime.now(ZoneId.of("UTC")));
				generatedMID = machineId.getMId() + 1;
			} catch (DataAccessLayerException e) {
				throw new MachineIdServiceException(MachineIdConstant.MID_INSERT_EXCEPTION.getErrorCode(),
						MachineIdConstant.MID_INSERT_EXCEPTION.getErrorMessage(), e);
			}
		}
		return String.valueOf(generatedMID);
	}
}
