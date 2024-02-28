package hu.bme.aut.stepsysterv.PooBer;


import hu.bme.aut.stepsysterv.PooBer.billing.BillingAssistant;
import hu.bme.aut.stepsysterv.PooBer.billing.data.AccountRepository;
import hu.bme.aut.stepsysterv.PooBer.billing.data.InvoiceRepository;
import hu.bme.aut.stepsysterv.PooBer.messagequeue.MQListener;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bme.aut.stepsysterv.PooBer.messagequeue.InvoiceSender;
import hu.bme.aut.stepsysterv.PooBer.users.RestAuthenticationEntryPoint;
import hu.bme.aut.stepsysterv.PooBer.users.SecurityConfig;
import hu.bme.aut.stepsysterv.PooBer.users.data.UserDetailsImpl;
import hu.bme.aut.stepsysterv.PooBer.users.data.UserDetailsServiceImpl;
import hu.bme.aut.stepsysterv.PooBer.users.data.UserRepository;
import hu.bme.aut.stepsysterv.PooBer.wcManager.MainController;
import hu.bme.aut.stepsysterv.PooBer.wcManager.WcRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
		locations = "classpath:application.properties")
@ExtendWith(SpringExtension.class)
//@WebMvcTest(MainController.class)
@AutoConfigureMockMvc(addFilters = false)
class PooBerApplicationTests {

	@Autowired
	private RabbitTemplate template;

	@Autowired
	private DirectExchange directTest;

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private BillingAssistant billingAssistant;

	@Autowired
	private MQListener mqListener;

	@Autowired
	private MockMvc mvc;

	@Autowired
	SecurityConfig sc;

	@Autowired
	RestAuthenticationEntryPoint raep;

	@MockBean
	WcRepository wcRepository;

	@MockBean
	InvoiceSender invoiceSender;

	@MockBean
	UserRepository userRepository;


	@Test
	public void allTest() throws Exception {
		mvc.perform(get("/registry/all").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void addWcTest() throws Exception {
		mvc.perform( MockMvcRequestBuilders
				.post("/registry/add")
				.content("ownerUID=1&location=Budapest&price=10&properties=little+bit+poopy")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	/* TEST CASES FOR BILLING MODULE */
	@Test
	void amqpIntegrityTest(){
		template.convertAndSend(directTest.getName(), "test", "test");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		verify(mqListener, times(1)).receiveTest("test");
	}


	@Test
	void receiveRegistration(){
		mqListener.receiveRegistrations(String.valueOf(999));
		//verify(billingAssistant, times(1)).createAccount(Mockito.any());
		assertNotNull(accountRepository.findByUid(999L));
	}

	@Test
	void receiveTopUp(){}

	@Test
	void receiveInvoice(){}

	@Test
	void simulateEndOfPeriod(){}

	@Test
	void simulateEndOfPeriod_withBan(){}

}
