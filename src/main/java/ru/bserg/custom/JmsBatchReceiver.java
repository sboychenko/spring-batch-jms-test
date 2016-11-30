package ru.bserg.custom;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by SBoichenko on 30.11.2016.
 *
 * Another example of BatchReadJms
 */
public class JmsBatchReceiver {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${batchSize}")
    private int batchSize;

    @Value("${queueOut}")
    private String queue;


    @Scheduled(initialDelay = 5000L, fixedRateString = "${restartJobDelay}")
    public void recive() {
        System.out.println("Start receive!");

        List<Object> list = new ArrayList<Object>();


        Object result = null;
        do {
            result = jmsTemplate.receiveAndConvert();
            if (result != null) {
                list.add(result);
            }
            sendBatch(list, batchSize);
        } while (result != null);
        sendBatch(list, 1);

    }

    private void sendBatch(List<Object> list, int batchSize) {
        if (list.size() >= batchSize) {
            jmsTemplate.send(
                    new ActiveMQQueue(queue),
                    session -> {
                        String s = "Size: " + list.size() + "\n" + list.stream().map(Object::toString).collect(Collectors.joining(" | "));
                        return session.createTextMessage(s);
                    });
            System.out.println("Send batch size: " + list.size());
            list.clear();
        }
    }
}
