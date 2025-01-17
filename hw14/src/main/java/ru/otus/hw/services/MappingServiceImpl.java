package ru.otus.hw.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.out.BooksGenresNew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MappingServiceImpl implements MappingService {

    private Map<String, Long> authorIdMapping = new HashMap<>();

    private Map<String, Long> bookIdMapping = new HashMap<>();

    private Map<String, Long> genreIdMapping = new HashMap<>();

    private List<BooksGenresNew> bookToGenreIdMapping = new ArrayList<>();

    @Override
    public long getAuthorNewIdByOldId(String oldId) {
        return authorIdMapping.get(oldId);
    }

    @Override
    public long getBookNewIdByOldId(String oldId) {
        return bookIdMapping.get(oldId);
    }

    @Override
    public long getGenreNewIdByOldId(String oldId) {
        return genreIdMapping.get(oldId);
    }

    @Override
    public List<BooksGenresNew> getBookToGenreList() {
        log.debug("getBookToGenreList size {}", bookToGenreIdMapping.size());
        return bookToGenreIdMapping;
    }

    @Override
    public void putAuthorIds(long newId, String oldId) {
        authorIdMapping.put(oldId, newId);
    }

    @Override
    public void putBookIds(long newId, String oldId) {
        bookIdMapping.put(oldId, newId);
    }

    @Override
    public void putGenreIds(long newId, String oldId) {
        genreIdMapping.put(oldId, newId);
    }

    @Override
    public void putBookToGenreIds(long bookNewId, long genreNewId) {
        bookToGenreIdMapping.add(new BooksGenresNew(bookNewId, genreNewId));

    }

    @Override
    public void cleanUp() {
        authorIdMapping.clear();
        bookIdMapping.clear();
        genreIdMapping.clear();
        bookToGenreIdMapping.clear();
    }

    @Override
    public void info() {

        log.debug("authorIdMapping size {}", authorIdMapping.size());
        log.debug("bookIdMapping size {}", bookIdMapping.size());
        log.debug("genreIdMapping size {}", genreIdMapping.size());
        log.debug("bookToGenreIdMapping size {}", bookToGenreIdMapping.size());
    }


}
