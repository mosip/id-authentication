package io.mosip.authentication.service.impl.otpgen.validator;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Manoj SP
 *
 */
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OTPRequestValidatorTest {

//	@Mock
//	private SpringValidatorAdapter validator;
//
//	@Mock
//	Errors error;
//
//	@Autowired
//	Environment env;
//
//	@InjectMocks
//	MosipRollingFileAppender idaRollingFileAppender;
//
//	@InjectMocks
//	private OTPRequestValidator otpRequestValidator;
//
//	@Mock
//	private OTPAuthServiceImpl otpAuthServiceImpl;
//
//	@Before
//	public void before() {
//		ReflectionTestUtils.setField(otpRequestValidator, "env", env);
//	}
//
//	@Test
//	public void testSupportTrue() {
//		assertTrue(otpRequestValidator.supports(OtpRequestDTO.class));
//	}
//
//	@Test
//	public void testSupportFalse() {
//		assertFalse(otpRequestValidator.supports(AuthRequestValidator.class));
//	}
//
//	@Ignore //FIXME
//	@Test
//	public void testValidUin() {
//		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
//		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
//		OtpRequestDTO.setReqTime(new Date());
//		OtpRequestDTO.setIdType(IdType.UIN.getType());
//		OtpRequestDTO.setId("426789089018");
//		otpRequestValidator.validate(OtpRequestDTO, errors);
//		assertFalse(errors.hasErrors());
//	}
//
//	@Test
//	public void testInvalidUin() {
//		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
//		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
//		OtpRequestDTO.setIdType(IdType.UIN.getType());
//		OtpRequestDTO.setId("234567890123");
//		OtpRequestDTO.setReqTime(new Date());
//		otpRequestValidator.validate(OtpRequestDTO, errors);
//		assertTrue(errors.hasErrors());
//	}
//
//	@Ignore //FIXME
//	@Test
//	public void testValidVid() {
//		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
//		OtpRequestDTO.setReqTime(new Date());
//		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
//		OtpRequestDTO.setIdType(IdType.VID.getType());
//		OtpRequestDTO.setId("5371843613598206");
//		otpRequestValidator.validate(OtpRequestDTO, errors);
//		assertFalse(errors.hasErrors());
//	}
//
//	@Test
//	public void testInvalidVid() {
//		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
//		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
//		OtpRequestDTO.setIdType(IdType.VID.getType());
//		OtpRequestDTO.setId("5371843613598211");
//		OtpRequestDTO.setReqTime(new Date());
//		otpRequestValidator.validate(OtpRequestDTO, errors);
//		assertTrue(errors.hasErrors());
//	}
//
//	@Test
//	public void testInvalidIdType() {
//		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
//		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
//		OtpRequestDTO.setIdType("abcd");
//		OtpRequestDTO.setReqTime(new Date());
//		OtpRequestDTO.setId("5371843613598211");
//		otpRequestValidator.validate(OtpRequestDTO, errors);
//		assertTrue(errors.hasErrors());
//	}
//
//	@Test
//	public void testInvalidTimestamp() {
//		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
//		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
//		OtpRequestDTO.setIdType("abcd");
//		OtpRequestDTO.setReqTime(new Date("1/1/2017"));
//		OtpRequestDTO.setId("5371843613598211");
//		otpRequestValidator.validate(OtpRequestDTO, errors);
//		assertTrue(errors.hasErrors());
//	}
}
