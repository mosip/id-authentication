package io.mosip.registration.processor.quality.check.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.quality.check.repository.QcuserRegRepositary;


@Component
public class ApplicantInfoDao {

	
	@Autowired
	private QcuserRegRepositary<QcuserRegistrationIdEntity, String> qcuserRegRepositary;
}
