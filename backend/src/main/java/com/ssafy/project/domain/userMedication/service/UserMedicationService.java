package com.ssafy.project.domain.userMedication.service;

import com.ssafy.project.domain.medicine.dto.MedicineDTO;
import com.ssafy.project.domain.medicine.entity.Medicine;
import com.ssafy.project.domain.medicine.repository.MedicineRepository;
import com.ssafy.project.domain.userDetail.entity.UserDetail;
import com.ssafy.project.domain.userDetail.repository.UserDetailRepository;
import com.ssafy.project.domain.userMedicationDetail.dto.UserMedicationDetailRequestDTO;
import com.ssafy.project.domain.userMedicationDetail.dto.UserMedicationDetailResponseDTO;
import com.ssafy.project.domain.userMedication.dto.UserMedicationRequestDTO;
import com.ssafy.project.domain.userMedication.dto.UserMedicationResponseDTO;
import com.ssafy.project.domain.userMedication.entity.UserMedication;
import com.ssafy.project.domain.userMedication.repository.UserMedicationRepository;
import com.ssafy.project.domain.userMedicationDetail.entity.UserMedicationDetail;
import com.ssafy.project.domain.userMedicationDetail.repository.UserMedicationDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMedicationService {
    private final UserMedicationRepository userMedicationRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserMedicationDetailRepository userMedicationDetailRepository;
    private final MedicineRepository medicineRepository;

    //복약정보 추가하기
    public void save(UserMedicationRequestDTO userMedicationRequestDTO) {

        UserDetail userDetail = userDetailRepository.findById(userMedicationRequestDTO.getUserDetailId()).orElseThrow(() ->
                new IllegalArgumentException("UserDetail not found"));

        int maxCount = 0; // 최대값을 저장할 변수 초기화

        UserMedication userMedication = userMedicationRequestDTO.toEntity(new ArrayList<>(), userDetail);

        for(UserMedicationDetailRequestDTO dto: userMedicationRequestDTO.getUserMedicationDetailList()) {
            Medicine medicine = medicineRepository.findById(dto.getMedicineId()).orElseThrow(() ->
                    new IllegalArgumentException("Medicine not found"));

            UserMedicationDetail detail = dto.toEntity(medicine, userMedication);
            userMedication.getUserMedicationDetailList().add(detail);

            int calculatedCount = userMedication.getPrescriptionDay() * detail.getDailyIntakeFrequency();

            // 현재 계산된 값이 기존 최대값보다 크면 업데이트
            if (calculatedCount > maxCount) {
                maxCount = calculatedCount;
            }
        }
        System.out.println(maxCount);
        userMedication.calculateAndSetCount(maxCount);
        userMedicationRepository.save(userMedication);
    }

    //모든 유저의 복약정보 가져오기
    public List<UserMedicationResponseDTO> getUserMedication(int userDetailId) {
        List<UserMedicationResponseDTO> responseDTOList = new ArrayList<>();
        List<UserMedication> userMedications = userMedicationRepository.findAllByUserDetail_Id(userDetailId);

        //자식부터
        for(UserMedication userMedication: userMedications) {
            List<UserMedicationDetailResponseDTO> tempList = new ArrayList<>();
            for(UserMedicationDetail userMedicationDetail: userMedication.getUserMedicationDetailList()) {
                MedicineDTO medicineDTO = MedicineDTO.toMedicineDTO(userMedicationDetail.getMedicine());
                tempList.add(UserMedicationDetailResponseDTO.toDto(userMedicationDetail, medicineDTO));
            }
            responseDTOList.add(UserMedicationResponseDTO.toResponseDTO(userMedication, tempList));
        }

        return responseDTOList;
    }

    //복약카드 수정
    public void updateUserMedication(UserMedicationRequestDTO userMedicationRequestDTO, int userMedicationId) {
        UserMedication userMedication = userMedicationRepository.findById(userMedicationId).orElseThrow(() ->
                new IllegalArgumentException("UserMedication not found"));

        userMedication.update(userMedicationRequestDTO.getName(), userMedicationRequestDTO.getIntakeAt(),
                userMedicationRequestDTO.getPrescriptionDay(), userMedicationRequestDTO.getHospitalName(),
                userMedicationRequestDTO.getPharmacyName());

        List<UserMedicationDetailRequestDTO> userMedicationDetails = userMedicationRequestDTO.getUserMedicationDetailList();
        for(UserMedicationDetailRequestDTO detail: userMedicationDetails) {
            UserMedicationDetail findDetail = userMedicationDetailRepository.findById(detail.getId()).orElseThrow(()
                    -> new IllegalArgumentException("UserMedicationDetail not found"));
            Medicine medicine = medicineRepository.findById(detail.getMedicineId()).orElseThrow(()
                    -> new IllegalArgumentException("Medicine not found"));
            findDetail.update(detail.getDailyIntakeFrequency(), detail.getPerAmount(), medicine);
        }
    }

}
