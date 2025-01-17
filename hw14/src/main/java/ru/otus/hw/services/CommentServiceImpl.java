package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Comment;
import ru.otus.hw.models.out.CommentNew;
import ru.otus.hw.repositories.CommentRepository;

import java.util.concurrent.ConcurrentLinkedQueue;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private static final String SEQUENCE_NAME = "COMMENTS_SEQ";

    private final MappingService mappingService;

    private final SequenceValueService sequenceValueService;

    private final CommentRepository commentRepository;

    private ConcurrentLinkedQueue<Long> reservedIds;

    public CommentNew getCommentNew(Comment comment) {
        if (reservedIds.isEmpty()) {
            throw new RuntimeException("There is no id to create new rows");
        }
        Long currentId  = reservedIds.poll();
        return new CommentNew(currentId,
                mappingService.getBookNewIdByOldId(comment.getBook().getId()),
                comment.getText());
    }

    @Override
    public void reserveSequenceValues() {

        reservedIds = sequenceValueService.getSequenceValuesQueue(commentRepository.count(), SEQUENCE_NAME);
    }
}
