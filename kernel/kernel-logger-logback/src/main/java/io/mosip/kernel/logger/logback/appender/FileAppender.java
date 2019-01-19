/*
 * 
 * 
 * 
 */
package io.mosip.kernel.logger.logback.appender;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import io.mosip.kernel.logger.logback.constant.ConfigurationDefault;



/**
 * File Appender for mosip
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@XmlRootElement
@XmlSeeAlso(RollingFileAppender.class)
public class FileAppender {

	/**
	 * Name of the appender
	 */
	private String appenderName;
	/**
	 * It ensures that logging events are immediately written out;with default
	 * true
	 */
	private boolean immediateFlush = ConfigurationDefault.DEFAULTIMMEDIATEFLUSH;
	/**
	 * Name of File in which logs will be written;<b>Mandatory field to pass</b>
	 */
	private String fileName;
	/**
	 * Append in current file;with default true
	 */
	private boolean append = ConfigurationDefault.DEFAULTAPPEND;
	/**
	 * FileAppender will safely write to the specified file if true,even in the
	 * presence of other FileAppender instances running in different JVMs,
	 * potentially running on different hosts; with default false
	 */
	private boolean prudent = ConfigurationDefault.DEFAULTPRUDENT;

	/**
	 * Getter for appenderName
	 * 
	 * @return name of current Appender
	 */
	public String getAppenderName() {
		return appenderName;
	}
	

	/**
	 * Getter for immediateFlush
	 * 
	 * @return current immediateFlush value
	 */
	public boolean isImmediateFlush() {
		return immediateFlush;
	}


	/**
	 * Setter for appenderName
	 * 
	 * @param appenderName
	 *            name of the appender
	 */
	@XmlAttribute
	public void setAppenderName(String appenderName) {
		this.appenderName = appenderName;
	}

	/**
	 * Setter for immediateFlush
	 * 
	 * @param immediateFlush
	 *            it ensures that logging events are immediately written
	 *            out;with default true
	 */
	@XmlElement
	public void setImmediateFlush(boolean immediateFlush) {
		this.immediateFlush = immediateFlush;
	}

	/**
	 * Getter for fileName
	 * 
	 * @return name of the file
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Setter for fileName
	 * 
	 * @param fileName
	 *            name of File in which logs will be written;<b>Mandatory field
	 *            to pass</b>
	 */
	@XmlElement
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Getter for append
	 * 
	 * @return current append value
	 */
	public boolean isAppend() {
		return append;
	}

	/**
	 * Setter for append
	 * 
	 * @param append
	 *            append in current file;with default true
	 */
	@XmlElement
	public void setAppend(boolean append) {
		this.append = append;
	}

	/**
	 * Getter for prudent
	 * 
	 * @return current prudent value
	 */
	public boolean isPrudent() {
		return prudent;
	}

	/**
	 * Setter for prudent
	 * 
	 * @param prudent
	 *            fileAppender will safely write to the specified file if
	 *            true,even in the presence of other FileAppender instances
	 *            running in different JVMs, potentially running on different
	 *            hosts; with default false
	 */
	@XmlElement
	public void setPrudent(boolean prudent) {
		this.prudent = prudent;
	}

}