package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Book;
import ru.otus.hw.models.in.Genre;
import ru.otus.hw.models.out.BookNew;
import ru.otus.hw.repositories.BookRepository;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final MappingService mappingService;

    private final NamedParameterJdbcOperations jdbc;

    private final BookRepository bookRepository;

    private long startIdValue;

    private long endIdValue;

    @Override
    public BookNew getBookNew(Book book) {
        mappingService.putBookIds(startIdValue, book.getId());

        for (Genre genre : book.getGenres()) {
            mappingService.putBookToGenreIds(startIdValue,
                    mappingService.getGenreNewIdByOldId(genre.getId()));
        }

        return new BookNew(startIdValue++,
                book.getTitle(),
                mappingService.getAuthorNewIdByOldId(book.getAuthor().getId()));
    }

    @Override
    public void reserveSequenceValues() {

        startIdValue = jdbc.queryForObject("VALUES NEXT VALUE FOR BOOKS_SEQ", Map.of(), Integer.class);
        log.debug("Author current id value {}", startIdValue);
        long countAuthors = bookRepository.count();
        endIdValue = startIdValue + countAuthors;
        log.debug("Author end id value {}", endIdValue);
        jdbc.update("ALTER SEQUENCE BOOKS_SEQ restart with :newSeqStart", Map.of("newSeqStart", endIdValue));
    }
}
