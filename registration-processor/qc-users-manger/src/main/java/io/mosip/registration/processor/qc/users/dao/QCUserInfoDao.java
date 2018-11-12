package io.mosip.registration.processor.qc.users.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.qc.users.entity.RoleListEntity;
import io.mosip.registration.processor.qc.users.entity.RoleListPKEntity;
import io.mosip.registration.processor.qc.users.entity.UserDetailEntity;
import io.mosip.registration.processor.qc.users.entity.UserDetailPKEntity;
import io.mosip.registration.processor.qc.users.entity.UserRoleEntity;
import io.mosip.registration.processor.qc.users.entity.UserRolePKEntity;
import io.mosip.registration.processor.qc.users.repository.QcuserRegRepositary;


@Component
public class QCUserInfoDao {
	
	
	@Autowired
	private QcuserRegRepositary<UserDetailEntity, UserDetailPKEntity> qcuserRepositary;
	
	/*@Autowired
	private QcuserRegRepositary<RoleListEntity, RoleListPKEntity> roleRepositary;*/
	
	@Autowired
	private QcuserRegRepositary<UserRoleEntity, UserRolePKEntity> qcuserroleRepositary;
	
	public List<String> getAllQcuserIds() {
		UserDetailPKEntity upk=new UserDetailPKEntity();
		upk.setId("qc001");
		UserDetailEntity ue=new UserDetailEntity();
		ue.setId(upk);
		ue.setEmail("****");
		ue.setIsActive(true);
		ue.setIsDeleted(false);
		ue.setLangCode("en");
		ue.setMobile("1234567890");
		ue.setName("Richard Madden");
		ue.setUinRefId("1234567890");
		ue.setStatusCode("active");
		
		UserDetailPKEntity upk1=new UserDetailPKEntity();
		upk1.setId("qc002");
		UserDetailEntity ue1=new UserDetailEntity();
		ue1.setId(upk);
		ue1.setEmail("****");
		ue1.setIsActive(true);
		ue1.setIsDeleted(false);
		ue1.setLangCode("en");
		ue1.setMobile("0987654321");
		ue1.setName("Richard Madden");
		ue1.setUinRefId("1234543210");
		ue1.setStatusCode("active");
		
		/*RoleListPKEntity rpk=new RoleListPKEntity();
		rpk.setLangCode("en");
		rpk.setRoleCode("001");
		RoleListEntity re=new RoleListEntity();
		re.setDescr("QCUSER");
		re.setId(rpk);
		re.setIsActive(true);
		re.setIsDeleted(false);*/
		
		UserRolePKEntity urpk1=new UserRolePKEntity();
		urpk1.setLangCode("en");
		urpk1.setRoleCode("001");
		urpk1.setUsrId("qc001");
		UserRoleEntity ure=new UserRoleEntity();
		ure.setIsActive(true);
		ure.setIsDeleted(false);
		ure.setId(urpk1);
		
		UserRolePKEntity urpk2=new UserRolePKEntity();
		urpk2.setLangCode("en");
		urpk2.setRoleCode("001");
		urpk2.setUsrId("qc002");
		UserRoleEntity ure1=new UserRoleEntity();
		ure1.setIsActive(true);
		ure1.setIsDeleted(false);
		ure1.setId(urpk2);
		
		//roleRepositary.save(re);
		qcuserRepositary.save(ue);
		qcuserRepositary.save(ue1);
		qcuserroleRepositary.save(ure);
		qcuserroleRepositary.save(ure1);
		return qcuserRepositary.findAllUserIds().stream()
	              .map(UserDetailPKEntity::getId)
	              .collect(Collectors.toList());
	}
	
}
