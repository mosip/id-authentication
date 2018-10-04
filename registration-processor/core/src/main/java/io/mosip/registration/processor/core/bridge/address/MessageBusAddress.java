package io.mosip.registration.processor.core.bridge.address;

public class MessageBusAddress {
	
	private MessageBusAddress() {
		
	}
	public static final String BATCH_BUS = "batch-bus";
	public static final String STRUCTURE_BUS_IN = "structure-bus-in";
	public static final String STRUCTURE_BUS_OUT = "structure-bus-out";
	public static final String DEMOGRAPHIC_BUS_IN = "demographic-bus-in";
	public static final String DEMOGRAPHIC_BUS_OUT = "demographic-bus-out";
	public static final String BIOMETRIC_BUS_IN = "biometric-bus-in";
	public static final String BIOMETRIC_BUS_OUT = "biometric-bus-out";
	public static final String FAILURE_BUS = "failure-bus";
	public static final String RETRY_BUS = "retry";
	public static final String ERROR = "error";

	public static String getBatchBus() {
		return BATCH_BUS;
	}

	public static String getStructureBusIn() {
		return STRUCTURE_BUS_IN;
	}

	public static String getStructureBusOut() {
		return STRUCTURE_BUS_OUT;
	}

	public static String getDemographicBusIn() {
		return DEMOGRAPHIC_BUS_IN;
	}

	public static String getDemographicBusOut() {
		return DEMOGRAPHIC_BUS_OUT;
	}

	public static String getBiometricBusIn() {
		return BIOMETRIC_BUS_IN;
	}

	public static String getBiometricBusOut() {
		return BIOMETRIC_BUS_OUT;
	}

	public static String getFailureBus() {
		return FAILURE_BUS;
	}

	public static String getRetryBus() {
		return RETRY_BUS;
	}

	public static String getError() {
		return ERROR;
	}
}
