package com.ssafy.project.domain.medicationApi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.project.domain.medicationApi.dto.MedicationApiRequestDto;
import com.ssafy.project.domain.medicationApi.dto.MedicationApiResponseAPI;
import com.ssafy.project.domain.userMedication.dto.UserMedicationRequestDTO;
import com.ssafy.project.domain.userMedication.entity.Status;
import com.ssafy.project.domain.userMedicationDetail.dto.UserMedicationDetailRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MedicationApiService {

    @Value ("${MEDICATION_API_KEY}")
    private String apiKey;
    private final WebClient webClient;

    public MedicationApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://datahub-dev.scraping.co.kr/scrap")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    //카카오톡 인증
    public String sendKakaoAuthentication(MedicationApiRequestDto requestdto) {
        try {
            requestdto.setJumin(encrypt(requestdto.getJumin()));
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
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //API 요청
    public List<UserMedicationRequestDTO> requestMedication(String callbackId, int userId) {
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
            return Collections.emptyList();
        }

        System.out.println(response);

        // errCode가 null인지 체크
        Object errCode = response.get("errCode");
        if (errCode == null) {
            System.out.println("errCode is null");
            return Collections.emptyList();
        }

//        String jsonString = response.toString();  // response를 JSON 문자열로 변환
        List<UserMedicationRequestDTO> parsedData = null;
        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        try {
            // Map을 JSON 형식의 문자열로 변환
            jsonString = mapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        try {
            // 아까 만든 parseMedicationData 함수 사용
            parsedData = parseMedicationData(jsonString, userId);
        }
        catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        // 파싱된 데이터가 없을 경우 처리
        if (parsedData.isEmpty()) {
            System.out.println("Parsed data is empty");
            return Collections.emptyList();
        }

        System.out.println(parsedData);
        return parsedData; // 필요한 데이터만 반환
    }

    public static List<UserMedicationRequestDTO> parseMedicationData(String jsonString, int userId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonString);

        List<UserMedicationRequestDTO> medicationList = new ArrayList<>();

        JsonNode medicineList = root.path("data").path("MEDICINELIST");

        for (JsonNode medicineNode : medicineList) {
            String treatMedicalName = medicineNode.path("TREATMEDICALNM").asText();
            String treatDateStr = medicineNode.path("TREATDATE").asText();
            LocalDate treatDate = LocalDate.parse(treatDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
            int prescriptionDay = medicineNode.path("PRESCRIBECNT").asInt();

            List<UserMedicationDetailRequestDTO> medicationDetails = new ArrayList<>();

            JsonNode detailList = medicineNode.path("DETAILLIST");
            for (JsonNode detailNode : detailList) {
                JsonNode drugInfoList = detailNode.path("DRUGINFOLIST");

                for (JsonNode drugInfoNode : drugInfoList) {
                    String payInfo = drugInfoNode.path("PAYINFO").asText();
                    if (!payInfo.isEmpty() && payInfo.contains("-")) {
                        int medicineId = Integer.parseInt(payInfo.split("-")[0]);

                        UserMedicationDetailRequestDTO detail = new UserMedicationDetailRequestDTO(
                                0, 1, 1, medicineId
                        );
                        medicationDetails.add(detail);
                    }
                }
            }

            if (!medicationDetails.isEmpty()) {
                UserMedicationRequestDTO medicationRequest = new UserMedicationRequestDTO(
                        treatMedicalName, Status.STOPPED, userId, treatDate.atStartOfDay(),
                        prescriptionDay, "Unknown", treatMedicalName, medicationDetails
                );
                medicationList.add(medicationRequest);
            }
        }
        return medicationList;
    }

    // 암호화
    public String encrypt(String plaintext) throws Exception {
        // AES 비밀키 생성
        String secretKey = System.getenv("AES_SECRET_KEY");
        String initVector = System.getenv("AES_INIT_VECTOR");
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");

        // 초기화 벡터 생성
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));

        // 암호화 모드 설정 (CBC 모드, PKCS5 패딩)
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        // 암호화 수행
        byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));

        // Base64로 인코딩하여 반환
        return Base64.getEncoder().encodeToString(encrypted);
    }
}
