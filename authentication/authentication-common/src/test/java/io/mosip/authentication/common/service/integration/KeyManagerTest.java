package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.integration.dto.CryptomanagerRequestDto;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.kernel.core.http.ResponseWrapper;

// 
/**
* The Class KeyManagerTest which covers KeyManager
* 
 * @author M1046368 Arun Bose S
*/
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
@WebMvcTest
public class KeyManagerTest {

              @Autowired
              private ObjectMapper mapper;

              /** The rest request factory. */
              @Mock
              private RestRequestFactory restRequestFactory;

              @Mock
              private RestHelper restHelper;

              /** The key manager. */
              @InjectMocks
              private KeyManager keyManager;

              @Before
              public void before() {
                             ReflectionTestUtils.setField(keyManager, "keySplitter", "#KEY_SPLITTER#");
                             ReflectionTestUtils.setField(keyManager, "partnerId", "PARTNER");
                             ReflectionTestUtils.setField(keyManager, "appId", "IDA");
              }

              /**
              * Request data test.
              *
              * @throws IdAuthenticationAppException the id authentication app exception
              * @throws IDDataValidationException    the ID data validation exception
              * @throws JsonProcessingException      the json processing exception
              */
              @Test
              public void requestDataTest()
                                           throws IdAuthenticationAppException, IDDataValidationException, JsonProcessingException {
                             Map<String, Object> reqMap = createRequest();
                             RestRequestDTO restRequestDTO = getRestRequestDTO();
                             new ResponseWrapper<>();
                             Map<String, Object> cryptoResMap = new HashMap<>();
                             cryptoResMap.put("data", "eyJ0aW1lc3RhbXAiOiIyMDE5LTA1LTA3VDEyOjQxOjU3LjA4NiswNTozMCIsInRyYW5zYWN0aW9uSUQiOiIxMjM0NTY3ODkwIiwiYmlvbWV0cmljcyI6W3siZGF0YSI6eyJiaW9UeXBlIjoiRklEIiwiYmlvU3ViVHlwZSI6IlVOS05PV04iLCJkZXZpY2VQcm92aWRlcklEIjoiY29nZW50IiwiYmlvVmFsdWUiOiJSazFTQUNBeU1BQUFBQUZjQUFBQlBBRmlBTVVBeFFFQUFBQW9OVUI5QU1GMFY0Q0JBS0JCUEVDMEFMNjhaSUM0QUtqTlpFQmlBSnZXWFVCUEFOUFdOVURTQUs3UlVJQzJBUUlmWkVESkFQTXhQRUJ5QUd3UFhZQ3BBUllQWkVDZkFGam9aRUNHQUV2OVpFQkVBRm10VjBCcEFVR05YVUMvQVVFRVNVQ1VBVklFUEVDMkFWTnhQSUNjQUxXdVpJQ3VBTG0zWkVDTkFKcXhRMENVQUkzR1EwQ1hBUGdoVjBCVkFLRE9aRUJmQVBxSFhVQkRBS2UvWklCOUFHM3hYVURQQUliWlVFQmNBR1loWkVDSUFTZ0hYWUJKQUdBblYwRGpBUjRqRzBES0FUcUpJVUNHQURHU1pFRFNBVVlHSVVBeEFEK25WMENYQUsrb1NVQm9BTHI2UTRDU0FPdUtYVUNpQUl2TlpFQzlBSnpRWklCTkFMYlRYVUJCQUw2OFYwQ2VBSERaWkVDd0FIUGFaRUJSQVB3SFVJQkhBSFcyWFVEWEFSQVVEVUM0QVM0SFpFRFhBUzBDUTBDWUFETDRaRUNzQVV6dVBFQmtBQ2dSWkFBQSIsInRpbWVzdGFtcCI6IjIwMTktMDUtMDdUMTM6NDE6NTcuMDg2KzA1OjMwIiwidHJhbnNhY3Rpb25JRCI6IjEyMzQ1Njc4OTAifX1dfQ");
                             Map<String, Object> responseMap = new HashMap<>();
                             responseMap.put("response", cryptoResMap);
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
                                                          .thenReturn(restRequestDTO);
              Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(responseMap);
                             Map<String, Object> decryptedReqMap = keyManager.requestData(reqMap, mapper, "PARTNER");
                             assertTrue(decryptedReqMap.containsKey("transactionID"));
              }

              /**
              * Request data mapper test.
              *
              * @throws IdAuthenticationAppException the id authentication app exception
              * @throws IDDataValidationException    the ID data validation exception
              * @throws JsonProcessingException      the json processing exception
              */

              @Test(expected = IdAuthenticationAppException.class)
              public void requestDataMapperTest()
                                           throws IdAuthenticationAppException, IDDataValidationException, JsonProcessingException {
                             Map<String, Object> reqMap = createRequest();
                             RestRequestDTO restRequestDTO = getRestRequestDTO();
                             Map<String, Object> cryptoResMap = new HashMap<>();
                             cryptoResMap.put("data", "-CAj77ZNbtHjmCOSlPUsb4IgqnqHSv0MS5FeLMj");
                             Map<String, Object> responseMap = new HashMap<>();
                             responseMap.put("response", cryptoResMap);
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
                                                          .thenReturn(restRequestDTO);
              Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(responseMap);
                             keyManager.requestData(reqMap, mapper, "PARTNER");

              }

              /**
              * Request invalid data test 1.
              *
              * @throws IDDataValidationException    the ID data validation exception
              * @throws IdAuthenticationAppException the id authentication app exception
              */

              @Test(expected = IdAuthenticationAppException.class)
              public void requestInvalidDataTest1() throws IDDataValidationException, IdAuthenticationAppException {
                             Map<String, Object> reqMap = createRequest();
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(
                                                          new IDDataValidationException(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED, "publickey expired"));
                             keyManager.requestData(reqMap, mapper, "PARTNER");
              }

              /**
              * Request invalid data test 2.
              *
              * @throws IdAuthenticationAppException the id authentication app exception
              * @throws IOException                  Signals that an I/O exception has
              *                                      occurred.
              * @throws IDDataValidationException    the ID data validation exception
              */

              @Test(expected = IdAuthenticationAppException.class)
              public void requestInvalidDataTest2() throws IdAuthenticationAppException, IOException, IDDataValidationException {
                             Map<String, Object> reqMap = createRequest();
                             RestRequestDTO restRequestDTO = getRestRequestDTO();
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
                                                          .thenReturn(restRequestDTO);
                             Mockito.when(restHelper.requestSync(Mockito.any()))
                                                          .thenThrow(new RestServiceException(IdAuthenticationErrorConstants.INVALID_REST_SERVICE));
                             keyManager.requestData(reqMap, mapper, "PARTNER");
              }

              @Test
              public void invalidKernelKeyManagerErrorRequest()
                                           throws IdAuthenticationAppException, IOException, IDDataValidationException {
                             Map<String, Object> reqMap = createRequest();
                             RestRequestDTO restRequestDTO = getRestRequestDTO();
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
                                                          .thenReturn(restRequestDTO);
                             String kernelErrorMapStr = "{\r\n" + "     \"errors\": [{\r\n" + "                                           \"errorCode\": \"KER-KMS-003\"\r\n"
                                                          + "                        }\r\n" + "\r\n" + "            ]\r\n" + "\r\n" + "}";
                             RestServiceException restException = new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR,
                                                          kernelErrorMapStr, new ObjectMapper().readValue(kernelErrorMapStr.getBytes("UTF-8"), Map.class));
              Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restException);
                try {     
                             keyManager.requestData(reqMap, mapper, "PARTNER");
                             }
                
                catch(IdAuthenticationAppException ex) {
                               assertEquals(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED.getErrorCode(), ex.getErrorCode());
                               assertEquals(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED.getErrorMessage(), ex.getErrorText());
                }
              }
              
              @Test
              public void invalidKernelDecryptErrorRequest()
                                           throws IdAuthenticationAppException, IOException, IDDataValidationException {
                             Map<String, Object> reqMap = createRequest();
                             RestRequestDTO restRequestDTO = getRestRequestDTO();
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
                                                          .thenReturn(restRequestDTO);
                             String kernelErrorMapStr = "{\r\n" + "     \"errors\": [{\r\n" + "                                           \"errorCode\": \"KER-FSE-003\"\r\n"
                                                          + "                        }\r\n" + "\r\n" + "            ]\r\n" + "\r\n" + "}";
                             RestServiceException restException = new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR,
                                                          kernelErrorMapStr, new ObjectMapper().readValue(kernelErrorMapStr.getBytes("UTF-8"), Map.class));
              Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restException);
                try {     
                             keyManager.requestData(reqMap, mapper, "PARTNER");
                             }
                
                catch(IdAuthenticationAppException ex) {
                               assertEquals(IdAuthenticationErrorConstants.INVALID_ENCRYPTION.getErrorCode(), ex.getErrorCode());
                               assertEquals(IdAuthenticationErrorConstants.INVALID_ENCRYPTION.getErrorMessage(), ex.getErrorText());
                }
              }
              
              
              

              @Test(expected = IdAuthenticationAppException.class)
              public void invalidKernelErrorRequest()
                                           throws IdAuthenticationAppException, IOException, IDDataValidationException {
                             Map<String, Object> reqMap = createRequest();
                             RestRequestDTO restRequestDTO = getRestRequestDTO();
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
                                                          .thenReturn(restRequestDTO);
                             String kernelErrorMapStr = "{\r\n" + "     \"errors\": [{\r\n" + "                                           \"errCode\": \"KER-KMS-004\"\r\n"
                                                          + "                        }\r\n" + "\r\n" + "            ]\r\n" + "\r\n" + "}";
                             RestServiceException restException = new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR,
                                                          kernelErrorMapStr, new ObjectMapper().readValue(kernelErrorMapStr.getBytes("UTF-8"), Map.class));
              Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restException);
                             keyManager.requestData(reqMap, mapper, "PARTNER");
              }

              @Test(expected = IdAuthenticationAppException.class)
              public void requestInvalidDataIOException() throws IDDataValidationException, IdAuthenticationAppException {
                             Map<String, Object> reqMap = createRequest();
                             RestRequestDTO restRequestDTO = getRestRequestDTO();
                             Map<String, Object> cryptoResMap = new HashMap<>();
                             cryptoResMap.put("data", "-CAj77ZNbtHjmCOSlPUsb4IgqnqHSv0MS5FeLMj");
                             Map<String, Object> responseMap = new HashMap<>();
                             responseMap.put("response", cryptoResMap);
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
                                                          .thenReturn(restRequestDTO);
              Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(responseMap);
                             Mockito.when(keyManager.requestData(reqMap, mapper, "PARTNER")).thenThrow(IOException.class);
              }

              /*
              * @Test(expected = IdAuthenticationAppException.class) public void
              * TestTspIdisNullorEmpty() throws IdAuthenticationAppException { Map<String,
              * Object> requestBody = new HashMap<>(); keyManager.requestData(requestBody,
              * mapper); }
              * 
               * @Ignore
              * 
               * @Test(expected = IdAuthenticationAppException.class) public void
              * TestTspIdisNull() throws IdAuthenticationAppException { Map<String, Object>
              * requestBody = new HashMap<>(); requestBody.put("tspID", null);
              * keyManager.requestData(requestBody, mapper); }
              */

              /*
              * @Test(expected = IdAuthenticationAppException.class) public void
              * TestTspIdisEmpty() throws IdAuthenticationAppException { Map<String, Object>
              * requestBody = new HashMap<>(); requestBody.put("tspID", "");
              * keyManager.requestData(requestBody, mapper); }
              */

              // ====================================================================
              // ********************** Helper Method *******************************
              // ====================================================================

              /**
              * Creates the request.
              *
              * @return the map
              */
              private Map<String, Object> createRequest() {
                             String data = "{\r\n" + "  \"consentObtained\": true,\r\n" + "  \"id\": \"mosip.identity.auth\",\r\n"
                                                          + "  \"individualId\": \"6892738569\",\r\n" + "  \"individualIdType\": \"UIN\",\r\n"
                                                          + "  \"keyIndex\": \"string\",\r\n"
                                                          + "  \"request\": \"iMw7w2duULAj3tgfJvPpKEmZ_KBdD1RruHE-jQlfssfHTl_pv8_6Ik_TlZbmcH2VyGWtC9sCxIYBoblXLZRkZIF4fErFg5VZgomvYCJ-RzQa78izAGkrehxuMfnXQcY3zaDObkSZPzlA7Law6sokohL6frRAnVcDdZ2kXxfjYgYyRR40CIqKtpuUXnEjBQbmiw8AguaBjTgK6r2pTTH9irfkONSvHjMDGbn6aTQFmGbuCJoOvCBR-qf2jaq9BpaAEXpNNTorr4XoS6Aen3hbdqYQHeyGm8yggmKTeiOZHVatEAaT8LVouYqlMVNV65eZKdmDQmZvY2f18aJu5RcU_XUlz0uvITtPkBIBpIbpVx7KGVIoe3iBKf6n9mmm3xjpGHMIipMZJZSdINMQx8t-pBXiAOr2fvrcR6rbvjLq24pzwBq6jkM3Zg1_QKbX9YwouraWKOhW79AZ8ZmVmDtYQr0DhQNypx-kNQrm2K8_BIjzP-Fvm3YhGudZ7yre1I05TwjNLCr-iLcEnOEl_KWAVNpfNLrotkWgwmaUbz2mZjkMbdGy5JtW5a0-yAg6_zZFiFWhCgOUEvaZ_226PZy1dZpElOrP3W-Mvc0ATJCc76e2F86JoNwpOA8SLpyYsd3j\",\r\n"
                                                          + "  \"requestHMAC\": \"TMXTCgmeuswzn2Ls2mWSz6ZXjiV8EWGmqfZoGHqKJEG0E__oDpBsoGjyEBzEK-GuXw70q8bWrPB7MoUuq2t3D7vV3r9X52B5mMezNH_xzsjrc6ruQrdWg-okg0nBasEs\",\r\n"
                                                          + "  \"requestSessionKey\": \"rBM6Ekp7xWwyzGgk1E4rgHRFUKUZCU3bAZG_wzRb35HI7tb-280bbTJYTBgik6U_LJIzXhTRmMRt2_sgPB4EmePsS4yC93HpyGF6uJ1yPoA6E--CKqHHaGx1z774moIH_sbB5TJw6fYQSiBGBABKFbrxSdj4FqLhejtIcyIxv5gRLN43Ye-WvIZNioHVOoBxCbRxgPCVESF8AHZK67S-SSy2jk8p-e-47g869C0yp6gYReo4dGIFs-1yD2bNSQC9b1mo-cC528W0iIt5j23PlJdELEj9a2oPI2PqIfU6zFtKEXqqYo_WmvpfA1Wu9r3yuY5MGp_C0F1nD0u_JIRjPw\",\r\n"
                                                          + "  \"requestTime\": \"2019-04-12T06:00:22.098Z\",\r\n" + "  \"requestedAuth\": {\r\n"
                                                          + "    \"bio\": true,\r\n" + "    \"demo\": false,\r\n" + "    \"otp\": false,\r\n"
                                                          + "    \"pin\": false\r\n" + "  },\r\n" + "  \"transactionID\": \"1234567890\",\r\n"
                                                          + "  \"version\": \"0.9\"\r\n" + "}" + "}";
                             Map<String, Object> readValue = null;
                             try {
                                           readValue = mapper.readValue(data, new TypeReference<Map<String, Object>>() {
                                           });
                                           readValue.put("request", Base64.decodeBase64((String) readValue.get("request")));
                             } catch (IOException e) {
                                           // TODO Auto-generated catch block
                                           e.printStackTrace();
                             }
                             return readValue;
              }

              /**
              * Gets the rest request DTO.
              *
              * @return the rest request DTO
              */
              private RestRequestDTO getRestRequestDTO() {
                             RestRequestDTO restRequestDTO = new RestRequestDTO();
                             restRequestDTO.setHttpMethod(HttpMethod.POST);
              restRequestDTO.setUri("http://localhost:8089/cryptomanager/v1.0/decrypt");
                             CryptomanagerRequestDto cryptomanagerRqt = new CryptomanagerRequestDto();
                             restRequestDTO.setRequestBody(cryptomanagerRqt);
                            restRequestDTO.setResponseType(OtpGeneratorResponseDto.class);
                             restRequestDTO.setTimeout(23);
                             return restRequestDTO;
              }
              
              private RestRequestDTO getRestRequestDTOEncrypt() {
                             RestRequestDTO restRequestDTO = new RestRequestDTO();
                             restRequestDTO.setHttpMethod(HttpMethod.POST);
              restRequestDTO.setUri("http://localhost:8089/cryptomanager/v1.0/encrypt");
                             CryptomanagerRequestDto cryptomanagerRqt = new CryptomanagerRequestDto();
                             restRequestDTO.setRequestBody(cryptomanagerRqt);
                            restRequestDTO.setResponseType(OtpGeneratorResponseDto.class);
                             restRequestDTO.setTimeout(23);
                             return restRequestDTO;
              }
              
              @Test
              public void encryptDataTest1() throws IDDataValidationException, IdAuthenticationAppException {
                             Map<String, Object> cryptoResMap = new HashMap<>();
                             cryptoResMap.put("data", "-CAj77ZNbtHjmCOSlPUsb4IgqnqHSv0MS5FeLMj");
                             Map<String, Object> responseMap = new HashMap<>();
                             responseMap.put("response", cryptoResMap);
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
                             .thenReturn(getRestRequestDTOEncrypt());
              Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(responseMap);
                             assertNotNull(keyManager.encryptData(createIdentity(), mapper));
              }
              
              @Test(expected=IdAuthenticationAppException.class)
              public void encryptDataTest2() throws IDDataValidationException, IdAuthenticationAppException {
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
                             .thenReturn(getRestRequestDTOEncrypt());
                           Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(IdAuthenticationErrorConstants.INVALID_REST_SERVICE));
                             keyManager.encryptData(createIdentity(), mapper);
              }
              
              private Map<String, Object> createIdentity(){
                             String identity = "{phoneNumber=[{value=9000007865}], gender=[{language=ara, value=الذكر}, {language=fra, value=mâle}], dob=[{value=1955/04/15}], name=[{language=ara, value=ابراهيم بن علي}, {language=fra, value=Ibrahim Ibn Ali}], location1=[{language=ara, value=الدار البيضاء}, {language=fra, value=Casablanca}], location2=[{language=ara, value=طنجة - تطوان - الحسيمة}, {language=fra, value=Tanger-Tétouan-Al Hoceima}], addressLine1=[{language=ara, value=عنوان العينة سطر 1}, {language=fra, value=exemple d'adresse ligne 1}], emailId=[{value=adc.xyz@mindtree.com}], addressLine2=[{language=ara, value=عنوان العينة سطر 2}, {language=fra, value=exemple d'adresse ligne 2}], location3=[{language=ara, value=فاس-مكناس}, {language=fra, value=Fès-Meknès}], addressLine3=[{language=ara, value=عنوان العينة سطر 2}, {language=fra, value=exemple d'adresse ligne 2}]}";
                             Map<String, Object> map = new HashMap<>();
                             Map<String, Object> readValue = new HashMap<>();
                             readValue.put("identity", identity);
                             map.put("identity", readValue);
                             return map;
              }
              
              @Test
              public void SignResponseTest1() throws IDDataValidationException, IdAuthenticationAppException {
                             Map<String, Object> cryptoResMap = new HashMap<>();
                             cryptoResMap.put("signature", "-CAj77ZNbtHjmCOSlPUsb4IgqnqHSv0MS5FeLMj");
                             Map<String, Object> responseMap = new HashMap<>();
                             responseMap.put("response", cryptoResMap);
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
                             .thenReturn(getRestRequestDTOEncrypt());
              Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(responseMap);
                             assertNotNull(keyManager.signResponse("qweewq"));
              }
              
              @Test(expected=IdAuthenticationAppException.class)
              public void  SignResponseTest2() throws IDDataValidationException, IdAuthenticationAppException {
                            Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
                             .thenReturn(getRestRequestDTOEncrypt());
                           Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(IdAuthenticationErrorConstants.INVALID_REST_SERVICE));
                             keyManager.signResponse("qweewq");
              }

}
