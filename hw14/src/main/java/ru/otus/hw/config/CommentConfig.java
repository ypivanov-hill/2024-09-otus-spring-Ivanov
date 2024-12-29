package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
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
import ru.otus.hw.models.in.Comment;
import ru.otus.hw.models.out.CommentNew;
import ru.otus.hw.services.CommentService;

import javax.sql.DataSource;
import java.util.Map;

import static ru.otus.hw.config.JobConfig.CHUNK_SIZE;

@Slf4j
@Configuration
public class CommentConfig {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private JobRepository jobRepository;

    @Bean
    public Step transformCommentStep(ItemReader<Comment> reader, JdbcBatchItemWriter<CommentNew> writer,
                                     ItemProcessor<Comment, CommentNew> itemProcessor) {
        return new StepBuilder("transformCommentStep", jobRepository)
                .<Comment, CommentNew>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .listener(new ChunkListener() {
                    public void beforeChunk(@NonNull ChunkContext chunkContext) {
                        log.info("Начало пачки комментариев ");
                    }

                    public void afterChunk(@NonNull ChunkContext chunkContext) {
                        log.info("Конец пачки  комментариев");
                    }

                    public void afterChunkError(@NonNull ChunkContext chunkContext) {
                        log.info("Ошибка пачки");
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public MongoPagingItemReader<Comment> readerComment(MongoTemplate template) {
        Map<String, Sort.Direction> sorts = Map.of("id", Sort.Direction.ASC);
        return new MongoPagingItemReaderBuilder<Comment>()
                .name("сommentItemReader")
                .template(template)
                .jsonQuery("{}")
                .targetType(Comment.class)
                .pageSize(10)
                .sorts(sorts)
                .build();
    }

    @Bean
    public ItemProcessor<Comment, CommentNew> processorComment(CommentService commentService) {
        return commentService::getCommentNew;
    }

    @Bean
    public JdbcBatchItemWriter<CommentNew> writerJdbcComment(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CommentNew>()
                .dataSource(dataSource)
                .sql("insert into comments (id, book_id, text) values (:id, :bookId, :text)")
                .beanMapped()
                .build();
    }
}
