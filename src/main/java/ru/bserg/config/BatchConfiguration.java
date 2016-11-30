package ru.bserg.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.jms.JmsItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import ru.bserg.RunScheduler;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by SBoichenko on 30.11.2016.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("${batchSize}")
    private int batchSize;

    @Bean
    public JmsItemReader reader() {
        JmsItemReader<Message> reader = new JmsItemReader<>();
        reader.setJmsTemplate(jmsTemplate);
        reader.setItemType(Message.class);
        return reader;
    }


    @Bean
    public ItemProcessor processor() {
        return new JmsProcessor();
    }

    @Bean
    public StringItemWriter writer() {
        return new StringItemWriter();
    }


    @Bean
    public Job testJob() {
        return jobBuilderFactory.get("testJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Message, String> chunk(batchSize)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }


    @Bean
    RunScheduler scheduler() {
        RunScheduler scheduler = new RunScheduler();
        scheduler.setJob(testJob());
        return scheduler;
    }

    class JmsProcessor implements ItemProcessor<Message, String> {

        @Override
        public String process(Message message) throws Exception {
            System.out.println("Process: " + message);
            return messageToString(message);
        }

        private String messageToString(Message m) {
            try {
                TextMessage tm = (TextMessage)m;
                return m.getJMSMessageID() + " [" + tm.getText() + "]";
            } catch (Exception e) {
                return e.getMessage();
            }
        }
    }

    class StringItemWriter implements ItemWriter<String> {

        @Override
        public void write(List<? extends String> list) throws Exception {

            System.out.println("Get batch size: " + list.size());


            System.out.println("> " + list.stream().collect(Collectors.joining(" | ")));
        }
    }



}
