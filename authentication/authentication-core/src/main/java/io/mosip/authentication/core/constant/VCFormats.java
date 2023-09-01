package io.mosip.authentication.core.constant;

public enum VCFormats {
	LDP_VC("ldp_vc"), 
	JWT_VC_JSON("jwt_vc_json"), 
	JWT_VC_JSON_LD("jwt_vc_json-ld"), 
	MSO_MDOC("mso_mdoc");

	private final String format;

	private VCFormats(String format) {
		this.format = format;
	}
	
	public String getFormat() {
		return format;
	}
}
