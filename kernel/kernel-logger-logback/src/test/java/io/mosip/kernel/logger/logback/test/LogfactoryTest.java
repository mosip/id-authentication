package io.mosip.kernel.logger.logback.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.mosip.kernel.core.exception.IllegalStateException;
import io.mosip.kernel.core.logger.exception.ClassNameNotFoundException;
import io.mosip.kernel.core.logger.exception.EmptyPatternException;
import io.mosip.kernel.core.logger.exception.FileNameNotProvided;
import io.mosip.kernel.core.logger.exception.ImplementationNotFound;
import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.logger.exception.XMLConfigurationParseException;
import io.mosip.kernel.core.exception.PatternSyntaxException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.ConsoleAppender;
import io.mosip.kernel.logger.logback.appender.FileAppender;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.constant.LoggerMethod;
import io.mosip.kernel.logger.logback.factory.Logfactory;

import org.springframework.core.io.ClassPathResource;

public class LogfactoryTest {

	private FileAppender mosipFileAppender;
	private ConsoleAppender mosipConsoleAppender;
	private RollingFileAppender mosipRollingFileAppender;
	private String FILENAME;
	private File consoleAppenderFile;
	private File fileAppenderFile;
	private File rollingFileAppenderFile;
	@Before
	public void setUp() throws IOException {
		FILENAME = "src/test/resources/test.txt";
		mosipFileAppender = new FileAppender();
		mosipConsoleAppender = new ConsoleAppender();
		mosipRollingFileAppender = new RollingFileAppender();
		consoleAppenderFile = new ClassPathResource("/consoleappender.xml")
				.getFile();
		fileAppenderFile = new ClassPathResource("/fileappender.xml").getFile();
		rollingFileAppenderFile = new ClassPathResource(
				"/rollingfileappender.xml").getFile();
	}

	//@Test
	public void testGetMosipDefaultConsoleLoggerClazz() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				Logfactory.getMosipDefaultConsoleLogger(
						mosipConsoleAppender, LogfactoryTest.class),
				isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultConsoleLoggerName() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				Logfactory.getMosipDefaultConsoleLogger(
						mosipConsoleAppender, "MosipLogfactoryTest"),
				isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultConsoleLoggerClazzImplementation() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(Logfactory.getMosipConsoleLogger(mosipConsoleAppender,
				LoggerMethod.MOSIPLOGBACK, LogfactoryTest.class),
				isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultConsoleLoggerClazzImplementationExcepTion() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				Logfactory.getMosipConsoleLogger(mosipConsoleAppender,
						null, LogfactoryTest.class),
				isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultConsoleLoggerNameImplementation() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				Logfactory.getMosipConsoleLogger(mosipConsoleAppender,
						LoggerMethod.MOSIPLOGBACK, "MosipLogfactoryTest"),
				isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultConsoleLoggerNameImplementationExcepTion() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(Logfactory.getMosipConsoleLogger(mosipConsoleAppender,
				null, "MosipLogfactoryTest"), isA(Logger.class));
	}

	//@Test(expected = ClassNameNotFoundException.class)
	public void testGetMosipDefaultConsoleLoggerNameWithTargetNameException() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		Logfactory.getMosipDefaultConsoleLogger(mosipConsoleAppender, "");
	}

	//@Test
	public void testGetMosipDefaultFileLoggerClassWithoutRolling() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(Logfactory.getMosipDefaultFileLogger(mosipFileAppender,
				LogfactoryTest.class), isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultFileLoggerNameWithoutRolling() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(Logfactory.getMosipDefaultFileLogger(mosipFileAppender,
				"MosipLogfactoryTest"), isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultFileLoggerClassWithoutRollingImplementation() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(Logfactory.getMosipFileLogger(mosipFileAppender,
				LoggerMethod.MOSIPLOGBACK, LogfactoryTest.class),
				isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultFileLoggerNameWithoutRollingImplementation() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(
				Logfactory.getMosipFileLogger(mosipFileAppender,
						LoggerMethod.MOSIPLOGBACK, "MosipLogfactoryTest"),
				isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultFileLoggerClassWithoutRollingImplementationException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(Logfactory.getMosipFileLogger(mosipFileAppender, null,
				LogfactoryTest.class), isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultFileLoggerNameWithoutRollingImplementationException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(Logfactory.getMosipFileLogger(mosipFileAppender, null,
				"MosipLogfactoryTest"), isA(Logger.class));
	}

	//@Test(expected = ClassNameNotFoundException.class)
	public void testGetMosipDefaultFileLoggerNameWithoutRollingNameException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		Logfactory.getMosipDefaultFileLogger(mosipFileAppender, "");
	}

	//@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithoutRollingFileNullException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(null);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		Logfactory.getMosipDefaultFileLogger(mosipFileAppender,
				"MosipLogfactoryTest");
	}

	//@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithoutRollingFileEmptyException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName("");
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		Logfactory.getMosipDefaultFileLogger(mosipFileAppender,
				"MosipLogfactoryTest");
	}

	//@Test
	public void testGetMosipDefaultFileLoggerClazzWithRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(
				Logfactory.getMosipDefaultRollingFileLogger(
						mosipRollingFileAppender, LogfactoryTest.class),
				isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultFileLoggerNameWithRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(
				Logfactory.getMosipDefaultRollingFileLogger(
						mosipRollingFileAppender, "MosipLogfactoryTest"),
				isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultFileLoggerClazzWithRollingImplementation() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(Logfactory.getMosipRollingFileLogger(
				mosipRollingFileAppender, LoggerMethod.MOSIPLOGBACK,
				LogfactoryTest.class), isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultFileLoggerNameWithRollingImplementation() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(
				Logfactory.getMosipRollingFileLogger(
						mosipRollingFileAppender,
						LoggerMethod.MOSIPLOGBACK, "MosipLogfactoryTest"),
				isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultFileLoggerClazzWithRollingImplementationException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(Logfactory.getMosipRollingFileLogger(
				mosipRollingFileAppender, null, LogfactoryTest.class),
				isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingImplementationException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(
				Logfactory.getMosipRollingFileLogger(
						mosipRollingFileAppender, null, "MosipLogfactoryTest"),
				isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultFileLoggerClazzWithFullRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		assertThat(
				Logfactory.getMosipDefaultRollingFileLogger(
						mosipRollingFileAppender, LogfactoryTest.class),
				isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultFileLoggerNameWithFullRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		assertThat(
				Logfactory.getMosipDefaultRollingFileLogger(
						mosipRollingFileAppender, "MosipLogfactoryTest"),
				isA(Logger.class));
	}

	//@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingFileNullException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(null);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		Logfactory.getMosipDefaultFileLogger(mosipRollingFileAppender,
				"MosipLogfactoryTest");
	}

	//@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingFileEmptyException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName("");
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		Logfactory.getMosipDefaultFileLogger(mosipRollingFileAppender,
				"MosipLogfactoryTest");
	}

	//@Test(expected = EmptyPatternException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingNullFilePattern() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern(null);
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		Logfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	//@Test(expected = EmptyPatternException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingEmptyFilePattern() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		Logfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	//@Test(expected = PatternSyntaxException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingWrongFilePattern() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern(FILENAME);
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");

		Logfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	//@Test(expected = PatternSyntaxException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingWrongFileNamePattern() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		Logfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	//@Test(expected = ClassNameNotFoundException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingClassMissing() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		Logfactory
				.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "");
	}

	//@Test(expected = IllegalStateException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingIllegalState() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{aaaa}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		Logfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	//@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingNullConstraintsException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(null);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		Logfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	//@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingEmptyConstraintsException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName("");
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		Logfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	//@Test(expected = PatternSyntaxException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingNotIConstraintsException() {
		mosipRollingFileAppender.setAppenderName("testFileRollingappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		Logfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	//@Test(expected = IllegalArgumentException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingIllegalArgumentException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("aaaaaaaaaaa");
		Logfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	//@Test
	public void testGetMosipDefaultConsoleLoggerDebugCall() {
		Logger mosipLogger = Mockito.mock(Logger.class);
		mosipLogger.debug(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		verify(mosipLogger, times(1)).debug(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());

	}

	//@Test
	public void testGetMosipDefaultConsoleLoggerTraceCall() {
		Logger mosipLogger = Mockito.mock(Logger.class);
		mosipLogger.trace(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		verify(mosipLogger, times(1)).trace(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());

	}

	//@Test
	public void testGetMosipDefaultConsoleLoggerErrorCall() {
		Logger mosipLogger = Mockito.mock(Logger.class);
		mosipLogger.error(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		verify(mosipLogger, times(1)).error(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());

	}

	//@Test
	public void testGetMosipDefaultConsoleLoggerWarnCall() {
		Logger mosipLogger = Mockito.mock(Logger.class);
		mosipLogger.warn(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		verify(mosipLogger, times(1)).warn(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());

	}

	//@Test
	public void testGetMosipDefaultConsoleLoggerInfoCall() {
		Logger mosipLogger = Mockito.mock(Logger.class);
		mosipLogger.info(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		verify(mosipLogger, times(1)).info(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
	}

	//@Test
	public void testGetMosipDefaultFileLoggerDebugCall() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		Logger mosipLogger = Logfactory.getMosipDefaultFileLogger(
				mosipFileAppender, LogfactoryTest.class);
		mosipLogger.debug("sessionid", "idType", "id", "description");
	}

	//@Test
	public void testGetMosipDefaultFileLoggerTraceCall() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		Logger mosipLogger = Logfactory.getMosipDefaultFileLogger(
				mosipFileAppender, LogfactoryTest.class);
		mosipLogger.trace("sessionid", "idType", "id", "description");

	}

	//@Test
	public void testGetMosipDefaultFileLoggerErrorCall() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		Logger mosipLogger = Logfactory.getMosipDefaultFileLogger(
				mosipFileAppender, LogfactoryTest.class);
		mosipLogger.error("sessionid", "idType", "id", "description");

	}

	//@Test
	public void testGetMosipDefaultFileLoggerWarnCall() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		Logger mosipLogger = Logfactory.getMosipDefaultFileLogger(
				mosipFileAppender, LogfactoryTest.class);
		mosipLogger.warn("sessionid", "idType", "id", "description");

	}

	//@Test
	public void testGetMosipDefaultFileLoggerInfoCall() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		Logger mosipLogger = Logfactory.getMosipDefaultFileLogger(
				mosipFileAppender, LogfactoryTest.class);
		mosipLogger.info("sessionid", "idType", "id", "description");
	}

	//@Test
	public void testGetMosipFileConsoleLoggerClazzImplementation() {
		assertThat(Logfactory.getMosipConsoleLogger(consoleAppenderFile,
				LoggerMethod.MOSIPLOGBACK, LogfactoryTest.class),
				isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipFileConsoleLoggerClazzImplementationExcepTion() {
		Logfactory.getMosipConsoleLogger(consoleAppenderFile, null,
				LogfactoryTest.class);
	}

	//@Test
	public void testGetMosipFileLoggerClazzImplementation() {
		assertThat(Logfactory.getMosipFileLogger(fileAppenderFile,
				LoggerMethod.MOSIPLOGBACK, LogfactoryTest.class),
				isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipFileLoggerClazzImplementationExcepTion() {
		Logfactory.getMosipFileLogger(fileAppenderFile, null,
				LogfactoryTest.class);
	}

	//@Test
	public void testGetMosipRollingFileConsoleLoggerClazzImplementation() {
		assertThat(Logfactory.getMosipRollingFileLogger(
				rollingFileAppenderFile, LoggerMethod.MOSIPLOGBACK,
				LogfactoryTest.class), isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipRollingFileConsoleLoggerClazzImplementationExcepTion() {
		Logfactory.getMosipRollingFileLogger(rollingFileAppenderFile, null,
				LogfactoryTest.class);
	}
	//@Test
	public void testGetMosipNameFileConsoleLoggerClazzImplementation() {
		assertThat(
				Logfactory.getMosipConsoleLogger(consoleAppenderFile,
						LoggerMethod.MOSIPLOGBACK, "MosipLogfactoryTest"),
				isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipNameFileConsoleLoggerClazzImplementationExcepTion() {
		Logfactory.getMosipConsoleLogger(consoleAppenderFile, null,
				"MosipLogfactoryTest");
	}

	//@Test
	public void testGetMosipNameFileLoggerClazzImplementation() {
		assertThat(
				Logfactory.getMosipFileLogger(fileAppenderFile,
						LoggerMethod.MOSIPLOGBACK, "MosipLogfactoryTest"),
				isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipNameFileLoggerClazzImplementationExcepTion() {
		Logfactory.getMosipFileLogger(fileAppenderFile, null,
				"MosipLogfactoryTest");
	}

	//@Test
	public void testGetMosipNameRollingFileConsoleLoggerClazzImplementation() {
		assertThat(Logfactory.getMosipRollingFileLogger(
				rollingFileAppenderFile, LoggerMethod.MOSIPLOGBACK,
				"MosipLogfactoryTest"), isA(Logger.class));
	}

	//@Test(expected = ImplementationNotFound.class)
	public void testGetMosipNameRollingFileConsoleLoggerClazzImplementationExcepTion() {
		Logfactory.getMosipRollingFileLogger(rollingFileAppenderFile, null,
				"MosipLogfactoryTest");
	}

	//@Test
	public void testGetMosipDefaultFileConsoleLoggerClazzImplementation() {
		assertThat(
				Logfactory.getMosipDefaultConsoleLogger(
						consoleAppenderFile, LogfactoryTest.class),
				isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultFileLoggerClazzImplementation() {
		assertThat(Logfactory.getMosipDefaultFileLogger(fileAppenderFile,
				LogfactoryTest.class), isA(Logger.class));
	}

	//@Test
	public void testGetMosipDefaultRollingFileConsoleLoggerClazzImplementation() {
		assertThat(
				Logfactory.getMosipDefaultRollingFileLogger(
						rollingFileAppenderFile, LogfactoryTest.class),
				isA(Logger.class));
	}

	//@Test
	public void testGetMosipNameDefaultFileConsoleLoggerClazzImplementation() {
		assertThat(
				Logfactory.getMosipDefaultConsoleLogger(
						consoleAppenderFile, "MosipLogfactoryTest"),
				isA(Logger.class));
	}

	//@Test
	public void testGetMosipNameDefaultFileLoggerClazzImplementation() {
		assertThat(Logfactory.getMosipDefaultFileLogger(fileAppenderFile,
				"MosipLogfactoryTest"), isA(Logger.class));
	}

	//@Test
	public void testGetMosipNameDefaultRollingFileConsoleLoggerClazzImplementation() {
		assertThat(
				Logfactory.getMosipDefaultRollingFileLogger(
						rollingFileAppenderFile, "MosipLogfactoryTest"),
				isA(Logger.class));
	}
	//@Test(expected = XMLConfigurationParseException.class)
	public void testGetMosipNameDefaultRollingFileConsoleLoggerClazzImplementationParseException()
			throws IOException {
		rollingFileAppenderFile = new ClassPathResource(
				"/rollingfileappenderexception.xml").getFile();
		Logfactory.getMosipDefaultRollingFileLogger(
				rollingFileAppenderFile, "MosipLogfactoryTest");
	}

}
