package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Book;
import ru.otus.hw.models.in.Genre;
import ru.otus.hw.models.out.BookNew;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final MappingService mappingService;

    private final NamedParameterJdbcOperations jdbc;

    private final BookRepository bookRepository;

    private List<Long> reservedIds;

    @Override
    public BookNew getBookNew(Book book) {
        log.debug("Book count of Id {}", reservedIds.size());
        if (reservedIds.isEmpty()) {
            throw new RuntimeException("There is no id to create new rows");
        }
        Long currentId  = reservedIds.get(0);
        reservedIds.remove(currentId);
        log.debug("Book count of Id {}", reservedIds.size());
        log.debug("Book currentId {}", currentId);

        mappingService.putBookIds(currentId, book.getId());

        for (Genre genre : book.getGenres()) {
            mappingService.putBookToGenreIds(currentId,
                    mappingService.getGenreNewIdByOldId(genre.getId()));
        }

        return new BookNew(currentId,
                book.getTitle(),
                mappingService.getAuthorNewIdByOldId(book.getAuthor().getId()));
    }

    @Override
    public void reserveSequenceValues() {

        long countAuthors = bookRepository.count();

        log.debug("Book countAuthors {}", countAuthors);

        reservedIds = jdbc.queryForList("select nextval('BOOKS_SEQ') from SYSTEM_RANGE(1, :cnt)",
                Map.of("cnt", countAuthors),
                Long.class);
        log.debug("Book count of Id {}", reservedIds.size());
    }
}
