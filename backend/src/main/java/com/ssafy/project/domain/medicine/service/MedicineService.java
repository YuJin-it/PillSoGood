package com.ssafy.project.domain.medicine.service;

import com.ssafy.project.domain.lists.combinationProhibition.dto.CombinationProhibitionDTO;
import com.ssafy.project.domain.medicine.dto.MedicineDTO;
import com.ssafy.project.domain.medicine.dto.MedicinePreviewDTO;
import com.ssafy.project.domain.medicine.entity.Medicine;
import com.ssafy.project.domain.medicine.repository.MedicineRepository;
import com.ssafy.project.global.utils.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineService {
    private final MedicineRepository medicineRepository;
    private final ElasticsearchService elasticsearchService;

    @Transactional (readOnly = true)
    public List<MedicinePreviewDTO> findAll() {
        Pageable pageable = PageRequest.of(0, 20); // 첫 번째 페이지에서 20개의 데이터만 가져옴
        List<Medicine> medicines = medicineRepository.findAll(pageable).getContent();

        return medicines.stream()
                .map(MedicinePreviewDTO::toMedicinePreviewDTO)
                .collect(Collectors.toList());

    }


    @Transactional
    public MedicineDTO findById(Integer id) {
        return medicineRepository.findById(id)
                .map(MedicineDTO::toMedicineDTO)
                .orElseThrow(() -> new NoSuchElementException("해당 ID로 약물을 찾을 수 없습니다: " + id));
    }

    public List<CombinationProhibitionDTO> findAllCombinationProhibition(List<Integer> medicineIds) {
        List<CombinationProhibitionDTO> combinationProhibitions = new ArrayList<>();

        for (int i = 0; i < medicineIds.size(); i++) {
            for (int j = i + 1; j < medicineIds.size(); j++) {
                // 두 개의 약물 선택
                int medicine1 = medicineIds.get(i);
                int medicine2 = medicineIds.get(j);

                // 조합된 약물의 금기사항을 찾거나 처리하는 로직 추가
                CombinationProhibitionDTO combinationProhibitionDTO = new CombinationProhibitionDTO();
                combinationProhibitionDTO = elasticsearchService.searchCombinationProhibition(medicine1, medicine2);
                combinationProhibitions.add(combinationProhibitionDTO);
            }
        }
        return combinationProhibitions;
    }
}
