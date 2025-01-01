package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Author;
import ru.otus.hw.models.out.AuthorNew;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final MappingService mappingService;

    private final AuthorRepository authorRepository;

    private final NamedParameterJdbcOperations jdbc;

    private List<Long> reservedIds;


    @Override
    public AuthorNew getAuthorNew(Author author) {
        log.debug("getAuthorNew count of Ids {}", reservedIds.size());
        if (reservedIds.isEmpty()) {
            throw new RuntimeException("There is no id to create new rows");
        }
        Long currentId  = reservedIds.get(0);
        reservedIds.remove(currentId);
        log.debug("getAuthorNew remaining count of Ids {}", reservedIds.size());
        log.debug("getAuthorNew set Id {}", currentId);
        mappingService.putAuthorIds(currentId, author.getId());
        return new AuthorNew(currentId, author.getFullName());
    }

    @Override
    public void reserveSequenceValues() {

        long countAuthors = authorRepository.count();
        log.debug("Author countAuthors {}", countAuthors);

        reservedIds = jdbc.queryForList("select nextval('AUTHORS_SEQ') from SYSTEM_RANGE(1, :cnt)",
                Map.of("cnt", countAuthors),
                Long.class);
        log.debug("Author reservedIds {}", reservedIds.size());

    }
}
