package io.mosip.kernel.vidgenerator.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.vidgenerator.constant.VIDGeneratorErrorCode;
import io.mosip.kernel.vidgenerator.constant.VidLifecycleStatus;
import io.mosip.kernel.vidgenerator.dto.VidFetchResponseDto;
import io.mosip.kernel.vidgenerator.entity.VidEntity;
import io.mosip.kernel.vidgenerator.exception.VidGeneratorServiceException;
import io.mosip.kernel.vidgenerator.repository.VidRepository;
import io.mosip.kernel.vidgenerator.service.VidService;
import io.mosip.kernel.vidgenerator.utils.MetaDataUtil;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Service
public class VidServiceImpl implements VidService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VidServiceImpl.class);
	
	@Value("${mosip.kernel.vid.time-to-renew-after-expiry}")
	private long timeToRenewAfterExpiry;
	
	@Autowired
	private VidRepository vidRepository;

	@Autowired
	private MetaDataUtil metaDataUtil;
	
	@Override
	@Transactional
	public VidFetchResponseDto fetchVid(LocalDateTime vidExpiry) {
		VidFetchResponseDto vidFetchResponseDto = new VidFetchResponseDto();
		VidEntity vidEntity = vidRepository.findFirstByStatus(VidLifecycleStatus.AVAILABLE);
		if (vidEntity != null) {
			if (vidExpiry != null) {
				vidEntity.setVidExpiry(vidExpiry);
			}
			vidEntity.setStatus(VidLifecycleStatus.ASSIGNED);
			metaDataUtil.setUpdateMetaData(vidEntity);
			vidFetchResponseDto.setVid(vidEntity.getVid());
			vidRepository.save(vidEntity);
		} else {
			throw new VidGeneratorServiceException(VIDGeneratorErrorCode.VID_NOT_AVAILABLE.getErrorCode(), VIDGeneratorErrorCode.VID_NOT_AVAILABLE.getErrorMessage());
		}
		return vidFetchResponseDto;
	}

	@Override
	public long fetchVidCount(String status) {
		return vidRepository.countByStatusAndIsDeletedFalse(status);
	}

	@Override
	public void expireAndRenew() {
		List<VidEntity> vidAssignedEntities = vidRepository.findByStatusAndIsDeletedFalse(VidLifecycleStatus.ASSIGNED);
		vidAssignedEntities.forEach(this::expireIfEligible);
		vidRepository.saveAll(vidAssignedEntities);
		List<VidEntity> vidExpiredEntities = vidRepository.findByStatusAndIsDeletedFalse(VidLifecycleStatus.EXPIRED);
		vidExpiredEntities.forEach(this::renewIfEligible);
		vidRepository.saveAll(vidExpiredEntities);
	}

	private void expireIfEligible(VidEntity entity) {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime();
		LOGGER.debug("currenttime {} for checking entity with expiry time {}",currentTime,entity.getVidExpiry());
		if ((entity.getVidExpiry().isBefore(currentTime) || entity.getVidExpiry().isEqual(currentTime))
				&& entity.getStatus().equals(VidLifecycleStatus.ASSIGNED)) {
           metaDataUtil.setUpdateMetaData(entity);
           entity.setStatus(VidLifecycleStatus.EXPIRED);
		}
	}
	
	private void renewIfEligible(VidEntity entity) {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime();
		LocalDateTime renewElegibleTime =entity.getVidExpiry().plusMinutes(timeToRenewAfterExpiry);
		LOGGER.debug("currenttime {} for checking entity with renew elegible time {}",currentTime,renewElegibleTime);
		if ((renewElegibleTime.isBefore(currentTime) || renewElegibleTime.isEqual(currentTime))
				&& entity.getStatus().equals(VidLifecycleStatus.EXPIRED)) {
           metaDataUtil.setUpdateMetaData(entity);
           entity.setStatus(VidLifecycleStatus.AVAILABLE);
		}
	}

}
