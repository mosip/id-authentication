package org.mosip.registration.dao.impl;

import static org.mosip.registration.consts.RegConstants.PACKET_STORE_LOCATION;

import java.util.Date;
import java.util.List;

import org.mosip.registration.consts.RegProcessorExceptionCode;
import org.mosip.registration.dao.RegistrationDAO;
import org.mosip.registration.entity.Registration;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.repositories.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationDAOImpl implements RegistrationDAO {

	@Autowired
	private RegistrationRepository registrationRepository;
	
	public void savePacketDetails(String zipFileName) throws RegBaseCheckedException {
		try {
			Registration registration = new Registration();
			registration.setEnrl_id((int)((long)System.currentTimeMillis()));
			registration.setPkt_name(zipFileName);
			registration.setLang_code("en");
			registration.setClient_status_code("00");
			registration.setCr_by("Enroll officer");
			registration.setCr_dtimes(new Date());
			//packet.setEnrl_id(zipFileName.substring(zipFileName.lastIndexOf(File.separator)+1));
			registration.setFile_sync_status("Y");
			registration.setFilespath(PACKET_STORE_LOCATION);
			registration.setIs_active("N");
			registration.setPkt_type_code("011");
			registration.setSync_count((short) 1);
			registration.setServer_status_code("UIN Generated");
			registrationRepository.create(registration);
		} catch (RegBaseUncheckedException uncheckedException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.CREATE_PACKET_ENTITY,
					uncheckedException.getMessage());
		}
	}

	@Override
	public void save(String zipFileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int update(String zipFileName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean upload(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> view(String zipFileName) {
		// TODO Auto-generated method stub
		return null;
	}

}
