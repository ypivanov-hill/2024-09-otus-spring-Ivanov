package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import ru.otus.hw.models.in.Author;
import ru.otus.hw.models.in.Book;
import ru.otus.hw.models.in.Genre;
import ru.otus.hw.models.out.AuthorNew;
import ru.otus.hw.models.out.BookNew;
import ru.otus.hw.models.out.GenreNew;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.config.JobConfig.IMPORT_JOB_NAME;

@SpringBootTest
@SpringBatchTest
public class JobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private NamedParameterJdbcOperations jdbc;

    @Autowired
    private MongoOperations mongoTemplate;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @DisplayName("запускать job и проверять результат работы")
    @Test
    void testJob() throws Exception {
        var actualBooks = mongoTemplate.findAll(Book.class);
        var actualAuthors = mongoTemplate.findAll(Author.class);

        assertThat(actualAuthors)
                .isNotEmpty();

        assertThat(actualBooks)
                .isNotEmpty();

        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(IMPORT_JOB_NAME);

        JobParameters parameters = new JobParametersBuilder()
                .addDate("start date", new Date())
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        var expectBooks = jdbc.query("SELECT id, title, author_id FROM books",
                new DataClassRowMapper<>(BookNew.class));

        assertThat(expectBooks)
                .isNotEmpty().
                hasSize(actualBooks.size());

        assertThat(expectBooks.stream().map(BookNew::getTitle))
                .containsExactly(actualBooks.stream().map(Book::getTitle).toArray(String[]::new));

        var expectAuthors = jdbc.query("SELECT id, full_name FROM authors",
                new DataClassRowMapper<>(AuthorNew.class));

        assertThat(expectAuthors)
                .isNotEmpty().
                hasSize(actualAuthors.size());

        assertThat(actualBooks.get(0).getGenres())
                .isNotEmpty();


        var expectGenres = jdbc.query("select " +
                "g.id, " +
                "g.name " +
                "from books b " +
                "left join books_genres bg on bg.book_id = b.id " +
                "left join genres g on g.id = bg.genre_id  " +
                "where b.title = :title", Map.of("title", actualBooks.get(0).getTitle()), new DataClassRowMapper<>(GenreNew.class));

        assertThat(expectGenres)
                .isNotEmpty()
                .hasSize(actualBooks.get(0).getGenres().size());

        assertThat(expectGenres.stream().map(GenreNew::getName))
                .contains(actualBooks.get(0).getGenres().stream().map(Genre::getName).toArray(String[]::new));
    }
}
