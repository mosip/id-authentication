package io.mosip.registration.processor.biodedupe.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDetDto;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.entity.RegBioRefEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

@Component
public class BioDedupDao {

	@Autowired
	private BasePacketRepository<AbisRequestEntity, String> abisRequestRepository;

	@Autowired
	private BasePacketRepository<AbisResponseDetEntity, String> abisResponseDetRepository;

	@Autowired
	private BasePacketRepository<AbisResponseEntity, String> abisResponseRepository;

	@Autowired
	private BasePacketRepository<RegBioRefEntity, String> regBioRefRepository;

	public List<AbisRequestEntity> getAbisRequestIDs(String latestTransactionId) {
		return abisRequestRepository.getAbisRequestIDs(latestTransactionId);

	}

	public List<AbisResponseDetEntity> getAbisResponseDetailRecords(String latestTransactionId) {
		List<AbisResponseDetEntity> abisResponseDetEntities = new ArrayList<>();
		List<AbisResponseEntity> abisResponseEntities = new ArrayList<>();

		List<AbisRequestEntity> abisRequestEntities = abisRequestRepository.getAbisRequestIDs(latestTransactionId);
		for (AbisRequestEntity abisRequestEntity : abisRequestEntities) {
			abisResponseEntities.addAll(abisResponseRepository.getAbisResponseIDs(abisRequestEntity));
		}
		for (AbisResponseEntity abisResponseEntity : abisResponseEntities) {
			abisResponseDetEntities.addAll(abisResponseDetRepository.getAbisResponseDetails(abisResponseEntity.getId().getId().toString()));
		}
		return abisResponseDetEntities;
	}

	public List<RegBioRefEntity> getBioRefIds(String matchRefId) {
		return regBioRefRepository.getBioRefIds(matchRefId);

	}

}
