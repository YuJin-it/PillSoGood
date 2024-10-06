package com.ssafy.project.domain.userDetail.entity;

import com.ssafy.project.domain.user.entity.User;
import com.ssafy.project.domain.userMedication.entity.UserMedication;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class UserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "family", nullable = false)
    private String family; //가족 정보

    @Column(name = "birth")
    private LocalDate birth;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "height")
    private double height;

    @Column(name = "weight")
    private double weight;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "pregnancy")
    private Pregnancy pregnancy;

    @Column(name = "allergy")
    private String allergy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "userDetail", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<UserMedication> userMedicationList;

    public List<String> getAllergies() {
        return Arrays.asList(this.allergy.split(","));
    }

    public void updateAllergies(List<String> allergies) {
        this.allergy = String.join(",", allergies);
    }

    public void updateFamily(String family) {
        this.family = family;
    }

    public void updateHeight(double height) {
        if (height > 0) {
            this.height = height;
        }
    }

    public void updateWeight(double weight) {
        if (weight > 0) {
            this.weight = weight;
        }
    }

    public void updatePregnancy(Pregnancy pregnancy) {
        this.pregnancy = pregnancy;
    }

    public void updateBirth(LocalDate birth) {
        this.birth = birth;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }
}
