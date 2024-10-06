package com.ssafy.project.domain.lists.combinationProhibition.entity;

import com.ssafy.project.domain.medicine.entity.Medicine;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@AllArgsConstructor (access = AccessLevel.PRIVATE)
@Builder
public class CombinationProhibition {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private int id;

    // 첫 번째 약물
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "medicine_id_A", referencedColumnName = "id")
    private Medicine medicineA;

    // 두 번째 약물
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "medicine_id_B", referencedColumnName = "id")
    private Medicine medicineB;

    @Column (name = "effect", columnDefinition = "TEXT")
    private String effect;

    @Column (name = "name_A", columnDefinition = "TEXT")
    private String nameA;

    @Column (name = "name_B", columnDefinition = "TEXT")
    private String nameB;
}
