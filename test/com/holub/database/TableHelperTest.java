package com.holub.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class TableHelperTest {

    @Test
    @DisplayName("주어진 테이블에 대해 정상적인 List<Map<String, String>>을 반환한다")
    void createListTest() {
        Table result = TableFactory.create("test", new String[]{"a", "b"});
        result.insert(Arrays.asList("1", "0"));
        result.insert(Arrays.asList("2", "1"));
        result.insert(Arrays.asList("3", "2"));

        List<Map<String, String>> mapList = TableHelper.tableToMapList(result);
        Assertions.assertEquals("1", mapList.get(0).getOrDefault("a", "null"));
        Assertions.assertEquals("0", mapList.get(0).getOrDefault("b", "null"));
        Assertions.assertEquals("2", mapList.get(1).getOrDefault("a", "null"));
        Assertions.assertEquals("1", mapList.get(1).getOrDefault("b", "null"));
        Assertions.assertEquals("3", mapList.get(2).getOrDefault("a", "null"));
        Assertions.assertEquals("2", mapList.get(2).getOrDefault("b", "null"));
    }

    @Test
    @DisplayName("column List가 주어진 경우 여기에 해당하는 column만 포함해야한다")
    void createListTestWhenColumnListGiven(){
        Table result = TableFactory.create("test", new String[]{"a", "b"});
        result.insert(Arrays.asList("1", "0"));
        result.insert(Arrays.asList("2", "1"));
        result.insert(Arrays.asList("3", "2"));

        List<Map<String, String>> mapList = TableHelper.tableToMapList(result.rows(), Collections.singletonList("a"));
        Assertions.assertEquals("1", mapList.get(0).getOrDefault("a", "null"));
        Assertions.assertEquals("null", mapList.get(0).getOrDefault("b", "null"));
        Assertions.assertEquals("2", mapList.get(1).getOrDefault("a", "null"));
        Assertions.assertEquals("null", mapList.get(1).getOrDefault("b", "null"));
        Assertions.assertEquals("3", mapList.get(2).getOrDefault("a", "null"));
        Assertions.assertEquals("null", mapList.get(2).getOrDefault("b", "null"));
    }
}