package hu.bme.aut.stepsysterv.PooBer.messagequeue;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import hu.bme.aut.stepsysterv.PooBer.billing.BillingAssistant;
import hu.bme.aut.stepsysterv.PooBer.billing.data.Account;
import hu.bme.aut.stepsysterv.PooBer.billing.data.Invoice;
import hu.bme.aut.stepsysterv.PooBer.users.data.User;
import hu.bme.aut.stepsysterv.PooBer.users.data.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class MQListener {

    Logger logger = LoggerFactory.getLogger(MQListener.class);

    private BillingAssistant billingAssistant;

    @Autowired
    public MQListener(BillingAssistant assistant) {
        billingAssistant = assistant;
    }

    @RabbitListener(queues = "#{queueInvoice.name}")
    public void receiveInvoices(String in) {
        Invoice createdInvoice = new Gson().fromJson(in, Invoice.class);
        logger.info("Received invoice from AMQP, issued between users {} and {} for {} Ft.", createdInvoice.getOwnerUID(), createdInvoice.getUserUID(), createdInvoice.getPrice());
        billingAssistant.createInvoice(createdInvoice);
    }

    @RabbitListener(queues = "#{queueRegistration.name}")
    public void receiveRegistrations(String in) {
        Long uid = Long.parseLong(in);
        logger.debug("Registration uid: " + uid);
        Account createdAccount = new Account();
        createdAccount.setUid(uid);
        logger.info("Received new registration request from AMQP, uid: {}.", uid);
        billingAssistant.createAccount(createdAccount);
    }

    @RabbitListener(queues = "#{queueTopup.name}")
    public void receiveTopUps(String in) {
        logger.info("TopUp msg has been received from AMQP: " + in);
        Type listType = new TypeToken<HashMap<String,Long>>(){}.getType();
        Gson gson = new Gson();
        Map<String,Long> myList = gson.fromJson(in, listType);

        long amount = myList.get("amount");
        long uid = myList.get("uid");
        if(amount < 0 || uid < 1){
            logger.error("A topup was received with invalid parameters.");
        }

        logger.info("Received topup request from AMQP, uid: {}, amount {}.", uid, amount);
        billingAssistant.topUp(uid, (int) amount);
    }

    @Autowired
    UserRepository userRepository;

    @RabbitListener(queues = "#{queueBanned.name}")
    public void receiveBan(String in) {
        logger.info("recieveBan function called");
        Type listType = new TypeToken<HashMap<String,String>>(){}.getType();
        Gson gson = new Gson();
        Map<String,String> myList = gson.fromJson(in, listType);
        String action = myList.get("action");
        long uid = Long.parseLong(myList.get("uid"));
        User u = userRepository.findById(uid).orElse(null);
        if(u == null){
            logger.error("No user to ban with uid: " + uid);
        }
        if(action.equals(Action.BANNED.label)){
            u.setBanned(true);
            logger.info(u.getId()+ " userid was banned");
        } else{
            u.setBanned(false);
            logger.info(u.getId()+ " userid was unbanned");
        }
        logger.info(u.getId()+ " userid ban status saved");
        userRepository.save(u);
    }
}
