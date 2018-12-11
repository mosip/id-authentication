package io.mosip.authentication.service.impl.notification.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.NotificationType;
import io.mosip.authentication.core.dto.indauth.SenderType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.id.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.integration.OTPManagerTest;
import io.mosip.authentication.service.integration.dto.SmsRequestDto;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IdTemplateManager.class,
		TemplateManagerBuilderImpl.class })
public class NotificationServiceImplTest {
//	@Mock
//	private RestRequestFactory restRequestFactory;

	@InjectMocks
	AuditRequestFactory auditFactory;
	@InjectMocks
	private RestRequestFactory restRequestFactory;

	@Autowired
	Environment environment;

	@Mock
	private RestHelper restHelper;

	@InjectMocks
	private NotificationServiceImpl notificationService;

	@Mock
	private IdTemplateManager idTemplateManager;
	
	@Mock
	private IdRepoService idInfoService;
	
	@Mock
	private IdInfoHelper demoHelper;
	
	@Mock
	private IdAuthServiceImpl idAuthServiceImpl;
	@Mock
	private NotificationManager notificationManager;
	@Before
	public void before() {
	ReflectionTestUtils.setField(restRequestFactory, "env", environment);
		ReflectionTestUtils.setField(auditFactory, "env", environment);
		ReflectionTestUtils.setField(notificationService, "env", environment);
		ReflectionTestUtils.setField(notificationService, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationManager, "restRequestFactory", restRequestFactory);
	}
	
	@BeforeClass
	public static void beforeClass() {
		RouterFunction<?> functionSuccessmail = RouterFunctions.route(RequestPredicates.POST("/notifier/email"),
				request -> ServerResponse.status(HttpStatus.OK).body(Mono.just(new String("success")), String.class));
		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccessmail);
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
		HttpServer.create(8087).start(adapter);

		RouterFunction<?> functionSuccessmsg = RouterFunctions.route(RequestPredicates.POST("/notifier/sms"),
				request -> ServerResponse.status(HttpStatus.OK).body(Mono.just(new String("success")), String.class));
		HttpHandler msgHttpHandler = RouterFunctions.toHttpHandler(functionSuccessmsg);
		ReactorHttpHandlerAdapter msgAadapter = new ReactorHttpHandlerAdapter(msgHttpHandler);
		HttpServer.create(8088).start(msgAadapter);
		System.err.println("started server");
	}

	@Test
	public void TestValidAuthSmsNotification() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
	AuthResponseDTO authResponseDTO=new AuthResponseDTO();
	ZoneOffset offset = ZoneOffset.MAX;
	authRequestDTO.setReqTime(Instant.now().atOffset(offset)
			.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authResponseDTO.setStatus("N");
		authResponseDTO.setResTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
		Supplier<Object> Supplier = () -> new String("Success");
		Mockito.when(restHelper.requestAsync(Mockito.any())).thenReturn(Supplier);
		String refId="4667732";
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Optional<String> uinOpt = Optional.of("426789089018");
		//Mockito.when(idAuthServiceImpl.getUIN(refId)).thenReturn(uinOpt);
		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo)).thenReturn("mosip");
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("internal.auth.notification.type", "none");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		mockenv.setProperty("mosip.auth.sms.template", "test");
		mockenv.setProperty("notification.date.format", "dd-MM-yyyy");
		mockenv.setProperty("notification.time.format", "HH:mm:ss");
		//mockenv.setProperty("internal.auth.notification.type", "none");
		mockenv.setProperty("mosip.otp.mail.subject.template", "test");
		mockenv.setProperty("mosip.otp.mail.content.template", "test");
		mockenv.setProperty("mosip.otp.sms.template", "test");
		ReflectionTestUtils.setField(notificationService, "env", mockenv);
		notificationService.sendAuthNotification(authRequestDTO, refId, authResponseDTO, idInfo, true);
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidAuthSmsNotification() throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IOException {
		SmsRequestDto smsRequestDto = new SmsRequestDto();
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
	AuthResponseDTO authResponseDTO=new AuthResponseDTO();
	ZoneOffset offset = ZoneOffset.MAX;
	authRequestDTO.setReqTime(Instant.now().atOffset(offset)
			.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authResponseDTO.setStatus("y");
		authResponseDTO.setResTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
		Supplier<Object> Supplier = () -> new String("Success");
		Mockito.when(restHelper.requestAsync(Mockito.any())).thenReturn(Supplier);
		String refId="4667732";
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Optional<String> uinOpt = Optional.of("");
		//Mockito.when(idAuthServiceImpl.getUIN(refId)).thenReturn(uinOpt);
		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo)).thenReturn("mosip");
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo)).thenReturn(" mosip ");
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Set<NotificationType> notificationtype = new HashSet<>();
		notificationtype.add(NotificationType.EMAIL);
		Map<String, Object> values = new HashMap<>();
		IDDataValidationException e = new IDDataValidationException(IdAuthenticationErrorConstants.NOTIFICATION_FAILED);
		IdAuthenticationBusinessException idAuthenticationBusinessException = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.NOTIFICATION_FAILED, e);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any()))
		.thenThrow(idAuthenticationBusinessException.getCause());
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("auth.notification.type", "email,sms");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		mockenv.setProperty("mosip.auth.sms.template", "test");
		mockenv.setProperty("notification.date.format", "dd-MM-yyyy");
		mockenv.setProperty("notification.time.format", "HH:mm:ss");
		//mockenv.setProperty("internal.auth.notification.type", "none");
		mockenv.setProperty("mosip.otp.mail.subject.template", "test");
		mockenv.setProperty("mosip.auth.mail.subject.template", "test");
		mockenv.setProperty("mosip.auth.mail.content.template", "test");
		mockenv.setProperty("mosip.otp.mail.content.template", "test");
		mockenv.setProperty("mosip.otp.sms.template", "test");
		ReflectionTestUtils.setField(notificationService, "env", mockenv);
		notificationService.sendAuthNotification(authRequestDTO, refId, authResponseDTO, idInfo, true);
	}
	private Map<String, Object> repoDetails(){
		Map<String, Object> map = new HashMap<>();
		map.put("registrationId", "863537");
		return map;
	}
}
