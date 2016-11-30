package ru.bserg.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import ru.bserg.custom.JmsBatchReceiver;
import ru.bserg.JmsSender;

import javax.jms.ConnectionFactory;

/**
 * Created by SBoichenko on 30.11.2016.
 */
@Configuration
@EnableJms
public class JmsConfiguration {

    @Value("${queue}")
    private String queue;

    @Value("${brokerUrl}")
    private String brokerUrl;

    @Bean
    public ActiveMQQueue inputQueue() {
        return new ActiveMQQueue(queue);
    }

    /*@Bean
    public DefaultMessageListenerContainer mqListenerContainer() {
        DefaultMessageListenerContainer bean = new DefaultMessageListenerContainer();
        bean.setDestination(inputQueue());
        //bean.setMessageListener();
        bean.setConnectionFactory(connectionFactory());
        bean.setAcceptMessagesWhileStopping(false);
        bean.setSessionTransacted(true);
        bean.setConcurrentConsumers(10);
        bean.setMaxMessagesPerTask(1);
        bean.setIdleConsumerLimit(5);
        bean.setIdleTaskExecutionLimit(5);
        bean.setReceiveTimeout(5000);
        return bean;
    }*/

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        jmsTemplate.setDefaultDestination(inputQueue());
        jmsTemplate.setReceiveTimeout(1000);
        return jmsTemplate;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerUrl);

        CachingConnectionFactory bean = new CachingConnectionFactory(activeMQConnectionFactory);
        bean.setSessionCacheSize(5);
        return bean;
    }


    @Bean
    public JmsSender sender() {
        JmsSender sender = new JmsSender();
        sender.setCount(1);
        return sender;
    }


    /*@Bean
    public JmsBatchReceiver batchReceiver() {
        JmsBatchReceiver batchReceiver = new JmsBatchReceiver();
        return batchReceiver;
    }*/
}
