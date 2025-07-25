package com.neoping.backend.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapping;

import com.neoping.backend.dto.CommentDto;
import com.neoping.backend.model.Comment;
import com.neoping.backend.model.Post;
import com.neoping.backend.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class CommentDTOMapper {

    public Comment mapFromDTO(CommentDto commentDto, Post post, User user) {
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    public CommentDto mapToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setPostId(comment.getPost().getId());
        commentDto.setUserId(comment.getUser().getId());
        commentDto.setUserName(comment.getUser().getUsername());
        commentDto.setParentCommentId(comment.getParentComment().getId());
        return commentDto;
    }
}
