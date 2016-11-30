package ru.bserg;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * Created by SBoichenko on 30.11.2016.
 */
public class RunScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    private Job job;

    @Scheduled(initialDelay = 10000L, fixedRateString = "${restartJobDelay}")
    public void run() {
        try {

            String dateParam = new Date().toString();
            JobParameters param =
                    new JobParametersBuilder().addString("date", dateParam).toJobParameters();

            System.out.println(dateParam);

            JobExecution execution = jobLauncher.run(job, param);
            System.out.println("Exit Status : " + execution.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
