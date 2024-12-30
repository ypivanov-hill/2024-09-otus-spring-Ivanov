package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
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
import ru.otus.hw.models.in.Genre;
import ru.otus.hw.models.out.GenreNew;
import ru.otus.hw.services.GenreService;

import javax.sql.DataSource;
import java.util.Map;

import static ru.otus.hw.config.JobConfig.CHUNK_SIZE;

@Slf4j
@Configuration
public class GenreConfig {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private GenreService genreService;

    @Bean
    public Step transformGenreStep(ItemReader<Genre> reader, JdbcBatchItemWriter<GenreNew> writer,
                                  ItemProcessor<Genre, GenreNew> itemProcessor) {
        return new StepBuilder("transformGenreStep", jobRepository)
                .<Genre, GenreNew>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .listener(new ChunkListener() {
                    public void beforeChunk(@NonNull ChunkContext chunkContext) {
                        log.info("Начало пачки жанров ");
                    }

                    public void afterChunk(@NonNull ChunkContext chunkContext) {
                        log.info("Конец пачки  жанров");
                    }

                    public void afterChunkError(@NonNull ChunkContext chunkContext) {
                        log.info("Ошибка пачки");
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public MongoPagingItemReader<Genre> readerGenre(MongoTemplate template) {
        Map<String, Sort.Direction> sorts = Map.of("id", Sort.Direction.ASC);
        return new MongoPagingItemReaderBuilder<Genre>()
                .name("genreItemReader")
                .template(template)
                .jsonQuery("{}")
                .targetType(Genre.class)
                .pageSize(10)
                .sorts(sorts)
                .build();
    }

    @Bean
    public ItemProcessor<Genre, GenreNew> processorGenre(GenreService genreService) {
        return genreService::getGenreNew;
    }

    @Bean
    public JdbcBatchItemWriter<GenreNew> writerJdbcGenre(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<GenreNew>()
                .dataSource(dataSource)
                .sql("insert into genres (id, name) values (:id, :name)")
                .beanMapped()
                .build();
    }

    @Bean
    public MethodInvokingTaskletAdapter beforeGenresTasklet() {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();

        adapter.setTargetObject(genreService);
        adapter.setTargetMethod("reserveSequenceValues");

        return adapter;
    }

    @Bean
    public Step beforeGenresStep() {
        return new StepBuilder("beforeGenresStep", jobRepository)
                .tasklet(beforeGenresTasklet(), platformTransactionManager)
                .build();
    }
}
