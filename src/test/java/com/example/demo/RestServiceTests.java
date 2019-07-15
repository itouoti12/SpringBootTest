package com.example.demo;

import com.example.demo.entity.RestService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@TestPropertySource(properties = "server.port=13003")
@SpringBootTest(webEnvironment = DEFINED_PORT)
public class RestServiceTests {

    @Autowired
    private RestService target;


    private WireMockServer server;


    @BeforeEach
    void setUp() {
        server = new WireMockServer();
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    @DisplayName("wiremockDemo")
    void wiring() {

        //GIVEN
        //stub(wireMock)
        configureFor("localhost", 13003);
        server.stubFor(get(urlEqualTo("/rest/api/hello"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "text/plain")
                                .withBody("HelloWorld!"))
        );
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity result = restTemplate.getForEntity("http://localhost:13003/rest/api/hello",String.class);

        //WHEN
        String actual = target.helloRequest();

        //THEN
        assertThat(actual).isEqualTo("HelloWorld!");

    }


}
