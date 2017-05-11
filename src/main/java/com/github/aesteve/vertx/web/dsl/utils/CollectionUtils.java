package com.github.aesteve.vertx.web.dsl.utils;

import java.util.LinkedHashMap;

public interface CollectionUtils {

    static<A, B>  B firstValue(LinkedHashMap<A, B> map) {
        return map.isEmpty() ? null :
                map.entrySet().iterator().next().getValue();
    }

}
