package hu.bme.aut.stepsysterv.PooBer.messagequeue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import hu.bme.aut.stepsysterv.PooBer.billing.data.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvoiceSender {
    Logger logger = LoggerFactory.getLogger(InvoiceSender.class);

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange directInvoice;

    @Autowired
    private DirectExchange directTopup;

    @Autowired
    private DirectExchange directRegistration;

    public void sendInvoice(Invoice invoice) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String msg = gson.toJson(invoice);
        template.convertAndSend(directInvoice.getName(), "invoice", msg);
    }

    public void sendTopup(long uid, int amount) {
        JsonObject msg = new JsonObject();
        msg.addProperty("amount", amount);
        msg.addProperty("uid", uid);
        template.convertAndSend(directTopup.getName(), "topup", msg.toString());
        logger.info("USER with uid {} did a {} topup", uid, amount);
    }


    public void sendRegistration(long uid) {
        template.convertAndSend(directRegistration.getName(), "registration", String.valueOf(uid));
        logger.info("USER with uid {} was banned - message was sent to the AMQP server", uid);
    }


}
