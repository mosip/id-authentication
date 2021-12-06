package  io.mosip.authentication.common.service.util;

import java.util.Map;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
import lombok.Data;

@Data
public class TestObjectWithMetadata implements ObjectWithMetadata {

	private Map<String, Object> metadata;
}
