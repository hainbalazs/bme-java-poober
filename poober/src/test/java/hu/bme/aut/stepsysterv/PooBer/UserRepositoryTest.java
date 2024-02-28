package hu.bme.aut.stepsysterv.PooBer;

import hu.bme.aut.stepsysterv.PooBer.users.data.User;
import hu.bme.aut.stepsysterv.PooBer.users.data.UserRepository;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setEmail("foo@bar.hu");
        user.setName("Foo Bar");
        user.setPassword("pass");

        long sizeBefore = repository.count();
        repository.save(user);
        long sizeAfter = repository.count();

        assertEquals("Count must have been incremented", sizeBefore + 1, sizeAfter);

        List<User> users = repository.findAll();
        assertTrue("findAll() must return saved user", users.contains(user));
    }
    
    /*@After
    public void after1(){
        repository.deleteById(repository.findByName("Foo Bar").getId());
    }*/

    @Test
    public void testDeleteUser(){
        //previously created Foo Bar user used here
        User user = new User();
        user.setEmail("foo@bar.hu");
        user.setName("Foo Bar");
        user.setPassword("pass");

        repository.deleteById(repository.findByName(user.getName()).getId());
        List<User> users = repository.findAll();
        assertFalse("deleted user not in list created by findAll()", users.contains(user));
    }

}
