package com.student_mng.student_management.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableScheduling
public class BatchSchedulingConfig {
    
    private final JobLauncher jobLauncher;
    private final Job attendanceReportJob;

    public BatchSchedulingConfig(JobLauncher jobLauncher, Job attendanceReportJob) {
        this.jobLauncher = jobLauncher;
        this.attendanceReportJob = attendanceReportJob;
    }

    @Scheduled(cron = "0 0 1 1 * ?") // Run at 1 AM on 1st day of every month
    public void runMonthlyAttendanceReport() throws Exception {
        JobParameters params = new JobParametersBuilder()
            .addDate("date", new Date())
            .toJobParameters();
            
        jobLauncher.run(attendanceReportJob, params);
    }
}
