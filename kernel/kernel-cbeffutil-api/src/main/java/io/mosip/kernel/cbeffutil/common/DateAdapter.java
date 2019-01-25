/**
 * 
 */
package io.mosip.kernel.cbeffutil.common;

/**
 * @author Ramadurai Pandian
 * Date Adaptor class to print date to specific format
 */
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMDDhhmmssZ");

    /**
	 * @param date
	 * @return string formatted date
	 */
    @Override
    public String marshal(Date v) throws Exception {
        synchronized (dateFormat) {
            return dateFormat.format(v);
        }
    }

    /**
   	 * @param string formatted date
   	 * @return date
   	 */
    @Override
    public Date unmarshal(String v) throws Exception {
        synchronized (dateFormat) {
            return dateFormat.parse(v);
        }
    }

}