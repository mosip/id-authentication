package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
@WebMvcTest
public class DemoNormalizerTest {
       
       @InjectMocks
       private DemoNormalizerImpl demoNormalizerImpl;
       
       @Autowired
       private Environment environment;
       
       @Before
       public void setup() {
             ReflectionTestUtils.setField(demoNormalizerImpl, "environment", environment);
       }

       @Test
       public void testNameNormalizer1() throws IdAuthenticationBusinessException {
             assertEquals("mosip", demoNormalizerImpl.normalizeName("mr mosip", "fra", () -> createFetcher()));
       }

       @Test
       public void testNameNormalizer2() throws IdAuthenticationBusinessException {
             assertEquals("mosip mosip", demoNormalizerImpl.normalizeName("Dr. mr. mrs mosip  ,    mosip*", "fra", () -> createFetcher()));

       }
       
       private Map<String, List<String>> createFetcher() {
             List<String> l = new ArrayList<>();
             l.add("Mr");
             l.add("Dr");
             l.add("Mrs");
             Map<String, List<String>> map = new HashMap<>();
             map.put("fra", l);
             return map;
       }

       @Test
       public void testNameNormalizer3() throws IdAuthenticationBusinessException {
             assertEquals("mosipmosip", demoNormalizerImpl.normalizeName("Dr. mr. mrs mosip,mosip*", "fra", () -> createFetcher()));

       }

       @Test
       public void testNameNormalizerFailureCase() throws IdAuthenticationBusinessException {
             assertNotEquals("mosipmosip", demoNormalizerImpl.normalizeName("Dr. mr. mrs mosip  ,    mosip*", "fra", () -> createFetcher()));

       }
       
       @Test
       public void testAddressNormalize1r() {
             assertEquals("mosip mosip", demoNormalizerImpl.normalizeAddress("C/o- Mr.mosip,.*      mosip", "eng"));
       }

       @Test
       public void testAddressNormalizer2() {
             assertEquals("mosip mosip", demoNormalizerImpl.normalizeAddress("c/o- Mr.mosip,.*  no    mosip", "eng"));
       }

}
