package io.mosip.authentication.service.impl.spin.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.spin.service.StaticPinService;
import io.mosip.authentication.service.entity.StaticPinEntity;
import io.mosip.authentication.service.entity.StaticPinHistoryEntity;
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

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(StaticPinServiceImpl.class);

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/**
	 * This method is to store the StaticPin in StaticPin and StaticPinHistory
	 * Table.
	 * 
	 * @param staticPinRequestDTO
	 * @param uinValue
	 * @throws IdAuthenticationBusinessException
	 */
	@Override
	public boolean storeSpin(StaticPinRequestDTO staticPinRequestDTO, String uinValue)
			throws IdAuthenticationBusinessException {
		try {
			boolean status = false;
			StaticPinEntity staticPinEntity = new StaticPinEntity();
			StaticPinHistoryEntity staticPinHistoryEntity = new StaticPinHistoryEntity();
			staticPinEntity.setUin(uinValue);
			String pinValue = staticPinRequestDTO.getRequest().getStaticPin();
			// TODO
			String hashedPin = hashStaticPin(pinValue.getBytes());
			staticPinEntity.setPin(hashedPin);
			staticPinEntity.setCreatedBy(IDA);
			staticPinEntity.setCreatedDTimes(new Date());
			staticPinEntity.setUpdatedBy(IDA);
			Date convertStringToDate = null;
			convertStringToDate = dateHelper.convertStringToDate(staticPinRequestDTO.getReqTime());
			staticPinEntity.setUpdatedOn(convertStringToDate);
			staticPinEntity.setGeneratedOn(convertStringToDate);
			staticPinEntity.setActive(true);
			staticPinEntity.setDeleted(false);
			staticPinHistoryEntity.setUin(uinValue);
			staticPinHistoryEntity.setPin(hashedPin);
			staticPinHistoryEntity.setCreatedBy(IDA);
			staticPinHistoryEntity.setCreatedDTimes(new Date());
			staticPinHistoryEntity.setGeneratedOn(convertStringToDate);
			staticPinHistoryEntity.setEffectiveDate(new Date());
			staticPinHistoryEntity.setActive(true);
			staticPinHistoryEntity.setDeleted(false);
			staticPinHistoryEntity.setUpdatedBy(IDA);
			staticPinHistoryEntity.setUpdatedOn(new Date());
			Optional<StaticPinEntity> entityValues = staticPinRepo.findById(uinValue);

			if (!entityValues.isPresent()) {
				staticPinRepo.save(staticPinEntity);

			} else {
				StaticPinEntity entity = entityValues.get();
				entity.setPin(hashedPin);
				entity.setUpdatedOn(new Date());
				entity.setUpdatedBy(IDA);
				staticPinRepo.update(entity);
			}
			status = Boolean.TRUE;
			staticPinHistoryRepo.save(staticPinHistoryEntity);
			return status;
		} catch (DataAccessException e) {
			logger.error(SESSION_ID, "StaticPinStoreImpl", e.getClass().getName(), e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.STATICPIN_NOT_STORED_PINVAUE, e);
		}
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
