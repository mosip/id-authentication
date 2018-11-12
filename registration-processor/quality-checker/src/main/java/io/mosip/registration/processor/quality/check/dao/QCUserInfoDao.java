package io.mosip.registration.processor.quality.check.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.quality.check.entity.UserDetailEntity;
import io.mosip.registration.processor.quality.check.entity.UserDetailPKEntity;
import io.mosip.registration.processor.quality.check.repository.QcuserRegRepositary;

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
