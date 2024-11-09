package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {
    private final JdbcOperations jdbc;
    @Override
    public List<Author> findAll() {
        return jdbc.query("select a.id, a.full_name from authors a", new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        return Optional.ofNullable(jdbc.queryForObject("select a from authors where id = :Id", new AuthorRowMapper(), id));
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            return new Author(rs.getLong("id"), rs.getString("full_name"));
        }
    }
}
