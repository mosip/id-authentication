/**
 * 
 */
package pt.dto.encrypted;

import java.util.Date;
import java.util.List;

/**
 * @author M1030608
 *
 */
public class AuthEntity {

	private AuthType authType;
	private String id;
	private String idvId;
	private String idvIdType;
	private Key key;
	private List<MatchInfoData> matchInfo;
	private String muaCode;
	private String reqHmac;
	private String reqTime;
	private String request;
	private String txnID;
	private String tspID;
	private String ver;

	/**
	 * 
	 */
	public AuthEntity() {
		// TODO Auto-generated constructor stub
	}

	public AuthEntity(AuthType authType, String id, String idvId, String idvIdType, Key key,
			List<MatchInfoData> matchInfo, String muaCode, String reqHmac, String reqTime, String request, String txnID,
			String tspID, String ver) {
		super();
		this.authType = authType;
		this.id = id;
		this.idvId = idvId;
		this.idvIdType = idvIdType;
		this.key = key;
		this.matchInfo = matchInfo;
		this.muaCode = muaCode;
		this.reqHmac = reqHmac;
		this.reqTime = reqTime;
		this.request = request;
		this.txnID = txnID;
		this.tspID = tspID;
		this.ver = ver;
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

	public List<MatchInfoData> getMatchInfo() {
		return matchInfo;
	}

	public void setMatchInfo(List<MatchInfoData> matchInfo) {
		this.matchInfo = matchInfo;
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
