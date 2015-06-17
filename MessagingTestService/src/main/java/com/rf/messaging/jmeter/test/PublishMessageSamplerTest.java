package com.rf.messaging.jmeter.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rf.messaging.consumer.domain.AccountDO;
import com.rf.messaging.consumer.domain.OrderDO;

public class PublishMessageSamplerTest {
	@BeforeClass
	public static void oneTimeSetUp() {
		System.out.println("@BeforeClass - for junit testMessagePublisher");
	}

	@AfterClass
	public static void oneTimeTearDown() {
		System.out.println("@AfterClass - for junit testMessagePublisher");
	}

	@Before
	public void setUp() {
		System.out.println("@Before - testMessagePublisher setUp");
	}

	@After
	public void tearDown() {
		System.out.println("@After - testMessagePublisher tearDown");
	}

	@Test
	public void testMessagePublisher() throws JsonGenerationException, JsonMappingException, IOException {
		System.out.println("@Test - testMessagePublisher method");
		String restHost = "http://localhost:8080";
		String uri = "/messaging/publish/account";
		
		AccountDO account = new AccountDO();
		account.setFirstName("FName");
		account.setLastName("LName");
		account.setAddress1("232 Latst Street");
		account.setCity("Sanfranscisco");
		account.setState("CA");
		account.setZip("98888");
		String json = convertToJson(account);
		makePostCall(json, uri, restHost);
		
		
	}

	public String makePostCall(String json, String uri, String restHost) {

		String resp = "";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(restHost + uri);

			StringEntity input = new StringEntity(json);
			input.setContentType("application/json");
			postRequest.setEntity(input);

			HttpResponse response = httpClient.execute(postRequest);

			if ((response.getStatusLine().getStatusCode() < 200) && (response.getStatusLine().getStatusCode() > 210)) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
			
			if(response.getStatusLine().getStatusCode() != 204){
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(response.getEntity().getContent())));
	
				System.out.println("Output from Server .... \n");
				String output = "";
				while ((output = br.readLine()) != null) {
					resp = output;
					System.out.println(uri + " response: " + output);
				}
			}

			httpClient.getConnectionManager().shutdown();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

		return resp;
	}

	public String convertToJson(Object domain) throws JsonGenerationException,
			JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, false);
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		return mapper.writeValueAsString(domain);

	}

	public AccountDO covertToAccountDO(String json)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());

		return mapper.readValue(json, AccountDO.class);

	}
}
