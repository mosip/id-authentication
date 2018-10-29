package io.mosip.registration.processor.quality.check.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.quality.check.dao.ApplicantInfoDao;
import io.mosip.registration.processor.quality.check.dto.ApplicantInfoDto;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdPKEntity;

@Component
public class QualityCheckManagerImpl implements QualityCheckManager<String, ApplicantInfoDto, QCUserDto> {
	@Autowired
	private ApplicantInfoDao applicantInfoDao;

	@Override
	public void assignQCUser(String applicantRegistrationId) {
		System.out.println("Assigning QC User");
	}

	@Override
	public List<ApplicantInfoDto> getPacketsforQCUser(String qcuserId) {
		return applicantInfoDao.getPacketsforQCUser(qcuserId); 
		 
	}

	@Override
	public void updateQCUserStatus(List<QCUserDto> qcUserDtos) {
		qcUserDtos.forEach(dto -> {
			if (applicantInfoDao.findById(dto.getQcUserId()) != null) {
				QcuserRegistrationIdEntity qcUserEntity = convertDtoToEntity(dto);
				applicantInfoDao.update(qcUserEntity);
			}
		});
	}

	private QcuserRegistrationIdEntity convertDtoToEntity(QCUserDto qcUserDto) {
		QcuserRegistrationIdEntity qcUserEntity = new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity qcuserPKEntity = new QcuserRegistrationIdPKEntity();
		qcuserPKEntity.setRegId(qcUserDto.getRegId());
		qcuserPKEntity.setUsrId(qcUserDto.getQcUserId());

		qcUserEntity.setId(qcuserPKEntity);
		qcUserEntity.setStatus(qcUserDto.getDecisionStatus());

		return qcUserEntity;

	}

	// private QCUserDto convertEntityToDto(QcuserRegistrationIdEntity qcUserEntity)
	// {
	// QCUserDto qcUserDto = new QCUserDto();
	//
	//
	// }

}
