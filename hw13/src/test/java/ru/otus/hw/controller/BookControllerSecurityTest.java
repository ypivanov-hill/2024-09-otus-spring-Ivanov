package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.otus.hw.models.User;
import ru.otus.hw.models.UserRole;
import ru.otus.hw.security.AclConfig;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AclServiceWrapperServiceImpl;

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

@DisplayName("Контролер для книг")
@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfiguration.class,
        AclConfig.class,
        AclServiceWrapperServiceImpl.class
})
public class BookControllerSecurityTest {

    private final static String LOGIN_URL = "http://localhost/login";

    @Autowired
    private MockMvc mvc;

    @DisplayName("должен разрешать доступ с пользователем к ресурсам и запрещать без пользователя")
    @ParameterizedTest(name = "Проверить доступ методом {0} на урл {1} с параметрами {5} {6} и пользователем {2}")
    @MethodSource("getTestArguments")
    void shouldTryToGetAssessToAllUrls(HttpMethod httpMethod,
                                       String urlTemplate,
                                       User user,
                                       ResultMatcher authStatus,
                                       boolean isAccessAllowed,
                                       String redirectedUrl,
                                       Map<String, String> param,
                                       Object... uriVariables) throws Exception {


        MockHttpServletRequestBuilder request = getRequest(httpMethod, urlTemplate, uriVariables);

        for (String key : param.keySet()) {
            request.param(key, param.get(key));
        }

        if (redirectedUrl != null) {
            mvc.perform(request)
                    .andExpectAll(status().isFound())
                    .andExpect(redirectedUrl(redirectedUrl));

        }

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + user.getRoles().get(0).getRole());
        mvc.perform(request
                        .with(user(user.getUsername()).authorities(grantedAuthority))
                )
                .andExpect(result -> {
                    if (!isAccessAllowed) {
                        assertInstanceOf(AccessDeniedException.class, result.getResolvedException());
                    }
                })
                .andExpect(authStatus)
        ;

    }

    @MethodSource("getTestArguments")
    public static Stream<Arguments> getTestArguments() {
        User user = new User(1, "user", "password", null);
        UserRole userRole =  new UserRole(1, user, "USER");
        user.setRoles(List.of(userRole));

        User admin = new User(1, "admin", "password", null);
        UserRole adminRole =  new UserRole(2, user, "ADMIN");
        admin.setRoles(List.of(adminRole));

        Map<String, Object> paramMap = Map.of("id", "2",
                "title", "Book!",
                "authorId", "1",
                "genreIds", "1");

        return Stream.of(
                Arguments.of(HttpMethod.GET, "/login", admin, status().isOk(), true, null, Map.of(), List.of().toArray()),
                Arguments.of(HttpMethod.GET, "/", admin, status().isOk(), true, LOGIN_URL, Map.of(), List.of().toArray()),
                Arguments.of(HttpMethod.DELETE, "/deleteBookById/{id}", admin, status().isFound(), true, LOGIN_URL, Map.of(), List.of(1).toArray()),
                Arguments.of(HttpMethod.POST, "/edit", admin, status().isFound(), true, LOGIN_URL, paramMap, List.of().toArray()),
                Arguments.of(HttpMethod.GET, "/edit/{id}", admin, status().isOk(), true, LOGIN_URL, Map.of(), List.of(1).toArray()),
                Arguments.of(HttpMethod.GET, "/create", admin, status().isOk(), true, LOGIN_URL, Map.of(), List.of().toArray()),

                Arguments.of(HttpMethod.GET, "/login", user, status().isOk(), true, null, Map.of(), List.of().toArray()),
                Arguments.of(HttpMethod.GET, "/", user, status().isOk(), true, LOGIN_URL, Map.of(), List.of().toArray()),
                Arguments.of(HttpMethod.DELETE, "/deleteBookById/{id}", user, status().isOk(), false, LOGIN_URL, Map.of(), List.of(1).toArray()),
                Arguments.of(HttpMethod.POST, "/edit", user, status().isOk(), true, LOGIN_URL, paramMap, List.of().toArray()),
                Arguments.of(HttpMethod.GET, "/edit/{id}", user, status().isOk(), false, LOGIN_URL, Map.of(), List.of(1).toArray()),
                Arguments.of(HttpMethod.GET, "/create", user, status().isOk(), true, LOGIN_URL, Map.of(), List.of().toArray())

        );
    }

    private static MockHttpServletRequestBuilder getRequest(HttpMethod httpMethod,
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
