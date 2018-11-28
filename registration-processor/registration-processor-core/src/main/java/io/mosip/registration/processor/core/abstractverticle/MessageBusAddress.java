package io.mosip.registration.processor.core.abstractverticle;

import java.io.Serializable;

/**
 * This class contains the address values to be used in Registration process.
 *
 * @author Pranav Kumar
 * @author Mukul Puspam
 * @since 0.0.1
 */
public class MessageBusAddress implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new message bus address.
	 */
	public MessageBusAddress() {
	}

	/** The address. */
	private String address;

	/**
	 * Instantiates a new message bus address.
	 *
	 * @param address
	 *            The bus address
	 */
	public MessageBusAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the address.
	 *
	 * @return The address
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address
	 *            the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/** The Constant BATCH_BUS. */
	public static final MessageBusAddress BATCH_BUS = new MessageBusAddress("batch-bus");

	/** The Constant STRUCTURE_BUS_IN. */
	public static final MessageBusAddress STRUCTURE_BUS_IN = new MessageBusAddress("structure-bus-in");

	/** The Constant STRUCTURE_BUS_OUT. */
	public static final MessageBusAddress STRUCTURE_BUS_OUT = new MessageBusAddress("structure-bus-out");

	/** The Constant DEMOGRAPHIC_BUS_IN. */
	public static final MessageBusAddress DEMOGRAPHIC_BUS_IN = new MessageBusAddress("demographic-bus-in");

	/** The Constant DEMOGRAPHIC_BUS_OUT. */
	public static final MessageBusAddress DEMOGRAPHIC_BUS_OUT = new MessageBusAddress("demographic-bus-out");

	/** The Constant BIOMETRIC_BUS_IN. */
	public static final MessageBusAddress BIOMETRIC_BUS_IN = new MessageBusAddress("biometric-bus-in");

	/** The Constant BIOMETRIC_BUS_OUT. */
	public static final MessageBusAddress BIOMETRIC_BUS_OUT = new MessageBusAddress("biometric-bus-out");

	/** The Constant FAILURE_BUS. */
	public static final MessageBusAddress FAILURE_BUS = new MessageBusAddress("failure-bus");

	/** The Constant RETRY_BUS. */
	public static final MessageBusAddress RETRY_BUS = new MessageBusAddress("retry");

	/** The Constant ERROR. */
	public static final MessageBusAddress ERROR = new MessageBusAddress("error");
	public static final MessageBusAddress QUALITY_CHECK_BUS = new MessageBusAddress("quality_check_bus");
	
	/** The Constant VIRUS_SCAN_BUS. */
	public static final MessageBusAddress VIRUS_SCAN_BUS_IN = new MessageBusAddress("virus-scanner-stage");
	
	/** The Constant FTP_SCAN_BUS. */
	public static final MessageBusAddress FTP_SCAN_BUS_OUT = new MessageBusAddress("ftp-scanner-stage");
	
	/** The Constant LANDING_ZONE_BUS. */
	public static final MessageBusAddress LANDING_ZONE_BUS_OUT = new MessageBusAddress("landing-zone-scanner-stage");

	/** The Constant STRUCTURE_BUS_IN. */
	public static final MessageBusAddress OSI_BUS_IN = new MessageBusAddress("osi-bus-in");

	/** The Constant STRUCTURE_BUS_OUT. */
	public static final MessageBusAddress OSI_BUS_OUT = new MessageBusAddress("osi-bus-out");
	
	/** The Constant DEMODEDUPE_BUS_IN. */
	public static final MessageBusAddress DEMODEDUPE_BUS_IN = new MessageBusAddress("demodedupe-bus-in");

	/** The Constant DEMODEDUPE_BUS_OUT. */
	public static final MessageBusAddress DEMODEDUPE_BUS_OUT = new MessageBusAddress("demodedupe-bus-out");
	
}
