package pt.dto.encrypted;

public class PersonalIdAuthEntity {

	private AuthType authType;
	private String id;
	private String idvId;
	private String idvIdType;
	private Key key;
	private String muaCode;
	private String reqHmac;
	private String reqTime;
	private String request;
	private String txnID;
	private String tspID;
	private String ver;

	public PersonalIdAuthEntity() {

	}

	public AuthType getAuthType() {
		return authType;
	}

	public void setAuthType(AuthType authType) {
		this.authType = authType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdvId() {
		return idvId;
	}

	public void setIdvId(String idvId) {
		this.idvId = idvId;
	}

	public String getIdvIdType() {
		return idvIdType;
	}

	public void setIdvIdType(String idvIdType) {
		this.idvIdType = idvIdType;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getMuaCode() {
		return muaCode;
	}

	public void setMuaCode(String muaCode) {
		this.muaCode = muaCode;
	}

	public String getReqHmac() {
		return reqHmac;
	}

	public void setReqHmac(String reqHmac) {
		this.reqHmac = reqHmac;
	}

	public String getReqTime() {
		return reqTime;
	}

	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getTxnID() {
		return txnID;
	}

	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}

	public String getTspID() {
		return tspID;
	}

	public void setTspID(String tspID) {
		this.tspID = tspID;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}
}
