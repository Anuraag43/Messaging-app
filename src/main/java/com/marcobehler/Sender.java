package com.marcobehler;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.TimeoutException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Sender {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory= new ConnectionFactory();

//        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

        try(Connection connection = factory.newConnection()){
            Channel channel = connection.createChannel();
            channel.queueDeclare("ActiveQueue", false, false, false, null);


            for(int i = 0; i<2000;  i++){

                long ms = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
                Date resultDate = new Date(ms);

                String message = String.format("is this the matrix? %s ", sdf.format(resultDate) );

                channel.basicPublish("", "ActiveQueue", false, null, message.getBytes());
                System.out.println("!!! Yes, Message has been sent " + sdf.format(resultDate));

                Thread.sleep(3000);

//               Timer time = new Timer();
//               time.wait(2000);

            }
        }
    }
}

// Sample working day 7:45 PM to 7:50 PM --> To mimic our 9 AM to 5 PM hours.

// We are starting in working day and will transition to
//end of day at 7:50

// We gave a delay of 60 seconds, after the end of day (7:50)
//we should start seeing msgs in OffHour queue once the give delay is met!.

// at 7:50 we should see diff as our working days ended
// and there is msg spike in ONLY exchange

// In Offhour queue we will see msgs after the set delay is met
// Let us stop sender, after sometime we should see decrease in exchange msg count

//All messages delayed in exchange have reached to OffHour Queue.

// End