package hu.bme.aut.stepsysterv.PooBer.messagequeue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    @Bean
    public DirectExchange directBanned() {
        return new DirectExchange("pb.banned");
    }

    @Bean
    public DirectExchange directInvoice() {
        return new DirectExchange("pb.invoice");
    }

    @Bean
    public DirectExchange directTopup() {
        return new DirectExchange("pb.topup");
    }

    @Bean
    public DirectExchange directRegistration() {
        return new DirectExchange("pb.registration");
    }

    @Bean
    public DirectExchange directTest(){
        return new DirectExchange("pb.test");
    }

    @Bean
    public Queue queueBanned() {
        return new Queue("bannedq");
    }

    @Bean
    public Queue queueInvoice() {
        return new Queue("invoiceq");
    }

    @Bean
    public Queue queueTopup() {
        return new Queue("topupq");
    }

    @Bean
    public Queue queueRegistration() {
        return new Queue("registrationq");
    }

    @Bean
    public Queue queueTest(){
        return new AnonymousQueue();
    }

    @Bean
    public Binding binding1a(DirectExchange directInvoice, Queue queueInvoice) {
        return BindingBuilder.bind(queueInvoice)
                .to(directInvoice)
                .with("invoice");
    }

    @Bean
    public Binding binding1b(DirectExchange directTopup, Queue queueTopup) {
        return BindingBuilder.bind(queueTopup)
                .to(directTopup)
                .with("topup");
    }

    @Bean
    public Binding binding1c(DirectExchange directRegistration, Queue queueRegistration) {
        return BindingBuilder.bind(queueRegistration)
                .to(directRegistration)
                .with("registration");
    }

    @Bean
    public Binding binding1d(DirectExchange directTest, Queue queueTest){
        return BindingBuilder.bind(queueTest)
                .to(directTest)
                .with("test");
    }

    @Bean
    public Binding binding1e(DirectExchange directBanned, Queue queueBanned){
        return BindingBuilder.bind(queueBanned)
                .to(directBanned)
                .with("banned");
    }
}
