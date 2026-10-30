package com.rmq.producer.controller;

import com.rmq.producer.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producer")
@RequiredArgsConstructor
public class ProducerController {

    private final ProducerService producerService;

    @PostMapping("/messages")
    public ResponseEntity<Void> publishTextMessage(@RequestBody String message) {
        producerService.publishTextMessage(message);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/json")
    public ResponseEntity<Void> publishJson(@RequestBody String message) {
        producerService.publishJsonMessage(message);
        return ResponseEntity.accepted().build();
    }
}
