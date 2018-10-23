package io.mosip.kernel.logger.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipConsoleAppender;
import io.mosip.kernel.logger.logback.appender.MosipFileAppender;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.constant.MosipLoggerMethod;
import io.mosip.kernel.logger.logback.exception.ClassNameNotFoundException;
import io.mosip.kernel.logger.logback.exception.EmptyPatternException;
import io.mosip.kernel.logger.logback.exception.FileNameNotProvided;
import io.mosip.kernel.logger.logback.exception.ImplementationNotFound;
import io.mosip.kernel.logger.logback.exception.MosipIllegalArgumentException;
import io.mosip.kernel.logger.logback.exception.MosipIllegalStateException;
import io.mosip.kernel.logger.logback.exception.MosipXMLConfigurationParseException;
import io.mosip.kernel.logger.logback.exception.PatternSyntaxException;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;

import org.springframework.core.io.ClassPathResource;

public class MosipLogfactoryTest {

	private MosipFileAppender mosipFileAppender;
	private MosipConsoleAppender mosipConsoleAppender;
	private MosipRollingFileAppender mosipRollingFileAppender;
	private String FILENAME;
	private File consoleAppenderFile;
	private File fileAppenderFile;
	private File rollingFileAppenderFile;
	@Before
	public void setUp() throws IOException {
		FILENAME = "src/test/resources/test.txt";
		mosipFileAppender = new MosipFileAppender();
		mosipConsoleAppender = new MosipConsoleAppender();
		mosipRollingFileAppender = new MosipRollingFileAppender();
		consoleAppenderFile = new ClassPathResource("/consoleappender.xml")
				.getFile();
		fileAppenderFile = new ClassPathResource("/fileappender.xml").getFile();
		rollingFileAppenderFile = new ClassPathResource(
				"/rollingfileappender.xml").getFile();
	}

	@Test
	public void testGetMosipDefaultConsoleLoggerClazz() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				MosipLogfactory.getMosipDefaultConsoleLogger(
						mosipConsoleAppender, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultConsoleLoggerName() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				MosipLogfactory.getMosipDefaultConsoleLogger(
						mosipConsoleAppender, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultConsoleLoggerClazzImplementation() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(MosipLogfactory.getMosipConsoleLogger(mosipConsoleAppender,
				MosipLoggerMethod.MOSIPLOGBACK, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultConsoleLoggerClazzImplementationExcepTion() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				MosipLogfactory.getMosipConsoleLogger(mosipConsoleAppender,
						null, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultConsoleLoggerNameImplementation() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				MosipLogfactory.getMosipConsoleLogger(mosipConsoleAppender,
						MosipLoggerMethod.MOSIPLOGBACK, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultConsoleLoggerNameImplementationExcepTion() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(MosipLogfactory.getMosipConsoleLogger(mosipConsoleAppender,
				null, "MosipLogfactoryTest"), isA(MosipLogger.class));
	}

	@Test(expected = ClassNameNotFoundException.class)
	public void testGetMosipDefaultConsoleLoggerNameWithTargetNameException() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		MosipLogfactory.getMosipDefaultConsoleLogger(mosipConsoleAppender, "");
	}

	@Test
	public void testGetMosipDefaultFileLoggerClassWithoutRolling() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(MosipLogfactory.getMosipDefaultFileLogger(mosipFileAppender,
				MosipLogfactoryTest.class), isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerNameWithoutRolling() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(MosipLogfactory.getMosipDefaultFileLogger(mosipFileAppender,
				"MosipLogfactoryTest"), isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerClassWithoutRollingImplementation() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(MosipLogfactory.getMosipFileLogger(mosipFileAppender,
				MosipLoggerMethod.MOSIPLOGBACK, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerNameWithoutRollingImplementation() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(
				MosipLogfactory.getMosipFileLogger(mosipFileAppender,
						MosipLoggerMethod.MOSIPLOGBACK, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultFileLoggerClassWithoutRollingImplementationException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(MosipLogfactory.getMosipFileLogger(mosipFileAppender, null,
				MosipLogfactoryTest.class), isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultFileLoggerNameWithoutRollingImplementationException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(MosipLogfactory.getMosipFileLogger(mosipFileAppender, null,
				"MosipLogfactoryTest"), isA(MosipLogger.class));
	}

	@Test(expected = ClassNameNotFoundException.class)
	public void testGetMosipDefaultFileLoggerNameWithoutRollingNameException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		MosipLogfactory.getMosipDefaultFileLogger(mosipFileAppender, "");
	}

	@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithoutRollingFileNullException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(null);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		MosipLogfactory.getMosipDefaultFileLogger(mosipFileAppender,
				"MosipLogfactoryTest");
	}

	@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithoutRollingFileEmptyException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName("");
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		MosipLogfactory.getMosipDefaultFileLogger(mosipFileAppender,
				"MosipLogfactoryTest");
	}

	@Test
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
				MosipLogfactory.getMosipDefaultRollingFileLogger(
						mosipRollingFileAppender, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
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
				MosipLogfactory.getMosipDefaultRollingFileLogger(
						mosipRollingFileAppender, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test
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
		assertThat(MosipLogfactory.getMosipRollingFileLogger(
				mosipRollingFileAppender, MosipLoggerMethod.MOSIPLOGBACK,
				MosipLogfactoryTest.class), isA(MosipLogger.class));
	}

	@Test
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
				MosipLogfactory.getMosipRollingFileLogger(
						mosipRollingFileAppender,
						MosipLoggerMethod.MOSIPLOGBACK, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
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
		assertThat(MosipLogfactory.getMosipRollingFileLogger(
				mosipRollingFileAppender, null, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
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
				MosipLogfactory.getMosipRollingFileLogger(
						mosipRollingFileAppender, null, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test
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
				MosipLogfactory.getMosipDefaultRollingFileLogger(
						mosipRollingFileAppender, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
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
				MosipLogfactory.getMosipDefaultRollingFileLogger(
						mosipRollingFileAppender, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test(expected = FileNameNotProvided.class)
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
		MosipLogfactory.getMosipDefaultFileLogger(mosipRollingFileAppender,
				"MosipLogfactoryTest");
	}

	@Test(expected = FileNameNotProvided.class)
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
		MosipLogfactory.getMosipDefaultFileLogger(mosipRollingFileAppender,
				"MosipLogfactoryTest");
	}

	@Test(expected = EmptyPatternException.class)
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
		MosipLogfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = EmptyPatternException.class)
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
		MosipLogfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = PatternSyntaxException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingWrongFilePattern() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern(FILENAME);
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");

		MosipLogfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = PatternSyntaxException.class)
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
		MosipLogfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = ClassNameNotFoundException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingClassMissing() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		MosipLogfactory
				.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "");
	}

	@Test(expected = MosipIllegalStateException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingIllegalState() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{aaaa}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		MosipLogfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingNullConstraintsException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(null);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		MosipLogfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingEmptyConstraintsException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName("");
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		MosipLogfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = PatternSyntaxException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingNotIConstraintsException() {
		mosipRollingFileAppender.setAppenderName("testFileappenderIConstraintsException");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		MosipLogfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingIllegalArgumentException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("aaaaaaaaaaa");
		MosipLogfactory.getMosipDefaultRollingFileLogger(
				mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test
	public void testGetMosipDefaultConsoleLoggerDebugCall() {
		MosipLogger mosipLogger = Mockito.mock(MosipLogger.class);
		mosipLogger.debug(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		verify(mosipLogger, times(1)).debug(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());

	}

	@Test
	public void testGetMosipDefaultConsoleLoggerTraceCall() {
		MosipLogger mosipLogger = Mockito.mock(MosipLogger.class);
		mosipLogger.trace(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		verify(mosipLogger, times(1)).trace(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());

	}

	@Test
	public void testGetMosipDefaultConsoleLoggerErrorCall() {
		MosipLogger mosipLogger = Mockito.mock(MosipLogger.class);
		mosipLogger.error(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		verify(mosipLogger, times(1)).error(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());

	}

	@Test
	public void testGetMosipDefaultConsoleLoggerWarnCall() {
		MosipLogger mosipLogger = Mockito.mock(MosipLogger.class);
		mosipLogger.warn(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		verify(mosipLogger, times(1)).warn(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());

	}

	@Test
	public void testGetMosipDefaultConsoleLoggerInfoCall() {
		MosipLogger mosipLogger = Mockito.mock(MosipLogger.class);
		mosipLogger.info(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		verify(mosipLogger, times(1)).info(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void testGetMosipDefaultFileLoggerDebugCall() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		MosipLogger mosipLogger = MosipLogfactory.getMosipDefaultFileLogger(
				mosipFileAppender, MosipLogfactoryTest.class);
		mosipLogger.debug("sessionid", "idType", "id", "description");
	}

	@Test
	public void testGetMosipDefaultFileLoggerTraceCall() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		MosipLogger mosipLogger = MosipLogfactory.getMosipDefaultFileLogger(
				mosipFileAppender, MosipLogfactoryTest.class);
		mosipLogger.trace("sessionid", "idType", "id", "description");

	}

	@Test
	public void testGetMosipDefaultFileLoggerErrorCall() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		MosipLogger mosipLogger = MosipLogfactory.getMosipDefaultFileLogger(
				mosipFileAppender, MosipLogfactoryTest.class);
		mosipLogger.error("sessionid", "idType", "id", "description");

	}

	@Test
	public void testGetMosipDefaultFileLoggerWarnCall() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		MosipLogger mosipLogger = MosipLogfactory.getMosipDefaultFileLogger(
				mosipFileAppender, MosipLogfactoryTest.class);
		mosipLogger.warn("sessionid", "idType", "id", "description");

	}

	@Test
	public void testGetMosipDefaultFileLoggerInfoCall() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		MosipLogger mosipLogger = MosipLogfactory.getMosipDefaultFileLogger(
				mosipFileAppender, MosipLogfactoryTest.class);
		mosipLogger.info("sessionid", "idType", "id", "description");
	}

	@Test
	public void testGetMosipFileConsoleLoggerClazzImplementation() {
		assertThat(MosipLogfactory.getMosipConsoleLogger(consoleAppenderFile,
				MosipLoggerMethod.MOSIPLOGBACK, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipFileConsoleLoggerClazzImplementationExcepTion() {
		MosipLogfactory.getMosipConsoleLogger(consoleAppenderFile, null,
				MosipLogfactoryTest.class);
	}

	@Test
	public void testGetMosipFileLoggerClazzImplementation() {
		assertThat(MosipLogfactory.getMosipFileLogger(fileAppenderFile,
				MosipLoggerMethod.MOSIPLOGBACK, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipFileLoggerClazzImplementationExcepTion() {
		MosipLogfactory.getMosipFileLogger(fileAppenderFile, null,
				MosipLogfactoryTest.class);
	}

	@Test
	public void testGetMosipRollingFileConsoleLoggerClazzImplementation() {
		assertThat(MosipLogfactory.getMosipRollingFileLogger(
				rollingFileAppenderFile, MosipLoggerMethod.MOSIPLOGBACK,
				MosipLogfactoryTest.class), isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipRollingFileConsoleLoggerClazzImplementationExcepTion() {
		MosipLogfactory.getMosipRollingFileLogger(rollingFileAppenderFile, null,
				MosipLogfactoryTest.class);
	}
	@Test
	public void testGetMosipNameFileConsoleLoggerClazzImplementation() {
		assertThat(
				MosipLogfactory.getMosipConsoleLogger(consoleAppenderFile,
						MosipLoggerMethod.MOSIPLOGBACK, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipNameFileConsoleLoggerClazzImplementationExcepTion() {
		MosipLogfactory.getMosipConsoleLogger(consoleAppenderFile, null,
				"MosipLogfactoryTest");
	}

	@Test
	public void testGetMosipNameFileLoggerClazzImplementation() {
		assertThat(
				MosipLogfactory.getMosipFileLogger(fileAppenderFile,
						MosipLoggerMethod.MOSIPLOGBACK, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipNameFileLoggerClazzImplementationExcepTion() {
		MosipLogfactory.getMosipFileLogger(fileAppenderFile, null,
				"MosipLogfactoryTest");
	}

	@Test
	public void testGetMosipNameRollingFileConsoleLoggerClazzImplementation() {
		assertThat(MosipLogfactory.getMosipRollingFileLogger(
				rollingFileAppenderFile, MosipLoggerMethod.MOSIPLOGBACK,
				"MosipLogfactoryTest"), isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipNameRollingFileConsoleLoggerClazzImplementationExcepTion() {
		MosipLogfactory.getMosipRollingFileLogger(rollingFileAppenderFile, null,
				"MosipLogfactoryTest");
	}

	@Test
	public void testGetMosipDefaultFileConsoleLoggerClazzImplementation() {
		assertThat(
				MosipLogfactory.getMosipDefaultConsoleLogger(
						consoleAppenderFile, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerClazzImplementation() {
		assertThat(MosipLogfactory.getMosipDefaultFileLogger(fileAppenderFile,
				MosipLogfactoryTest.class), isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultRollingFileConsoleLoggerClazzImplementation() {
		assertThat(
				MosipLogfactory.getMosipDefaultRollingFileLogger(
						rollingFileAppenderFile, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipNameDefaultFileConsoleLoggerClazzImplementation() {
		assertThat(
				MosipLogfactory.getMosipDefaultConsoleLogger(
						consoleAppenderFile, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipNameDefaultFileLoggerClazzImplementation() {
		assertThat(MosipLogfactory.getMosipDefaultFileLogger(fileAppenderFile,
				"MosipLogfactoryTest"), isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipNameDefaultRollingFileConsoleLoggerClazzImplementation() {
		assertThat(
				MosipLogfactory.getMosipDefaultRollingFileLogger(
						rollingFileAppenderFile, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}
	@Test(expected = MosipXMLConfigurationParseException.class)
	public void testGetMosipNameDefaultRollingFileConsoleLoggerClazzImplementationParseException()
			throws IOException {
		rollingFileAppenderFile = new ClassPathResource(
				"/rollingfileappenderexception.xml").getFile();
		MosipLogfactory.getMosipDefaultRollingFileLogger(
				rollingFileAppenderFile, "MosipLogfactoryTest");
	}

}
