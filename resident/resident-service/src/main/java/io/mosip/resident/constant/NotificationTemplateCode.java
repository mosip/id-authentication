package io.mosip.resident.constant;

public enum NotificationTemplateCode {
	RS_AUTH_HIST_Status("RS_AUTH_HIST_Status"),
	RS_DOW_UIN_Status("RS_DOW_UIN_Status"),
	RS_INV_DATA_NOT("RS_INV_DATA_NOT"),
	RS_INV_RID_NOT("RS_INV_RID_NOT"),
	RS_INV_UIN_VID_NOT("RS_INV_UIN-VID_NOT"),
	RS_LOCK_AUTH_Status("RS_LOCK_AUTH_Status"),
	RS_LOST_RID_Status("RS_LOST_RID_Status"),
	RS_NO_MOB_MAIL_ID("RS_NO_MOB-MAIL-ID"),
	RS_UIN_GEN_Status("RS_UIN_GEN_Status"),
	RS_UIN_RPR_Status("RS_UIN_RPR_Status"),
	RS_UIN_UPD_REQ("RS_UIN_UPD_REQ"),
	RS_UIN_UPD_Status("RS_UIN_UPD_Status"),
	RS_UIN_UPD_VAL("RS_UIN_UPD_VAL"),
	RS_UNLOCK_AUTH_Status("RS_UNLOCK_AUTH_Status"),
	RS_VIN_GEN_Status("RS_VIN_GEN_Status"),
	RS_VIN_REV_Status("RS_VIN_REV_Status");
	
	public String templateCode;

	private NotificationTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	@Override
	public String toString() {
		return templateCode;
	}
	
	
	
}
