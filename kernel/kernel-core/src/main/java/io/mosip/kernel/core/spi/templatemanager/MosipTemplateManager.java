package io.mosip.kernel.core.spi.templatemanager;

import java.io.InputStream;
import java.io.Writer;
import java.util.Map;

public interface MosipTemplateManager {
	public InputStream mergeTemplate(InputStream template, Map<String, Object> values);
	public boolean merge(String templateName, Writer writer, Map<String, Object> values);
	public boolean merge(String templateName, Writer writer, Map<String, Object> values, final String encodingType);
}
