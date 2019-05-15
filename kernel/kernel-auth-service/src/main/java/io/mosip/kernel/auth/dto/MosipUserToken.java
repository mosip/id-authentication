package io.mosip.kernel.auth.dto;

public class MosipUserToken {
	private String token;
	private MosipUser mosipUser;

	public MosipUserToken() {
	}

	public MosipUserToken(MosipUser mosipUser, String token) {
		this.mosipUser = mosipUser;
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public MosipUser getMosipUser() {
		return mosipUser;
	}

	public void setMosipUser(MosipUser mosipUser) {
		this.mosipUser = mosipUser;
	}
}
