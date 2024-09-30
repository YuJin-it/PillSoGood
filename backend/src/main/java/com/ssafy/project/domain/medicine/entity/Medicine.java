package com.ssafy.project.domain.medicine.entity;

import com.ssafy.project.domain.lists.ageProhibition.entity.AgeProhibition;
import com.ssafy.project.domain.lists.amountProhibition.entity.AmountProhibition;
import com.ssafy.project.domain.lists.medicineInformation.entity.MedicineInformation;
import com.ssafy.project.domain.lists.pregnancyProhibition.entity.PregnancyProhibition;
import com.ssafy.project.domain.lists.seniorProhibition.entity.SeniorProhibition;
import com.ssafy.project.domain.userMedicationDetail.entity.UserMedicationDetail;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "kor_name", nullable = false, columnDefinition = "TEXT")
    private String korName;

    @Column(name = "eng_name", columnDefinition = "TEXT")
    private String engName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Column(name = "company", columnDefinition = "TEXT")
    private String company;

    @Column(name = "drug_form", columnDefinition = "TEXT")
    private String drugForm;

    @Column(name = "characters", columnDefinition = "TEXT")
    private String characters;

    @Column(name = "kind", columnDefinition = "TEXT")
    private String kind;

    @Column(name = "effect", columnDefinition = "TEXT")
    private String effect;

    @Column(name = "usages", columnDefinition = "TEXT")
    private String usages;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    // Medicine이 여러 MedicineInformation을 갖고 있는 일대다 관계 설정
    @OneToMany(mappedBy = "medicine", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MedicineInformation> medicineInformation = new ArrayList<>();

    @OneToOne(mappedBy = "medicine", fetch = FetchType.LAZY)
    private AgeProhibition ageProhibition;

    @OneToOne(mappedBy = "medicine", fetch = FetchType.LAZY)
    private AmountProhibition amountProhibition;

    @OneToOne(mappedBy = "medicine", fetch = FetchType.LAZY)
    private PregnancyProhibition pregnancyProhibition;

    @OneToOne(mappedBy = "medicine", fetch = FetchType.LAZY)
    private SeniorProhibition seniorProhibition;
}
