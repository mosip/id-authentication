package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;

import lombok.Data;

/**
 * The Class CandidatesDto.
 * 
 * @author M1048860 Kiran Raj
 */
@Data
public class CandidatesDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4746292144740017993L;

	/** The reference id. */
	private String referenceId;

	/** The scaled score. */
	private String scaledScore;

	private String internalScore;

	private Analytics[] analytics;

	private AbisScores[] scores;

	public Analytics[] getAnalytics() {
		return analytics != null ? analytics.clone() : null;
	}

	public void setAnalytics(Analytics[] analytics) {
		this.analytics = analytics != null ? analytics : null;
	}

	public AbisScores[] getScores() {
		return scores != null ? scores.clone() : null;
	}

	public void setScores(AbisScores[] scores) {
		this.scores = scores != null ? scores : null;
	}

}
