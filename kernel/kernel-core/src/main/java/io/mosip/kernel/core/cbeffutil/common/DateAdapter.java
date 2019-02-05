/**
 * 
 */
package io.mosip.kernel.core.cbeffutil.common;

/**
 * @author Ramadurai Pandian
 * Date Adaptor class to print date to specific format
 */
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, LocalDateTime> {

    /**
   	 * @param v formatted date
   	 * @return Date
   	 */    
	@Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }
 
	/**
	 * @param v date
	 * @return String formatted date
	 */
	@Override
    public String marshal(LocalDateTime v) throws Exception {
        return v.toInstant(ZoneOffset.UTC).toString();
    }

}