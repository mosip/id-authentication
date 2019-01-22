/**
 * 
 */
package io.mosip.kernel.cbeffutil.abandoned;

import java.util.Date;
import java.util.List;

import io.mosip.kernel.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;

/**
 * @author M1049825
 *
 */
public class BIR2 {
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
}
