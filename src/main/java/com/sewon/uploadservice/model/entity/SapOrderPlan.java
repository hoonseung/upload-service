package com.sewon.uploadservice.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SapOrderPlan {

    private LocalDate uploadDate;
    private String carProps;
    private String alc;
    private String doorType;
    private String region;
    private String airBag;
    private String groupProps;
    private String partNo;
    private String factory;
    private String version;
    private Integer circuitCount;
    private Integer result;
    private Integer trim;
    private Integer line;
    private Integer yra;
    private Integer oem;
    private Integer inhouse;
    private Integer inspection;
    private Integer ct;
    private Integer ct2;
    private Integer assembly;
    private Integer sub;
    private Integer setComplete;
    private Integer setProcessing;
    private Integer total;
    private Integer weihai;
    private Integer rizhao;
    private Integer dezhou;
    private Integer vina;
    private Integer day0;
    private Integer day1;
    private Integer day2;
    private Integer day3;
    private Integer day4;
    private Integer day5;
    private Integer day6;
    private Integer day7;
    private Integer day8;
    private Integer day9;
    private Integer day10;
    private Integer day11;
    private Integer day12;
    private Integer day13;
    private Integer day14;
    private Integer day15;
    private Integer day16;
    private Integer day17;
    private Integer day18;
    private Integer day19;
    private Integer day20;
    private Integer day21;
    private Integer day22;
    private Integer day23;
    private Integer day24;
    private Integer day25;
    private Integer day26;
    private Integer day27;
    private Integer day28;
    private Integer day29;
    private Integer day30;
    private Integer rem;
    private Integer hTotal;
    private Integer m;
    private Integer nextMonth;
    private Integer mitu;
    private Integer longStock;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;



    public static SapOrderPlan of (LocalDate uploadDate,
     String carProps,
     String alc,
     String doorType,
     String region,
     String airBag,
     String groupProps,
     String partNo,
     String factory,
     String version,
     Integer circuitCount,
     Integer result,
     Integer trim,
     Integer line,
     Integer yra,
     Integer oem,
     Integer inhouse,
     Integer inspection,
     Integer ct,
     Integer ct2,
     Integer set,
     Integer sub,
     Integer setComplete,
     Integer subProcessing,
     Integer total,
     Integer weihai,
     Integer rizhao,
     Integer dezhou,
     Integer vina,
     Integer day0,
     Integer day1,
     Integer day2,
     Integer day3,
     Integer day4,
     Integer day5,
     Integer day6,
     Integer day7,
     Integer day8,
     Integer day9,
     Integer day10,
     Integer day11,
     Integer day12,
     Integer day13,
     Integer day14,
     Integer day15,
     Integer day16,
     Integer day17,
     Integer day18,
     Integer day19,
     Integer day20,
     Integer day21,
     Integer day22,
     Integer day23,
     Integer day24,
     Integer day25,
     Integer day26,
     Integer day27,
     Integer day28,
     Integer day29,
     Integer day30,
     Integer rem,
     Integer hTotal,
     Integer m,
     Integer nextMonth,
     Integer mitu,
     Integer longStock
    ){
        return new SapOrderPlan(
            uploadDate,
            carProps,
            alc,
            doorType,
            region,
            airBag,
            groupProps,
            partNo,
            factory,
            version,
            circuitCount,
            result,
            trim,
            line,
            yra,
            oem,
            inhouse,
            inspection,
            ct,
            ct2,
            set,
            sub,
            setComplete,
            subProcessing,
            total,
            weihai,
            rizhao,
            dezhou,
            vina,
            day0,
            day1,
            day2,
            day3,
            day4,
            day5,
            day6,
            day7,
            day8,
            day9,
            day10,
            day11,
            day12,
            day13,
            day14,
            day15,
            day16,
            day17,
            day18,
            day19,
            day20,
            day21,
            day22,
            day23,
            day24,
            day25,
            day26,
            day27,
            day28,
            day29,
            day30,
            rem,
            hTotal,
            m,
            nextMonth,
            mitu,
            longStock,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
