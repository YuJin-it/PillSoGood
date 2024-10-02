package com.ssafy.project.domain.medicationApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MedicationApiRequestDto {

    @JsonProperty("LOGINOPTION")
    private String loginOption;

    //AS256 암호화 필요
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
