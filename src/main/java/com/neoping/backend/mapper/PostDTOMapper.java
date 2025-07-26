package com.neoping.backend.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.neoping.backend.dto.PostRequest;
import com.neoping.backend.dto.PostResponse;
import com.neoping.backend.model.Post;
import com.neoping.backend.model.User;
import com.neoping.backend.model.VoteType;
import com.neoping.backend.repository.CommentRepository;
import com.neoping.backend.repository.VoteRepository;
import com.neoping.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
@Component
public class PostDTOMapper {

    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final AuthService authService;

    public PostResponse mapToDto(Post post) {
        PostResponse response = new PostResponse();

        response.setPostId(post.getId());
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setDescription(post.getDescription());
        response.setContent(post.getDescription());
        response.setUrl(post.getUrl());
        response.setCreatedDate(post.getCreatedDate());
        response.setTimestamp(post.getCreatedDate());
        response.setUserName(post.getUser().getUsername());
        response.setUser(post.getUser().getUsername());
        response.setCommentCount(getCommentCount(post));
        response.setVoteCount(post.getVoteCount() != null ? post.getVoteCount().intValue() : 0);
        response.setLikes(response.getVoteCount());
        response.setUpVote(isPostUpVoted(post));
        response.setDownVote(isPostDownVoted(post));
        response.setDuration(String.valueOf(post.getCreatedDate()));

        response.setImage(post.getImage());
        response.setVideo(post.getVideo());
        response.setPollQuestion(post.getPollQuestion());
        response.setPollOptions(post.getPollOptions());
        response.setPollDuration(post.getPollDuration());
        response.setLinkUrl(post.getLinkUrl());
        response.setLinkTitle(post.getLinkTitle());
        response.setCommunity(post.getCommunity());

        // Constants and defaults
        response.setComments(null);
        response.setShares(0);
        response.setImageUrl("");
        response.setLiked(false);
        response.setSaved(false);
        response.setSuccess(true);
        response.setError("");
        response.setMessage("");

        return response;
    }

    public Post mapPostRequestToPost(PostRequest postRequest, User user) {
        Post newPost = new Post();
        newPost.setTitle(postRequest.getTitle());
        newPost.setDescription(postRequest.getDescription());
        newPost.setUrl(postRequest.getUrl());
        newPost.setCreatedDate(Instant.now());
        newPost.setVoteCount(0L);
        newPost.setUser(user);
        newPost.setComments(null);
        newPost.setImage(postRequest.getImage());
        newPost.setVideo(postRequest.getVideo());
        newPost.setPollQuestion(postRequest.getPollQuestion());
        newPost.setPollOptions(postRequest.getPollOptions());
        newPost.setPollDuration(postRequest.getPollDuration());
        newPost.setLinkUrl(postRequest.getLinkUrl());
        newPost.setLinkTitle(postRequest.getLinkTitle());
        newPost.setCommunity(postRequest.getCommunity());
        return newPost;
    }

    private int getCommentCount(Post post) {
        if (post == null || post.getId() == null) return 0;
        return (int) commentRepository.countByPostId(post.getId());
    }

    private boolean isPostUpVoted(Post post) {
        try {
            User currentUser = authService.getCurrentUser();
            return voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, currentUser)
                    .map(vote -> vote.getVoteType().equals(VoteType.UPVOTE))
                    .orElse(false);
        } catch (Exception e) {
            log.debug("Failed to check upvote status for post {}: {}", post.getId(), e.getMessage());
            return false;
        }
    }

    private boolean isPostDownVoted(Post post) {
        try {
            User currentUser = authService.getCurrentUser();
            return voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, currentUser)
                    .map(vote -> vote.getVoteType().equals(VoteType.DOWNVOTE))
                    .orElse(false);
        } catch (Exception e) {
            log.debug("Failed to check downvote status for post {}: {}", post.getId(), e.getMessage());
            return false;
        }
    }

    private String formatDuration(Instant createdDate) {
        try {
            return TimeAgo.using(createdDate.toEpochMilli());
        } catch (Exception e) {
            return "just now";
        }
    }
}

