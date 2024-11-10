package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(jdbc.query("select b.id, " +
                "b.title , " +
                "b.author_id, " +
                "a.full_name author_name , " +
                "g.id genre_id, " +
                "g.name genre_name " +
                "from books b " +
                "left join authors a on a.id = b.author_id " +
                "left join books_genres bg on bg.book_id = b.id " +
                "left join genres g on g.id = bg.genre_id  " +
                "where b.id = :id", Map.of("id", id), new BookResultSetExtractor()));
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        jdbc.update("delete from books b where b.id = :id ", Map.of("id", id));
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbc.query("select b.id, b.title, a.id author_id, a.full_name " +
                "from books b " +
                "left join authors a on a.id = b.author_id", new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbc.query("select bg.book_id, bg.genre_id from books_genres bg",
                new DataClassRowMapper<>(BookGenreRelation.class));
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        List<Book> updatedBooks = booksWithoutGenres.stream()
                .peek(book -> {
                    var bookGenres = genres.stream().
                            filter(genre -> relations.stream()
                                    .anyMatch(bookGenreRelation -> bookGenreRelation.genreId() == genre.getId()
                                            && bookGenreRelation.bookId() == book.getId())
                            ).collect(Collectors.toList());
                    book.setGenres(bookGenres);
                })
                .toList();
        booksWithoutGenres.clear();
        booksWithoutGenres.addAll(updatedBooks);
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        SqlParameterSource namedParameters = new MapSqlParameterSource("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId());

        jdbc.update("insert into books (title ,author_id) values (:title ,:author_id)", namedParameters, keyHolder);

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        String sql = "update books set title = :title, author_id = :author_id where id = :id";
        var sqlParameters = Map.of("title", book.getTitle(), "author_id",
                book.getAuthor().getId(), "id", book.getId());
        int updateCount = jdbc.update(sql, sqlParameters);

        if (updateCount == 0) {
            throw new EntityNotFoundException("Book for update not found");
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {

         jdbc.getJdbcOperations().batchUpdate("insert into books_genres (book_id, genre_id) values (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, book.getId());
                        ps.setLong(2, book.getGenres().get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return book.getGenres().size();
                    }
                });
    }

    private void removeGenresRelationsFor(Book book) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("book_id", book.getId());
        jdbc.update("delete from books_genres bg where bg.book_id = :book_id", namedParameters);
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));
            Author author = new Author(rs.getLong("author_id"), rs.getString("full_name"));
            book.setAuthor(author);
            return book;
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            while (rs.next()) {
                if (book == null) {
                    book = new Book();
                    book.setId(rs.getLong("id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(new Author(rs.getLong("author_id"), rs.getString("author_name")));
                    book.setGenres(new ArrayList<Genre>());
                }
                Genre genre = new Genre();
                genre.setId(rs.getLong("genre_id"));
                genre.setName(rs.getString("genre_name"));
                book.getGenres().add(genre);
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
