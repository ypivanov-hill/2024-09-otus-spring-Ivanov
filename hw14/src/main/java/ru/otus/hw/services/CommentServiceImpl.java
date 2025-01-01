package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Comment;
import ru.otus.hw.models.out.CommentNew;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final MappingService mappingService;

    private final NamedParameterJdbcOperations jdbc;

    private final CommentRepository commentRepository;

    private List<Long> reservedIds;

    public CommentNew getCommentNew(Comment comment) {
        if (reservedIds.isEmpty()) {
            throw new RuntimeException("There is no id to create new rows");
        }
        Long currentId  = reservedIds.get(0);
        reservedIds.remove(currentId);
        return new CommentNew(currentId,
                mappingService.getBookNewIdByOldId(comment.getBook().getId()),
                comment.getText());
    }

    @Override
    public void reserveSequenceValues() {

        long countAuthors = commentRepository.count();

        reservedIds = jdbc.queryForList("select nextval('COMMENTS_SEQ') from SYSTEM_RANGE(1, :cnt)",
                Map.of("cnt", countAuthors),
                Long.class);
    }
}
