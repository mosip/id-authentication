package io.mosip.authentication.service.config;

import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.boot.actuate.endpoint.Show;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Kamesh Shekhar Prasad
 */

@Component
public class CustomEnvEndpoint extends EnvironmentEndpoint {

    public CustomEnvEndpoint(Environment environment) {
        super(environment, Collections.emptyList(), Show.ALWAYS);
    }

    @Override
    protected Object stringifyIfNecessary(Object value) {
        if (value != null) {
            // Handle ArrayList or any other collection
            if (value instanceof List) {
                return value;  // Keep the List intact, do not convert to a string
            }
            // Handle any other Collection types (e.g., Set)
            else if (value instanceof Collection) {
                return value.toString();  // Convert collection to a string representation
            }
            // For non-primitive and non-wrapper types, keep as is or handle accordingly
            else if (!isSimpleValueType(value)) {
                return value;  // Keep the complex type as is without converting to string
            }
        }
        return super.stringifyIfNecessary(value);  // Fallback to default behavior
    }

    private boolean isSimpleValueType(Object value) {
        return value instanceof CharSequence || value.getClass().isPrimitive() ||
                Number.class.isAssignableFrom(value.getClass()) ||
                Boolean.class.isAssignableFrom(value.getClass()) ||
                Character.class.isAssignableFrom(value.getClass());
    }
}

