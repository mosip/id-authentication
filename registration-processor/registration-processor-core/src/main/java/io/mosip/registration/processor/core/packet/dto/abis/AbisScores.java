package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;
import java.util.Arrays;

import lombok.Data;

@Data
public class AbisScores implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 785216354868291150L;
private String biometricType;
private String scaledScore;

private String internalScore;
private Analytics[] analytics;
public Analytics[] getAnalytics() {
	if(analytics!=null)
		return Arrays.copyOf(analytics, analytics.length);
	return null;
}

public void setAnalytics(Analytics[] analytics) {
	this.analytics = analytics!=null?analytics:null;
}

}
