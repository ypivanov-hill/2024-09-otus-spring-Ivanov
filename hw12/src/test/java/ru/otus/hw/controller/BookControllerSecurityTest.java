package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import ru.otus.hw.models.User;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Контролер для книг")
@WebMvcTest(value = BookController.class)
@Import(SecurityConfiguration.class)
@TestPropertySource(properties = "mongock.enabled=false")
public class BookControllerSecurityTest {

    private final static String LOGIN_URL = "http://localhost/login";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;


    @DisplayName("должен разрешать доступ к ресурсам")
    @ParameterizedTest(name = "Проверить доступ методом {0} на урл {1} с параметрами {5} и пользователем {2}")
    @MethodSource("getTestArguments")
    void shouldTryToGetAssessToAllUrls(HttpMethod httpMethod,
                                       String urlTemplate,
                                       User user,
                                       ResultMatcher authStatus,
                                       String redirectedUrl,
                                       Object... uriVariables) throws Exception {

        MockHttpServletRequestBuilder request = getRequest(httpMethod, urlTemplate, uriVariables);

        mvc.perform(request
                        .with(user(user.getUsername()).authorities(new SimpleGrantedAuthority("ROLE_" + user.getRoles().get(0))))
                )
                .andExpect(authStatus);

    }

    @DisplayName("должен запрещать доступ к ресурсам")
    @ParameterizedTest(name = "Проверить доступ методом {0} на урл {1} с параметрами {4}")
    @MethodSource("getTestArguments")
    void shouldGetNoAssessToAllUrlsWithOutUser(HttpMethod httpMethod,
                                               String urlTemplate,
                                               User user,
                                               ResultMatcher authStatus,
                                               String redirectedUrl,
                                               Object... uriVariables) throws Exception {

        MockHttpServletRequestBuilder request = getRequest(httpMethod, urlTemplate, uriVariables);

        if (redirectedUrl != null) {
            mvc.perform(request)
                    .andExpectAll(status().isFound())
                    .andExpect(redirectedUrl(redirectedUrl));

        } else {
            mvc.perform(request)
                    .andExpectAll(status().isOk());
        }
    }

    @MethodSource("getTestArguments")
    public static Stream<Arguments> getTestArguments() {
        User user = new User(null, "user", "password", List.of("USER"));
        return Stream.of(
                Arguments.of(HttpMethod.GET, "/login", user, status().isOk(), null, List.of().toArray()),
                Arguments.of(HttpMethod.GET, "/", user, status().isOk(), LOGIN_URL, List.of().toArray()),
                Arguments.of(HttpMethod.DELETE, "/deleteBookById/{id}", user, status().isFound(), LOGIN_URL, List.of("bookId").toArray()),
                Arguments.of(HttpMethod.POST, "/edit", user, status().isFound(), LOGIN_URL, List.of().toArray()),
                Arguments.of(HttpMethod.GET, "/edit/{id}", user, status().isOk(), LOGIN_URL, List.of("bookId").toArray()),
                Arguments.of(HttpMethod.GET, "/create", user, status().isOk(), LOGIN_URL, List.of().toArray())
        );
    }

    private static MockHttpServletRequestBuilder getRequest (HttpMethod httpMethod,
                                                             String urlTemplate,
                                                             Object... uriVariables) {
        MockHttpServletRequestBuilder request = null;
        if (HttpMethod.GET.equals(httpMethod)) {
            request = get(urlTemplate, uriVariables);
        } else if (HttpMethod.POST.equals(httpMethod)) {
            request = post(urlTemplate, uriVariables);
        } else if (HttpMethod.DELETE.equals(httpMethod)) {
            request = delete(urlTemplate, uriVariables);
        } else {
            fail("Unsupported method!");
        }
        return request;
    }
}
