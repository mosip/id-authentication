package io.mosip.testrig.apirig.auth.testrunner;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import io.mosip.testrig.apirig.auth.utils.IdAuthConfigManager;

/**
 * Drops the &lt;test&gt; blocks that do not belong to the current run mode
 * before TestNG starts. A block marked with
 * {@code <parameter name="runMode" value="local"/>} runs only when
 * localRun=yes, one marked {@code env} runs only on env runs, and a block
 * without the parameter runs in both modes.
 */
public class RunModeSuiteFilter implements IAlterSuiteListener {
	private static final Logger LOGGER = Logger.getLogger(RunModeSuiteFilter.class);

	public static final String RUN_MODE_PARAM = "runMode";
	public static final String MODE_LOCAL = "local";
	public static final String MODE_ENV = "env";

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
		for (XmlTest test : suite.getTests()) {
			String blockMode = test.getLocalParameters().get(RUN_MODE_PARAM);
			if (blockMode == null || blockMode.isBlank() || blockMode.trim().equalsIgnoreCase(mode)) {
				kept.add(test);
			} else {
				dropped.add(test.getName());
			}
		}
		if (!dropped.isEmpty()) {
			suite.setTests(kept);
			LOGGER.info("Run mode " + mode + " - " + suite.getName() + ": running " + kept.size() + " of "
					+ (kept.size() + dropped.size()) + " test blocks, not running " + dropped);
		}
		for (XmlSuite child : suite.getChildSuites()) {
			filter(child, mode);
		}
	}
}
