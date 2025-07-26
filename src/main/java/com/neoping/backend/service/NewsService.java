package com.neoping.backend.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.neoping.backend.model.NewsComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.neoping.backend.dto.NewsDto;
import com.neoping.backend.model.Bookmark;
import com.neoping.backend.model.News;
import com.neoping.backend.repository.BookmarkRepository;
import com.neoping.backend.repository.NewsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsService {
    @Autowired
    private com.neoping.backend.repository.NewsCommentRepository newsCommentRepository;
    private final NewsRepository newsRepository;
    @Autowired
    private BookmarkRepository bookmarkRepository;

    // Create a news article
    public NewsDto createNews(NewsDto newsDto) {
        News news = new News();
        news.setUser(newsDto.getUser());
        news.setAvatar(newsDto.getAvatar());
        news.setTitle(newsDto.getTitle());
        news.setExcerpt(newsDto.getExcerpt());
        news.setImage(newsDto.getImage());
        news.setCategory(newsDto.getCategory());
        news.setTimestamp(newsDto.getTimestamp() != null ? newsDto.getTimestamp() : Instant.now());
        news.setUpvotes(0);
        news.setComments(0);
        News saved = newsRepository.save(news);
        return toDto(saved);
    }

    // Get all comments for a news article
    public List<String> getCommentsForNews(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));
        List<com.neoping.backend.model.NewsComment> comments = newsCommentRepository.findByNews(news);
        return comments.stream().map(com.neoping.backend.model.NewsComment::getContent).collect(Collectors.toList());
    }

    // Get a single news article by ID
    public NewsDto getNewsById(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));
        return toDto(news);
    }

    public List<NewsDto> getNews(String category, String search, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<News> newsPage;
        if (category != null && !category.equalsIgnoreCase("All")) {
            newsPage = newsRepository.findByCategoryIgnoreCase(category, pageable);
        } else if (search != null && !search.isEmpty()) {
            newsPage = newsRepository.findByTitleContainingIgnoreCaseOrExcerptContainingIgnoreCase(search, search,
                    pageable);
        } else {
            newsPage = newsRepository.findAll(pageable);
        }
        return newsPage.getContent().stream().map(this::toDto).collect(Collectors.toList());
    }

    public void upvoteNews(Long newsId, String username) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));
        news.setUpvotes(news.getUpvotes() + 1);
        newsRepository.save(news);
        // Optionally: track which users have upvoted to prevent multiple upvotes
    }

    public void downvoteNews(Long newsId, String username) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));
        news.setUpvotes(news.getUpvotes() - 1);
        newsRepository.save(news);
        // Optionally: track which users have downvoted
    }

    public void addComment(Long newsId, String username, String commentText) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));
        NewsComment comment = new NewsComment();
        comment.setNews(news);
        comment.setUsername(username);
        comment.setContent(commentText);
        comment.setCreatedAt(Instant.now());
        newsCommentRepository.save(comment);
        news.setComments(news.getComments() + 1);
        newsRepository.save(news);
    }

    public void bookmarkNews(Long newsId, String username) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));
        if (!bookmarkRepository.existsByUsernameAndNewsId(username, newsId)) {
            Bookmark bookmark = new Bookmark();
            bookmark.setUsername(username);
            bookmark.setNews(news);
            bookmark.setBookmarkedAt(Instant.now());
            bookmarkRepository.save(bookmark);
        }
    }

    private NewsDto toDto(News n) {
        NewsDto dto = new NewsDto();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setExcerpt(n.getExcerpt());
        dto.setCategory(n.getCategory());
        dto.setTimestamp(n.getTimestamp());
        dto.setUpvotes(n.getUpvotes());
        dto.setComments(n.getComments());
        dto.setAvatar(n.getAvatar());
        dto.setUser(n.getUser());
        dto.setImage(n.getImage());
        dto.setUpvoted(false);
        dto.setDownvoted(false);
        dto.setSaved(false);
        return dto;
    }
}
