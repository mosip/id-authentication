package io.mosip.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.registration.dao.DeviceTypeDAO;
import io.mosip.registration.entity.DeviceType;
import io.mosip.registration.repositories.DeviceTypeRepository;

@Repository
public class DeviceTypeDAOImpl implements DeviceTypeDAO {
	@Autowired
	private DeviceTypeRepository deviceTypeRepository;

	@Override
	public List<DeviceType> getAllDeviceTypes() {
		return deviceTypeRepository.findByIsActiveTrue();
	}

}
