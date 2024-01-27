package com.marcobehler;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        msgConsume();
    }

    private static void msgConsume() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("ActiveQueue", false, false, false, null);

        LocalTime currentTime = LocalTime.now();
//        System.out.println("The current time in consumer is " + currentTime.getHour());

        channel.basicConsume("ActiveQueue", false, new DeliverCallback() {
            @Override
            public void handle(String s, Delivery delivery) throws IOException {
/*

//        Continuous consume
                    String m = new String(delivery.getBody(), "UTF-8");
                    System.out.println("I just received a message : " + m);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);


*/


//        Consume with Scheduling
                if (isWorkingHour()) {
                    String m = new String(delivery.getBody(), "UTF-8");
                    System.out.println("I just received a message : " + m);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
                } else {
                    System.out.println("Message rejected and being requeued");
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);
                }



            }
        }, new CancelCallback() {
            @Override
            public void handle(String consumerTag) throws IOException {
            }
        });
    }
    private static boolean isWorkingHour() {
        LocalTime startTime = LocalTime.of(13, 30);
        LocalTime endTime = LocalTime.of(14, 00);

        LocalTime currentTime = LocalTime.now();
//        System.out.println("The current time in Scheduler is " + currentTime.getHour());
        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }

}

//------------------------------------
//        2nd Approach
//
//6 PM - 18hr  ==> 9AM
//10PM         ==> 13PM
//
//4AM          ==> 10PM