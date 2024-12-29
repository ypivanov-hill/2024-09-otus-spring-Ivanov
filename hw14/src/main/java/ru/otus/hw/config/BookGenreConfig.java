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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.models.out.BooksGenresNew;
import ru.otus.hw.services.MappingService;

import javax.sql.DataSource;

import static ru.otus.hw.config.JobConfig.CHUNK_SIZE;

@Slf4j
@Configuration
public class BookGenreConfig {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private JobRepository jobRepository;

    @Bean
    public Step transformBookGenreStep(ItemReader<BooksGenresNew> reader, JdbcBatchItemWriter<BooksGenresNew> writer,
                                     ItemProcessor<BooksGenresNew, BooksGenresNew> itemProcessor) {
        return new StepBuilder("transformBookGenreStep", jobRepository)
                .<BooksGenresNew, BooksGenresNew>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .listener(new ChunkListener() {
                    public void beforeChunk(@NonNull ChunkContext chunkContext) {
                        log.info("Начало пачки комментарий - книга ");
                    }

                    public void afterChunk(@NonNull ChunkContext chunkContext) {
                        log.info("Конец пачки  комментарий - книга");
                    }

                    public void afterChunkError(@NonNull ChunkContext chunkContext) {
                        log.info("Ошибка пачки");
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<BooksGenresNew> readerBookGenre(MappingService mappingService) {
        return new ListItemReader<>(mappingService.getBookToGenreList());
    }

    @Bean
    public ItemProcessor<BooksGenresNew, BooksGenresNew> processorBookGenre() {
        return new ItemProcessor<>() {
            @Override
            public BooksGenresNew process(BooksGenresNew booksGenresNew) {
                return booksGenresNew;
            }
        };
    }

    @Bean
    public JdbcBatchItemWriter<BooksGenresNew> writerJdbcBookGenre(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<BooksGenresNew>()
                .dataSource(dataSource)
                .sql("insert into books_genres ( book_id, genre_id) values (:bookId, :genreId)")
                .beanMapped()
                .build();
    }

}
