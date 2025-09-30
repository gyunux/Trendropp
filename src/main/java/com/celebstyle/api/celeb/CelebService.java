package com.celebstyle.api.celeb;

import com.celebstyle.api.celeb.dto.CelebCreateRequest;
import com.celebstyle.api.celeb.dto.CelebCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CelebService {
    private final CelebRepository celebRepository;

    @Transactional
    public CelebCreateResponse create(CelebCreateRequest request){
        Celeb newCeleb = Celeb.builder()
                .name(request.getName())
                .profileImageUrl(request.getProfileImageUrl())
                .instagramName(request.getInstagramName())
                .build();

        Celeb celeb = celebRepository.save(newCeleb);

        return CelebCreateResponse.fromEntity(celeb);
    }
}