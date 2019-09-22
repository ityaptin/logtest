package com.example.logtest;

import com.example.logtest.models.UsernamePasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.text.MessageFormat;

@RestController
@RequestMapping("")
@Slf4j
public class EchoController implements EchoInterface {

    private final EchoClient echoClient;

    @Autowired
    public EchoController(EchoClient echoClient) {
        this.echoClient = echoClient;
    }


    @GetMapping("/ping")
    public String ping() {
        log.info("[GET] /ping");
        return "pong";
    }


    @GetMapping("/echo")
    @Override
    public String echo(@RequestParam("req") @NotBlank String req) {
        log.info("[GET] /echo");
        return req;
    }

    @GetMapping("/feign")
    public String feign(@RequestParam("req") @NotBlank String req) {
        log.info("[GET] /feign");
        return echoClient.echo(req);
    }

    @PostMapping("/sensitive")
    public String sensitive(@RequestBody UsernamePasswordRequest req) {
        log.info("[POST] /sensitive");
        return MessageFormat.format("Request from user {0} was processed", req.getUsername());
    }

    @PostMapping("/error")
    public String error(@RequestBody UsernamePasswordRequest req) {
        log.info("[POST] /sensitive");
        throw new RuntimeException("Issue during parsing");
    }
}
