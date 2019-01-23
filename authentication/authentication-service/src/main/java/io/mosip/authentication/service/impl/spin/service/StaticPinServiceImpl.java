package io.mosip.authentication.service.impl.spin.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.spin.service.StaticPinService;
import io.mosip.authentication.service.entity.StaticPinEntity;
import io.mosip.authentication.service.entity.StaticPinHistoryEntity;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.repository.StaticPinHistoryRepository;
import io.mosip.authentication.service.repository.StaticPinRepository;
import io.mosip.kernel.core.logger.spi.Logger;
/**
 * 
 * @author Prem Kumar
 *
 */
@Service
public class StaticPinServiceImpl implements StaticPinService{
	
	private static final String IDA = "IDA";

	@Autowired
	private StaticPinRepository staticPinRepo;
	
	@Autowired
	private StaticPinHistoryRepository staticPinHistoryRepo;
	
	@Autowired
	private DateHelper dateHelper;
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(StaticPinServiceImpl.class);

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";
	
	@Override
	public boolean storeSpin(StaticPinRequestDTO staticPinRequestDTO,String uinValue) throws IdAuthenticationBusinessException {
		boolean status=false;
		StaticPinEntity staticPinEntity=new StaticPinEntity();
		StaticPinHistoryEntity staticPinHistoryEntity=new StaticPinHistoryEntity();
		staticPinEntity.setUin(uinValue);
		String pinValue = staticPinRequestDTO.getRequest().getPinValue();
		staticPinEntity.setPin(pinValue);
		staticPinEntity.setCorrectedBy(null);
		staticPinEntity.setCorrectedDate(new Date());
	staticPinEntity.setCorrectedDate(new Date());
	staticPinEntity.setUpdatedBy(IDA);
		// FIXME utilize Instant
				Date convertStringToDate = null;
				try {
					convertStringToDate = dateHelper.convertStringToDate(staticPinRequestDTO.getReqTime());
				} catch (IDDataValidationException e) {
					logger.error(DEFAULT_SESSION_ID, null, null, e.getErrorText());
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST_TIMESTAMP,
							e);
				}
				staticPinEntity.setUpdatedOn(convertStringToDate);
		staticPinEntity.setGeneratedOn(convertStringToDate);
		staticPinEntity.setActive(true);
		staticPinEntity.setDeleted(false);
		staticPinHistoryEntity.setUin(uinValue);
		staticPinHistoryEntity.setPin(pinValue);
		staticPinHistoryEntity.setCorrectedBy(IDA);
		staticPinHistoryEntity.setCorrectedDate(new Date());
		staticPinHistoryEntity.setGeneratedOn(convertStringToDate);
		staticPinHistoryEntity.setEffectiveDate(new Date());
		staticPinHistoryEntity.setActive(true);
		staticPinHistoryEntity.setDeleted(false);
		staticPinHistoryEntity.setUpdatedBy(IDA);
		staticPinHistoryEntity.setUpdatedOn(new Date());
		Optional<StaticPinEntity> entityValues = staticPinRepo.findById(uinValue);
		if(!entityValues.isPresent()) {
			try {
			staticPinRepo.saveAndFlush(staticPinEntity);
			}
			catch(DataIntegrityViolationException e) {
				logger.error(DEFAULT_SESSION_ID, null, null, e.getMessage());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.STATICPIN_NOT_STORED_PINVAUE,
						e);
			}
			status=Boolean.TRUE;
		}
		else {
			entityValues.get().setPin(pinValue);
			entityValues.get().setUpdatedOn(new Date());
			entityValues.get().setUpdatedBy(IDA);
			staticPinRepo.update(entityValues.get());
			status=Boolean.TRUE;
		}
		staticPinHistoryRepo.save(staticPinHistoryEntity);
		return status;
	}

}
