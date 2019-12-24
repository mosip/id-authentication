package io.mosip.admin.packetstatusupdater.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.packetstatusupdater.constant.AuditConstant;
import io.mosip.admin.packetstatusupdater.dto.PacketStatusUpdateResponseDto;
import io.mosip.admin.packetstatusupdater.service.PacketStatusUpdateService;
import io.mosip.admin.packetstatusupdater.util.AuditUtil;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;


/**
 * The Class PacketUpdateStatusController.
 * @author Srinivasan
 */
@RestController
@RequestMapping("/packetStatusUpdate")
public class PacketUpdateStatusController {

	/** The packet update status service. */
	@Autowired
	private PacketStatusUpdateService packetUpdateStatusService;
	
	@Autowired
	private AuditUtil auditUtil;

	/**
	 * Validate packet.
	 *
	 * @param rId the r id
	 * @return the response wrapper
	 */
	@GetMapping
    @PreAuthorize("hasRole('ZONAL_ADMIN')")
	@ResponseFilter
	public ResponseWrapper<PacketStatusUpdateResponseDto> validatePacket(@RequestParam(value = "rid") String rId) {
		auditUtil.auditRequest(AuditConstant.PKT_STATUS_UPD_API_CALLED,AuditConstant.AUDIT_SYSTEM,AuditConstant.PKT_STATUS_UPD_API_CALLED,"ADM-2000");
		ResponseWrapper<PacketStatusUpdateResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(packetUpdateStatusService.getStatus(rId));
		auditUtil.auditRequest(AuditConstant.PKT_STATUS_UPD_SUCCESS,AuditConstant.AUDIT_SYSTEM,AuditConstant.PKT_STATUS_UPD_API_CALLED,"ADM-2001");
		return responseWrapper;
	}
}
