package org.mosip.registration.dao;

import org.mosip.registration.entity.UserManagmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserManagementRepository  extends JpaRepository<UserManagmentEntity, Integer>{
	public UserManagmentEntity getUserByUserName(String loginId);

}
