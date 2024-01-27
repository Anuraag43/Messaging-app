package com.marcobehler;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ConsumerThree {
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

/*

//        Consume with Scheduling- Infinite Loop
                if (isWorkingHour()) {
                    String m = new String(delivery.getBody(), "UTF-8");
                    System.out.println("I just received a message : " + m);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
                } else {
                    System.out.println("Message rejected and being requeued");
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);
                }
*/
//        Consume with Scheduling- Non working hours
                if (isWorkingHour()) {
                    String m = new String(delivery.getBody(), "UTF-8");
                    System.out.println("Message sent for printing : " + m);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
                } else {
                    try {
                        System.out.println("Message delayed by 60 sec and sent to OffHour queue");
                        handleOffHourMessages(delivery);
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
                    } catch (TimeoutException e) {
                        channel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);
                        throw new RuntimeException(e);
                    }
//                    System.out.println("Message Sent to OffHourQueue");
//                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);
                }

            }

        }, new CancelCallback() {
            @Override
            public void handle(String consumerTag) throws IOException {
            }
        });
    }
    private static boolean isWorkingHour() {
        LocalTime startTime = LocalTime.of(19, 45);
        LocalTime endTime = LocalTime.of(19, 50);

        LocalTime currentTime = LocalTime.now();
//        System.out.println("The current time in Scheduler is " + currentTime.getHour());
        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }

    private static void handleOffHourMessages(Delivery delivery) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();


        try(Connection connection = factory.newConnection()){

            Channel channel = connection.createChannel();
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-delayed-type", "direct");

            channel.exchangeDeclare("my-exchange", "x-delayed-message", true, false, args);
            channel.queueDeclare("OffHourQueue", false, false, false, null);

            byte[] messageBodyBytes = delivery.getBody().toString().getBytes("UTF-8");
            Map<String, Object> headers = new HashMap<String, Object>();
            headers.put("x-delay", 60000);
            AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder().headers(headers);
            channel.queueBind("OffHourQueue", "my-exchange", "");


            channel.basicPublish("my-exchange", "", props.build(), messageBodyBytes);


//            String message = Arrays.toString(delivery.getBody());
//            channel.basicPublish("", "OffHourQueue", null, message.getBytes());
        }
//        try(Connection connection = fa)
    }

}


/*

------------------------------------

        2nd Approach

6 PM - 18hr  ==> 9AM
10PM         ==> 13PM

4AM          ==> 10PM

*/
