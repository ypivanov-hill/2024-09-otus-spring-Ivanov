package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConvertor;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.models.User;
import ru.otus.hw.models.UserRole;
import ru.otus.hw.security.AclConfig;
import ru.otus.hw.security.AclMethodSecurityConfiguration;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AclServiceWrapperServiceImpl;
import ru.otus.hw.services.AuthorServiceImpl;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.CommentServiceImpl;
import ru.otus.hw.services.CommentServiceSecuredImpl;
import ru.otus.hw.services.GenreServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Контролер для комментариев")
@WebMvcTest
@AutoConfigureDataJpa
@Import({SecurityConfiguration.class,
        AclConfig.class,
        AclServiceWrapperServiceImpl.class,
        BookServiceImpl.class,
        BookConverter.class,
        AuthorConverter.class,
        AuthorServiceImpl.class,
        GenreServiceImpl.class,
        GenreConverter.class,
        CommentServiceImpl.class,
        CommentServiceSecuredImpl.class,
        CommentConvertor.class,
        AclMethodSecurityConfiguration.class
})
public class CommentControllerSecurityTest {

    private final static String LOGIN_URL = "http://localhost/login";

    @Autowired
    private MockMvc mvc;

    @DisplayName("должен разрешать доступ к ресурсам и запрещать доступ к ресурсам без пользователя")
    @ParameterizedTest(name = "Проверить доступ методом {0} на урл {1} с параметрами {4} {5} и пользователем {2}")
    @MethodSource("getTestArguments")
    void shouldTryToGetAssessToAllUrls(HttpMethod httpMethod,
                                       String urlTemplate,
                                       User user,
                                       ResultMatcher status,
                                       Map<String, String> param,
                                       Integer commentCount,
                                       boolean isAccessAllowed) throws Exception {

        MockHttpServletRequestBuilder request = getRequest(httpMethod, urlTemplate, List.of().toArray());

        for (String key : param.keySet()) {
            request.param(key, param.get(key));
        }

        mvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(LOGIN_URL));

        mvc.perform(request
                        .with(user(user.getUsername()).authorities(new SimpleGrantedAuthority("ROLE_" + user.getRoles().get(0).getRole())))
                )
                .andExpect(r -> {
                    if(commentCount != null) {
                        if(r.getModelAndView().getModel().get("comments") != null &&
                                ((List<?>)r.getModelAndView().getModel().get("comments")).size() != commentCount) {
                            System.out.println("((List<?>)r.getModelAndView().getModel().get(\"comments\")).size() " + ((List<?>)r.getModelAndView().getModel().get("comments")).size());
                            fail("Количество комментариев не совпадает");
                        }
                    }})
                .andExpect(result -> {
                    if (!isAccessAllowed) {
                        assertInstanceOf(AccessDeniedException.class, result.getResolvedException());
                    }
                })
                .andExpect(status);

    }

    @MethodSource("getTestArguments")
    public static Stream<Arguments> getTestArguments() {
        User user = new User(1, "user", "password", null);
        UserRole userRole =  new UserRole(1, user, "USER");
        user.setRoles(List.of(userRole));

        User admin = new User(1, "admin", "password", null);
        UserRole adminRole =  new UserRole(2, user, "ADMIN");
        admin.setRoles(List.of(adminRole));

        User guest = new User(3, "guest", "password", null);
        UserRole guestRole =  new UserRole(3, user, "USER");
        guest.setRoles(List.of(guestRole));

        return Stream.of(
                Arguments.of(HttpMethod.GET, "/comment", admin, status().isOk(), Map.of("bookId", "3"), 1, true),
                Arguments.of(HttpMethod.GET, "/comment/new", admin, status().isOk(), Map.of("bookId", "3"), null, true),
                Arguments.of(HttpMethod.POST, "/comment/new", admin, status().isFound(), Map.of("bookId", "3"), null, true),
                Arguments.of(HttpMethod.GET, "/comment/edit", admin, status().isOk(), Map.of("bookId", "3"), 1, true),
                Arguments.of(HttpMethod.POST, "/comment/edit", admin, status().isFound(), Map.of("bookId", "3", "id", "4", "text", "new text"), null, true),
                Arguments.of(HttpMethod.DELETE, "/comment/deleteById", admin, status().isFound(), Map.of("bookId", "3", "id", "4"), null, true),
                Arguments.of(HttpMethod.GET, "/comment", admin, status().isOk(), Map.of("bookId", "3"), null, true),

                Arguments.of(HttpMethod.GET, "/comment", user, status().isOk(), Map.of("bookId", "2"), 1, true),
                Arguments.of(HttpMethod.GET, "/comment/new", user, status().isOk(), Map.of("bookId", "2"), null, true),
                Arguments.of(HttpMethod.POST, "/comment/new", user, status().isFound(), Map.of("bookId", "2"), null, true),
                Arguments.of(HttpMethod.GET, "/comment/edit", user, status().isOk(), Map.of("bookId", "2"), 1, true),
                Arguments.of(HttpMethod.POST, "/comment/edit", user, status().isFound(), Map.of("bookId", "2", "id", "3", "text", "new text"), null, true),
                Arguments.of(HttpMethod.DELETE, "/comment/deleteById", user, status().isFound(), Map.of("bookId", "2", "id", "3"),null, true),
                Arguments.of(HttpMethod.GET, "/comment", user, status().isOk(), Map.of("bookId", "2"), null, true),

                Arguments.of(HttpMethod.GET, "/comment", guest, status().isOk(), Map.of("bookId", "1"), 0, true),
                Arguments.of(HttpMethod.GET, "/comment/new", guest, status().isOk(), Map.of("bookId", "1"), null, true),
                Arguments.of(HttpMethod.POST, "/comment/new", guest, status().isFound(), Map.of("bookId", "1"), null, true),
                Arguments.of(HttpMethod.GET, "/comment/edit", guest, status().isOk(), Map.of("bookId", "1"), 0, true),
                Arguments.of(HttpMethod.POST, "/comment/edit", guest, status().isOk(), Map.of("bookId", "1", "id", "1", "text", "new text"), null, false),
                Arguments.of(HttpMethod.DELETE, "/comment/deleteById", guest, status().isOk(), Map.of("bookId", "1", "id", "1"), null, false),
                Arguments.of(HttpMethod.GET, "/comment", guest, status().isOk(), Map.of("bookId", "1"), null, true)
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
