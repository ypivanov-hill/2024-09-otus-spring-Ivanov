package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.services.MappingService;

@Slf4j
@Configuration
public class JobConfig {

    public static final int CHUNK_SIZE = 10;

    public static final String IMPORT_JOB_NAME = "importJob";

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private MappingService mappingService;

    @Bean
    public Job importJob(Flow splitFlow,
                         Step transformBookStep,
                         Step transformCommentStep,
                         Step transformBookGenreStep,
                         Step cleanUpStep) {
        return new JobBuilder(IMPORT_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(splitFlow)
                .next(transformBookStep)
                .next(transformCommentStep)
                .next(transformBookGenreStep)
                .next(cleanUpStep)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(@NonNull JobExecution jobExecution) {
                        log.info("Начало job");
                    }

                    @Override
                    public void afterJob(@NonNull JobExecution jobExecution) {
                        log.info("Конец job");
                    }
                })
                .build();
    }


    @Bean
    public MethodInvokingTaskletAdapter cleanUpTasklet() {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();

        adapter.setTargetObject(mappingService);
        adapter.setTargetMethod("cleanUp");

        return adapter;
    }

    @Bean
    public Step cleanUpStep() {
        return new StepBuilder("cleanUpStep", jobRepository)
                .tasklet(cleanUpTasklet(), platformTransactionManager)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    public Flow flowAuthor(Step transformAuthorStep) {
        return new FlowBuilder<SimpleFlow>("flowAuthor")
                .start(transformAuthorStep)
                        .build();
    }

    @Bean
    public Flow flowGenre(Step transformGenreStep) {
        return new FlowBuilder<SimpleFlow>("flowGenre")
                .start(transformGenreStep)
                .build();
    }

    @Bean
    public Flow splitFlow(Step transformAuthorStep, Step transformGenreStep) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(taskExecutor())
                .add(flowAuthor(transformAuthorStep), flowGenre(transformGenreStep))
                .build();
    }

}
