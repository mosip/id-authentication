package io.mosip.preregistration.auth.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.mosip.preregistration.auth.entity.UserManagmentEntity;

@Repository
public interface UserManagementRepository  extends JpaRepository<UserManagmentEntity, Integer>{
	public UserManagmentEntity getUserByUserName(String loginId);

}
