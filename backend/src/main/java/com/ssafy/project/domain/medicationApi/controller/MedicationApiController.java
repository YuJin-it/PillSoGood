package com.ssafy.project.domain.medicationApi.controller;

import com.ssafy.project.domain.medicationApi.dto.MedicationApiRequestDto;
import com.ssafy.project.domain.medicationApi.dto.MedicationApiResponseAPI;
import com.ssafy.project.domain.medicationApi.service.MedicationApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medication-api")
public class MedicationApiController {

    private final MedicationApiService medicationApiService;

    @PostMapping("/certify")
    public ResponseEntity<String> certifyKakao(@RequestBody MedicationApiRequestDto request) {
        System.out.println(request.toString());
        return ResponseEntity.ok(medicationApiService.sendKakaoAuthentication(request));
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestMedication(@RequestBody String callbackId) {
        // 서비스 호출 후 필요한 필드만 추출
        return ResponseEntity.ok(medicationApiService.requestMedication(callbackId));
    }
}
