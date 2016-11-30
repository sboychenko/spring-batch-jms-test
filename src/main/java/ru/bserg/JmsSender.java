package ru.bserg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by SBoichenko on 30.11.2016.
 */
public class JmsSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    private static int count = 1;


    @Scheduled(initialDelay = 10000L, fixedRateString = "${sendJmsDelay}")
    public void main() {

        for (int i = 1; i <= count; i++) {
            final int j = i;
            getJmsTemplate().send(getJmsTemplate().getDefaultDestination(), session -> session.createTextMessage("message "+ j));
        }
        System.out.println("sended  " + count + " jms");
    }

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
