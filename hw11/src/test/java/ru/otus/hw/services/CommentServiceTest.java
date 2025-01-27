
package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.controllers.AuthorController;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.controllers.GenreController;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConvertor;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Сервис для комментариев должен")
@WebFluxTest(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = BookController.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CommentController.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = GenreController.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AuthorController.class)
})
@Import({CommentServiceImpl.class,
        BookConverter.class,
        AuthorConverter.class,
        GenreConverter.class,
        CommentConvertor.class})
@TestPropertySource(properties = "mongock.enabled=false")
class CommentServiceTest {

    private static final String NEW_COMMENT_TEXT = "BookComment_X";

    private Author author = new Author("AuthorId1", "Author 1 FullName");
    private List<Genre> genres = List.of(new Genre("GenreId1", "Genre1"),
            new Genre("GenreId2", "Genre2"),
            new Genre("GenreId3", "Genre3"),
            new Genre("GenreId4", "Genre4"));

    private Book book = new Book("BoolId1", "TestBook1", author, List.of(genres.get(0), genres.get(1)));

    private List<Comment> comments = List.of(new Comment("CommentId1", "Comment 1", book),
            new Comment("CommentId2", "Comment 2", book));

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookConverter bookConverter;

    @DisplayName("возвращать комментарий и книгу по его id")
    @Test
    void shouldFindById() {
        var expectedComment = comments.get(0);
        when(commentRepository.findById(expectedComment.getId())).thenReturn(Mono.just(expectedComment));

        Mono<CommentDto> returnedComment = commentService.findById(expectedComment.getId());

        StepVerifier
                .create(returnedComment)
                .assertNext(comment -> {
                            assertThat(comment)
                                    .isNotNull()
                                    .hasFieldOrPropertyWithValue("text", expectedComment.getText())
                                    .extracting("book")
                                    .hasFieldOrPropertyWithValue("title", expectedComment.getBook().getTitle());
                        }
                )
                .expectComplete()
                .verify();

    }

    @DisplayName("находить все комментарии по id книги")
    @Test
    void shouldFindAllCommentsByBookId() {
        Flux<Comment> commentsFlux = Flux
                .fromIterable(comments)
                .log();

        when(commentRepository.findByBookId(book.getId())).thenReturn(commentsFlux);

        Flux<CommentDto> expectedComments = commentService.findByBookId(book.getId());

        StepVerifier
                .create(expectedComments)
                .expectNextCount(comments.size())
                .thenConsumeWhile(commentDto -> book.getTitle().equals(commentDto.getBook().getTitle()))
                .expectComplete()
                .verify();
    }

    @DisplayName("добавлять новые комментарии к книгам")
    @Test
    void shouldInsertComment() {

        when(bookRepository.findById(book.getId()))
                .thenReturn(Mono.just(book));

        when(bookRepository.findById(book.getId()))
                .thenReturn(Mono.just(book));
        Comment firstComment = comments.get(0);
        firstComment.setText(NEW_COMMENT_TEXT);
        Comment newComment = firstComment;
        newComment.setId(null);

        when(commentRepository.save(newComment))
                .thenReturn(Mono.just(firstComment));

        Mono<CommentDto> expectedComment = commentService.insert(NEW_COMMENT_TEXT, bookConverter.bookToDto(book));

        StepVerifier
                .create(expectedComment)
                .assertNext(comment -> {
                            assertThat(comment).isNotNull()
                                    .hasFieldOrPropertyWithValue("text", newComment.getText());
                        }
                )
                .expectComplete()
                .verify();
    }

    @DisplayName("изменять комментарии")
    @Test
    void shouldUpdateComment() {

        when(bookRepository.findById(book.getId()))
                .thenReturn(Mono.just(book));
        Comment expectedComment = comments.get(0);

        when(commentRepository.findById(expectedComment.getId())).thenReturn(Mono.just(expectedComment));

        expectedComment.setText(NEW_COMMENT_TEXT);

        when(commentRepository.save(expectedComment))
                .thenReturn(Mono.just(expectedComment));

        Mono<CommentDto> returnedComment = commentService.update(expectedComment.getId(),
                expectedComment.getText(),
                bookConverter.bookToDto(expectedComment.getBook()));

        StepVerifier
                .create(returnedComment)
                .assertNext(comment -> assertThat(comment).isNotNull())
                .expectComplete()
                .verify();

    }

    @DisplayName("удалять комментарии по id")
    @Test
    void shouldDeleteCommentById() {
        var expectedComment = comments.get(0);

        when(commentRepository.deleteById(expectedComment.getId())).thenReturn(Mono.empty());

        var deleteIdMono = commentService.deleteById(expectedComment.getId());
        StepVerifier
                .create(deleteIdMono)
                .assertNext(comment -> {
                    assertThat(comment).isNotNull().isEqualTo(expectedComment.getId());
                })
                .expectComplete()
                .verify();
    }

    @DisplayName("должен отображать автора")
    @Test
    void shouldFindAuthorInBookById() {
        var expectedComment = comments.get(0);

        when(commentRepository.findById(expectedComment.getId())).thenReturn(Mono.just(expectedComment));

        Mono<CommentDto> returnedComment = commentService.findById(expectedComment.getId());

        StepVerifier
                .create(returnedComment)
                .assertNext(comment -> {
                    assertThat(comment).isNotNull();

                    assertThat(comment.getBook())
                            .isNotNull()
                            .extracting(BookDto::getAuthor)
                            .extracting(AuthorDto::getId)
                            .hasToString(comment.getBook().getAuthor().getId());
                })
                .expectComplete()
                .verify();
    }

    @DisplayName("не должен отображать жанры")
    @Test
    void shouldFindNoneGenresInBook() {
        var expectedComment = comments.get(0);

        when(commentRepository.findById(expectedComment.getId())).thenReturn(Mono.just(expectedComment));

        Mono<CommentDto> returnedComment = commentService.findById(expectedComment.getId());

        StepVerifier
                .create(returnedComment)
                .assertNext(comment -> {
                    assertThat(comment).isNotNull();

                    assertThat(comment)
                            .extracting(CommentDto::getBook)
                            .isNotNull()
                            .extracting(BookDto::getGenres)
                            .isNotNull();
                    assertThat(comment.getBook().getGenres())
                            .isEmpty();
                })
                .expectComplete()
                .verify();
    }

    @DisplayName("должен выдавать ошибку если книги нет ")
    @Test
    void shouldReturnExceptionWhenBookIsNotFound() {

        var expectedComment = comments.get(0);

        when(commentRepository.findById(expectedComment.getId())).thenReturn(Mono.just(expectedComment));

        when(bookRepository.findById(book.getId()))
                .thenReturn(Mono.empty());

        Mono<CommentDto> commentDtoMono = commentService.update(expectedComment.getId(), NEW_COMMENT_TEXT, bookConverter.bookToDto(book));

        StepVerifier
                .create(commentDtoMono)
                .expectError()
                .verify();
    }
}
