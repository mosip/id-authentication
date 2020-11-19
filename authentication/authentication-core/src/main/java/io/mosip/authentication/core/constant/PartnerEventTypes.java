package io.mosip.authentication.core.constant;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MISP_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_API_KEY_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.POLICY_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_MISP_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED;
public enum PartnerEventTypes {
	
	MISP_UPDATED(MISP_UPDATED_EVENT_NAME, IDA_WEBSUB_TOPIC_PMP_MISP_UPDATED),
	PARTNER_UPDATED(PARTNER_UPDATED_EVENT_NAME, IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED),
	PARTNER_API_KEY_UPDATED(PARTNER_API_KEY_UPDATED_EVENT_NAME, IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED),
	POLICY_UPDATED(POLICY_UPDATED_EVENT_NAME, IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED);

	private String name;
	private String topicPropertyName;
	
	private PartnerEventTypes(String name, String topicPropertyName) {
		this.name = name;
		this.topicPropertyName = topicPropertyName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTopicPropertyName() {
		return topicPropertyName;
	}
}
