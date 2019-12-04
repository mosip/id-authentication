package io.mosip.resident.dto;

import java.io.Serializable;
import java.util.List;

import io.mosip.resident.constant.IdType;
import io.mosip.resident.constant.NotificationTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDto implements Serializable {
	static final long serialVersionUID = 3726544930055329455L;
	private String id;
	private IdType idType;
	private String registrationType;
	private NotificationTemplate templateType;
}
