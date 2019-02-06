package io.mosip.authentication.service.impl.spin.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.spin.service.StaticPinService;
import io.mosip.authentication.service.entity.StaticPin;
import io.mosip.authentication.service.entity.StaticPinHistory;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.repository.StaticPinHistoryRepository;
import io.mosip.authentication.service.repository.StaticPinRepository;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
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

	/** The DateHelper */
	@Autowired
	private DateHelper dateHelper;

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
			StaticPin staticPin = new StaticPin();
			StaticPinHistory staticPinHistory = new StaticPinHistory();
			staticPin.setUin(uinValue);
			String pinValue = staticPinRequestDTO.getRequest().getStaticPin();
			// TODO
			String hashedPin = hashStaticPin(pinValue.getBytes());
			staticPin.setPin(hashedPin);
			staticPin.setCreatedBy(IDA);
			staticPin.setCreatedDTimes(new Date());
			staticPin.setUpdatedBy(IDA);
			Date convertStringToDate = null;
			convertStringToDate = dateHelper.convertStringToDate(staticPinRequestDTO.getReqTime());
			staticPin.setUpdatedOn(convertStringToDate);
			staticPin.setActive(true);
			staticPin.setDeleted(false);
			staticPinHistory.setUin(uinValue);
			staticPinHistory.setPin(hashedPin);
			staticPinHistory.setCreatedBy(IDA);
			staticPinHistory.setCreatedDTimes(new Date());
			staticPinHistory.setEffectiveDate(new Date());
			staticPinHistory.setActive(true);
			staticPinHistory.setDeleted(false);
			staticPinHistory.setUpdatedBy(IDA);
			staticPinHistory.setUpdatedOn(new Date());
			Optional<StaticPin> entityValues = staticPinRepo.findById(uinValue);

			if (!entityValues.isPresent()) {
				staticPinRepo.save(staticPin);

			} else {
				StaticPin entity = entityValues.get();
				entity.setPin(hashedPin);
				entity.setUpdatedOn(new Date());
				entity.setUpdatedBy(IDA);
				staticPinRepo.update(entity);
			}
			status = Boolean.TRUE;
			staticPinHistoryRepo.save(staticPinHistory);
			return status;
	}

	/**
	 * Hash the Static Pin.
	 *
	 * @param pinValue
	 *            the Static Pin
	 * @return the string
	 */
	private String hashStaticPin(byte[] pinValue) {
		return CryptoUtil.encodeBase64(HMACUtils.generateHash(pinValue));
	}

}
