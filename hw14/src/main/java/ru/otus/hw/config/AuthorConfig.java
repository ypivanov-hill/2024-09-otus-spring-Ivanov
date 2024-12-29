package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.models.in.Author;
import ru.otus.hw.models.out.AuthorNew;
import ru.otus.hw.services.AuthorService;

import javax.sql.DataSource;
import java.util.Map;

import static ru.otus.hw.config.JobConfig.CHUNK_SIZE;

@Slf4j
@Configuration
public class AuthorConfig {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private JobRepository jobRepository;

    @Bean
    public Step transformAuthorStep(ItemReader<Author> reader, JdbcBatchItemWriter<AuthorNew> writer,
                                    ItemProcessor<Author, AuthorNew> itemProcessor) {
        return new StepBuilder("transformAuthorStep", jobRepository)
                .<Author, AuthorNew>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .listener(new ChunkListener() {
                    public void beforeChunk(@NonNull ChunkContext chunkContext) {
                        log.info("Начало пачки авторы");
                    }

                    public void afterChunk(@NonNull ChunkContext chunkContext) {
                        log.info("Конец пачки  авторы ");
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public MongoPagingItemReader<Author> readerAuthor(MongoTemplate template) {
        Map<String, Sort.Direction> sorts = Map.of("id", Sort.Direction.ASC);
        var reader =  new MongoPagingItemReaderBuilder<Author>()
                .name("authorItemReader")
                .template(template)
                .jsonQuery("{}")
                .targetType(Author.class)
                .pageSize(100)
                .sorts(sorts)
                .build();
        reader.setSaveState(Boolean.FALSE);
        return reader;
    }

    @Bean
    public ItemProcessor<Author, AuthorNew> processorAuthor(AuthorService authorService) {
        return authorService::getAuthorNew;
    }

    @Bean
    public JdbcBatchItemWriter<AuthorNew> writerJdbcAuthor(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<AuthorNew>()
                .dataSource(dataSource)
                .sql("insert into authors (id, full_name) values (:id, :fullName)")
                .beanMapped()
                .build();
    }
}
