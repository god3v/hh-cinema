package com.cinema.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageService {

    public void send(String message) throws InterruptedException {
        Thread.sleep(500L);
        log.info(message);
    }
}
