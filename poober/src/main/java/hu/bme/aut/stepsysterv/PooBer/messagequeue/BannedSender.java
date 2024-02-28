package hu.bme.aut.stepsysterv.PooBer.messagequeue;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BannedSender {
    Logger logger = LoggerFactory.getLogger(BannedSender.class);

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange directBanned;

    public void send(long uid, Action type) {
        JsonObject msg = new JsonObject();
        msg.addProperty("action", type.label);
        msg.addProperty("uid", uid);
        template.convertAndSend(directBanned.getName(), "banned", msg.toString());


        logger.info("USER with uid {} was {} - message was sent to the AMQP server", uid, type.label);
    }
}
