package io.mosip.registration.processor.qc.users.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.qc.users.entity.UserDetailEntity;
import io.mosip.registration.processor.qc.users.entity.UserDetailPKEntity;
import io.mosip.registration.processor.qc.users.repository.QcuserRegRepositary;


@Component
public class QCUserInfoDao {
	
	
	@Autowired
	private QcuserRegRepositary<UserDetailEntity, UserDetailPKEntity> qcuserRepositary;
	
	
	public List<String> getAllQcuserIds() {
		
		return qcuserRepositary.findAllUserIds().stream()
	              .map(UserDetailPKEntity::getId)
	              .collect(Collectors.toList());
	}
	
}
