package org.mosip.auth.service.dao;

import org.mosip.auth.service.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Integer> {

	public OtpEntity findByUniqueID(String virtualID);

	public OtpEntity findByTempID(String uniqueID);
}
