package hu.bme.aut.stepsysterv.PooBer;

import hu.bme.aut.stepsysterv.PooBer.billing.BillingAssistant;
import hu.bme.aut.stepsysterv.PooBer.billing.data.AccountRepository;
import hu.bme.aut.stepsysterv.PooBer.billing.data.InvoiceRepository;
import hu.bme.aut.stepsysterv.PooBer.messagequeue.BannedSender;
import hu.bme.aut.stepsysterv.PooBer.messagequeue.InvoiceSender;
import hu.bme.aut.stepsysterv.PooBer.users.data.User;
import hu.bme.aut.stepsysterv.PooBer.users.data.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class PooBerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(PooBerApplication.class, args);
	}

	@Autowired
	InvoiceSender invoiceSender;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Override
	public void run(String... args) throws Exception {
		if(userRepository.findById(1L).orElse(null) == null){
			User master_user = new User();
			master_user.setName("root");
			master_user.setEmail("pooberapplication@gmail.com");
			//master_user.setId(1L);
			master_user.setRoles(List.of("ROLE_ADMIN"));
			master_user.setPassword(encoder.encode("root"));
			invoiceSender.sendRegistration(1L);
			userRepository.save(master_user);
		}
	}
}
