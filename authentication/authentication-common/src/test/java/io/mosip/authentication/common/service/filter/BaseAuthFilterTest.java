package io.mosip.authentication.common.service.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.PublicKey;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class BaseAuthFilterTest {

	private final class ServletInputStreamExtension extends ServletInputStream {
		private final ByteArrayInputStream bais;

		private ServletInputStreamExtension(ByteArrayInputStream bais) {
			this.bais = bais;
		}

		@Override
		public int read() throws IOException {
			return bais.read();
		}

		@Override
		public void setReadListener(ReadListener listener) {

		}

		@Override
		public boolean isReady() {
			return bais.available() != 0;
		}

		@Override
		public boolean isFinished() {
			return bais.available() == 0;
		}
	}

	@Autowired
	Environment env;

	@Mock
	ResettableStreamHttpServletRequest requestWrapper;

	BaseAuthFilter baseAuthFilter = new BaseAuthFilter() {

		@Override
		protected void validateDecipheredRequest(ResettableStreamHttpServletRequest requestWrapper,
				Map<String, Object> decipherRequest) throws IdAuthenticationAppException {

		}
	};

	ObjectMapper mapper = new ObjectMapper();

	@Mock
	KeyManager keyManager;

	@Mock
	private WebApplicationContext wac;

	@Mock
	private ServletContext sc;

	@Mock
	BaseAuthFilter ba = mock(BaseAuthFilter.class);

	@Before
	public void setup() {
		ReflectionTestUtils.setField(baseAuthFilter, "env", env);
		ReflectionTestUtils.setField(baseAuthFilter, "mapper", mapper);
		ReflectionTestUtils.setField(baseAuthFilter, "keyManager", keyManager);
	}

	@Test
	public void testInit() throws Exception {
		FilterConfig mockFilterConfig = Mockito.mock(FilterConfig.class);
		Mockito.when(mockFilterConfig.getServletContext()).thenReturn(sc);
		when(wac.getBean(Environment.class)).thenReturn(env);
		when(wac.getBean(ObjectMapper.class)).thenReturn(mapper);
		when(sc.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(wac);
		ReflectionTestUtils.invokeMethod(baseAuthFilter, "init", mockFilterConfig);
	}

	@Test
	public void testConsumeRequest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException, IdAuthenticationAppException {
		String signature = "eyJ4NWMiOlsiTUlJRE5qQ0NBaDZnQXdJQkFnSUlBZWVYcGRmVDF6OHdEUVlKS29aSWh2Y05BUUVGQlFBd1d6RU9NQXdHQTFVRUJoTUZhVzVrYVdFeEdUQVhCZ05WQkFvVEVFMXBibVIwY21WbElFeHBiV2wwWldReEh6QWRCZ05WQkFzVEZrMXBibVIwY21WbElFaHBMVlJsWTJnZ1YyOXliR1F4RFRBTEJnTlZCQU1UQkhOaGJub3dIaGNOTVRrd016STRNVE16T1RNeldoY05ORFl3T0RFeU1UTXpPVE16V2pCYk1RNHdEQVlEVlFRR0V3VnBibVJwWVRFWk1CY0dBMVVFQ2hNUVRXbHVaSFJ5WldVZ1RHbHRhWFJsWkRFZk1CMEdBMVVFQ3hNV1RXbHVaSFJ5WldVZ1NHa3RWR1ZqYUNCWGIzSnNaREVOTUFzR0ExVUVBeE1FYzJGdWVqQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUkwRWRsNUtRTzViUWhSUHZMZVRoWWhXWVJrSUVHRm9nYXEzWE4zeHI5MllrMFFBV1pMd0ZNS2F4UVVCS01HSmU1UE02VUtqVThINk1rcWxKWEV0VnRsNFBINkdXRTQ2M1J3UFpsRENHNHlIZXdONjRYUDIrTkliZ1QwTFc4UXZpR1ZtTmZ4aGFVOHNSeS9seVh5LzJ6bVpmT0k2WWRxTnNkTVZMeFFER3dIV3ZMWnJIUk5UeGVMejJOS0VEYlFhK0c3bEpWUTJDOFNqT2dxWlQyN3ZiNHNJRDNhQXIrMzFOdSt1OWNDSDhEZDk3Ui9MRXk5alhZLzMxZWxOQ2hVOFlSb1hHTTdVTk9adGpRZWlLVnRuNWhrQVdReDA2ZnI0R1lWa0ZUb3RZdXNjWW42YlVBakxJTDlRSkJ3RERQVFBiRFdtRzN2eXQwUFlSMll4eWlxaDF3VUNBd0VBQVRBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQU85MTE5OG9NdnNkWVIxMGw4aVlCenJQWjEwcHdqckptYndzaFBnRGdxMlhhMkVFK0h5T1Fta2FZRml2MGQwRnZpaDdlUVpRcHpJTnZNbjRLVmVIcWEvaHdOSGZhdmlqc3BIbk96blRCUGcrWm9ad3lsTWJxQ255dXdzbWdaeVlENzdzSFM4NUtHaFNvaFNjaitEMzZidEgrVVlaL0g5Y25tdHgzenhEVXpxTlFnTUNMYmJCZDZHcGpVZy84T25zY2tnaFJZK0lKSFZVblZaZElCMVNqVlFvdzQ1eCtkNEZJMXZxOWFlQ2ZKYTIxS3JnaHJIdUdHRUdydU5URWs1MHIyQnNHY2R4VUt3RFpnMWtNMVZZWVRoWEZUUTdZK29ob2prMXBLdkw2VUNuM1VQdUVkSWY1SjN0Rm5SNUdVd25mbjhBUjVtcm5zK1gzZ0dqTDhjOXlUdz09Il0sImFsZyI6IlJTMjU2In0.MDcxOEY3NDEwMTE4ODdERjFCNTk5NUI0MzkyODFCQzAwRDk1OUVENjIwQ0I3OTE5NEMzMjUxRTk4MUM2RjI1MA.hFzxGmtap9JGRLni4nDrUy_gjP8Yp5MOGaiVPDHcvKV6ZNXlWH2-8LgJw2ZMeZo6XPljJkVtxzGOnpZPILZLKYl1dITsVkhqLVm2so-_1LYyfwexoQgsfbDZDlf5UeNipdBLlr4fkd4htQzsZV5p9vMwmhOoVq_TEBXIY7jZ6QWSJx1HioNajaOzc3lwSM8T0zxYxowQD10tGXj5Wdrk21ruf1XDjL5SNzMRVhOZpZR3SpFL19y5orW0CFCiRld7FpfE9NfSHOR61sGLbQL8AMVUgZQuVkkD_W5j_aAXJGZ8wirWcKFnoI7bBZrrAq-vOjH_QUl0jyO7fIl5QPEDcQ";
		Mockito.when(requestWrapper.getInputStream()).thenReturn(newServletInputStream(), newServletInputStream(),
				newServletInputStream(), newServletInputStream(), newServletInputStream(), newServletInputStream(),
				newServletInputStream(), newServletInputStream(), newServletInputStream(), newServletInputStream());
		StringBuffer sbf = new StringBuffer("http://localhost:8090/identity/auth/0.9/12213123/12321312");
		Mockito.when(requestWrapper.getRequestURL()).thenReturn(sbf);
		Mockito.when(requestWrapper.getContextPath()).thenReturn("/identity");
		Mockito.when(requestWrapper.getHeader("Authorization")).thenReturn(signature);
		ReflectionTestUtils.invokeMethod(baseAuthFilter, "consumeRequest", requestWrapper, createRequestBody());
	}

	private ServletInputStreamExtension newServletInputStream() {
		String req = "{\r\n" + "  \"consentObtained\": true,\r\n" + "  \"id\": \"mosip.identity.auth\",\r\n"
				+ "  \"individualId\": \"6521768301\",\r\n" + "  \"individualIdType\": \"UIN\",\r\n"
				+ "  \"request\": \"7sBCLgrrSNhB-6vNR_MKtzv0wgjaKzB6HCnf1mx0u0YH0IiUL0PaUDOqaQi1RuN4Ar0yAlhJsIJIW-uxWHNxFEoD6h-r75pzKyBoyefMdpLHboLbljUBrPHoqrJLfv1nkas9RS-fD6M5S4e0D5YbZ5c9toNpb4thjj-QbHkEsOKQsVv7R7g59wMzMYx49NHSWnYnB4Dphsks-EGdCkHYR6IUR_Ys4OB1aZTGwfHrnXw8iWRhkrkv2hJA7QkpA9TGG_I9_dQT3amIWB3cDISlW0tKnaG7EVMApuPkeKO5LaxwKhE6_0RWCCvr9LSzVl6b8l8tZ8zybcRnrNS22nGAxytuwkx_BeEFdqnGrht0Q7y23pMmjn5JPYN3mnfQbuENYXKU5f5LGslUZm48ouqa1_-oPktSIq6K0PrTEKGlSukkmitqBXApuSbS1VJJ6uqhpudMnWB7QyO37XDPfMqsJKQueGbdLObdq_wT81-c5PT3h1QhFBe16N5Sf3qg0AgzRxzZ6T3kNiFIy7CkLZD9B7dKCmuL4nV-ixSyjzNQUALvEbDeatSF2-wjlTVVewtwcYzByCJbet2agYqfG0hdiTR5LbyclO8E67E1k73pJT07OgBihlXb9oIw0ljIZo_9smN_shoJUfxy1f8UUKJVWg\",\r\n"
				+ "  \"requestHMAC\": \"OPDtwdW7bHnQ3d7T8pQURjIyNYBt3hkE8ZbY7H1cBFtufkjYKd9rpcE7w57hZg6xUGsN2QZM8PtbEUjWShjrZIezI7nmGnlPS2lhIE1F6pw4Nl9AJ3oabrN4D7sULeVs\",\r\n"
				+ "  \"requestSessionKey\": \"AmbpO_u3WC83w4emk0f8G4RDdwIhk8KablUfBw6OVy_3a4UrHxx8TE7mpHgRMp-M7V01qAoYguO3XwWKFQfWHTZNaVuXYMkf3biNHWQiPm4UKob3MZjhyLUXaacy0MPFj8OIRYoRcQ5uwtpknzUC6qyjfyh9CdGlvvREyCgjSOFaBalFW3pFPkeZK7J7BfHv0OF1BoncE5Z4ITgUFAwyCAHBp3RrU0Oy6DqJXsm5I2ICuZKqYzE-bCK85Gw2eq-ECllQzmTvE34ILWpAOgmcUwie62rJnNtGJTzFoLCuzOxo7qtJBpi2ida4rzd1_jWVWGwe2cW4l2jGSEtDMve-Og\",\r\n"
				+ "  \"requestTime\": \"2019-03-28T10:01:57.086+05:30\",\r\n" + "  \"requestedAuth\": {\r\n"
				+ "    \"bio\": true,\r\n" + "    \"demo\": false,\r\n" + "    \"otp\": false,\r\n"
				+ "    \"pin\": false\r\n" + "  },\r\n" + "  \"transactionID\": \"1234567890\",\r\n"
				+ "  \"version\": \"0.9\"\r\n" + "}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		return new ServletInputStreamExtension(bais);
	}

	@Test
	public void testConsumeRequest2() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		String req = "{\"id\":\"mosip.identity.auth\",\"individualId\":\"2410478395\",\"individualIdType\":\"D\",\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\"requestTime\":\"2019-03-13T10:01:57.086+05:30\",\"requestedAuth\":{\"bio\":false,\"demo\":true,\"otp\":false,\"pin\":false},\"requestSessionKey\":\"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\"transactionID\":\"1234567890\",\"version\":\"1.0\"}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {

			}

			@Override
			public boolean isReady() {
				return bais.available() != 0;
			}

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}
		};
		String signature = "eyJ4NWMiOlsiTUlJRE56Q0NBaCtnQXdJQkFnSUpBS0pEeldKYUNCQmlNQTBHQ1NxR1NJYjNEUUVCQlFVQU1Gc3hEakFNQmdOVkJBWVRCV2x1WkdsaE1Sa3dGd1lEVlFRS0V4Qk5hVzVrZEhKbFpTQk1hVzFwZEdWa01SOHdIUVlEVlFRTEV4Wk5hVzVrZEhKbFpTQklhUzFVWldOb0lGZHZjbXhrTVEwd0N3WURWUVFERXdSellXNTZNQjRYRFRFNU1ETXhNekV3TlRnME1sb1hEVFEyTURjeU9ERXdOVGcwTWxvd1d6RU9NQXdHQTFVRUJoTUZhVzVrYVdFeEdUQVhCZ05WQkFvVEVFMXBibVIwY21WbElFeHBiV2wwWldReEh6QWRCZ05WQkFzVEZrMXBibVIwY21WbElFaHBMVlJsWTJnZ1YyOXliR1F4RFRBTEJnTlZCQU1UQkhOaGJub3dnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFDUmV5UER2ZEU0dHhkMVJ5bGRwVk1aQ1RLT2ZMTGlkS3MxcjRiYnhCQlhBV3hmdTVHZzZPOTJ2b3JWNlNZb212Wk4rem9KR09EME5XUnM5dS9tVGZzQ2RjNXk5Uk8rSFJQYk9qdFUrUElPcE5MM2doaEpFY044WjB3YU9zT3RoSThPdkd1cGhyeklGQXVPc3pJcGFGSUM5Y3prb2tjL3pLZzlENlBiZ1ZvOENZYVd4OWM1MnZHUnkwZzVFRW02OVgrNzFObW8xeVFndW9nQjFXSUVjc3lKalhTNzhGMU1BdHRiRDNyeks3YnBrSWZsKzQvaWRlR2VXeWllRlpSOUtmS1JTMmRVSEZlQkVYUFRUbnRTdC9aQ2hxdUsvZllGTE1zVlV0YmE3Q1greHR6K09Dc3lnL1N2bC9kRmpBaHA5VXZLZURXdUhYZXN3WFF0ZXdDWWxrNmZBZ01CQUFFd0RRWUpLb1pJaHZjTkFRRUZCUUFEZ2dFQkFJMGwyRFRnY0pqQW9ETG81cW9CcnVEbGNOQnZpUGZqNkoxcFU2Q1Q4cmlRVWU4U2N2aXFIQnFISzQvSU56SzI4TjNSYzlPVUljNnliMGttQmcvcTcrWURoY2JFcy8ySGNSTGVWcjFYYi8rODFGZm10WkdtQ09md3RKUEIzcmkxT1Y2NTFKOWZUai9hQnVEQmk4ZlFNWGJVRUJvQmNFODRkMmFWZ21mUllIbUQxb28vWU51cUt5T1J4dTBmd2dkK2RheitCcjhRRzdGeTQ3SlFzVTd6WUNiUEhnVE9QaVJYWVVrL0hMbUJKa09XSXY4R093NStaN2dkQ0NkQjgzWlJUeUI0Mzd2SkZUc2xCK3hyWHpyUnhtNlgyazFpZTFvcVcwRDZjN0NDc0UwdmtseUk1M21DSCtnMVAwMm5ORWFxamh5aWdMK3FUdVBuc25vSUt5NThlUXc9Il0sImFsZyI6IlJTMjU2In0.N0MxRjI4MTBGNDY5NUVFRjI2OUY2ODk2QjI1MUI2NEJGNkFGRUIzNzExNjM1QzQ2NUZDMzBBMUIzMzMxQjlDOQ.RUvFuf5fysT8-uJNOxAG7AlOPN87ko-9G3xDdJeLRtawacYWYe4e8cn-hFwgkFa_NBQMYUj3CO2eo52OpgESgQr8DOR5RPAysDVRuOQmmRI39N8dZms4zD407UTKb-d9Oh0Xtvj8gGOkfV-fCYPIfZLUsz4r-VlojsmeXrqeovueCT-JnVy1V6auV24rN5rGG-9fzu-wSSCa79MZh1PPJcYzRGaCV4QIlj_IxR5cmnmJgAsT30VoGBOetshfpgGLezaFazgeBsH_sGgUBoPQNXeO0x9iYRRswiJ9KBToBCdlXgblLJZmQf56b-jAygcXXxRHugJNTEq0ukYINcY9hw";
		Mockito.when(requestWrapper.getInputStream()).thenReturn(servletInputStream);
		StringBuffer sbf = new StringBuffer("http://localhost:8090/identity/auth/0.8/12213123/12321312");
		Mockito.when(requestWrapper.getRequestURL()).thenReturn(sbf);
		Mockito.when(requestWrapper.getContextPath()).thenReturn("/identity");
		Mockito.when(requestWrapper.getHeader("Authorization")).thenReturn(signature);
		try {
			ReflectionTestUtils.invokeMethod(baseAuthFilter, "consumeRequest", requestWrapper, createRequestBody());
		} catch (UndeclaredThrowableException e) {
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}

	@Test
	public void testConsumeRequest3() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		String req = "{\"id\":\"mosip.identity.auth\",\"individualId\":\"2410478395\",\"individualIdType\":\"D\",\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\"requestTime\":\"2019-03-13T10:01:57.086+05:30\",\"requestedAuth\":{\"bio\":false,\"demo\":true,\"otp\":false,\"pin\":false},\"requestSessionKey\":\"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\"transactionID\":\"1234567890\",\"version\":\"1.0\"}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {

			}

			@Override
			public boolean isReady() {
				return bais.available() != 0;
			}

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}
		};
		String signature = "eyJ4NWMiOlsiTUlJRlVqQ0NCRHFnQXdJQkFnSVNBMlViOFRZamVQYnVuNG8weXg2a0dIVUhNQTBHQ1NxR1NJYjNEUUVCQ3dVQU1Fb3hDekFKQmdOVkJBWVRBbFZUTVJZd0ZBWURWUVFLRXcxTVpYUW5jeUJGYm1OeWVYQjBNU013SVFZRFZRUURFeHBNWlhRbmN5QkZibU55ZVhCMElFRjFkR2h2Y21sMGVTQllNekFlRncweE9ERXhNamd4TnpFM05EZGFGdzB4T1RBeU1qWXhOekUzTkRkYU1CY3hGVEFUQmdOVkJBTVRER1JsZGk1dGIzTnBjQzVwYnpDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBTFhVcitpTXZMdmpSeUVNRnBFTUswN2NNU0xFUEs0d09rWE91d2F0Umt1dERsSTFZRUFVVGxtZk44TFhKaXhIV3doTU10WTFNa3p2NldHNTdUOHRLcm1TclFBZDBVUkY0ODVSZmZXVjBKdWRTSXJMUVJNdFdBUWpKbTJSUUMvOHdDeVJsczBnb2p0UW1YRUJPcnJTajBlelROR1drSUZxQmtmeWNUV2RONEs2YkMwcVJSR2taUTNrT1VwM1ZhZUFRSE80djk2SStNZ3JidlRUQXlKOHlKeU1US2dzVkVFZ0d2RldDOERxVUM4enlKc3c2VTY1NTN5UjZoQkZkeUJPc1l2SHVqRjFYUm1XU1J1L3BKVEZtZi9CUGhmTmttN3lyYjluUmY1QURUcFdZRFdUWnZIS1NydG1tS0FXbjFLWXAvdGx0UkNybHI4dW1TcEJaYzRxM044Q0F3RUFBYU9DQW1Nd2dnSmZNQTRHQTFVZER3RUIvd1FFQXdJRm9EQWRCZ05WSFNVRUZqQVVCZ2dyQmdFRkJRY0RBUVlJS3dZQkJRVUhBd0l3REFZRFZSMFRBUUgvQkFJd0FEQWRCZ05WSFE0RUZnUVV1L2ZCNUVpc0xESnRXSWRvQkxNYmhQQ1lqK0l3SHdZRFZSMGpCQmd3Rm9BVXFFcHFZd1I5M2JybTBUbTNwa1ZsNy9PbzdLRXdid1lJS3dZQkJRVUhBUUVFWXpCaE1DNEdDQ3NHQVFVRkJ6QUJoaUpvZEhSd09pOHZiMk56Y0M1cGJuUXRlRE11YkdWMGMyVnVZM0o1Y0hRdWIzSm5NQzhHQ0NzR0FRVUZCekFDaGlOb2RIUndPaTh2WTJWeWRDNXBiblF0ZURNdWJHVjBjMlZ1WTNKNWNIUXViM0puTHpBWEJnTlZIUkVFRURBT2dneGtaWFl1Ylc5emFYQXVhVzh3VEFZRFZSMGdCRVV3UXpBSUJnWm5nUXdCQWdFd053WUxLd1lCQkFHQzN4TUJBUUV3S0RBbUJnZ3JCZ0VGQlFjQ0FSWWFhSFIwY0RvdkwyTndjeTVzWlhSelpXNWpjbmx3ZEM1dmNtY3dnZ0VHQmdvckJnRUVBZFo1QWdRQ0JJSDNCSUgwQVBJQWR3QXBQRkdXVk1nNVpicXFVUHhZQjlTM2I3OVllaWx5M0tURERQVGxSVWYwZUFBQUFXZGJpcEpjQUFBRUF3QklNRVlDSVFEeWVBUy9QOXVUSTFPRFFQQ3ozRXNrZE5qZWJOSFdTem4zRjBtS2hNL2tvZ0loQUxOSGVQZHFFeU8ySW9tdHhIZWNuTE9JWWZEWWF2TWNOcXZ4bWZ2MVhzSmVBSGNBNG1sTHJpYm82VUFKNklZYnRqdUQxRDduL25TSSs2U1BLSk1CbmQzeDIvNEFBQUZuVzRxVCt3QUFCQU1BU0RCR0FpRUFsSVdUUStGYllwcTlqNnZRTHorNzJzM29CNnFDdzNJM0ZOQzkxTW1IM044Q0lRQ0c2eVRsa1VOd092ZlQrUmFaUTRMeVhtTzVYa0RmZzFnamJQZHd0V2lnTVRBTkJna3Foa2lHOXcwQkFRc0ZBQU9DQVFFQWJ5ZmlmaW5yZE1rcmMyN3JpL0VGYWFYa1VZR1JMUkV3aUdWRzNQSVBCTmlXL1h0UHhFUzQvR3RNZE5CSGVPSUhCNDY4ejd3UmZsTlE2TnFpajF1c1hZUjRrYnhuZlVDRURmeXN2SUovUG1zSGU5bkovT3JGZEE5MTFKaVJWTzc2RTVHT21xbnBkZXNtakVDZWZXK1lva1lMTm1CbzdJYnEzUi9QZkR3dDRsZGJYTG9PU29XbUYybU9IWHV5SXZjc0Q3ZDIvUUlzZzd4eVlWbnZQaTFCRENNY2dYTzhkNzBUR2lhazdTOTRPQnVBaHA2UzRYOWprSTRmRnBsVjNjRng1YXFLd2FXWHNkR2Y3SUpaRzhkZHA1cmh1MDFqS3IzTXRDY3Mvb1FoaHZyU09CWDM3TjFHWjF4a3pYaFNVZ1I3alVVR0t3WWxKWDQ5TDdVUW5Ib1hwZz09Il0sImFsZyI6IlJTMjU2In0.MTJBRTMyQ0IxRUMwMkQwMUVEQTM1ODFCMTI3QzFGRUUzQjBEQzUzNTcyRUQ2QkFGMjM5NzIxQTAzRDgyRTEyNg.SwWrw3jHqN8gJQZmz8HPDqDUfnzyQVFFUtl16DyhSvFn0J8guFEuHUc7F9xfRqJDy0izRd7c_vt-h9E78oO2Oax7tUjHzJz1QldAeNKJpjiEskCBYxGbeHIr_ztN9srLtwZYxkA5mhBnblP1bTXJMserb5uGZt1lG2Ht8QlZi5eqee8LZbmjHc5eCAserqvIj8eCzQvReBAPrsMz8pi6gbMO-s9g7VHTtVO7fZpUuSgzkdOxAMZi_ykV9I6s7qF42_PKB-xz9DH_YpJEMl792QYvqF6DhNSZZeq7qhuprRfnyF6Q6fBm-4wdivLOuY032wXjDxpOGGX4CQQE1cHngQ";
		Mockito.when(requestWrapper.getInputStream()).thenReturn(servletInputStream);
		StringBuffer sbf = new StringBuffer("http://localhost:8090/identity/auth/0.8/12213123/12321312");
		Mockito.when(requestWrapper.getRequestURL()).thenReturn(sbf);
		Mockito.when(requestWrapper.getContextPath()).thenReturn("/identity");
		Mockito.when(requestWrapper.getHeader("Authorization")).thenReturn(signature);
		try {
			ReflectionTestUtils.invokeMethod(baseAuthFilter, "consumeRequest", requestWrapper, createRequestBody());
		} catch (UndeclaredThrowableException e) {
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}

	@SuppressWarnings("static-access")
	@Test
	public void encodeTest() throws IdAuthenticationAppException {
		assertNull(baseAuthFilter.encode(null));
	}

	@SuppressWarnings("static-access")
	@Test
	public void encodeTest2() throws IdAuthenticationAppException {
		baseAuthFilter.encode(new String("~!@@&^#&^&*(**&*&~?><::KJHIJ"));
	}

	@SuppressWarnings("static-access")
	@Test
	public void decodeTest() throws IdAuthenticationAppException {
		assertNull(baseAuthFilter.decode(null));
	}

	@SuppressWarnings("static-access")
	@Test
	public void decodeTest2() throws IdAuthenticationAppException {
		baseAuthFilter.decode("asdsad");
	}

	@Test
	public void transformResponseTest() throws IdAuthenticationAppException {
		assertNull(baseAuthFilter.transformResponse(null));
	}

	@Test
	public void requesthmacTest() throws IdAuthenticationAppException {
	  try {	
		baseAuthFilter.validateRequestHMAC("de3eiac3452", "reqData");
	  }
	  catch(IdAuthenticationAppException ex) {
		  assertEquals(IdAuthenticationErrorConstants.HMAC_VALIDATION_FAILED.getErrorCode(), ex.getErrorCode());
		  assertEquals(IdAuthenticationErrorConstants.HMAC_VALIDATION_FAILED.getErrorMessage(),ex.getErrorText());
	  }
	}

	@Test
	public void testConsumeRequestinvalidCertificate() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.setProperty("mosip.jws.certificate.organization", "xyz");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.setProperty("mosip.ida.api.ids.auth", "mosip.identity.auth");
		mockenv.setProperty("mosip.jws.certificate.algo", "RS256");
		mockenv.setProperty("mosip.jws.certificate.type", "X.509");
		ReflectionTestUtils.setField(baseAuthFilter, "env", mockenv);
		String req = "{\"id\":\"mosip.identity.auth\",\"individualId\":\"2410478395\",\"individualIdType\":\"D\",\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\"requestTime\":\"2019-03-13T10:01:57.086+05:30\",\"requestedAuth\":{\"bio\":false,\"demo\":true,\"otp\":false,\"pin\":false},\"requestSessionKey\":\"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\"transactionID\":\"1234567890\",\"version\":\"0.8\"}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {

			}

			@Override
			public boolean isReady() {
				return bais.available() != 0;
			}

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}
		};
		String signature = "eyJ4NWMiOlsiTUlJRE5qQ0NBaDZnQXdJQkFnSUlRZXcvUFpQSEgwSXdEUVlKS29aSWh2Y05BUUVGQlFBd1d6RU9NQXdHQTFVRUJoTUZhVzVrYVdFeEdUQVhCZ05WQkFvVEVFMXBibVIwY21WbElFeHBiV2wwWldReEh6QWRCZ05WQkFzVEZrMXBibVIwY21WbElFaHBMVlJsWTJnZ1YyOXliR1F4RFRBTEJnTlZCQU1UQkhOaGJub3dIaGNOTVRrd016RXpNVEV5T1RFeldoY05ORFl3TnpJNE1URXlPVEV6V2pCYk1RNHdEQVlEVlFRR0V3VnBibVJwWVRFWk1CY0dBMVVFQ2hNUVRXbHVaSFJ5WldVZ1RHbHRhWFJsWkRFZk1CMEdBMVVFQ3hNV1RXbHVaSFJ5WldVZ1NHa3RWR1ZqYUNCWGIzSnNaREVOTUFzR0ExVUVBeE1FYzJGdWVqQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUlPeFpXQ2VUNmtPaUNhMkRNSFFjZU1DTXR6eVE0empqbWZSaVRuN0RRdXB1TmJ1N0JPOTg1MWJCcWk0QmYxd2oyWFhBdTlVVFN5cm9ORW0vUTQvWE5QSzNPZzdiTHhQUTVhRVVraU5SYnNoY0JCMk9VeXB6ZG8zdk5wQjdnSzJoY2hBUjZHZm1ZcXZuRngyTXR6VGp3Zy9GdmhVNnI4T0hWQVU5SkluMy9xTStYS1hMRm81R3VNSnp5NUExLytiTXRwSFZiZ0NJWkhHTllCVk9MZkdZNThqWDN6eGgyVXQ1QjlEYjZ2R3hKZHhYcVRzMi8wb0V0ZHRrUU5tSmNNcC9vdUphb3g1REo2dXJlQkt0SytSQ0lzc0F3SkdIUFEweHptbDNEb0dNK2xsUHo3WVhGZy8rK3U2YkZBN1hjdSsyVmp3QjVkOFo2cHhJaU5XUlhrTGsxVUNBd0VBQVRBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQVM3Z3ZiTXpmV3duVExOSFhBM1VqU3ducXZ6cXp4WUV0dnpUdThDQkVRZllWUXhTOEpSTjBHZ3lEYnJBMmxjd1JMRm5IdHdKTDBIemI1aVNnREM4WGtsUHcxYkdyQUZHT2FhMGtKQlVFazR0ekFBQkkrNndyNmZQVmVoWTlnUUsxTysyQm01bW1CT1haNnBoSHN2Tnk3SGFlUkZJNFo0Mzg4V21DM25uTXZaQUNSWEhhWTcrb1FxS1Rac055VU93dEI0V0lsYnBFWGFKazEydEpCcVVmaG9RTTdJWXJ4VVk3SjR3RFVLVTVIMTlRMTc2QnNwYzI2UFF1NWppbk8zRUNSQ3RHeTA1NHhBdGpvN2czRDhLRWhTRHRPUG9EYldGWjIwaGdnbE1RTXpycWVUMjJkdFh1YlBlUno4dUVQeXNQVnhsNVA1NXM0RDFVT1h4K0RCNmRxZz09Il0sImFsZyI6IlJTMjU2In0.RTNCMEM0NDI5OEZDMUMxNDlBRkJGNEM4OTk2RkI5MjQyN0FFNDFFNDY0OUI5MzRDQTQ5NTk5MUI3ODUyQjg1NQ.BSxmFZMecWJuIuSlLw7ULzV2fewA9sOr8vtWyv3D2j0HK5sqCOuOUj7_NBRxYh6jXTlQZ8Ua6rsRC4fQvMTa51rWoxQfZgpHO4TTrVmRTDvOfmXjtNrS5LM7Gqywmfl9sgwUFzkC0mOrUoi6KLZxberornvKhjn1quXl-sTiCOliMq-N2ZUbPahLNSWk0XuUKdnNq7CiNtuBm739-aep_c1E6LJhj4QTJpIc55cCqaRtRUWbQlVzQEXB7z6Mu_dlGYqnvaIq3pqkQXPDVS4vnj4MdiJ4KbiEY8gDpgXomGNmM29foLH8JQpFeINQrdEs0SBOipvNgLJspyqtPbdKeA";
		Mockito.when(requestWrapper.getInputStream()).thenReturn(servletInputStream);
		StringBuffer sbf = new StringBuffer("http://localhost:8090/identity/auth/0.8/12213123/12321312");
		Mockito.when(requestWrapper.getRequestURL()).thenReturn(sbf);
		Mockito.when(requestWrapper.getContextPath()).thenReturn("/identity");
		Mockito.when(requestWrapper.getHeader("Authorization")).thenReturn(signature);
		try {
			ReflectionTestUtils.invokeMethod(baseAuthFilter, "consumeRequest", requestWrapper, createRequestBody());
		} catch (UndeclaredThrowableException e) {
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}

	@Test
	public void testConsumeRequestinvalidcertificate2() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.setProperty("mosip.jws.certificate.organization", "xyz");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.setProperty("mosip.ida.api.ids.auth", "mosip.identity.auth");
		mockenv.setProperty("mosip.jws.certificate.algo", "RS257");
		mockenv.setProperty("mosip.jws.certificate.type", "X.509");
		ReflectionTestUtils.setField(baseAuthFilter, "env", mockenv);
		String req = "{\"id\":\"mosip.identity.auth\",\"individualId\":\"2410478395\",\"individualIdType\":\"D\",\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\"requestTime\":\"2019-03-13T10:01:57.086+05:30\",\"requestedAuth\":{\"bio\":false,\"demo\":true,\"otp\":false,\"pin\":false},\"requestSessionKey\":\"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\"transactionID\":\"1234567890\",\"version\":\"0.8\"}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {

			}

			@Override
			public boolean isReady() {
				return bais.available() != 0;
			}

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}
		};
		String signature = "eyJ4NWMiOlsiTUlJRE5qQ0NBaDZnQXdJQkFnSUlRZXcvUFpQSEgwSXdEUVlKS29aSWh2Y05BUUVGQlFBd1d6RU9NQXdHQTFVRUJoTUZhVzVrYVdFeEdUQVhCZ05WQkFvVEVFMXBibVIwY21WbElFeHBiV2wwWldReEh6QWRCZ05WQkFzVEZrMXBibVIwY21WbElFaHBMVlJsWTJnZ1YyOXliR1F4RFRBTEJnTlZCQU1UQkhOaGJub3dIaGNOTVRrd016RXpNVEV5T1RFeldoY05ORFl3TnpJNE1URXlPVEV6V2pCYk1RNHdEQVlEVlFRR0V3VnBibVJwWVRFWk1CY0dBMVVFQ2hNUVRXbHVaSFJ5WldVZ1RHbHRhWFJsWkRFZk1CMEdBMVVFQ3hNV1RXbHVaSFJ5WldVZ1NHa3RWR1ZqYUNCWGIzSnNaREVOTUFzR0ExVUVBeE1FYzJGdWVqQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUlPeFpXQ2VUNmtPaUNhMkRNSFFjZU1DTXR6eVE0empqbWZSaVRuN0RRdXB1TmJ1N0JPOTg1MWJCcWk0QmYxd2oyWFhBdTlVVFN5cm9ORW0vUTQvWE5QSzNPZzdiTHhQUTVhRVVraU5SYnNoY0JCMk9VeXB6ZG8zdk5wQjdnSzJoY2hBUjZHZm1ZcXZuRngyTXR6VGp3Zy9GdmhVNnI4T0hWQVU5SkluMy9xTStYS1hMRm81R3VNSnp5NUExLytiTXRwSFZiZ0NJWkhHTllCVk9MZkdZNThqWDN6eGgyVXQ1QjlEYjZ2R3hKZHhYcVRzMi8wb0V0ZHRrUU5tSmNNcC9vdUphb3g1REo2dXJlQkt0SytSQ0lzc0F3SkdIUFEweHptbDNEb0dNK2xsUHo3WVhGZy8rK3U2YkZBN1hjdSsyVmp3QjVkOFo2cHhJaU5XUlhrTGsxVUNBd0VBQVRBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQVM3Z3ZiTXpmV3duVExOSFhBM1VqU3ducXZ6cXp4WUV0dnpUdThDQkVRZllWUXhTOEpSTjBHZ3lEYnJBMmxjd1JMRm5IdHdKTDBIemI1aVNnREM4WGtsUHcxYkdyQUZHT2FhMGtKQlVFazR0ekFBQkkrNndyNmZQVmVoWTlnUUsxTysyQm01bW1CT1haNnBoSHN2Tnk3SGFlUkZJNFo0Mzg4V21DM25uTXZaQUNSWEhhWTcrb1FxS1Rac055VU93dEI0V0lsYnBFWGFKazEydEpCcVVmaG9RTTdJWXJ4VVk3SjR3RFVLVTVIMTlRMTc2QnNwYzI2UFF1NWppbk8zRUNSQ3RHeTA1NHhBdGpvN2czRDhLRWhTRHRPUG9EYldGWjIwaGdnbE1RTXpycWVUMjJkdFh1YlBlUno4dUVQeXNQVnhsNVA1NXM0RDFVT1h4K0RCNmRxZz09Il0sImFsZyI6IlJTMjU2In0.RTNCMEM0NDI5OEZDMUMxNDlBRkJGNEM4OTk2RkI5MjQyN0FFNDFFNDY0OUI5MzRDQTQ5NTk5MUI3ODUyQjg1NQ.BSxmFZMecWJuIuSlLw7ULzV2fewA9sOr8vtWyv3D2j0HK5sqCOuOUj7_NBRxYh6jXTlQZ8Ua6rsRC4fQvMTa51rWoxQfZgpHO4TTrVmRTDvOfmXjtNrS5LM7Gqywmfl9sgwUFzkC0mOrUoi6KLZxberornvKhjn1quXl-sTiCOliMq-N2ZUbPahLNSWk0XuUKdnNq7CiNtuBm739-aep_c1E6LJhj4QTJpIc55cCqaRtRUWbQlVzQEXB7z6Mu_dlGYqnvaIq3pqkQXPDVS4vnj4MdiJ4KbiEY8gDpgXomGNmM29foLH8JQpFeINQrdEs0SBOipvNgLJspyqtPbdKeA";
		Mockito.when(requestWrapper.getInputStream()).thenReturn(servletInputStream);
		StringBuffer sbf = new StringBuffer("http://localhost:8090/identity/auth/0.8/12213123/12321312");
		Mockito.when(requestWrapper.getRequestURL()).thenReturn(sbf);
		Mockito.when(requestWrapper.getContextPath()).thenReturn("/identity");
		Mockito.when(requestWrapper.getHeader("Authorization")).thenReturn(signature);
		try {
			ReflectionTestUtils.invokeMethod(baseAuthFilter, "consumeRequest", requestWrapper, createRequestBody());
		} catch (UndeclaredThrowableException e) {
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}

	@Test
	public void testConsumeRequestinvalidcertificate3() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.setProperty("mosip.jws.certificate.organization", "xyz");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.setProperty("mosip.ida.api.ids.auth", "mosip.identity.auth");
		mockenv.setProperty("mosip.jws.certificate.algo", "RS257");
		mockenv.setProperty("mosip.jws.certificate.type", "X.509");
		PublicKey pkey = null;
		ReflectionTestUtils.setField(baseAuthFilter, "publicKey", pkey);
		ReflectionTestUtils.setField(baseAuthFilter, "env", mockenv);
		String req = "{\"id\":\"mosip.identity.auth\",\"individualId\":\"2410478395\",\"individualIdType\":\"D\",\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\"requestTime\":\"2019-03-13T10:01:57.086+05:30\",\"requestedAuth\":{\"bio\":false,\"demo\":true,\"otp\":false,\"pin\":false},\"requestSessionKey\":\"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\"transactionID\":\"1234567890\",\"version\":\"0.8\"}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {

			}

			@Override
			public boolean isReady() {
				return bais.available() != 0;
			}

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}
		};
		String signature = "eyJ4NWMiOlsiTUlJRE5qQ0NBaDZnQXdJQkFnSUlRZXcvUFpQSEgwSXdEUVlKS29aSWh2Y05BUUVGQlFBd1d6RU9NQXdHQTFVRUJoTUZhVzVrYVdFeEdUQVhCZ05WQkFvVEVFMXBibVIwY21WbElFeHBiV2wwWldReEh6QWRCZ05WQkFzVEZrMXBibVIwY21WbElFaHBMVlJsWTJnZ1YyOXliR1F4RFRBTEJnTlZCQU1UQkhOaGJub3dIaGNOTVRrd016RXpNVEV5T1RFeldoY05ORFl3TnpJNE1URXlPVEV6V2pCYk1RNHdEQVlEVlFRR0V3VnBibVJwWVRFWk1CY0dBMVVFQ2hNUVRXbHVaSFJ5WldVZ1RHbHRhWFJsWkRFZk1CMEdBMVVFQ3hNV1RXbHVaSFJ5WldVZ1NHa3RWR1ZqYUNCWGIzSnNaREVOTUFzR0ExVUVBeE1FYzJGdWVqQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUlPeFpXQ2VUNmtPaUNhMkRNSFFjZU1DTXR6eVE0empqbWZSaVRuN0RRdXB1TmJ1N0JPOTg1MWJCcWk0QmYxd2oyWFhBdTlVVFN5cm9ORW0vUTQvWE5QSzNPZzdiTHhQUTVhRVVraU5SYnNoY0JCMk9VeXB6ZG8zdk5wQjdnSzJoY2hBUjZHZm1ZcXZuRngyTXR6VGp3Zy9GdmhVNnI4T0hWQVU5SkluMy9xTStYS1hMRm81R3VNSnp5NUExLytiTXRwSFZiZ0NJWkhHTllCVk9MZkdZNThqWDN6eGgyVXQ1QjlEYjZ2R3hKZHhYcVRzMi8wb0V0ZHRrUU5tSmNNcC9vdUphb3g1REo2dXJlQkt0SytSQ0lzc0F3SkdIUFEweHptbDNEb0dNK2xsUHo3WVhGZy8rK3U2YkZBN1hjdSsyVmp3QjVkOFo2cHhJaU5XUlhrTGsxVUNBd0VBQVRBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQVM3Z3ZiTXpmV3duVExOSFhBM1VqU3ducXZ6cXp4WUV0dnpUdThDQkVRZllWUXhTOEpSTjBHZ3lEYnJBMmxjd1JMRm5IdHdKTDBIemI1aVNnREM4WGtsUHcxYkdyQUZHT2FhMGtKQlVFazR0ekFBQkkrNndyNmZQVmVoWTlnUUsxTysyQm01bW1CT1haNnBoSHN2Tnk3SGFlUkZJNFo0Mzg4V21DM25uTXZaQUNSWEhhWTcrb1FxS1Rac055VU93dEI0V0lsYnBFWGFKazEydEpCcVVmaG9RTTdJWXJ4VVk3SjR3RFVLVTVIMTlRMTc2QnNwYzI2UFF1NWppbk8zRUNSQ3RHeTA1NHhBdGpvN2czRDhLRWhTRHRPUG9EYldGWjIwaGdnbE1RTXpycWVUMjJkdFh1YlBlUno4dUVQeXNQVnhsNVA1NXM0RDFVT1h4K0RCNmRxZz09Il0sImFsZyI6IlJTMjU2In0.RTNCMEM0NDI5OEZDMUMxNDlBRkJGNEM4OTk2RkI5MjQyN0FFNDFFNDY0OUI5MzRDQTQ5NTk5MUI3ODUyQjg1NQ.BSxmFZMecWJuIuSlLw7ULzV2fewA9sOr8vtWyv3D2j0HK5sqCOuOUj7_NBRxYh6jXTlQZ8Ua6rsRC4fQvMTa51rWoxQfZgpHO4TTrVmRTDvOfmXjtNrS5LM7Gqywmfl9sgwUFzkC0mOrUoi6KLZxberornvKhjn1quXl-sTiCOliMq-N2ZUbPahLNSWk0XuUKdnNq7CiNtuBm739-aep_c1E6LJhj4QTJpIc55cCqaRtRUWbQlVzQEXB7z6Mu_dlGYqnvaIq3pqkQXPDVS4vnj4MdiJ4KbiEY8gDpgXomGNmM29foLH8JQpFeINQrdEs0SBOipvNgLJspyqtPbdKeA";
		Mockito.when(requestWrapper.getInputStream()).thenReturn(servletInputStream);
		StringBuffer sbf = new StringBuffer("http://localhost:8090/identity/auth/0.8/12213123/12321312");
		Mockito.when(requestWrapper.getRequestURL()).thenReturn(sbf);
		Mockito.when(requestWrapper.getContextPath()).thenReturn("/identity");
		Mockito.when(requestWrapper.getHeader("Authorization")).thenReturn(signature);
		try {
			ReflectionTestUtils.invokeMethod(baseAuthFilter, "consumeRequest", requestWrapper, createRequestBody());
		} catch (UndeclaredThrowableException e) {
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}

	@Test
	public void testConsumeRequestinvalid() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		String req = "{\"id\":\"mosip.identity.auth\",\"individualId\":\"2410478395\",\"individualIdType\":\"D\",\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\"requestTime\":\"2019-03-13T10:01:57.086+05:30\",\"requestedAuth\":{\"bio\":false,\"demo\":true,\"otp\":false,\"pin\":false},\"requestSessionKey\":\"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\"transactionID\":\"1234567890\",\"version\":\"0.8\"}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {

			}

			@Override
			public boolean isReady() {
				return bais.available() != 0;
			}

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}
		};
		String signature = "eyJ4NWMiOlsiTUlJRlVqQ0NCRHFnQXdJQkFnSVNBMlViOFRZamVQYnVuNG8weXg2a0dIVUhNQTBHQ1NxR1NJYjNEUUVCQ3dVQU1Fb3hDekFKQmdOVkJBWVRBbFZUTVJZd0ZBWURWUVFLRXcxTVpYUW5jeUJGYm1OeWVYQjBNU013SVFZRFZRUURFeHBNWlhRbmN5QkZibU55ZVhCMElFRjFkR2h2Y21sMGVTQllNekFlRncweE9ERXhNamd4TnpFM05EZGFGdzB4T1RBeU1qWXhOekUzTkRkYU1CY3hGVEFUQmdOVkJBTVRER1JsZGk1dGIzTnBjQzVwYnpDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBTFhVcitpTXZMdmpSeUVNRnBFTUswN2NNU0xFUEs0d09rWE91d2F0Umt1dERsSTFZRUFVVGxtZk44TFhKaXhIV3doTU10WTFNa3p2NldHNTdUOHRLcm1TclFBZDBVUkY0ODVSZmZXVjBKdWRTSXJMUVJNdFdBUWpKbTJSUUMvOHdDeVJsczBnb2p0UW1YRUJPcnJTajBlelROR1drSUZxQmtmeWNUV2RONEs2YkMwcVJSR2taUTNrT1VwM1ZhZUFRSE80djk2SStNZ3JidlRUQXlKOHlKeU1US2dzVkVFZ0d2RldDOERxVUM4enlKc3c2VTY1NTN5UjZoQkZkeUJPc1l2SHVqRjFYUm1XU1J1L3BKVEZtZi9CUGhmTmttN3lyYjluUmY1QURUcFdZRFdUWnZIS1NydG1tS0FXbjFLWXAvdGx0UkNybHI4dW1TcEJaYzRxM044Q0F3RUFBYU9DQW1Nd2dnSmZNQTRHQTFVZER3RUIvd1FFQXdJRm9EQWRCZ05WSFNVRUZqQVVCZ2dyQmdFRkJRY0RBUVlJS3dZQkJRVUhBd0l3REFZRFZSMFRBUUgvQkFJd0FEQWRCZ05WSFE0RUZnUVV1L2ZCNUVpc0xESnRXSWRvQkxNYmhQQ1lqK0l3SHdZRFZSMGpCQmd3Rm9BVXFFcHFZd1I5M2JybTBUbTNwa1ZsNy9PbzdLRXdid1lJS3dZQkJRVUhBUUVFWXpCaE1DNEdDQ3NHQVFVRkJ6QUJoaUpvZEhSd09pOHZiMk56Y0M1cGJuUXRlRE11YkdWMGMyVnVZM0o1Y0hRdWIzSm5NQzhHQ0NzR0FRVUZCekFDaGlOb2RIUndPaTh2WTJWeWRDNXBiblF0ZURNdWJHVjBjMlZ1WTNKNWNIUXViM0puTHpBWEJnTlZIUkVFRURBT2dneGtaWFl1Ylc5emFYQXVhVzh3VEFZRFZSMGdCRVV3UXpBSUJnWm5nUXdCQWdFd053WUxLd1lCQkFHQzN4TUJBUUV3S0RBbUJnZ3JCZ0VGQlFjQ0FSWWFhSFIwY0RvdkwyTndjeTVzWlhSelpXNWpjbmx3ZEM1dmNtY3dnZ0VHQmdvckJnRUVBZFo1QWdRQ0JJSDNCSUgwQVBJQWR3QXBQRkdXVk1nNVpicXFVUHhZQjlTM2I3OVllaWx5M0tURERQVGxSVWYwZUFBQUFXZGJpcEpjQUFBRUF3QklNRVlDSVFEeWVBUy9QOXVUSTFPRFFQQ3ozRXNrZE5qZWJOSFdTem4zRjBtS2hNL2tvZ0loQUxOSGVQZHFFeU8ySW9tdHhIZWNuTE9JWWZEWWF2TWNOcXZ4bWZ2MVhzSmVBSGNBNG1sTHJpYm82VUFKNklZYnRqdUQxRDduL25TSSs2U1BLSk1CbmQzeDIvNEFBQUZuVzRxVCt3QUFCQU1BU0RCR0FpRUFsSVdUUStGYllwcTlqNnZRTHorNzJzM29CNnFDdzNJM0ZOQzkxTW1IM044Q0lRQ0c2eVRsa1VOd092ZlQrUmFaUTRMeVhtTzVYa0RmZzFnamJQZHd0V2lnTVRBTkJna3Foa2lHOXcwQkFRc0ZBQU9DQVFFQWJ5ZmlmaW5yZE1rcmMyN3JpL0VGYWFYa1VZR1JMUkV3aUdWRzNQSVBCTmlXL1h0UHhFUzQvR3RNZE5CSGVPSUhCNDY4ejd3UmZsTlE2TnFpajF1c1hZUjRrYnhuZlVDRURmeXN2SUovUG1zSGU5bkovT3JGZEE5MTFKaVJWTzc2RTVHT21xbnBkZXNtakVDZWZXK1lva1lMTm1CbzdJYnEzUi9QZkR3dDRsZGJYTG9PU29XbUYybU9IWHV5SXZjc0Q3ZDIvUUlzZzd4eVlWbnZQaTFCRENNY2dYTzhkNzBUR2lhazdTOTRPQnVBaHA2UzRYOWprSTRmRnBsVjNjRng1YXFLd2FXWHNkR2Y3SUpaRzhkZHA1cmh1MDFqS3IzTXRDY3Mvb1FoaHZyU09CWDM3TjFHWjF4a3pYaFNVZ1I3alVVR0t3WWxKWDQ5TDdVUW5Ib1hwZz09Il0sImFsZyI6IlJTMjU2In0.RTlFNUMxQzlFNEY2Mjc3MzM5RDFCQ0RFMDczM0E1OUJENDJGODczMUY0NDlEQTZEQzEzMDEwQTkxNjkzMEQ0OA.A3JKfawY5WflIy5YiwRTijJDrKx0_bWhpoE_6nyAyyE5f91P0xVXG8Pnz-qLkjO-zQ5YGmoHoWsdhrdmvjdXtO4Y4fddHhAr-QEZpOgGyIqtuY9E57wp-iw30GhJ4aRjrZo2CD3fHblwHLpl1PZDVsJkLOwFPqHSJTen1c8M9iWjiLss0PdEvns33j1zN7CM9um97mJFV-PSyKjhfKj6JhPn47bdbtAFb1DwNdPv1AaeCGucE07O48Qs-ruGf0pnaXCOPfZvK4FN0ADbLanGeXf-AXOyetPRtAnzZscw6Bg8jAXbd2nGKWNoJZWzgqPLAsMMfoWjgRIxc2tX_dq8jw";
		Mockito.when(requestWrapper.getInputStream()).thenReturn(servletInputStream);
		StringBuffer sbf = new StringBuffer("http://localhost:8090/identity/auth/0.8/12213123/12321312");
		Mockito.when(requestWrapper.getRequestURL()).thenReturn(sbf);
		Mockito.when(requestWrapper.getContextPath()).thenReturn("/identity");
		Mockito.when(requestWrapper.getHeader("Authorization")).thenReturn(signature);
		try {
			ReflectionTestUtils.invokeMethod(baseAuthFilter, "consumeRequest", requestWrapper, createRequestBody());
		} catch (UndeclaredThrowableException e) {
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	private Map<String, Object> createRequestBody(){
		String req = "{\"id\":\"mosip.identity.auth\",\"individualId\":\"2410478395\",\"individualIdType\":\"D\",\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\"requestTime\":\"2019-03-13T10:01:57.086+05:30\",\"requestedAuth\":{\"bio\":false,\"demo\":true,\"otp\":false,\"pin\":false},\"requestSessionKey\":\"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\"transactionID\":\"1234567890\",\"version\":\"0.8\"}";
		try {
			return mapper.readValue(req, new TypeReference<Map<String, Object>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
