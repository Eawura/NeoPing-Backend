package com.neoping.backend.mapper;

import com.neoping.backend.dto.CommunityDto;
import com.neoping.backend.model.Community;
import com.neoping.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommunityDTOMapper {

    private final UserRepository userRepository;

    public CommunityDto toDto(Community community) {
        CommunityDto communityDto = new CommunityDto();
        communityDto.setAvatar(community.getAvatar());
        communityDto.setId(community.getId());
        communityDto.setCategory(community.getCategory());
        communityDto.setDescription(community.getDescription());
        communityDto.setName(community.getName());
        communityDto.setCreatorId(community.getCreator().getId());
        communityDto.setCreatorName(community.getCreator().getUsername());
        communityDto.setDisplayName(community.getDisplayName());
        communityDto.setMembers(community.getMembers());
        communityDto.setCreatedAt(community.getCreatedAt());
        communityDto.setRules(community.getRules());
        return communityDto;
    }

    public Community fromDto(CommunityDto dto) {
        Community community = new Community();
        community.setCreator(userRepository.findByUsername(dto.getCreatorName()).orElse(null));
        community.setCreatedAt(dto.getCreatedAt());
        community.setDisplayName(dto.getDisplayName());
        community.setCategory(dto.getCategory());
        community.setAvatar(dto.getAvatar());
        community.setMembers(dto.getMembers());
        community.setRules(dto.getRules());

        return community;
    }

}
