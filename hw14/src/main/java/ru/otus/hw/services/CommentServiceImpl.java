package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Comment;
import ru.otus.hw.models.out.CommentNew;
import ru.otus.hw.repositories.CommentRepository;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final MappingService mappingService;

    private final NamedParameterJdbcOperations jdbc;

    private final CommentRepository commentRepository;

    private long startIdValue;

    private long endIdValue;

    public CommentNew getCommentNew(Comment comment) {
        return new CommentNew(startIdValue++,
                mappingService.getBookNewIdByOldId(comment.getBook().getId()),
                comment.getText());
    }

    @Override
    public void reserveSequenceValues() {

        startIdValue = jdbc.queryForObject("VALUES NEXT VALUE FOR COMMENTS_SEQ", Map.of(), Integer.class);
        long countAuthors = commentRepository.count();
        endIdValue = startIdValue + countAuthors;
        jdbc.update("ALTER SEQUENCE COMMENTS_SEQ restart with :newSeqStart", Map.of("newSeqStart", endIdValue));
    }
}
