package org.mosip.registration.processor.status.dao;

import java.util.List;

import org.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shashank Agrawal
 * @author Jyoti Prakash Nayak
 *
 */
@Repository
public interface RegistrationStatusDao extends JpaRepository<RegistrationStatusEntity, String> {

	public List<RegistrationStatusEntity> getEnrolmentStatusByStatus(String status);

	
}