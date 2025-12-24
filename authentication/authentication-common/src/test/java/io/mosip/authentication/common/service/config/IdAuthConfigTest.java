package io.mosip.authentication.common.service.config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.LocaleResolver;

import io.mosip.authentication.common.service.util.EnvUtil;
@RunWith(MockitoJUnitRunner.class)
public class IdAuthConfigTest {

    private IdAuthConfig config;

    @Mock
    private EnvUtil environment;

    @Before
    public void setup() throws Exception {
        // anonymous subclass (allowed)
        config = new IdAuthConfig() {
            @Override protected boolean isFingerAuthEnabled() { return true; }
            @Override protected boolean isFaceAuthEnabled() { return true; }
            @Override protected boolean isIrisAuthEnabled() { return true; }
        };

        // 🔑 inject @Autowired field manually
        java.lang.reflect.Field field =
                IdAuthConfig.class.getDeclaredField("environment");
        field.setAccessible(true);
        field.set(config, environment);
    }

    @Test
    public void testMessageSource() {
        assertNotNull(config.messageSource());
    }

    @Test
    public void testThreadPoolTaskScheduler() {
        assertNotNull(config.threadPoolTaskScheduler());
    }

    @Test
    public void testAfterburnerModule() {
        assertNotNull(config.afterburnerModule());
    }

    @Test
    public void testRestRequestBuilder() {
        assertNotNull(config.getRestRequestBuilder());
    }
}
