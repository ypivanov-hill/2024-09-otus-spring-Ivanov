package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Author;
import ru.otus.hw.models.out.AuthorNew;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final MappingService mappingService;

    private final AuthorRepository authorRepository;

    private final NamedParameterJdbcOperations jdbc;

    private long startIdValue;

    private long endIdValue;

    @Override
    public AuthorNew getAuthorNew(Author author) {
        mappingService.putAuthorIds(startIdValue, author.getId());
        return new AuthorNew(startIdValue++, author.getFullName());
    }

    @Override
    public void reserveSequenceValues() {

        startIdValue = jdbc.queryForObject("VALUES NEXT VALUE FOR authors_seq", Map.of(), Integer.class);
        log.debug("Author current id value {}", startIdValue);
        long countAuthors = authorRepository.count();
        log.debug("Author countAuthors {}", countAuthors);
        endIdValue = startIdValue + countAuthors;
        log.debug("Author newSeqStart {}", startIdValue + countAuthors);
        jdbc.update("ALTER SEQUENCE authors_seq restart with :newSeqStart", Map.of("newSeqStart", endIdValue));
    }


}
