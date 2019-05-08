package io.mosip.preregistration.notification.dto;

import java.util.List;

import io.mosip.preregistration.core.common.dto.NotificationDTO;
import lombok.Data;
/**
 * @author Sanober Noor
 *
 */
@Data
public class NotificationListRequestDTO {
List<NotificationDTO> notification;
}
