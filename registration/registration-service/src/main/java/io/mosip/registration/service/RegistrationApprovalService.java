package io.mosip.registration.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationApprovalUiDto;
import io.mosip.registration.entity.Registration;

/**
 * @author M1047623
 *
 */
@Service
public class RegistrationApprovalService {

	@Autowired
	RegistrationDAO registrationDAO;

	public List<RegistrationApprovalUiDto> getAllEnrollments() {
		
		List<RegistrationApprovalUiDto> list= new ArrayList<RegistrationApprovalUiDto>();
		List<Registration> details= registrationDAO.approvalList();
		
		details.forEach((detail) -> {
			try {
				list.add(new RegistrationApprovalUiDto(detail.getId(),
						detail.getClientStatusCode(),
						detail.getIndividualName(),
						detail.getCrBy(),
						detail.getUserdetail().getName(),
						detail.getAckFilename()
						));
				
			} catch (Exception e) {
				// TODO: Handle exception
			}
		});
		return list;
	}

	public List<Registration> getEnrollmentByStatus(String status) {
		return registrationDAO.getEnrollmentByStatus(status);
	}

	public Boolean packetUpdateStatus(String id,String clientStatusCode, String approverUserId, String statusComments,
			String updBy) {
		return registrationDAO.updateStatus(id, clientStatusCode, approverUserId, statusComments, updBy) != null;
	}

}
