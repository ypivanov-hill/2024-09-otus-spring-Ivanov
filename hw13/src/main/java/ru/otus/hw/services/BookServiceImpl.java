package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookCompliteDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookConverter bookConverter;

    private final AclServiceWrapperService aclServiceWrapperService;

    @Transactional(readOnly = true)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PreAuthorize("canRead(#id, T(ru.otus.hw.dto.BookDto))")
    @Override
    public Optional<BookDto> findById(long id) {
        Optional<Book>  book = bookRepository.findById(id);
        return book.map(bookConverter::bookToDto);
    }

    @Transactional(readOnly = true)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Override
    public List<BookCompliteDto> findAll() {
        List<Book>  books = bookRepository.findAll();
        return books.stream().map(bookConverter::bookToCompliteDto).toList();
    }

    @Override
    @PreAuthorize("canWrite(#id, T(ru.otus.hw.dto.BookDto))")
    public void save(Long id, BookDto bookDto) {
        if (id == null || id == 0) {
            insert(bookDto.getTitle(), bookDto.getAuthorId(), new HashSet<>(bookDto.getGenreIds()));
        } else {
            update(id, bookDto.getTitle(), bookDto.getAuthorId(), new HashSet<>(bookDto.getGenreIds()));
        }
    }

    @Transactional
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Override
    public BookDto insert(String title, long authorId, Set<Long> genresIds) {
        BookDto returnedBook = save(0, title, authorId, genresIds);
        aclServiceWrapperService.createAllPermission(returnedBook);
        return returnedBook;
    }

    @Transactional
    @PreAuthorize("canWrite(#id, T(ru.otus.hw.dto.BookDto))")
    @Override
    public BookDto update(long id, String title, long authorId, Set<Long> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Transactional
    @PreAuthorize("canDelete(#id, T(ru.otus.hw.dto.BookDto))")
    @Override
    public void deleteById(long id) {
        aclServiceWrapperService.deletePermission("ru.otus.hw.dto.BookDto", id);
        bookRepository.deleteById(id);
    }


    private BookDto save(long id, String title, long authorId, Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findAllByIdIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new Book(id, title, author, genres);
        return bookConverter.bookToDto(bookRepository.save(book));
    }
}
