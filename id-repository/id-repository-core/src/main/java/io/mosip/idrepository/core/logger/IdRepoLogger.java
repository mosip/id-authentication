package io.mosip.idrepository.core.logger;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;

/**
 * Logger for IdRepo which provides implementation from kernel logback.
 * 
 * @author Manoj SP
 *
 */
public final class IdRepoLogger {

	private static String uin;
	
	private static String rid;
	
	private static String vid;

	public static String getVid() {
		return vid;
	}

	public static void setVid(String vid) {
		IdRepoLogger.vid = vid;
	}

	private static RollingFileAppender mosipRollingFileAppender;

	static {
		mosipRollingFileAppender = new RollingFileAppender();
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setAppenderName("fileappender");
		mosipRollingFileAppender.setFileName("logs/id-repo.log");
		mosipRollingFileAppender.setFileNamePattern("logs/id-repo-%d{yyyy-MM-dd}-%i.log");
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setMaxFileSize("1mb");
		mosipRollingFileAppender.setMaxHistory(3);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setTotalCap("10mb");
	}

	public static String getUin() {
		return uin;
	}

	public static void setUin(String uin) {
		IdRepoLogger.uin = uin;
	}

	/**
	 * Instantiates a new id repo logger.
	 */
	private IdRepoLogger() {
	}

	public static String getRid() {
		return rid;
	}

	public static void setRid(String rid) {
		IdRepoLogger.rid = rid;
	}

	/**
	 * Method to get the rolling file logger for the class provided.
	 *
	 * @param clazz the clazz
	 * @return the logger
	 */
	public static Logger getLogger(Class<?> clazz) {
		return Logfactory.getDefaultRollingFileLogger(mosipRollingFileAppender, clazz);
	}
}
