package com.example.logtest;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "echo", url="http://localhost:8080/")
public interface EchoClient extends EchoInterface {

}
