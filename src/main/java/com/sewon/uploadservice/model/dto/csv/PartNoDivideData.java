package com.sewon.uploadservice.model.dto.csv;

import java.time.LocalDate;

public record PartNoDivideData(
    String item,
    String partNo,
    String site,
    String etc,
    String fixed,
    LocalDate stDate
) {

    public static PartNoDivideData of(String item, String partNo, String site, String etc, String fixed,
        LocalDate stDate) {
        return new PartNoDivideData(item, partNo, site, etc, fixed, stDate);
    }
}
