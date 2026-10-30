package com.rmq.consumer.service;

import com.rmq.consumer.dto.QueueMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final ObjectMapper objectMapper;

    @RabbitListener(containerFactory = "listenerContainerFactory", queues = "${rabbitmq.queuename}")
    public void consumerMessage(Message message){

        String jsonMessage = rmqMessageToString(message);

        QueueMessage queueMessage = jsonObject(jsonMessage, QueueMessage.class);

        System.out.println("Message: " + queueMessage.toString());
    }

    private String rmqMessageToString(Message message){
        return new String(message.getBody(), StandardCharsets.UTF_8);
    }

    private  <T> T jsonObject(String jsonString, Class<T> clazz){
        return objectMapper.readValue(jsonString, clazz);
    }
}
