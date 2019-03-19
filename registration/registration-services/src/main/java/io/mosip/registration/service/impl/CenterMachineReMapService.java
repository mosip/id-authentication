package io.mosip.registration.service.impl;

public interface CenterMachineReMapService {

	/**
	 * Checks and handles all the operations to be done when the machine is re
	 * mapped to another center
	 * 
	 */
	void handleReMapProcess(int step);

	/**
	 * checks if there is any Registration packets are not yet been processed by the
	 * Reg processor
	 * 
	 * @return boolean
	 */
	boolean isPacketsPendingForProcessing();

	/**
	 * Checks if there is any Reg packets are pending for EOD Approval
	 * 
	 * @return boolean
	 */
	boolean isPacketsPendingForEOD();

	/**
	 * checks if the Machine has been re mapped to some other center
	 * 
	 * @return
	 */
	Boolean isMachineRemapped();
}