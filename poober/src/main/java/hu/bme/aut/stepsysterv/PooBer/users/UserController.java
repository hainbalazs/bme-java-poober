package hu.bme.aut.stepsysterv.PooBer.users;

import hu.bme.aut.stepsysterv.PooBer.messagequeue.InvoiceSender;
import hu.bme.aut.stepsysterv.PooBer.users.data.User;
import hu.bme.aut.stepsysterv.PooBer.users.data.UserRepository;
import hu.bme.aut.stepsysterv.PooBer.wcManager.data.Wc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;


/**
 * Rest API provider, Controller of requests
 * All request concerning users should be addressed here
 */

@Controller
@RequestMapping("user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    InvoiceSender invoiceSender;

    @Autowired
    private JavaMailSender mailSender;


    //Endpoint for testing purposes
    @GetMapping("/test")
    public ResponseEntity<String> testEndPoint(){
        logger.info("/user endpoint tested");
        return new ResponseEntity<>("i hope you see this, /user REST API is up!", HttpStatus.OK);
    }

    //Deleting user with id, admins only
    @DeleteMapping("/delete")
    @Secured(User.ROLE_ADMIN)
    public ResponseEntity<String> deleteUser(@RequestParam Long user_id){
        try{
            repository.deleteById(user_id);
        } catch (Exception e){
            e.printStackTrace();
            logger.info("Unsuccessful user deletion");
            new ResponseEntity<>("Unsuccessful deletion of user", HttpStatus.BAD_REQUEST);
        }

        logger.info("Successful user deletion of id:"+user_id);
        return new ResponseEntity<>("user with id:"+user_id+" successfully deleted", HttpStatus.OK);
    }

    //Register requests handled here, validated with javax.validation (see User.java)
    //Also creating balance account for newly registered user
    @PostMapping("/register")
    public ResponseEntity<String> processReg(@Valid  User user){
        try {
            user.setPassword(encoder.encode(user.getPassword()));
            user.setRoles(List.of("ROLE_USER"));
            repository.save(user);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("Unsuccessful user registration");
            return new ResponseEntity<String>("Unsuccesful Registration", HttpStatus.BAD_REQUEST);
        }

        User u = repository.findByName(user.getName());
        invoiceSender.sendRegistration(u.getId());
        logger.info("New user registered: " + u.getName() + " ID: "+ u.getId());

        return new ResponseEntity<String>("Succesful Registration", HttpStatus.OK);
    }

    //Bad request response handler, providing error message
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    //Endpoint which can be called to authenticate user
    //Returns account info on success, error message otherwise
    @GetMapping("/auth")
    public @ResponseBody User authHello(Principal principal) {
        User u = null;
        try{
            u = repository.findByName(principal.getName()); //NULL ellenorzni
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return u;
    }

    //Requesting data of all users, admin only
    @GetMapping("/all")
    @Secured(User.ROLE_ADMIN)
    public @ResponseBody Iterable<User> getAllWcs(){
        logger.info("All user queried");

        return repository.findAll();
    }

    //Request to send an email to address of parameter
    //E-mail will consist of reciever account information
    //Configurations can be seen at JavaMailer
    @PostMapping("/sendmail")
    @Secured(User.ROLE_ADMIN)
    public ResponseEntity<String> userData(Principal principal, @RequestParam Long userId ) {
        User u;
        try{
            u = repository.findById(userId).orElse(null); //NULL ellenorzni
        } catch (NullPointerException e){
            return new ResponseEntity<>("User not found, or not authenticated", HttpStatus.BAD_REQUEST);
        }

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(u.getEmail());
        email.setSubject("Poober - Sending account information");
        email.setText("Your account information are the following:\n" +
                "Name: " + u.getName() +"\n" +
                "E-mail: " + u.getEmail() +"\n" +
                "Password: Haha, not so easy :)");
        mailSender.send(email);

        logger.info("Email sent to user: " + u.getName() + " with e-mail: " + u.getEmail());

        return new ResponseEntity<String>("Email sent to: "+u.getEmail(), HttpStatus.OK);
    }


    //Test endpoint for admin privilage checking
    @GetMapping("/admin_hello")
    @Secured(User.ROLE_ADMIN)
    public ResponseEntity<String> adminHello() {
        return new ResponseEntity<String>("You are an admin", HttpStatus.OK);
    }
}
