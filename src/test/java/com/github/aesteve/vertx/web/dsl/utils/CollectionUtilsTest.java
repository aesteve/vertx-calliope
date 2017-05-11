package com.github.aesteve.vertx.web.dsl.utils;

import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CollectionUtilsTest {

    @Test
    public void firstValueOfEmptyMapIsNull() {
        assertNull(CollectionUtils.firstValue(new LinkedHashMap<>()));
    }

    @Test
    public void getFirstValueOfNonEmptyMap() {
        LinkedHashMap<String, Integer> test = new LinkedHashMap<>();
        test.put("a", 1);
        test.put("b", 2);
        test.put("c", 3);
        assertEquals((Integer)1, CollectionUtils.firstValue(test));
    }

}
