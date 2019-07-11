package pt.dto.encrypted;

public class MatchInfoData {

	private String authType;
	private String language;
	private String matchingStrategy;
	private String matchingThreshold;

	public MatchInfoData() {

	}

	public MatchInfoData(String authType, String language, String matchingStrategy, String matchingThreshold) {
		super();
		this.authType = authType;
		this.language = language;
		this.matchingStrategy = matchingStrategy;
		this.matchingThreshold = matchingThreshold;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMatchingStrategy() {
		return matchingStrategy;
	}

	public void setMatchingStrategy(String matchingStrategy) {
		this.matchingStrategy = matchingStrategy;
	}

	public String getMatchingThreshold() {
		return matchingThreshold;
	}

	public void setMatchingThreshold(String matchingThreshold) {
		this.matchingThreshold = matchingThreshold;
	}
}
