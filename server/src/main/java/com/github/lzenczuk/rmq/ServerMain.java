package com.github.lzenczuk.rmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

/**
 * @author lzenczuk 28/08/2015
 */
public class ServerMain {

    public static void main(String[] args) throws IOException, TimeoutException {

        final Object consumerLock = new Object();

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(Params.QUEUE_NAME, false, false, false, null);

        Consumer consumer = new DefaultConsumer(channel){
            private int messages = 0;

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("Receive message: "+new String(body, Charset.forName("UTF-8")));
                if(messages==6){
                    synchronized (consumerLock) {
                        consumerLock.notify();
                    }
                }

                messages++;

            }
        };

        channel.basicConsume(Params.QUEUE_NAME, consumer);

        synchronized (consumerLock) {
            try {
                System.out.println("Waiting for messages");
                consumerLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Messages consume. Stopping application.");

        channel.close();
        connection.close();

        System.out.println("Application stopped");
    }
}
