package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Author;
import ru.otus.hw.models.out.AuthorNew;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private static final String SEQUENCE_NAME = "AUTHORS_SEQ";

    private final MappingService mappingService;

    private final AuthorRepository authorRepository;

    private final SequenceValueService sequenceValueService;

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

        reservedIds = sequenceValueService.getSequenceValues(authorRepository.count(), SEQUENCE_NAME);
        log.debug("Author reservedIds {}", reservedIds.size());
    }
}
