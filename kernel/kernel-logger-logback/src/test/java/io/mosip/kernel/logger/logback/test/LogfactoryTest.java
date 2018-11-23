package io.mosip.kernel.logger.logback.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.exception.IllegalStateException;
import io.mosip.kernel.core.exception.PatternSyntaxException;
import io.mosip.kernel.core.logger.exception.ClassNameNotFoundException;
import io.mosip.kernel.core.logger.exception.EmptyPatternException;
import io.mosip.kernel.core.logger.exception.FileNameNotProvided;
import io.mosip.kernel.core.logger.exception.ImplementationNotFound;
import io.mosip.kernel.core.logger.exception.XMLConfigurationParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.ConsoleAppender;
import io.mosip.kernel.logger.logback.appender.FileAppender;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.constant.LoggerMethod;
import io.mosip.kernel.logger.logback.factory.Logfactory;

public class LogfactoryTest {

	private FileAppender mosipFileAppender;
	private ConsoleAppender mosipConsoleAppender;
	private RollingFileAppender mosipRollingFileAppender;
	private static  String FILENAME;
	private static  String FILEPATH;
	private File consoleAppenderFile;
	private File fileAppenderFile;
	private File rollingFileAppenderFile;
	 
	@BeforeClass
	public static void preSetUp() throws IOException {
		FILEPATH = "src/test/resources/test";
		FILENAME = FILEPATH+"/test.txt";
	}
	@Before
	public void setUp() throws IOException {
		mosipFileAppender = new FileAppender();
		mosipConsoleAppender = new ConsoleAppender();
		mosipRollingFileAppender = new RollingFileAppender();
		
		consoleAppenderFile = new ClassPathResource("/consoleappender.xml")
				.getFile();
		fileAppenderFile = new ClassPathResource("/fileappender.xml").getFile();
		rollingFileAppenderFile = new ClassPathResource(
				"/rollingfileappender.xml").getFile();
	}
	@AfterClass
	public static void cleanUp() throws IOException {
		Files.walk(new File("src/test/resources/test").toPath())
	    .sorted(Comparator.reverseOrder())
	    .map(Path::toFile)
	    .forEach(File::delete);
	}

	@Test
	public void testgetDefaultConsoleLoggerClazz() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				Logfactory.getDefaultConsoleLogger(
						mosipConsoleAppender, LogfactoryTest.class),
				isA(Logger.class));
	}

	@Test
	public void testgetDefaultConsoleLoggerName() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				Logfactory.getDefaultConsoleLogger(
						mosipConsoleAppender, "LogfactoryTest"),
				isA(Logger.class));
	}

	@Test
	public void testgetDefaultConsoleLoggerClazzImplementation() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(Logfactory.getConsoleLogger(mosipConsoleAppender,
				LoggerMethod.MOSIPLOGBACK, LogfactoryTest.class),
				isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetDefaultConsoleLoggerClazzImplementationExcepTion() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				Logfactory.getConsoleLogger(mosipConsoleAppender,
						null, LogfactoryTest.class),
				isA(Logger.class));
	}

	@Test
	public void testgetDefaultConsoleLoggerNameImplementation() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				Logfactory.getConsoleLogger(mosipConsoleAppender,
						LoggerMethod.MOSIPLOGBACK, "LogfactoryTest"),
				isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetDefaultConsoleLoggerNameImplementationExcepTion() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(Logfactory.getConsoleLogger(mosipConsoleAppender,
				null, "LogfactoryTest"), isA(Logger.class));
	}

	@Test(expected = ClassNameNotFoundException.class)
	public void testgetDefaultConsoleLoggerNameWithTargetNameException() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		Logfactory.getDefaultConsoleLogger(mosipConsoleAppender, "");
	}

	@Test
	public void testgetDefaultFileLoggerClassWithoutRolling() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(Logfactory.getDefaultFileLogger(mosipFileAppender,
				LogfactoryTest.class), isA(Logger.class));
	}

	@Test
	public void testgetDefaultFileLoggerNameWithoutRolling() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(Logfactory.getDefaultFileLogger(mosipFileAppender,
				"LogfactoryTest"), isA(Logger.class));
	}

	@Test
	public void testgetDefaultFileLoggerClassWithoutRollingImplementation() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(Logfactory.getFileLogger(mosipFileAppender,
				LoggerMethod.MOSIPLOGBACK, LogfactoryTest.class),
				isA(Logger.class));
	}

	@Test
	public void testgetDefaultFileLoggerNameWithoutRollingImplementation() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(
				Logfactory.getFileLogger(mosipFileAppender,
						LoggerMethod.MOSIPLOGBACK, "LogfactoryTest"),
				isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetDefaultFileLoggerClassWithoutRollingImplementationException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(Logfactory.getFileLogger(mosipFileAppender, null,
				LogfactoryTest.class), isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetDefaultFileLoggerNameWithoutRollingImplementationException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(Logfactory.getFileLogger(mosipFileAppender, null,
				"LogfactoryTest"), isA(Logger.class));
	}

	@Test(expected = ClassNameNotFoundException.class)
	public void testgetDefaultFileLoggerNameWithoutRollingNameException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		Logfactory.getDefaultFileLogger(mosipFileAppender, "");
	}

	@Test(expected = FileNameNotProvided.class)
	public void testgetDefaultFileLoggerNameWithoutRollingFileNullException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(null);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		Logfactory.getDefaultFileLogger(mosipFileAppender,
				"LogfactoryTest");
	}

	@Test(expected = FileNameNotProvided.class)
	public void testgetDefaultFileLoggerNameWithoutRollingFileEmptyException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName("");
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		Logfactory.getDefaultFileLogger(mosipFileAppender,
				"LogfactoryTest");
	}

	@Test
	public void testgetDefaultFileLoggerClazzWithRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("src/test/resources/test/testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(
				Logfactory.getDefaultRollingFileLogger(
						mosipRollingFileAppender, LogfactoryTest.class),
				isA(Logger.class));
	}

	@Test
	public void testgetDefaultFileLoggerNameWithRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("src/test/resources/test/testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(
				Logfactory.getDefaultRollingFileLogger(
						mosipRollingFileAppender, "LogfactoryTest"),
				isA(Logger.class));
	}

	@Test
	public void testgetDefaultFileLoggerClazzWithRollingImplementation() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("src/test/resources/test/testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(Logfactory.getRollingFileLogger(
				mosipRollingFileAppender, LoggerMethod.MOSIPLOGBACK,
				LogfactoryTest.class), isA(Logger.class));
	}

	@Test
	public void testgetDefaultFileLoggerNameWithRollingImplementation() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("src/test/resources/test/testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(
				Logfactory.getRollingFileLogger(
						mosipRollingFileAppender,
						LoggerMethod.MOSIPLOGBACK, "LogfactoryTest"),
				isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetDefaultFileLoggerClazzWithRollingImplementationException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("src/test/resources/test/testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(Logfactory.getRollingFileLogger(
				mosipRollingFileAppender, null, LogfactoryTest.class),
				isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetDefaultFileLoggerNameWithRollingImplementationException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("src/test/resources/test/testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(
				Logfactory.getRollingFileLogger(
						mosipRollingFileAppender, null, "LogfactoryTest"),
				isA(Logger.class));
	}

	@Test
	public void testgetDefaultFileLoggerClazzWithFullRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("src/test/resources/test/testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		assertThat(
				Logfactory.getDefaultRollingFileLogger(
						mosipRollingFileAppender, LogfactoryTest.class),
				isA(Logger.class));
	}

	@Test
	public void testgetDefaultFileLoggerNameWithFullRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("src/test/resources/test/testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		assertThat(
				Logfactory.getDefaultRollingFileLogger(
						mosipRollingFileAppender, "LogfactoryTest"),
				isA(Logger.class));
	}

	@Test(expected = FileNameNotProvided.class)
	public void testgetDefaultFileLoggerNameWithRollingFileNullException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(null);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("src/test/resources/test/testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		Logfactory.getDefaultFileLogger(mosipRollingFileAppender,
				"LogfactoryTest");
	}

	@Test(expected = FileNameNotProvided.class)
	public void testgetDefaultFileLoggerNameWithRollingFileEmptyException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName("");
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender
				.setFileNamePattern("src/test/resources/test/testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		Logfactory.getDefaultFileLogger(mosipRollingFileAppender,
				"LogfactoryTest");
	}

	@Test(expected = EmptyPatternException.class)
	public void testgetDefaultFileLoggerNameWithRollingNullFilePattern() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern(null);
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		Logfactory.getDefaultRollingFileLogger(
				mosipRollingFileAppender, "LogfactoryTest");
	}

	@Test(expected = EmptyPatternException.class)
	public void testgetDefaultFileLoggerNameWithRollingEmptyFilePattern() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		Logfactory.getDefaultRollingFileLogger(
				mosipRollingFileAppender, "LogfactoryTest");
	}

	@Test(expected = PatternSyntaxException.class)
	public void testgetDefaultFileLoggerNameWithRollingWrongFilePattern() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern(FILENAME);
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");

		Logfactory.getDefaultRollingFileLogger(
				mosipRollingFileAppender, "LogfactoryTest");
	}

	@Test(expected = PatternSyntaxException.class)
	public void testgetDefaultFileLoggerNameWithRollingWrongFileNamePattern() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("src/test/resources/test/test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		Logfactory.getDefaultRollingFileLogger(
				mosipRollingFileAppender, "LogfactoryTest");
	}

	@Test(expected = ClassNameNotFoundException.class)
	public void testgetDefaultFileLoggerNameWithRollingClassMissing() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("src/test/resources/test/test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		Logfactory
				.getDefaultRollingFileLogger(mosipRollingFileAppender, "");
	}

	@Test(expected = IllegalStateException.class)
	public void testgetDefaultFileLoggerNameWithRollingIllegalState() {
		mosipRollingFileAppender.setAppenderName("testRollingFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("src/test/resources/test/test-%d{aaaa}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		Logfactory.getDefaultRollingFileLogger(
				mosipRollingFileAppender, "LogfactoryTest");
	}

	@Test(expected = FileNameNotProvided.class)
	public void testgetDefaultFileLoggerNameWithRollingNullConstraintsException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(null);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("src/test/resources/test/test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		Logfactory.getDefaultRollingFileLogger(
				mosipRollingFileAppender, "LogfactoryTest");
	}

	@Test(expected = FileNameNotProvided.class)
	public void testgetDefaultFileLoggerNameWithRollingEmptyConstraintsException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName("");
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("src/test/resources/test/test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		Logfactory.getDefaultRollingFileLogger(
				mosipRollingFileAppender, "LogfactoryTest");
	}

	@Test(expected = PatternSyntaxException.class)
	public void testgetDefaultFileLoggerNameWithRollingNotIConstraintsException() {
		mosipRollingFileAppender.setAppenderName("testFileRollingappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("src/test/resources/test/test-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		Logfactory.getDefaultRollingFileLogger(
				mosipRollingFileAppender, "LogfactoryTest");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testgetDefaultFileLoggerNameWithRollingIllegalArgumentException() {
		mosipRollingFileAppender.setAppenderName("testRollingFileappenderIllegalArgumentException");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("src/test/resources/test/test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("aaaaaaaaaaa");
		Logfactory.getDefaultRollingFileLogger(
				mosipRollingFileAppender, "LogfactoryTest");
	}


	@Test
	public void testgetDefaultFileLoggerDebugCall() {
	 
		Logger mosipLogger = Logfactory.getDefaultConsoleLogger(mosipConsoleAppender, LogfactoryTest.class);
		mosipLogger.debug("sessionid", "idType", "id", "description");
	}

	@Test
	public void testgetDefaultFileLoggerTraceCall() {
		Logger mosipLogger = Logfactory.getDefaultConsoleLogger(mosipConsoleAppender, LogfactoryTest.class);
		mosipLogger.trace("sessionid", "idType", "id", "description");

	}

	@Test
	public void testgetDefaultFileLoggerErrorCall() {
		
		Logger mosipLogger = Logfactory.getDefaultConsoleLogger(mosipConsoleAppender, LogfactoryTest.class);
		mosipLogger.error("sessionid", "idType", "id", "description");

	}

	@Test
	public void testgetDefaultFileLoggerWarnCall() {
		Logger mosipLogger = Logfactory.getDefaultConsoleLogger(mosipConsoleAppender, LogfactoryTest.class);
		mosipLogger.warn("sessionid", "idType", "id", "description");

	}

	@Test
	public void testgetDefaultFileLoggerInfoCall() {
			Logger mosipLogger = Logfactory.getDefaultConsoleLogger(mosipConsoleAppender, LogfactoryTest.class);
		mosipLogger.info("sessionid", "idType", "id", "description");
	}

	@Test
	public void testgetFileConsoleLoggerClazzImplementation() {
		assertThat(Logfactory.getConsoleLogger(consoleAppenderFile,
				LoggerMethod.MOSIPLOGBACK, LogfactoryTest.class),
				isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetFileConsoleLoggerClazzImplementationExcepTion() {
		Logfactory.getConsoleLogger(consoleAppenderFile, null,
				LogfactoryTest.class);
	}

	@Test
	public void testgetFileLoggerClazzImplementation() {
		assertThat(Logfactory.getFileLogger(fileAppenderFile,
				LoggerMethod.MOSIPLOGBACK, LogfactoryTest.class),
				isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetFileLoggerClazzImplementationExcepTion() {
		Logfactory.getFileLogger(fileAppenderFile, null,
				LogfactoryTest.class);
	}

	@Test
	public void testgetRollingFileConsoleLoggerClazzImplementation() {
		assertThat(Logfactory.getRollingFileLogger(
				rollingFileAppenderFile, LoggerMethod.MOSIPLOGBACK,
				LogfactoryTest.class), isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetRollingFileConsoleLoggerClazzImplementationExcepTion() {
		Logfactory.getRollingFileLogger(rollingFileAppenderFile, null,
				LogfactoryTest.class);
	}
	@Test
	public void testgetNameFileConsoleLoggerClazzImplementation() {
		assertThat(
				Logfactory.getConsoleLogger(consoleAppenderFile,
						LoggerMethod.MOSIPLOGBACK, "LogfactoryTest"),
				isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetNameFileConsoleLoggerClazzImplementationExcepTion() {
		Logfactory.getConsoleLogger(consoleAppenderFile, null,
				"LogfactoryTest");
	}

	@Test
	public void testgetNameFileLoggerClazzImplementation() {
		assertThat(
				Logfactory.getFileLogger(fileAppenderFile,
						LoggerMethod.MOSIPLOGBACK, "LogfactoryTest"),
				isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetNameFileLoggerClazzImplementationExcepTion() {
		Logfactory.getFileLogger(fileAppenderFile, null,
				"LogfactoryTest");
	}

	@Test
	public void testgetNameRollingFileConsoleLoggerClazzImplementation() {
		assertThat(Logfactory.getRollingFileLogger(
				rollingFileAppenderFile, LoggerMethod.MOSIPLOGBACK,
				"LogfactoryTest"), isA(Logger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testgetNameRollingFileConsoleLoggerClazzImplementationExcepTion() {
		Logfactory.getRollingFileLogger(rollingFileAppenderFile, null,
				"LogfactoryTest");
	}

	@Test
	public void testgetDefaultFileConsoleLoggerClazzImplementation() {
		assertThat(
				Logfactory.getDefaultConsoleLogger(
						consoleAppenderFile, LogfactoryTest.class),
				isA(Logger.class));
	}

	@Test
	public void testgetDefaultFileLoggerClazzImplementation() {
		assertThat(Logfactory.getDefaultFileLogger(fileAppenderFile,
				LogfactoryTest.class), isA(Logger.class));
	}

	@Test
	public void testgetDefaultRollingFileConsoleLoggerClazzImplementation() {
		assertThat(
				Logfactory.getDefaultRollingFileLogger(
						rollingFileAppenderFile, LogfactoryTest.class),
				isA(Logger.class));
	}

	@Test
	public void testgetNameDefaultFileConsoleLoggerClazzImplementation() {
		assertThat(
				Logfactory.getDefaultConsoleLogger(
						consoleAppenderFile, "LogfactoryTest"),
				isA(Logger.class));
	}

	@Test
	public void testgetNameDefaultFileLoggerClazzImplementation() {
		assertThat(Logfactory.getDefaultFileLogger(fileAppenderFile,
				"LogfactoryTest"), isA(Logger.class));
	}

	@Test
	public void testgetNameDefaultRollingFileConsoleLoggerClazzImplementation() {
		assertThat(
				Logfactory.getDefaultRollingFileLogger(
						rollingFileAppenderFile, "LogfactoryTest"),
				isA(Logger.class));
	}
	@Test(expected = XMLConfigurationParseException.class)
	public void testgetNameDefaultRollingFileConsoleLoggerClazzImplementationParseException()
			throws IOException {
		rollingFileAppenderFile = new ClassPathResource(
				"/rollingfileappenderexception.xml").getFile();
		Logfactory.getDefaultRollingFileLogger(
				rollingFileAppenderFile, "LogfactoryTest");
	}

}
