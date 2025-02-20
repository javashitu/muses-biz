package com.muses.common.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @ClassName MapComputeUtils
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/23 14:28
 */
@Slf4j
public class MapComputeUtils {

    /**
     * 把key一样的computeObject合并在一个List里
     */
    public static <V, T> void computeMapList(Map<V, List<T>> map, T computeObject, Function<T, V> mapKeySupplier) {
        map.compute(mapKeySupplier.apply(computeObject), (key, value) -> {
            if (value == null) {
                value = Lists.newArrayList();
            }
            value.add(computeObject);
            return value;
        });
    }

    public static <V, T> void computeMapList(Map<V, List<T>> map, T computeObject, Supplier<V> mapKeySupplier) {
        map.compute(mapKeySupplier.get(), (key, value) -> {
            if (value == null) {
                value = Lists.newArrayList();
            }
            value.add(computeObject);
            return value;
        });
    }

    public static <V, T> void computeDelMapList(Map<V, List<T>> map, T computeObject, Function<T, V> mapKeySupplier, BiFunction<T, T, Boolean> compareFunction) {
        map.compute(mapKeySupplier.apply(computeObject), (key, value) -> {
            if (CollectionUtils.isEmpty(value)) {
                log.info("compute map's list is empty, so can't compute, compute key is {}", mapKeySupplier.apply(computeObject));
                return null;
            }
            for (int i = value.size() - 1; i >= 0; i--) {
                if (BooleanUtils.isTrue(compareFunction.apply(computeObject, value.get(i)))) {
                    value.remove(i);
                    break;
                }
            }
            return value;
        });
        if (CollectionUtils.isEmpty(map.get(mapKeySupplier.apply(computeObject)))) {
            map.remove(mapKeySupplier.apply(computeObject));
        }
    }
}
