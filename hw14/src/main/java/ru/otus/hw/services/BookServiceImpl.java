package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Book;
import ru.otus.hw.models.in.Genre;
import ru.otus.hw.models.out.BookNew;
import ru.otus.hw.repositories.BookRepository;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private static final String SEQUENCE_NAME = "BOOKS_SEQ";

    private final MappingService mappingService;

    private final SequenceValueService sequenceValueService;

    private final BookRepository bookRepository;

    private ConcurrentLinkedQueue<Long> reservedIds;

    @Override
    public BookNew getBookNew(Book book) {
        log.debug("Book before count of Id {}", reservedIds.size());
        if (reservedIds.isEmpty()) {
            throw new RuntimeException("There is no id to create new rows");
        }
        Long currentId  = reservedIds.poll();
        log.debug("Book after count of Id {}", reservedIds.size());
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

        reservedIds = sequenceValueService.getSequenceValuesQueue(bookRepository.count(), SEQUENCE_NAME);
        log.debug("Book count of Id {}", reservedIds.size());
    }
}
