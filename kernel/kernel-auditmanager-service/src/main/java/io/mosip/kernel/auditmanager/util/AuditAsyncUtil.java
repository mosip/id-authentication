/**
 * 
 */
package io.mosip.kernel.auditmanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.auditmanager.spi.AuditHandler;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class AuditAsyncUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditAsyncUtil.class);

	/**
	 * Field for audit handler
	 */
	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	/**
	 * @param auditRequestDto
	 */
	@Async
	public void addAudit(AuditRequestDto auditRequestDto) {
		auditHandler.addAudit(auditRequestDto);
		LOGGER.info("{}- Added audit data for audit request with {} {} {}", Thread.currentThread().getName(),
				auditRequestDto.getSessionUserId(), auditRequestDto.getIdType(), auditRequestDto.getId());
	}

}
