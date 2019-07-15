package com.example.demo;

import com.example.demo.entity.Hoge;
import com.example.demo.entity.RestService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestServiceImpl implements RestService {

    @Override
    public Hoge getRequest(){
        RestTemplate restTemplate = new RestTemplate();
        Hoge result = restTemplate.getForObject("http://localhost:13003/rest/api/hoge", Hoge.class);
        return result;
    }

    @Override
    public String helloRequest() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity resultStr = restTemplate.getForEntity("http://localhost:13003/rest/api/hello",String.class);
        String result = restTemplate.getForObject("http://localhost:13003/rest/api/hello", String.class);
        return result;
    }
}
