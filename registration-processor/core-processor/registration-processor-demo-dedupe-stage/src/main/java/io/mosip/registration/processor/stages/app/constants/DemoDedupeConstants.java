package io.mosip.registration.processor.stages.app.constants;

public class DemoDedupeConstants {

	public static final String FINGERTYPE = "fingerType";

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant USER. */
	public static final String USER = "MOSIP_SYSTEM";

	/** The Constant CREATED_BY. */
	public static final String CREATED_BY = "MOSIP";

	/** The Constant IDENTIFY. */
	public static final String IDENTIFY = "IDENTIFY";

	public static final String NO_DATA_IN_DEMO = "Duplicate data not saved in demo list table";
	public static final String RECORD_INSERTED_FROM_ABIS_HANDLER = "Record is inserted in demo dedupe potential match, destination stage is abis handler";
	public static final String DEMO_SUCCESS = "Demo dedupe successful. No duplicates found";
	public static final String SENDING_TO_REPROCESS = "Failed in Abis. Hence sending to Reprocess";
	public static final String NO_DUPLICATES_FOUND = "ABIS response Details null, hence no duplicates found";
	public static final String SENDING_FOR_MANUAL = "ABIS response Details found. Hence sending to manual adjudication";
	public static final String REJECTED_OR_REREGISTER = "The packet status is Rejected or Re-Register. Hence ignoring Registration Id";
	public static final String NO_MATCH_FOUND = "No matched RegistrationId's found. Hence data is not inserting in manual adjudication table";
	public static final String DEMO_SKIP = "Demographic Deduplication Skipped";
}
