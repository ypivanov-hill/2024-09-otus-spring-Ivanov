package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Book;
import ru.otus.hw.models.in.Genre;
import ru.otus.hw.models.out.BookNew;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final MappingService mappingService;

    private int idValue = 1;

    @Override
    public BookNew getBookNew(Book book) {
        mappingService.putBookIds(idValue, book.getId());

        for (Genre genre : book.getGenres()) {
            mappingService.putBookToGenreIds(idValue, mappingService.getGenreNewIdByOldId(genre.getId()));
        }

        return new BookNew(idValue++, book.getTitle(), mappingService.getAuthorNewIdByOldId(book.getAuthor().getId()));
    }
}
