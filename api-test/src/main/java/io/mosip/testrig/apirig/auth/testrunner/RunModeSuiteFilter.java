package io.mosip.testrig.apirig.auth.testrunner;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import io.mosip.testrig.apirig.auth.utils.IdAuthConfigManager;

/**
 * Adjusts the &lt;test&gt; blocks to the current run mode before TestNG starts.
 * A block marked with {@code <parameter name="runMode" value="local"/>} runs
 * only when localRun=yes, and a block without the parameter runs in both
 * modes. A block marked {@code env} runs on env runs; on local runs it is
 * dropped when it carries {@code localReplacedBy} (a local block stands in for
 * it), and otherwise kept with {@link #LOCAL_IGNORED_PARAM} so that its
 * testcases are reported as ignored and the local total matches an env run.
 */
public class RunModeSuiteFilter implements IAlterSuiteListener {
	private static final Logger LOGGER = Logger.getLogger(RunModeSuiteFilter.class);

	public static final String RUN_MODE_PARAM = "runMode";
	public static final String MODE_LOCAL = "local";
	public static final String MODE_ENV = "env";
	public static final String LOCAL_REPLACED_BY_PARAM = "localReplacedBy";
	public static final String LOCAL_IGNORED_PARAM = "localIgnoredEnvBlock";

	@Override
	public void alter(List<XmlSuite> suites) {
		String mode = IdAuthConfigManager.isLocalRun() ? MODE_LOCAL : MODE_ENV;
		for (XmlSuite suite : suites) {
			filter(suite, mode);
		}
	}

	private static void filter(XmlSuite suite, String mode) {
		List<XmlTest> kept = new ArrayList<>();
		List<String> dropped = new ArrayList<>();
		List<String> ignored = new ArrayList<>();
		for (XmlTest test : suite.getTests()) {
			String blockMode = test.getLocalParameters().get(RUN_MODE_PARAM);
			if (blockMode == null || blockMode.isBlank() || blockMode.trim().equalsIgnoreCase(mode)) {
				kept.add(test);
			} else if (MODE_LOCAL.equals(mode) && MODE_ENV.equalsIgnoreCase(blockMode.trim())
					&& isBlank(test.getLocalParameters().get(LOCAL_REPLACED_BY_PARAM))) {
				test.addParameter(LOCAL_IGNORED_PARAM, "true");
				kept.add(test);
				ignored.add(test.getName());
			} else {
				dropped.add(test.getName());
			}
		}
		if (!dropped.isEmpty()) {
			suite.setTests(kept);
			LOGGER.info("Run mode " + mode + " - " + suite.getName() + ": running " + kept.size() + " of "
					+ (kept.size() + dropped.size()) + " test blocks, not running " + dropped);
		}
		if (!ignored.isEmpty()) {
			LOGGER.info("Run mode " + mode + " - " + suite.getName() + ": env-only blocks reported as ignored "
					+ ignored);
		}
		for (XmlSuite child : suite.getChildSuites()) {
			filter(child, mode);
		}
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
