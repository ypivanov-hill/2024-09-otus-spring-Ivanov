package ru.otus.hw.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Configuration
public class FunctionalEndpointsConfig {

    @RouterOperations({@RouterOperation(method = RequestMethod.GET, path = "/api/v1/author", beanClass = AuthorService.class, beanMethod = "findAll"),
                @RouterOperation(method = RequestMethod.GET, path = "/api/v1/author/{id}", beanClass = AuthorService.class, beanMethod = "findById",
                        operation = @Operation(
                                description = "findById",
                                operationId =  "findById",
                                method = "GET",
                                parameters =
                                @Parameter(
                                        name = "id",
                                        in = ParameterIn.PATH,
                                        style = ParameterStyle.SIMPLE,
                                        explode = Explode.FALSE,
                                        required = true
                ))),
                @RouterOperation(method = RequestMethod.DELETE, path = "/api/v1/author/{id}", beanClass = AuthorService.class, beanMethod = "deleteById",
                        operation = @Operation(
                                operationId =  "deleteById",
                                description = "deleteById",
                                method = "DELETE",
                                parameters =
                                @Parameter(
                                        name = "id",
                                        in = ParameterIn.PATH,
                                        style = ParameterStyle.SIMPLE,
                                        explode = Explode.FALSE,
                                        required = true
                                )))
       }
       )
    @Bean
    public RouterFunction<ServerResponse> composedRoutes(AuthorService authorService) {
        return route()
                .GET("/api/v1/author",
                        request -> ok().contentType(APPLICATION_JSON).body(authorService.findAll(), AuthorDto.class)
                )
                .GET("/api/v1/author/{id}",
                        request ->
                                authorService.findById(request.pathVariable("id"))
                                        .flatMap(person -> ok().contentType(APPLICATION_JSON).body(fromValue(person)))
                                        .switchIfEmpty(notFound().build())
                )
                .DELETE("/api/v1/author/{id}",
                        request -> {
                            String id = request.pathVariable("id");
                            authorService.deleteById(id);
                            return ok().contentType(MediaType.TEXT_PLAIN).bodyValue(id);
                        })
                .build();

    }
}
