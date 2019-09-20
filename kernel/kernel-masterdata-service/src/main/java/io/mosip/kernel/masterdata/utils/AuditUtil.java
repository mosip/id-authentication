package io.mosip.kernel.masterdata.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.auditmanager.util.AuditUtils;
import io.mosip.kernel.core.util.DateUtils;

/**
 * AuditUtil.
 */
public class AuditUtil {

	/** The Constant APPLICATION_ID. */
	private static final String APPLICATION_ID = "10009";

	/** The Constant APPLICATION_NAME. */
	private static final String APPLICATION_NAME = "Admin_Portal";

	/** The Constant UNKNOWN_HOST. */
	private static final String UNKNOWN_HOST = "Unknown Host";

	private String hostIpAddress = null;

	private String hostName = null;

	/** The audit utils. */
	@Autowired
	AuditUtils auditUtils;

	/**
	 * Audit request.
	 *
	 * @param auditRequestDto
	 *            the audit request dto
	 */
	public void auditRequest(AuditRequestDto auditRequestDto) {

		setAuditRequestDto(auditRequestDto);
	}

	/**
	 * Sets the audit request dto.
	 *
	 * @param auditRequestDto
	 *            the new audit request dto
	 */
	private void setAuditRequestDto(AuditRequestDto auditRequestDto) {

		if (!validateSecurityContextHolder()) {
			// TODO;
		}
		auditRequestDto.setActionTimeStamp(DateUtils.getUTCCurrentDateTime());
		auditRequestDto.setHostIp(hostIpAddress);
		auditRequestDto.setHostName(hostName);
		auditRequestDto.setApplicationId(APPLICATION_ID);
		auditRequestDto.setApplicationName(APPLICATION_NAME);
		auditRequestDto.setSessionUserId(SecurityContextHolder.getContext().getAuthentication().getName());
		auditRequestDto.setSessionUserName(SecurityContextHolder.getContext().getAuthentication().getName());
		auditRequestDto.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

	}

	/**
	 * Validate security context holder.
	 *
	 * @return true, if successful
	 */
	private boolean validateSecurityContextHolder() {
		Predicate<SecurityContextHolder> contextPredicate = i -> SecurityContextHolder.getContext() != null;
		Predicate<SecurityContextHolder> authPredicate = i -> SecurityContextHolder.getContext()
				.getAuthentication() != null;
		Predicate<SecurityContextHolder> principlePredicate = i -> SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal() != null;
		return contextPredicate.and(authPredicate).and(principlePredicate) != null;

	}

	/**
	 * Gets the server ip.
	 *
	 * @return the server ip
	 */
	public String getServerIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return UNKNOWN_HOST;
		}
	}

	/**
	 * Gets the server name.
	 *
	 * @return the server name
	 */
	public String getServerName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return UNKNOWN_HOST;
		}
	}

	/**
	 * To Set the Host Ip & Host Name
	 */
	@PostConstruct
	public void getHostDetails() {
		hostIpAddress = getServerIp();
		hostName = getServerName();
	}

}
