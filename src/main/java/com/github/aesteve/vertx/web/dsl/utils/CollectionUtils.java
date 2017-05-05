package com.github.aesteve.vertx.web.dsl.utils;

import java.util.Map;

public interface CollectionUtils {

    static<A, B>  B firstValue(Map<A,B> map) {
        return map.isEmpty() ? null :
                map.entrySet().iterator().next().getValue();
    }

}
