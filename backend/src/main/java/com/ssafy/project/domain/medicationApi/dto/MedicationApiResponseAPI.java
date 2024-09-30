package com.ssafy.project.domain.medicationApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicationApiResponseAPI {

    private String errCode;
    private String errMsg;
    private String result;
    private DataDto data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataDto {
        private String callbackId;
        private String callbackType;
        private String callbackData;
        private int timeout;
    }
}
