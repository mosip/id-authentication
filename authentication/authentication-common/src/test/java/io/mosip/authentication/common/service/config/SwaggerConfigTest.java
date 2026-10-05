package io.mosip.authentication.common.service.config;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springdoc.core.models.GroupedOpenApi;

@RunWith(MockitoJUnitRunner.class)
public class SwaggerConfigTest {

    private SwaggerConfig config;

    @Before
    public void setup() throws Exception {
        config = new SwaggerConfig();

        // Setup License
        LicenseProperty license = new LicenseProperty();
        license.setName("MIT");
        license.setUrl("http://license.url");

        // Setup Info
        InfoProperty info = new InfoProperty();
        info.setTitle("Test API");
        info.setVersion("1.0");
        info.setDescription("Test description");
        info.setLicense(license);

        // Setup Server
        io.mosip.authentication.common.service.config.Server server = new io.mosip.authentication.common.service.config.Server();
        server.setDescription("Test server");
        server.setUrl("http://localhost:8080");

        // Setup Service
        Service service = new Service();
        service.setServers(List.of(server));

        // Setup Group
        Group group = new Group();
        group.setName("test-group");
        group.setPaths(List.of("/api/**"));

        // Setup OpenApiProperties
        OpenApiProperties openApiProperties = new OpenApiProperties();
        openApiProperties.setInfo(info);
        openApiProperties.setService(service);
        openApiProperties.setGroup(group);

        // Inject private field using reflection
        Field field = SwaggerConfig.class.getDeclaredField("openApiProperties");
        field.setAccessible(true);
        field.set(config, openApiProperties);
    }

    @Test
    public void testOpenApi() {
        OpenAPI api = config.openApi();
        assertNotNull(api);

        // Validate Info
        assertEquals("Test API", api.getInfo().getTitle());
        assertEquals("1.0", api.getInfo().getVersion());
        assertEquals("Test description", api.getInfo().getDescription());

        // Validate License
        assertNotNull(api.getInfo().getLicense());
        assertEquals("MIT", api.getInfo().getLicense().getName());
        assertEquals("http://license.url", api.getInfo().getLicense().getUrl());

        // Validate Server
        assertNotNull(api.getServers());
        assertEquals(1, api.getServers().size());
        Server swaggerServer = api.getServers().get(0);
        assertEquals("Test server", swaggerServer.getDescription());
        assertEquals("http://localhost:8080", swaggerServer.getUrl());
    }

    @Test
    public void testGroupedOpenApi() {
        GroupedOpenApi groupedApi = config.groupedOpenApi();
        assertNotNull(groupedApi);
        assertEquals("test-group", groupedApi.getGroup());
        assertArrayEquals(new String[]{"/api/**"}, groupedApi.getPathsToMatch().toArray(new String[0]));
    }
}
