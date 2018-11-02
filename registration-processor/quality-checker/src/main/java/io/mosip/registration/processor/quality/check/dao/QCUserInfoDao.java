package io.mosip.registration.processor.quality.check.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.quality.check.entity.UserDetailEntity;
import io.mosip.registration.processor.quality.check.repository.QcuserRegRepositary;

@Component
public class QCUserInfoDao {
	
	
	@Autowired
	private QcuserRegRepositary<UserDetailEntity, String> qcuserRepositary;
	
	public List<String> getAllQcuserIds() {
		
		return qcuserRepositary.findAllUserIds();
	}
	
}
