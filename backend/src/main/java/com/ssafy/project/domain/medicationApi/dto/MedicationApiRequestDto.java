package com.ssafy.project.domain.medicationApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MedicationApiRequestDto {

    @JsonProperty("LOGINOPTION")
    private String loginOption;

    @JsonProperty("JUMIN")
    private String jumin;

    @JsonProperty("USERNAME")
    private String userName;

    @JsonProperty("HPNUMBER")
    private String hpNumber;

    @JsonProperty("TELECOMGUBUN")
    private String telecomGubun;

    @JsonProperty("DETAILPARSE")
    private int detailParse;

}
