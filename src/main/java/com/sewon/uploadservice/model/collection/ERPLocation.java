package com.sewon.uploadservice.model.collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ERPLocation {

    // 검사완료
    PYEONGTAEK_COMPLETION("10", "6000", "평택완제품"),
    GYEONGSAN_COMPLETION("10", "6010", "경산완제품"),

    // ERP_검사대기
    PYEONGTAEK_WEIHAI_INSPECTION_WAITING("10", "5300", "위해검사대기-평택"),
    PYEONGTAEK_DEZHOU_INSPECTION_WAITING("10", "5320", "덕주검사대기-평택"),
    PYEONGTAEK_VINA_INSPECTION_WAITING("10", "5330", "우신검사대기-평택"),
    GYEONGSAN_WEIHAI_INSPECTION_WAITING("10", "5301", "위해검사대기-경산"),
    GYEONGSAN_DEZHOU_INSPECTION_WAITING("10", "5321", "덕주검사대기-경산"),
    GYEONGSAN_VINA_INSPECTION_WAITING("10", "5331", "우신검사대기-경산"),


    // 국내입고대기
    PYEONGTAEK_BOND("10", "5200", "평택"),
    WEIHAI_IMPORT_WAITING("12", "5100", "위해"),
    DEZHOU_IMPORT_WAITING("14", "5100", "덕주"),
    VINA_IMPORT_WAITING("15", "5100", "우신비나"),

    // 장기성재고
    PYEONGTAEK_LONG_TERM_STOCK("10", "6200", "장기성창고"),

    // 고객반송
    OEM_20("10", "AA20", "매출창고 청주유라"),
    OEM_30("10", "AA30", "유라-매출저장위치"),

    ;

    private final String factory;
    private final String location;
    private final String description;


    public static ERPLocation getLocationByFactoryAndLocation(String factory, String location) {
        for (ERPLocation erpLocation : ERPLocation.values()) {
            if (erpLocation.factory.equals(factory) && erpLocation.location.equals(location)) {
                return erpLocation;
            }
        }
        return null;
    }
}
