package hu.bme.aut.stepsysterv.PooBer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import hu.bme.aut.stepsysterv.PooBer.wcManager.MainController;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;


import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class wcManagerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    private MainController mainController;
    @Test
    public void contextLoads() throws Exception {
        assertThat(mainController).isNotNull();
    }



}
