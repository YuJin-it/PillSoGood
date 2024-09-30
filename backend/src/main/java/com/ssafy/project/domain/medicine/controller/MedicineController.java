package com.ssafy.project.domain.medicine.controller;

import com.ssafy.project.domain.lists.ageProhibition.dto.AgeProhibitionDTO;
import com.ssafy.project.domain.lists.amountProhibition.dto.AmountProhibitionDTO;
import com.ssafy.project.domain.lists.combinationProhibition.dto.CombinationProhibitionDTO;
import com.ssafy.project.domain.lists.medicineInformation.dto.MedicineInformationDTO;
import com.ssafy.project.domain.lists.pregnancyProhibition.dto.PregnancyProhibitionDTO;
import com.ssafy.project.domain.lists.pregnancyProhibition.entity.PregnancyProhibition;
import com.ssafy.project.domain.lists.seniorProhibition.dto.SeniorProhibitionDTO;
import com.ssafy.project.domain.lists.seniorProhibition.entity.SeniorProhibition;
import com.ssafy.project.domain.medicine.dto.MedicineDTO;
import com.ssafy.project.domain.medicine.dto.MedicineListDTO;
import com.ssafy.project.domain.medicine.dto.MedicinePreviewDTO;
import com.ssafy.project.domain.medicine.entity.Category;
import com.ssafy.project.domain.medicine.service.MedicineService;
import com.ssafy.project.global.utils.ElasticsearchService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/api/medicine")
public class MedicineController {

    private final MedicineService medicineService;
    private final HttpServletRequest request;
    private final ElasticsearchService elasticsearchService;

    //알약 전체조회
    @GetMapping ("")
    public ResponseEntity<List<MedicinePreviewDTO>> getMedicines() {
        return ResponseEntity.ok(medicineService.findAll());
    }

    // ES 전체조회
    @GetMapping ("/d")
    public ResponseEntity<List<MedicinePreviewDTO>> getMedicinestmp(@RequestParam (required = false) String prefix,
                                                                    @RequestParam (required = false) List<Category> categories) {

        return ResponseEntity.ok(elasticsearchService.searchMedicinePreview(prefix, categories));
    }

    //알약상세조회
    @GetMapping ("/{medicineId}")
    public ResponseEntity<MedicineDTO> getMedicineById(@PathVariable Integer medicineId) {
//        int id = (Integer)request.getAttribute("userId");
//        System.out.println(id);
        return ResponseEntity.ok(medicineService.findById(medicineId));
    }

    //알약상세조회 ES
    @GetMapping ("/d/{medicineId}")
    public ResponseEntity<MedicineDTO> getMedicineByIdtmp(@PathVariable Integer medicineId) {
        MedicineDTO medicineDTO = elasticsearchService.searchMedicine(medicineId);

        PregnancyProhibitionDTO pregnancyProhibitions = elasticsearchService.searchProhibition(PregnancyProhibitionDTO.class, "pregnancy_prohibition_index", medicineId);
        SeniorProhibitionDTO seniorProhibitions = elasticsearchService.searchProhibition(SeniorProhibitionDTO.class, "senior_prohibition_index", medicineId);
        AmountProhibitionDTO amountProhibitions = elasticsearchService.searchProhibition(AmountProhibitionDTO.class, "amount_prohibition_index", medicineId);
        AgeProhibitionDTO ageProhibitions = elasticsearchService.searchProhibition(AgeProhibitionDTO.class, "age_prohibition_index", medicineId);
        List<MedicineInformationDTO> medicineInformations = elasticsearchService.searchMedicineInformation(medicineId);

        medicineDTO.setPregnancyProhibition(pregnancyProhibitions);
        medicineDTO.setSeniorProhibition(seniorProhibitions);
        medicineDTO.setAmountProhibition(amountProhibitions);
        medicineDTO.setAgeProhibition(ageProhibitions);
        medicineDTO.setMedicineInformation(medicineInformations);

        return ResponseEntity.ok(medicineDTO);
    }

    @PostMapping ("/compare")
    public ResponseEntity<List<CombinationProhibitionDTO>> compareMedicine(@RequestBody MedicineListDTO medicineList) {
        return ResponseEntity.ok(medicineService.findAllCombinationProhibition(medicineList.getMedicineIds()));
    }
}
