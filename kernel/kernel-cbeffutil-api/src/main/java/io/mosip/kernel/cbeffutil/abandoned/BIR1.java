/**
 * 
 */
package io.mosip.kernel.cbeffutil.abandoned;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.mosip.kernel.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;

/**
 * @author M1049825
 *
 */
public class BIR1 {
	
	private int major; 
	private int minor; 
	private int cbeffMajor; 
	private int cbeffMinor; 
	private Date creationDate;
	private String creator;
	private String index; 
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
	private byte[] bdb;
	
	public BIR1(BIRBuilder1 birBuilder) {
		this.major = birBuilder.major;
		this.minor = birBuilder.minor;
		this.cbeffMajor = birBuilder.cbeffMajor;
		this.cbeffMinor = birBuilder.cbeffMinor;
		this.creationDate = birBuilder.creationDate;
		this.creator = birBuilder.creator;
		this.index = birBuilder.index;
		this.integrity = birBuilder.integrity;
		this.notValidAfter = birBuilder.notValidAfter;
		this.notValidBefore = birBuilder.notValidBefore;
		this.BDBCreationDate = birBuilder.BDBCreationDate;
		this.BDBFormatOwner = birBuilder.BDBFormatOwner;
		this.BDBFormatType = birBuilder.BDBFormatType;
		this.BDBProductOwner = birBuilder.BDBProductOwner;
		this.BDBProductType = birBuilder.BDBProductType;
		this.quality = birBuilder.quality;
		this.purpose = birBuilder.purpose;
		this.level = birBuilder.level;
		this.bdbNotValidAfter = birBuilder.bdbNotValidAfter;
		this.bdbNotValidBefore = birBuilder.bdbNotValidBefore;
		this.type = birBuilder.type;
		this.subtype = birBuilder.subtype;
		this.bdb = birBuilder.bdb;
	}
	
	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getCbeffMajor() {
		return cbeffMajor;
	}

	public int getCbeffMinor() {
		return cbeffMinor;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getCreator() {
		return creator;
	}

	public String getIndex() {
		return index;
	}

	public boolean isIntegrity() {
		return integrity;
	}

	public Date getNotValidAfter() {
		return notValidAfter;
	}

	public Date getNotValidBefore() {
		return notValidBefore;
	}

	public Date getBDBCreationDate() {
		return BDBCreationDate;
	}

	public long getBDBFormatOwner() {
		return BDBFormatOwner;
	}

	public long getBDBFormatType() {
		return BDBFormatType;
	}

	public long getBDBProductOwner() {
		return BDBProductOwner;
	}

	public long getBDBProductType() {
		return BDBProductType;
	}

	public int getQuality() {
		return quality;
	}

	public PurposeType getPurpose() {
		return purpose;
	}

	public ProcessedLevelType getLevel() {
		return level;
	}

	public Date getBdbNotValidAfter() {
		return bdbNotValidAfter;
	}

	public Date getBdbNotValidBefore() {
		return bdbNotValidBefore;
	}

	public List<SingleType> getType() {
		return type;
	}

	public List<String> getSubtype() {
		return subtype;
	}

	public byte[] getBdb() {
		return bdb;
	}

	public static class BIRBuilder1{
		
		private int major; 
		private int minor; 
		private int cbeffMajor; 
		private int cbeffMinor; 
		private Date creationDate;
		private String creator;
		private String index; 
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
		private byte[] bdb;
		
		public BIRBuilder1() {
		}
		
		public BIRBuilder1 withMajor(int major) {
			this.major = major;
			return this;
		}
		public BIRBuilder1 withMinor(int minor) {
			this.minor = minor;
			return this;
		}
		public BIRBuilder1 withCbeffMajor(int cbeffMajor) {
			this.cbeffMajor = cbeffMajor;
			return this;
		}
		public BIRBuilder1 withCbeffMinor(int cbeffMinor) {
			this.cbeffMinor = cbeffMinor;
			return this;
		}
		public BIRBuilder1 withCreationDate(Date creationDate) {
			this.creationDate = creationDate;
			return this;
		}
		public BIRBuilder1 withCreator(String creator) {
			this.creator = creator;
			return this;
		}
		public BIRBuilder1 withIndex(String index) {
			this.index = index;
			return this;
		}
		public BIRBuilder1 withIntegrity(boolean integrity) {
			this.integrity = integrity;
			return this;
		}
		public BIRBuilder1 withNotValidAfter(Date notValidAfter) {
			this.notValidAfter = notValidAfter;
			return this;
		}
		public BIRBuilder1 withNotValidBefore(Date notValidBefore) {
			this.notValidBefore = notValidBefore;
			return this;
		}
		public BIRBuilder1 withBDBCreationDate(Date bDBCreationDate) {
			BDBCreationDate = bDBCreationDate;
			return this;
		}
		public BIRBuilder1 withBDBFormatOwner(long bDBFormatOwner) {
			BDBFormatOwner = bDBFormatOwner;
			return this;
		}
		public BIRBuilder1 withBDBFormatType(long bDBFormatType) {
			BDBFormatType = bDBFormatType;
			return this;
		}
		public BIRBuilder1 withBDBProductOwner(long bDBProductOwner) {
			BDBProductOwner = bDBProductOwner;
			return this;
		}
		public BIRBuilder1 withBDBProductType(long bDBProductType) {
			BDBProductType = bDBProductType;
			return this;
		}
		public BIRBuilder1 withQuality(int quality) {
			this.quality = quality;
			return this;
		}
		public BIRBuilder1 withPurpose(PurposeType purpose) {
			this.purpose = purpose;
			return this;
		}
		public BIRBuilder1 withLevel(ProcessedLevelType level) {
			this.level = level;
			return this;
		}
		public BIRBuilder1 withBdbNotValidAfter(Date bdbNotValidAfter) {
			this.bdbNotValidAfter = bdbNotValidAfter;
			return this;
		}
		public BIRBuilder1 withBdbNotValidBefore(Date bdbNotValidBefore) {
			this.bdbNotValidBefore = bdbNotValidBefore;
			return this;
		}
		public BIRBuilder1 withType(List<SingleType> type) {
			this.type = type;
			return this;
		}
		public BIRBuilder1 withSubtype(List<String> subtype) {
			this.subtype = subtype;
			return this;
		}
		public BIRBuilder1 withBdb(byte[] bdb) {
			this.bdb = bdb;
			return this;
		}	
	
		public BIR1 build()
		{
			return new BIR1(this);
		}
	
	}
	
}
