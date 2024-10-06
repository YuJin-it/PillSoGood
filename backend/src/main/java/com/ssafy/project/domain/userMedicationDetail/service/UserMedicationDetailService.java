package com.ssafy.project.domain.userMedicationDetail.service;

import com.ssafy.project.domain.medicine.entity.Medicine;
import com.ssafy.project.domain.medicine.repository.MedicineRepository;
import com.ssafy.project.domain.userMedication.entity.UserMedication;
import com.ssafy.project.domain.userMedication.repository.UserMedicationRepository;
import com.ssafy.project.domain.userMedicationDetail.dto.UserMedicationDetailRequestDTO;
import com.ssafy.project.domain.userMedicationDetail.entity.UserMedicationDetail;
import com.ssafy.project.domain.userMedicationDetail.repository.UserMedicationDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMedicationDetailService {
    private final UserMedicationDetailRepository userMedicationDetailRepository;
    private final MedicineRepository medicineRepository;
    private final UserMedicationRepository userMedicationRepository;

    //복약카드디테일 추가
    public void addUserMedicationDetail(List<UserMedicationDetailRequestDTO> userMedicationDetailRequestDTO, int userMedicationId) {
        for(UserMedicationDetailRequestDTO detail: userMedicationDetailRequestDTO) {
            UserMedication userMedication = userMedicationRepository.findById(userMedicationId).orElseThrow(()
                    -> new IllegalArgumentException("UserMedication not found"));
            Medicine medicine = medicineRepository.findById(detail.getMedicineId()).orElseThrow(()
                    -> new IllegalArgumentException("Medicine not found"));
            userMedicationDetailRepository.save(detail.toEntity(medicine, userMedication));
        }
    }

    //복약카드디테일 삭제
    public void removeUserMedicationDetail(int userMedicationDetailId) {
        userMedicationDetailRepository.deleteById(userMedicationDetailId);
    }

    //복약정보디테일 수정
    public void updateUserMedicationDetail(UserMedicationDetailRequestDTO dto, int userMedicationDetailId) {
        UserMedicationDetail detail = userMedicationDetailRepository.findById(userMedicationDetailId).orElseThrow(()
                -> new IllegalArgumentException("UserMedication not found"));
        Medicine medicine = medicineRepository.findById(dto.getMedicineId()).orElseThrow(()
                -> new IllegalArgumentException("Medicine not found"));
        detail.update(dto.getDailyIntakeFrequency(), dto.getPerAmount(), medicine);
        userMedicationDetailRepository.save(detail);
    }
}
