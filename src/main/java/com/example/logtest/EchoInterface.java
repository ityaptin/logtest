package com.example.logtest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;

public interface EchoInterface {
    @GetMapping("/echo")
    String echo(@RequestParam("req") @NotBlank String req);
}
