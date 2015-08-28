package com.github.lzenczuk.rmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author lzenczuk 28/08/2015
 */
public class ClientMain {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(Params.QUEUE_NAME, false, false, false, null);

        for(int x=0;x<10;x++) {
            channel.basicPublish("", Params.QUEUE_NAME, null, ("Message number "+x).getBytes());
            System.out.println("Message sent");
        }

        channel.close();
        connection.close();
    }
}
