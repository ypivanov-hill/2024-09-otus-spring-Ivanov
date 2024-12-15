package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookCountByGenreDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookConverter bookConverter;

    @Override
    public Optional<BookDto> findById(String id) {
        Optional<Book>  book = bookRepository.findById(id);
        return book.map(bookConverter::bookToDto);
    }

    @Override
    public Optional<BookDto> findByTitleIgnoreCase(String title) {
        Optional<Book>  book = bookRepository.findByTitleIgnoreCase(title);
        return book.map(bookConverter::bookToDto);
    }

    @Override
    public List<BookDto> findAll() {
        List<Book>  books = bookRepository.findAll();
        return books.stream().map(bookConverter::bookToDto).toList();
    }

    @Override
    public BookDto insert(String title, String authorId, Set<String> genreIds) {
        return save(null, title, authorId, genreIds);
    }

    @Override
    public BookDto update(String id, String title, String authorId, Set<String> genreIds) {
        return save(id, title, authorId, genreIds);
    }

    @Override
    public BookDto update(BookDto bookDto) {
        return save(bookDto);
    }

    @Override
    public void deleteById(String id) {
        bookRepository.deleteBookById(id);
    }

    @Override
    public void deleteByTitle(String title) {
        bookRepository.deleteBookByTitle(title);
    }


    private BookDto save(String id, String title, String authorId, Set<String> genreIds) {
        if (isEmpty(genreIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() ->new EntityNotFoundException("Author with ids %s not found".formatted(genreIds)));
        var genres = genreRepository.findAllById(genreIds);
        if (isEmpty(genres) || genreIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genreIds));
        }

        var book = new Book(id, title, author, genres);
        return bookConverter.bookToDto(bookRepository.save(book));
    }

    private BookDto save(BookDto bookDto) {
        if (bookDto.getGenres().isEmpty()) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(bookDto.getAuthor().getId())
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(bookDto.getAuthor().getFullName())));
        var genres = genreRepository.findAllById(bookDto.getGenres().stream().map(GenreDto::getId).toList());
        if (genres.isEmpty() || bookDto.getGenres().size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(bookDto.getGenres().stream().map(GenreDto::getName)));
        }

        var book = new Book(bookDto.getId(), bookDto.getTitle(), author, genres);
        return bookConverter.bookToDto(bookRepository.save(book));
    }

    public List<BookCountByGenreDto> getBookCountByGenre() {
        return bookRepository.getBookCountByGenre();
    }
}
