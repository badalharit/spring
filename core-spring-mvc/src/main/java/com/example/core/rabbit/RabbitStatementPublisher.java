package com.example.core.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class RabbitStatementPublisher {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root123";
    private static final String VHOST = "/";

    private static final String QUEUE = "statements";

    public boolean publish(String jsonBody) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost(VHOST);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE, true, false, false, null);
            channel.basicPublish("", QUEUE, null, jsonBody.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

