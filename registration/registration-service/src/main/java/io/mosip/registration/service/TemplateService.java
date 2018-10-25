package io.mosip.registration.service;

import io.mosip.registration.entity.Template;
import io.mosip.registration.exception.RegBaseCheckedException;

public interface TemplateService {

	public Template getTemplate();
	public String createReceipt() throws RegBaseCheckedException;
}
