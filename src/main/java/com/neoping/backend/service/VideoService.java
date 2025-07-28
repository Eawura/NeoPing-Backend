package com.neoping.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.neoping.backend.dto.VideoDto;
import com.neoping.backend.model.Video;
import com.neoping.backend.repository.VideoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;

    public List<VideoDto> getVideos(String search, String category, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "uploadedAt"));
        Page<Video> videoPage;
        if (search != null && !search.isEmpty()) {
            videoPage = videoRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search,
                    pageable);
        } else if (category != null && !category.equalsIgnoreCase("All")) {
            videoPage = videoRepository.findByCategoryIgnoreCase(category, pageable);
        } else {
            videoPage = videoRepository.findAll(pageable);
        }
        return videoPage.getContent().stream().map(this::toDto).collect(Collectors.toList());
    }

    private VideoDto toDto(Video v) {
        VideoDto dto = new VideoDto();
        dto.setId(v.getId());
        dto.setTitle(v.getTitle());
        dto.setDescription(v.getDescription());
        dto.setUrl(v.getUrl());
        dto.setThumbnail(v.getThumbnail());
        dto.setCategory(v.getCategory());
        dto.setUploadedAt(v.getUploadedAt());
        dto.setUploader(v.getUploader());
        dto.setViews(v.getViews());
        return dto;
    }
}