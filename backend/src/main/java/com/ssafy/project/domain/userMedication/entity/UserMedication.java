package com.ssafy.project.domain.userMedication.entity;

import com.ssafy.project.domain.userDetail.entity.UserDetail;
import com.ssafy.project.domain.userMedicationDetail.entity.UserMedicationDetail;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class UserMedication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "is_take", nullable = false)
    private Status status;

    @Column(name = "intake_at")
    private LocalDateTime intakeAt;

    @Column(name = "prescription_day")
    private int prescriptionDay;

    @Column(name = "hospital_name")
    private String hospitalName;

    @Column(name = "pharmacy_name")
    private String pharmacyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_detail_id", nullable = false)
    private UserDetail userDetail;

    @OneToMany(mappedBy = "userMedication", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<UserMedicationDetail> userMedicationDetailList = new ArrayList<>();

    public void update(String name, LocalDateTime intakeAt, int prescriptionDay, String hospitalName, String pharmacyName) {
        this.name = name;
        this.intakeAt = intakeAt;
        this.prescriptionDay = prescriptionDay;
        this.hospitalName = hospitalName;
        this.pharmacyName = pharmacyName;
    }

    //복약 상태 업데이트
    public void updateStatus(Status status) {
        this.status = status;
    }
}
