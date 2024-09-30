package com.ssafy.project.domain.userDetail.service;

import com.ssafy.project.domain.user.entity.User;
import com.ssafy.project.domain.user.repository.UserRepository;
import com.ssafy.project.domain.userDetail.dto.UserDetailRequestDTO;
import com.ssafy.project.domain.userDetail.dto.UserDetailResponseDTO;
import com.ssafy.project.domain.userDetail.entity.UserDetail;
import com.ssafy.project.domain.userDetail.repository.UserDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailService {
    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;

    // 사용자 조회
    @Transactional(readOnly = true)
    public UserDetailResponseDTO getUser(int userId, String family) {

        if (family != null) {
            UserDetail userDetail = userDetailRepository.findByUserIdAndFamily(userId, family)
                    .orElseThrow(() -> new IllegalArgumentException("해당 가족 정보를 찾을 수 없습니다."));
            return UserDetailResponseDTO.fromEntity(userDetail);
        }

        UserDetail userDetail = userDetailRepository.findByUserIdAndFamily(userId, "나")
                .orElseThrow(() -> new IllegalArgumentException("사용자 세부 정보를 찾을 수 없습니다."));
        return UserDetailResponseDTO.fromEntity(userDetail);
    }

    // 사용자 가족 조회
    @Transactional(readOnly = true)
    public List<UserDetailResponseDTO> getUserFamily(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<UserDetail> userDetails = userDetailRepository.findAllByUser(user);

        List<UserDetailResponseDTO> familyList = new ArrayList<>();
        for (UserDetail userDetail : userDetails) {
            familyList.add(UserDetailResponseDTO.fromEntity(userDetail));
        }
        return familyList;
    }

    // 사용자 등록
    @Transactional
    public void registerUserDetail(int userId, UserDetailRequestDTO userDetailRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Optional<UserDetail> existingUserDetail = userDetailRepository.findByUserIdAndFamily(userId, userDetailRequestDTO.getFamily());

        if (existingUserDetail.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 사용자 정보가 있습니다.");
        }
        UserDetail userDetail = userDetailRequestDTO.toEntity(user);
        userDetailRepository.save(userDetail);
    }

    // 사용자 정보 수정
    @Transactional
    public void modifyUserDetail(int userId, String family, UserDetailRequestDTO userDetailRequestDTO) {
        UserDetail userDetail = userDetailRepository.findByUserIdAndFamily(userId, family)
                .orElseThrow(() -> new IllegalArgumentException("사용자 세부 정보를 찾을 수 없습니다."));

        userDetailRequestDTO.updateUserDetail(userDetail);
        userDetailRepository.save(userDetail);
    }

    // 회원 탈퇴 -> flag or 카카오 api 반영
    @Transactional
    public void deleteUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
    }

    // 개별 가족 정보 삭제
    @Transactional
    public void deleteFamily(int userId, String family) {
        UserDetail userDetail = userDetailRepository.findByUserIdAndFamily(userId, family)
                .orElseThrow(() -> new IllegalArgumentException("해당 가족 정보를 찾을 수 없습니다."));

        userDetailRepository.delete(userDetail);
    }
}
