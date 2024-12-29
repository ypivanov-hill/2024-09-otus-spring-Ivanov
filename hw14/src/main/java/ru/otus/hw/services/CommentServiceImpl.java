package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Comment;
import ru.otus.hw.models.out.CommentNew;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {


    private final MappingService mappingService;

    private int idValue = 1;

    public CommentNew getCommentNew(Comment comment) {
        return new CommentNew(idValue++,
                mappingService.getBookNewIdByOldId(comment.getBook().getId()),
                comment.getText());
    }
}
