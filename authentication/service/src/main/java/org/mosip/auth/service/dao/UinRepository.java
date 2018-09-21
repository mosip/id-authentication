package org.mosip.auth.service.dao;

import java.util.Optional;

import org.mosip.auth.service.entity.UinEntity;
import org.mosip.kernel.core.dao.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UinRepository extends BaseRepository<UinEntity, String>{
	public UinEntity findByUin(String uin);
	
	public Optional<UinEntity> findById(String refId);
}
