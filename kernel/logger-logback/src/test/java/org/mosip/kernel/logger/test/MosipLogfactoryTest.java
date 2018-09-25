package org.mosip.kernel.logger.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipConsoleAppender;
import org.mosip.kernel.logger.appenders.MosipFileAppender;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.exception.ClassNameNotFoundException;
import org.mosip.kernel.logger.exception.EmptyPatternException;
import org.mosip.kernel.logger.exception.FileNameNotProvided;
import org.mosip.kernel.logger.exception.ImplementationNotFound;
import org.mosip.kernel.logger.exception.MosipIllegalArgumentException;
import org.mosip.kernel.logger.exception.MosipIlligalStateException;
import org.mosip.kernel.logger.exception.PatternSyntaxExeption;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.kernel.logger.impl.MosipLogback;

public class MosipLogfactoryTest {

	private MosipFileAppender mosipFileAppender;
	private MosipConsoleAppender mosipConsoleAppender;
	private MosipRollingFileAppender mosipRollingFileAppender;
	private String FILENAME;

	@Before
	public void setUp() {
		FILENAME = "src/test/resources/test.txt";
		mosipFileAppender = new MosipFileAppender();
		mosipConsoleAppender = new MosipConsoleAppender();
		mosipRollingFileAppender = new MosipRollingFileAppender();
	}

	@Test
	public void testGetMosipDefaultConsoleLoggerClazz() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(MosipLogfactory.getMosipDefaultConsoleLogger(mosipConsoleAppender, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultConsoleLoggerName() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(MosipLogfactory.getMosipDefaultConsoleLogger(mosipConsoleAppender, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultConsoleLoggerClazzImplementation() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(MosipLogfactory.getMosipConsoleLogger(mosipConsoleAppender, MosipLogback.class,
				MosipLogfactoryTest.class), isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultConsoleLoggerClazzImplementationExcepTion() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(MosipLogfactory.getMosipConsoleLogger(mosipConsoleAppender, MosipLogfactoryTest.class,
				MosipLogfactoryTest.class), isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultConsoleLoggerNameImplementation() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(
				MosipLogfactory.getMosipConsoleLogger(mosipConsoleAppender, MosipLogback.class, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultConsoleLoggerNameImplementationExcepTion() {
		mosipConsoleAppender.setAppenderName("testConsoleappender");
		mosipConsoleAppender.setImmediateFlush(true);
		mosipConsoleAppender.setTarget("System.out");
		assertThat(MosipLogfactory.getMosipConsoleLogger(mosipConsoleAppender, MosipLogfactoryTest.class,
				"MosipLogfactoryTest"), isA(MosipLogger.class));
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
		assertThat(MosipLogfactory.getMosipDefaultFileLogger(mosipFileAppender, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerNameWithoutRolling() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(MosipLogfactory.getMosipDefaultFileLogger(mosipFileAppender, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerClassWithoutRollingImplementation() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(MosipLogfactory.getMosipFileLogger(mosipFileAppender, MosipLogback.class, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerNameWithoutRollingImplementation() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(MosipLogfactory.getMosipFileLogger(mosipFileAppender, MosipLogback.class, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultFileLoggerClassWithoutRollingImplementationException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(MosipLogfactory.getMosipFileLogger(mosipFileAppender, MosipLogfactoryTest.class,
				MosipLogfactoryTest.class), isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultFileLoggerNameWithoutRollingImplementationException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName(FILENAME);
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		assertThat(
				MosipLogfactory.getMosipFileLogger(mosipFileAppender, MosipLogfactoryTest.class, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
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
		MosipLogfactory.getMosipDefaultFileLogger(mosipFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithoutRollingFileEmptyException() {
		mosipFileAppender.setAppenderName("testFileappender");
		mosipFileAppender.setAppend(true);
		mosipFileAppender.setFileName("");
		mosipFileAppender.setImmediateFlush(true);
		mosipFileAppender.setPrudent(false);
		MosipLogfactory.getMosipDefaultFileLogger(mosipFileAppender, "MosipLogfactoryTest");
	}

	@Test
	public void testGetMosipDefaultFileLoggerClazzWithRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(
				MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerNameWithRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerClazzWithRollingImplementation() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(MosipLogfactory.getMosipRollingFileLogger(mosipRollingFileAppender, MosipLogback.class,
				MosipLogfactoryTest.class), isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerNameWithRollingImplementation() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(MosipLogfactory.getMosipRollingFileLogger(mosipRollingFileAppender, MosipLogback.class,
				"MosipLogfactoryTest"), isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultFileLoggerClazzWithRollingImplementationException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(MosipLogfactory.getMosipRollingFileLogger(mosipRollingFileAppender, MosipLogfactoryTest.class,
				MosipLogfactoryTest.class), isA(MosipLogger.class));
	}

	@Test(expected = ImplementationNotFound.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingImplementationException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("testFileappender-%d{ss}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		assertThat(MosipLogfactory.getMosipRollingFileLogger(mosipRollingFileAppender, MosipLogfactoryTest.class,
				"MosipLogfactoryTest"), isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerClazzWithFullRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		assertThat(
				MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, MosipLogfactoryTest.class),
				isA(MosipLogger.class));
	}

	@Test
	public void testGetMosipDefaultFileLoggerNameWithFullRolling() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		assertThat(MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest"),
				isA(MosipLogger.class));
	}

	@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingFileNullException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(null);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		MosipLogfactory.getMosipDefaultFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = FileNameNotProvided.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingFileEmptyException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName("");
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("testFileappender-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		mosipRollingFileAppender.setMaxFileSize("10kb");
		MosipLogfactory.getMosipDefaultFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest");
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
		MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest");
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
		MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = PatternSyntaxExeption.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingWrongFilePattern() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern(FILENAME);
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");

		MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = PatternSyntaxExeption.class)
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
		MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest");
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
		MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "");
	}

	@Test(expected = MosipIlligalStateException.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingIllegalState() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{aaaa}.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest");
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
		MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest");
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
		MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test(expected = PatternSyntaxExeption.class)
	public void testGetMosipDefaultFileLoggerNameWithRollingNotIConstraintsException() {
		mosipRollingFileAppender.setAppenderName("testFileappender");
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setFileName(FILENAME);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setFileNamePattern("test-%d{ss}-%i.txt");
		mosipRollingFileAppender.setMaxHistory(5);
		mosipRollingFileAppender.setTotalCap("100KB");
		MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest");
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
		MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, "MosipLogfactoryTest");
	}

	@Test
	public void testGetMosipDefaultConsoleLoggerDebugCall() {
		MosipLogger logger = Mockito.mock(MosipLogger.class);
		logger.debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		verify(logger, times(1)).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

	}

	@Test
	public void testGetMosipDefaultConsoleLoggerTraceCall() {
		MosipLogger logger = Mockito.mock(MosipLogger.class);
		logger.trace(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		verify(logger, times(1)).trace(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

	}

	@Test
	public void testGetMosipDefaultConsoleLoggerErrorCall() {
		MosipLogger logger = Mockito.mock(MosipLogger.class);
		logger.error(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		verify(logger, times(1)).error(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

	}

	@Test
	public void testGetMosipDefaultConsoleLoggerWarnCall() {
		MosipLogger logger = Mockito.mock(MosipLogger.class);
		logger.warn(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		verify(logger, times(1)).warn(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

	}

	@Test
	public void testGetMosipDefaultConsoleLoggerInfoCall() {
		MosipLogger logger = Mockito.mock(MosipLogger.class);
		logger.info(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		verify(logger, times(1)).info(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
	}

}
