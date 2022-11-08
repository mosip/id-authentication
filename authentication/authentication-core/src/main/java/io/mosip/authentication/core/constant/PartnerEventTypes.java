package io.mosip.authentication.core.constant;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.APIKEY_APPROVED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MISP_LICENSE_GENERATED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MISP_LICENSE_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.OIDC_CLIENT_CREATED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.OIDC_CLIENT_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_API_KEY_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.POLICY_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_GENERATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_OIDC_CLIENT_CREATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_OIDC_CLIENT_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_APPROVED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED;
public enum PartnerEventTypes {
	
	API_KEY_APPROVED(APIKEY_APPROVED, IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_APPROVED),
	MISP_LIC_GENERATED(MISP_LICENSE_GENERATED, IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_GENERATED),
	MISP_LIC_UPDATED(MISP_LICENSE_UPDATED, IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_UPDATED),
	PARTNER_UPDATED(PARTNER_UPDATED_EVENT_NAME, IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED),
	PARTNER_API_KEY_UPDATED(PARTNER_API_KEY_UPDATED_EVENT_NAME, IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED),
	POLICY_UPDATED(POLICY_UPDATED_EVENT_NAME, IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED),
	OIDC_CLI_CREATED(OIDC_CLIENT_CREATED, IDA_WEBSUB_TOPIC_PMP_OIDC_CLIENT_CREATED),
	OIDC_CLI_UPDATED(OIDC_CLIENT_UPDATED, IDA_WEBSUB_TOPIC_PMP_OIDC_CLIENT_UPDATED);

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
