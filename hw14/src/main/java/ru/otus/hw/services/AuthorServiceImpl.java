package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Author;
import ru.otus.hw.models.out.AuthorNew;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final MappingService mappingService;

    private int idGenerator = 1;

    @Override
    public AuthorNew getAuthorNew(Author author) {
        mappingService.putAuthorIds(idGenerator, author.getId());
        return new AuthorNew(idGenerator++, author.getFullName());
    }


}
