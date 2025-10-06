package com.celebstyle.api.celeb;

import com.celebstyle.api.celeb.dto.CelebCreateRequest;
import com.celebstyle.api.celeb.dto.CelebCreateResponse;
import com.celebstyle.api.celeb.dto.CelebUpdateRequest;
import com.celebstyle.api.celeb.dto.CelebAdminView;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
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

    @Transactional(readOnly = true)
    public List<CelebAdminView> findAllForAdminView(){
        List<Celeb> celebList = celebRepository.findAll();
        List<CelebAdminView> celebViewList = new ArrayList<>();
        for(Celeb celeb : celebList){
            celebViewList.add(
                    CelebAdminView.builder()
                            .id(celeb.getId())
                            .name(celeb.getName())
                            .profileImageUrl(celeb.getProfileImageUrl())
                            .instagramName(celeb.getInstagramName())
                            .build()
            );
        }
        return celebViewList;
    }

    @Transactional
    public void update(Long id, CelebUpdateRequest request) {
        Celeb celeb = celebRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 셀럽을 찾을 수 없습니다: " + id));

        celeb.updateInfo(request.getProfileImageUrl(), request.getInstagramName());

    }

    @Transactional
    public void delete(Long id) {
        if (!celebRepository.existsById(id)) {
            throw new EntityNotFoundException("해당 ID의 셀럽을 찾을 수 없습니다: " + id);
        }
        celebRepository.deleteById(id);
    }
}