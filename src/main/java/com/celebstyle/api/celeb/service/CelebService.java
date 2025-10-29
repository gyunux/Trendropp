package com.celebstyle.api.celeb.service;

import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.celeb.CelebRepository;
import com.celebstyle.api.celeb.dto.CelebCreateRequest;
import com.celebstyle.api.celeb.dto.CelebCreateResponse;
import com.celebstyle.api.celeb.dto.CelebUpdateRequest;
import com.celebstyle.api.celeb.dto.CelebAdminView;
import com.celebstyle.api.celeb.dto.CelebView;
import com.celebstyle.api.common.S3UploadService;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CelebService {
    private final CelebRepository celebRepository;
    private final S3UploadService s3UploadService;
    @Transactional
    public CelebCreateResponse create(CelebCreateRequest request) throws IOException {

        String imageUrl = s3UploadService.upload(request.getProfileImage(),"celebs");

        Celeb newCeleb = Celeb.builder()
                .name(request.getName())
                .instagramName(request.getInstagramName())
                .profileImageUrl(imageUrl)
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

    @Transactional(readOnly = true)
    public List<CelebView> findAllForCelebsName(){
        List<Celeb> celebList = celebRepository.findAll();
        List<CelebView> celebViewList = new ArrayList<>();
        for(Celeb celeb : celebList){
            celebViewList.add(new CelebView(celeb));
        }
        return celebViewList;
    }

    @Transactional
    public void update(Long id, CelebUpdateRequest request) throws IOException {
        Celeb celeb = celebRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 셀럽을 찾을 수 없습니다: " + id));

        MultipartFile newProfileImage = request.getProfileImage();

        // [핵심] 2. 새로운 이미지 파일이 들어왔는지 확인합니다.
        if (newProfileImage != null && !newProfileImage.isEmpty()) {

            String newImageUrl = s3UploadService.upload(newProfileImage, "celebs");
            celeb.setProfileImageUrl(newImageUrl);
        }
        celeb.setInstagramName(request.getInstagramName());
    }

    @Transactional
    public void delete(Long id) {
        if (!celebRepository.existsById(id)) {
            throw new EntityNotFoundException("해당 ID의 셀럽을 찾을 수 없습니다: " + id);
        }
        celebRepository.deleteById(id);
    }
}