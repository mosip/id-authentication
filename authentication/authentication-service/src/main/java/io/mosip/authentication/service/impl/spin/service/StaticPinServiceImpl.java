package io.mosip.authentication.service.impl.spin.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.spin.service.StaticPinService;
import io.mosip.authentication.service.entity.StaticPin;
import io.mosip.authentication.service.entity.StaticPinHistory;
import io.mosip.authentication.service.repository.StaticPinHistoryRepository;
import io.mosip.authentication.service.repository.StaticPinRepository;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * This Class will provide service for storing the Static Pin.
 * 
 * @author Prem Kumar
 *
 */
@Service
public class StaticPinServiceImpl implements StaticPinService {

	/** The Constant for IDA */
	private static final String IDA = "IDA";

	/** The StaticPinRepository */
	@Autowired
	private StaticPinRepository staticPinRepo;

	/** The StaticPinHistoryRepository */
	@Autowired
	private StaticPinHistoryRepository staticPinHistoryRepo;

	@Autowired
	Environment env;


	/**
	 * This method is to store the StaticPin in StaticPin and StaticPinHistory
	 * Table.
	 * 
	 * @param staticPinRequestDTO
	 * @param uinValue
	 * @throws IdAuthenticationBusinessException
	 */
	@Override
	@Transactional
	public boolean storeSpin(StaticPinRequestDTO staticPinRequestDTO, String uinValue)
			throws IdAuthenticationBusinessException {
		boolean status = false;

		String pinValue = staticPinRequestDTO.getRequest().getStaticPin();
		String hashedPin = hashStaticPin(pinValue.getBytes());
		Optional<StaticPin> entityValues = staticPinRepo.findById(uinValue);
		if (!entityValues.isPresent()) {
			StaticPin staticPin = new StaticPin();
			staticPin.setUin(uinValue);
			staticPin.setPin(hashedPin);
			staticPin.setCreatedBy(IDA);
			staticPin.setCreatedDTimes(now());
			staticPin.setUpdatedBy(IDA);
			staticPin.setUpdatedOn(now());
			staticPin.setActive(true);
			staticPin.setDeleted(false);
			staticPinRepo.save(staticPin);
		} else {
			StaticPin staticPinEntity = entityValues.get();
			staticPinEntity.setPin(hashedPin);
			staticPinEntity.setUpdatedOn(now());
			staticPinEntity.setUpdatedBy(IDA);
			staticPinRepo.update(staticPinEntity);
		}
		status = true;
		StaticPinHistory staticPinHistory = getPinHistory(uinValue, hashedPin);
		staticPinHistoryRepo.save(staticPinHistory);
		return status;
	}

	/**
	 * Method to get UTC Date time from kernal
	 * 
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private LocalDateTime now(){
		return DateUtils.getUTCCurrentDateTime();
	}

	/**
	 * Hash the Static Pin.
	 *
	 * @param pinValue the Static Pin
	 * @return the string
	 */
	private String hashStaticPin(byte[] pinValue) {
		return CryptoUtil.encodeBase64(HMACUtils.generateHash(pinValue));
	}

	/**
	 * To generate Static Pin History
	 * 
	 * @param uinValue
	 * @param hashedPin
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private StaticPinHistory getPinHistory(String uinValue, String hashedPin) throws IdAuthenticationBusinessException {
		StaticPinHistory staticPinHistory = new StaticPinHistory();
		staticPinHistory.setUin(uinValue);
		staticPinHistory.setPin(hashedPin);
		staticPinHistory.setCreatedBy(IDA);
		staticPinHistory.setCreatedDTimes(now());
		staticPinHistory.setEffectiveDate(now());
		staticPinHistory.setActive(true);
		staticPinHistory.setDeleted(false);
		staticPinHistory.setUpdatedBy(IDA);
		staticPinHistory.setUpdatedOn(now());
		return staticPinHistory;
	}

}
