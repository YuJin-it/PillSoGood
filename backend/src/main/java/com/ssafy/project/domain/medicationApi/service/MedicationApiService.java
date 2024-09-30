package com.ssafy.project.domain.medicationApi.service;

import com.ssafy.project.domain.medicationApi.dto.MedicationApiRequestDto;
import com.ssafy.project.domain.medicationApi.dto.MedicationApiResponseAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MedicationApiService {

    @Value("${MEDICATION_API_KEY}")
    private String apiKey;
    private final WebClient webClient;

    public MedicationApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://datahub-dev.scraping.co.kr/scrap")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    //카카오톡 인증
    public String sendKakaoAuthentication(MedicationApiRequestDto requestdto) {

        MedicationApiResponseAPI response = webClient.post()
                .uri("/common/nhis/TreatmentDosageInfoSimple")
                .header("Authorization", apiKey)
                .bodyValue(requestdto)
                .retrieve() //응답가져옴
                .bodyToMono(MedicationApiResponseAPI.class) //변환
                .block(); //비동기 처리 대기

        System.out.println(response.toString());
        return response.getData().getCallbackId();
    }

    //API 요청
    public Map<String, Object> requestMedication(String callbackId) {
        // WebClient로 API 요청
        Map<String, Object> response = webClient.post()
                .uri("/captcha")
                .header("Authorization", apiKey)
                .bodyValue(Collections.singletonMap("callbackId", callbackId))
                .retrieve()
                .bodyToMono(Map.class)
                .block(); // 비동기 결과를 동기 방식으로 기다림

        // response가 null인지 체크
        if (response == null) {
            System.out.println("Response is null");
            return Collections.emptyMap(); // response가 null이면 빈 맵 반환
        }

        System.out.println(response);

        // errCode가 null인지 체크
        Object errCode = response.get("errCode");
        if (errCode == null) {
            System.out.println("errCode is null");
            return Collections.emptyMap(); // errCode가 없으면 빈 맵 반환
        }

        // 필요한 정보를 추출
        Map<String, Object> extractedData = Map.of(
                "errCode", errCode,
                "TREATMEDICALNM", extractField(response, "TREATMEDICALNM"),
                "TREATDATE", extractField(response, "TREATDATE"),
                "PRESCRIBECNT", extractField(response, "PRESCRIBECNT"),
                "MEDICINENM", extractField(response, "MEDICINENM"),
                "ADMINISTERCNT", extractField(response, "ADMINISTERCNT"),
                "PAYINFO", extractField(response, "PAYINFO")
        );

        System.out.println(extractedData);
        return extractedData; // 필요한 데이터만 반환
    }

    private String extractField(Map<String, Object> data, String key) {
        if (data == null || !data.containsKey("data")) {
            System.out.println("Data is null or does not contain 'data' key");
            return null; // data가 null이거나 "data" 키가 없으면 null 반환
        }

        Map<String, Object> dataMap = (Map<String, Object>) data.get("data");
        if (dataMap == null || !dataMap.containsKey("MEDICINELIST")) {
            System.out.println("dataMap is null or does not contain 'MEDICINELIST' key");
            return null; // dataMap이 null이거나 "MEDICINELIST" 키가 없으면 null 반환
        }

        // MEDICINELIST에서 원하는 필드 추출
        List<Map<String, Object>> medicineList = (List<Map<String, Object>>) dataMap.get("MEDICINELIST");
        if (medicineList == null || medicineList.isEmpty()) {
            System.out.println("medicineList is null or empty");
            return null; // medicineList가 null이거나 비어 있으면 null 반환
        }

        Object fieldValue = medicineList.get(0).get(key);
        return fieldValue != null ? fieldValue.toString() : null; // fieldValue가 null인지 체크 후 반환
    }

}
