package ru.otus.hw.services;

import ru.otus.hw.models.in.Comment;
import ru.otus.hw.models.out.CommentNew;

public interface CommentService {

    CommentNew getCommentNew(Comment comment);

    void reserveSequenceValues();
}
