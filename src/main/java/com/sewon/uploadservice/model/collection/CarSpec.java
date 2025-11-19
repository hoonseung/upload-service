package com.sewon.uploadservice.model.collection;

import com.sewon.uploadservice.model.dto.car.sgn.CarGroupProps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarSpec {

    MAIN("MAIN", new String[]{"패신저"}),
    FLOOR("FLOOR", new String[]{"카고밴", "샤시캡"}),
    FEM("FEM", new String[]{"패신저", "카고밴", "샤시캡"}),
    F_PAS("FPAS", new String[]{"패신저", "카고밴", "샤시캡"}),
    R_PAS("RPAS", new String[]{"패신저", "카고밴", "샤시캡"}),
    B_WEX("BWEX", new String[]{"샤시캡"}),
    LAMP_EXT("LAMP_EXT", new String[]{"샤시캡"}),
    T_GATE("T/GATE", new String[]{"패신저"}),
    T_GATE_EXT("T/GATE_EXT", new String[]{"패신저"}),
    F_RDR_DRI("FRDR_DRI", new String[]{"패신저", "카고밴", "샤시캡"}),
    F_RDR_ASS("FRDR_ASS", new String[]{"패신저", "카고밴", "샤시캡"}),
    T_SWG_LH("T/SWG LH", new String[]{"카고밴"}),
    T_SWG_RH("T/SWG RH", new String[]{"카고밴"}),
    T_SWG_LH_EXTN("T/SWG LH EXTN", new String[]{"카고밴"}),
    T_SWG_RH_EXTN("T/SWG RH EXTN", new String[]{"카고밴"}),
    ROOF_FRT("ROOF FRT", new String[]{"패신저", "카고밴", "샤시캡"}),
    ROOF_CTR("ROOF CTR", new String[]{"패신저"}),
    ROOF_RR("ROOT RR", new String[]{"패신저"}),

    ;



    private final String name;
    private final String[] specs;

    public static CarSpec getSpecs(String groupName){
        for(CarSpec carSpec : CarSpec.values()){
            if(carSpec.getName().equals(groupName)){
                return carSpec;
            }
        }
        return null;
    }

    public static String[] getSpecArray(String groupName){
        CarSpec spec = getSpecs(groupName);
        return spec == null ? new String[]{} : spec.getSpecs();
    }

    public static int getSpecSize(CarGroupProps props){
        String groupName = props.shouldSplitGroupProps().substring(props.shouldSplitGroupProps().indexOf(" ") + 1);
        CarSpec spec = getSpecs(groupName);
        if (spec == null) return 0;
        return spec.getSpecs().length;
    }
}
