package io.mosip.registration.processor.qc.users.repository;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.registration.processor.qc.users.entity.BaseQcuserEntity;


@Repository
public interface QcuserRegRepositary<T extends BaseQcuserEntity<?>, E> extends BaseRepository<T, E> {

	
	@Query("SELECT qcUser.id FROM UserDetailEntity qcUser WHERE qcUser.isActive=TRUE AND qcUser.isDeleted=FALSE")
	public List<E> findAllUserIds();
		
}