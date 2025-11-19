package com.sewon.uploadservice.model.dto.car.sgn;

import com.sewon.uploadservice.model.collection.CarSpec;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record CarPropsGroupSpecCombineSpec(
    String carProps,
    String groupProps,
    String etc
) {


    public static List<CarPropsGroupSpecCombineSpec> from(CarGroupProps props) {
        String groupName = props.shouldSplitGroupProps().substring(props.shouldSplitGroupProps().indexOf(" ") + 1);
        CarSpec specs = CarSpec.getSpecs(groupName);
        if (specs == null){
            return List.of();
        }
        return Arrays.stream(
            Objects.requireNonNull(CarSpec.getSpecs(groupName)).getSpecs())
            .map(item -> new CarPropsGroupSpecCombineSpec(props.carProps(), groupName, item))
            .toList();
    }

}
