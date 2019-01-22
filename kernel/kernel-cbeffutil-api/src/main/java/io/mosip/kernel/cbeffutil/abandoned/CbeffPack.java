/**
 * 
 */
package io.mosip.kernel.cbeffutil.abandoned;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.mosip.kernel.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;

/**
 * @author M1049825
 *
 */
public class CbeffPack {
	
	private int major; 
	private int minor; 
	private int cbeffMajor; 
	private int cbeffMinor; 
	private Date creationDate;
	private String creator;
	private String index; //uuid : [a-fA-F0-9]{8}\-([a-fA-F0-9]{4}\-){3}[a-fA-F0-9]{12}
	private boolean integrity;
	private Date notValidAfter;
	private Date notValidBefore;
	private Date BDBCreationDate;
	private long BDBFormatOwner;
	private long BDBFormatType;
	private long BDBProductOwner;
	private long BDBProductType;
	private int quality;
	private PurposeType purpose;
	private ProcessedLevelType level;
	private Date bdbNotValidAfter;
	private Date bdbNotValidBefore;
	private List<SingleType> type;
	private List<String> subtype;
	private int SBIFormatOwner;
	private int SBIFormatType;
	private byte[] bdb;
	private byte[] sb;
	
	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getCbeffMajor() {
		return cbeffMajor;
	}

	public void setCbeffMajor(int cbeffMajor) {
		this.cbeffMajor = cbeffMajor;
	}

	public int getCbeffMinor() {
		return cbeffMinor;
	}

	public void setCbeffMinor(int cbeffMinor) {
		this.cbeffMinor = cbeffMinor;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public boolean isIntegrity() {
		return integrity;
	}

	public void setIntegrity(boolean integrity) {
		this.integrity = integrity;
	}

	public Date getNotValidAfter() {
		return notValidAfter;
	}

	public void setNotValidAfter(Date notValidAfter) {
		this.notValidAfter = notValidAfter;
	}

	public Date getNotValidBefore() {
		return notValidBefore;
	}

	public void setNotValidBefore(Date notValidBefore) {
		this.notValidBefore = notValidBefore;
	}

	public Date getBDBCreationDate() {
		return BDBCreationDate;
	}

	public void setBDBCreationDate(Date bDBCreationDate) {
		BDBCreationDate = bDBCreationDate;
	}

	public long getBDBFormatOwner() {
		return BDBFormatOwner;
	}

	public void setBDBFormatOwner(long bDBFormatOwner) {
		BDBFormatOwner = bDBFormatOwner;
	}

	public long getBDBFormatType() {
		return BDBFormatType;
	}

	public void setBDBFormatType(long bDBFormatType) {
		BDBFormatType = bDBFormatType;
	}

	public long getBDBProductOwner() {
		return BDBProductOwner;
	}

	public void setBDBProductOwner(long bDBProductOwner) {
		BDBProductOwner = bDBProductOwner;
	}

	public long getBDBProductType() {
		return BDBProductType;
	}

	public void setBDBProductType(long bDBProductType) {
		BDBProductType = bDBProductType;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public PurposeType getPurpose() {
		return purpose;
	}

	public void setPurpose(PurposeType purpose) {
		this.purpose = purpose;
	}

	public ProcessedLevelType getLevel() {
		return level;
	}

	public void setLevel(ProcessedLevelType level) {
		this.level = level;
	}

	public Date getBdbNotValidAfter() {
		return bdbNotValidAfter;
	}

	public void setBdbNotValidAfter(Date bdbNotValidAfter) {
		this.bdbNotValidAfter = bdbNotValidAfter;
	}

	public Date getBdbNotValidBefore() {
		return bdbNotValidBefore;
	}

	public void setBdbNotValidBefore(Date bdbNotValidBefore) {
		this.bdbNotValidBefore = bdbNotValidBefore;
	}

	public int getSBIFormatOwner() {
		return SBIFormatOwner;
	}

	public void setSBIFormatOwner(int sBIFormatOwner) {
		SBIFormatOwner = sBIFormatOwner;
	}

	public int getSBIFormatType() {
		return SBIFormatType;
	}

	public void setSBIFormatType(int sBIFormatType) {
		SBIFormatType = sBIFormatType;
	}

	public byte[] getBdb() {
		return bdb;
	}

	public void setBdb(byte[] bdb) {
		this.bdb = bdb;
	}

	public byte[] getSb() {
		return sb;
	}

	public void setSb(byte[] sb) {
		this.sb = sb;
	}

	public List<SingleType> getType() {
        if (type == null) {
            type = new ArrayList<SingleType>();
        }
        return this.type;
    }
	
	 public List<String> getSubtype() {
	        if (subtype == null) {
	            subtype = new ArrayList<String>();
	        }
	        return this.subtype;
	    }

}
