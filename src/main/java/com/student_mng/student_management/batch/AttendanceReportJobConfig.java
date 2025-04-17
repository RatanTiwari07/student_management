package com.student_mng.student_management.batch;

import com.student_mng.student_management.entity.Attendance;
import com.student_mng.student_management.entity.AttendanceReport;
import com.student_mng.student_management.entity.Student;
import com.student_mng.student_management.repository.AttendanceReportRepository;
import com.student_mng.student_management.repository.AttendanceRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class AttendanceReportJobConfig {

    @Bean
    public Job generateMonthlyAttendanceReport(
            JobRepository jobRepository,
            Step generateReportStep) {
        return new JobBuilder("monthlyAttendanceReport", jobRepository)
                .start(generateReportStep)
                .build();
    }

    @Bean
    public Step generateReportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Student> studentReader,
            ItemProcessor<Student, AttendanceReport> reportProcessor,
            ItemWriter<AttendanceReport> reportWriter) {
        return new StepBuilder("generateReportStep", jobRepository)
                .<Student, AttendanceReport>chunk(100, transactionManager)
                .reader(studentReader)
                .processor(reportProcessor)
                .writer(reportWriter)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Student> studentReader(DataSource dataSource) throws Exception {
        return new JdbcPagingItemReaderBuilder<Student>()
                .name("studentReader")
                .dataSource(dataSource)
                .queryProvider(createQueryProvider(dataSource))
                .rowMapper(new BeanPropertyRowMapper<>(Student.class))
                .pageSize(100)
                .build();
    }

    @Bean
    public PagingQueryProvider createQueryProvider(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("SELECT *");
        provider.setFromClause("FROM student");
        provider.setSortKey("id");
        return provider.getObject();
    }

    @Bean
    public ItemProcessor<Student, AttendanceReport> reportProcessor(
            AttendanceRepository attendanceRepository) {
        return student -> {
            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate endOfMonth = LocalDate.now().withDayOfMonth(
                    LocalDate.now().lengthOfMonth());

            List<Attendance> monthlyAttendance = attendanceRepository
                    .findByStudentAndDateBetween(student, startOfMonth, endOfMonth);

            double percentage = calculateAttendancePercentage(monthlyAttendance);

            return new AttendanceReport(student, percentage, startOfMonth, endOfMonth);
        };
    }

    @Bean
    public ItemWriter<AttendanceReport> reportWriter(
            AttendanceReportRepository reportRepository) {
        return reportRepository::saveAll;
    }

    private double calculateAttendancePercentage(List<Attendance> attendances) {
        if (attendances.isEmpty()) {
            return 0.0;
        }
        long presentCount = attendances.stream()
                .filter(Attendance::isPresent)
                .count();
        return (presentCount * 100.0) / attendances.size();
    }
}