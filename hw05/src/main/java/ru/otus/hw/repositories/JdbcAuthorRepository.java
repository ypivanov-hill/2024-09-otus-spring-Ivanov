package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Author> findAll() {
        return jdbc.query("select a.id, a.full_name from authors a", new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        String sql = "select a.id, a.full_name from authors a where a.id = :id";
        Author author;
        try {
            author = jdbc.queryForObject(sql, Map.of("id",id), new AuthorRowMapper());
        } catch (DataAccessException  exception) {
            return Optional.empty();
        }
        return Optional.of(author);
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            return new Author(rs.getLong("id"), rs.getString("full_name"));
        }
    }
}
