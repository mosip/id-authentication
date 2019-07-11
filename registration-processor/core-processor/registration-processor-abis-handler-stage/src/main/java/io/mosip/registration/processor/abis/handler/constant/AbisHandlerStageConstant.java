package io.mosip.registration.processor.abis.handler.constant;

import java.sql.Timestamp;

public class AbisHandlerStageConstant {

	/** The Constant BIO_DEDUPE_STAGE. */
	public static final String BIO_DEDUPE_STAGE = "BioDedupeStage";

	/** The Constant DEMO_DEDUPE_STAGE. */
	public static final String DEMO_DEDUPE_STAGE = "DemoDedupeStage";

	/** The Constant MOSIP_ABIS_INSERT. */
	public static final String MOSIP_ABIS_INSERT = "mosip.abis.insert";

	/** The Constant VERSION. */
	public static final String VERSION = "1.0";

	/** The Constant TIMESTAMP. */
	public static final String TIMESTAMP = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() / 1000L);

	/** The Constant ENG. */
	public static final String ENG = "eng";

	/** The Constant USER. */
	public static final String USER = "MOSIP";

	/** The Constant INSERT. */
	public static final String INSERT = "INSERT";

	/** The Constant IDENTIFY. */
	public static final String IDENTIFY = "IDENTIFY";

	/** The Constant MOSIP_ABIS_IDENTIFY. */
	public static final String MOSIP_ABIS_IDENTIFY = "mosip.abis.identify";

	/** The Constant DEMOGRAPHIC_VERIFICATION. */
	public static final String DEMOGRAPHIC_VERIFICATION = "DEMOGRAPHIC_VERIFICATION";

	/** The Constant BIOGRAPHIC_VERIFICATION. */
	public static final String BIOGRAPHIC_VERIFICATION = "BIOGRAPHIC_VERIFICATION";
	
	/** The Constant DETAILS_NOT_FOUND. */
	public static final String DETAILS_NOT_FOUND = "Abis Queue details not found";
	
	/** The Constant ABIS_HANDLER_SUCCESS. */
	public static final String ABIS_HANDLER_SUCCESS = "Abis Handler Success";
	
	/** The Constant ERROR_IN_ABIS_HANDLER. */
	public static final String ERROR_IN_ABIS_HANDLER = "Internal Error occured in Abis Handler";
	
	/** The Constant ERROR_IN_ABIS_HANDLER_IDENTIFY_REQUEST. */
	public static final String ERROR_IN_ABIS_HANDLER_IDENTIFY_REQUEST =  "Internal Error occured in Abis Handler identify request";
	
	/** The Constant NO_RECORD_FOUND. */
	public static final String NO_RECORD_FOUND ="Potential Match Records are Not Found for Demo Dedupe Potential Match";
}
