package io.mosip.registrationProcessor.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketFlowStatus {
	List<String> stageBits;

	public PacketFlowStatus() {
		stageBits = Arrays.asList("SUCCESS", "SUCCESS", "SUCCESS", "SUCCESS", "SUCCESS", "SUCCESS", "SUCCESS",
				"SUCCESS");
	}

	public List<String> setStageStatus(List<Integer> stageStatus) {
		for (int i : stageStatus) {
			if (i == 0) {
				stageBits.set(stageStatus.indexOf(i), "FAILED");
			}
		}
		return stageBits;
	}
	

}
