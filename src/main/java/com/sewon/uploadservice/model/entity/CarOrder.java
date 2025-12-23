package com.sewon.uploadservice.model.entity;

import com.sewon.uploadservice.model.collection.ERPLocation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CarOrder {

    private Long id;

    private String category;        // 구분

    private String supplier;        // 납입처

    private String itemName;        // 품명 null

    private String alc;             // ALC

    private String itemCode;        // 품번

    private String remark;          // 비고 null

    private String tTime;           // T-TIME

    private String space;          // 공백

    private Integer result;         // 실적

    private Integer line;            // 라인

    private Integer yra;            // 유라

    private Integer inhouse;         // 사내 (중복 발생 → inhouse1, inhouse2 권장)

    private Integer inspection;      // 검사

    private Integer container;       // CT (중복 발생 → ct1, ct2 권장)

    private Integer longTerm;        // 장기

    private Integer oem;             // OEM

    private Integer inhouseRelease;    // 사내출고 null (template 용)
    private Integer urgentRelease;    // 긴급출고 null (template 용)
    private Integer sameDayRelease;  // 당일출고 null (template 용)
    private Integer dPlus3Release;   // D+3출고 null (template 용)
    private Integer containerRelease;   // CT출고 null (template 용)
    private Integer box1; // mes box 1 ~ 5 null  (template 용)
    private Integer box2;
    private Integer box3;
    private Integer box4;
    private Integer box5;

    private Integer total;          // 합계 null
    private Integer space12; // 12번째 공백

    private Integer d;

    private Integer dPlus1;

    private Integer dPlus2;

    private Integer dPlus3;

    private Integer dPlus4;

    private Integer dPlus5;

    private Integer dPlus6;

    private Integer dPlus7;

    private Integer dPlus8;

    private Integer dPlus9;

    private Integer dPlus10;

    private Integer dPlus11;

    private Integer dPlus12;

    private Integer dPlus13;

    private Integer dPlus14;

    private Integer dPlus15;

    private Integer dPlus16;

    private Integer dPlus17;

    private Integer dPlus18;

    private Integer dPlus19;

    private Integer dPlus20;

    private Integer rem;

    private String ctLocations;

    private LocalDate uploadDate;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;

    private String factory;


    public static String parseCtLocation(Set<ERPLocation> locations) {
        StringBuilder sb = new StringBuilder();
        if (locations != null && !locations.isEmpty()) {
            int idx = 0;
            int size = locations.size();
            for (ERPLocation it : locations) {
                sb.append(it.getDescription());
                idx++;
                if (idx < size) {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
    }
}
