package hu.bme.aut.stepsysterv.PooBer.users;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * JavaMailer contains configurations to provide backend with e-mail sending tools
 * We use Google's public SMTP server, which is free for every address (up until 100 mails per day)
 * We created a Gmail account just for this application: pooberapplication@gmail.com
 */

@Configuration
public class JavaMailer {

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("pooberapplication@gmail.com");
        mailSender.setPassword("Schbitches413");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
