/**
 * 
 */
package io.mosip.registration.service.audit.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.audit.AuditService;

/**
 * Audit Service
 * 
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
@Service
public class AuditServiceImpl extends BaseService implements AuditService {

	@Autowired
	AuditDAO auditDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.audit.AuditService#deleteAuditLogs()
	 */
	@Override
	public ResponseDTO deleteAuditLogs() {
		ResponseDTO responseDTO = new ResponseDTO();

		/* TODO Get Calculated xhrs from DataBase */
		final int configuredDays = 2;

		/* TODO Get auditFromTime and AuditToTime */
		final LocalDateTime auditLogFromDtimes = new Timestamp(119, 00, 01, 12, 12, 12, 12).toLocalDateTime();
		final LocalDateTime auditLogToDtimes = new Timestamp(119, 00, 29, 16, 49, 07, 0).toLocalDateTime();
		try {
			auditDAO.deleteAll(auditLogFromDtimes, auditLogToDtimes);
			setSuccessResponse(responseDTO, "DeletedSuccessFully", null);

		} catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();
			setErrorResponse(responseDTO, "Unable TO Delete", null);
		}

		return responseDTO;
	}

}
