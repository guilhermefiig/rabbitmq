package com.rmq.producer.service;

import com.rmq.producer.dto.QueueMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.queuename}")
    private String queueName;

    public void publishTextMessage(String message){
        rabbitTemplate.convertAndSend(queueName, message);
    }

    public void publishJsonMessage(String message) {

        QueueMessage queueMessage = jsonObject(message, QueueMessage.class);

        JsonNode jsonNode = objecToJsonNode(queueMessage);

        rabbitTemplate.convertAndSend(queueName, jsonNode);
    }

    private JsonNode objecToJsonNode(Object jsonObject){
        return objectMapper.valueToTree(jsonObject);
    }

    private <T> T jsonObject(String jsonString, Class<T> clazz){
        return objectMapper.readValue(jsonString, clazz);
    }
}
