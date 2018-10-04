package io.mosip.registration.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.UserManagmentEntity;

@Repository
public interface UserManagementRepository  extends JpaRepository<UserManagmentEntity, Integer>{
	public UserManagmentEntity getUserByUserName(String loginId);

}
