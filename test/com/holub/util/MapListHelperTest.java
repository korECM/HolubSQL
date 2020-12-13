package com.holub.util;

import com.holub.database.Table;
import com.holub.database.TableFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class MapListHelperTest {

    Table result;
    MapListHelper mapListHelper;

    @BeforeEach
    void setUp() {
        mapListHelper = new MapListHelper();
    }


    @Test
    @DisplayName("verify는 테이블과 주어진 데이터가 일치하면 true를 반환한다")
    void verifyWhenCorrect() {
        mapListHelper.setColumnName("a");
        mapListHelper.addRow("1");
        mapListHelper.addRow("2");
        mapListHelper.addRow("3");

        result = TableFactory.create("test", new String[]{"a"});
        result.insert(Collections.singletonList("1"));
        result.insert(Collections.singletonList("2"));
        result.insert(Collections.singletonList("3"));

        Assertions.assertTrue(mapListHelper.verify(result));
    }

    @Test
    @DisplayName("verify는 테이블과 주어진 데이터가 일치하지 않으면 false를 반환한다")
    void verifyWhenIncorrect() {
        mapListHelper.setPrintReason(false);

        mapListHelper.setColumnName("a");
        mapListHelper.addRow("1");
        mapListHelper.addRow("2");
        mapListHelper.addRow("4");

        result = TableFactory.create("test", new String[]{"a"});
        result.insert(Collections.singletonList("1"));
        result.insert(Collections.singletonList("2"));
        result.insert(Collections.singletonList("3"));

        Assertions.assertFalse(mapListHelper.verify(result));
        mapListHelper.clear();

        mapListHelper.setColumnName("a");
        mapListHelper.addRow("1");
        mapListHelper.addRow("2");
        mapListHelper.addRow("3");
        mapListHelper.addRow("4");
        Assertions.assertFalse(mapListHelper.verify(result));
        mapListHelper.clear();

        mapListHelper.setColumnName("a");
        mapListHelper.addRow("1");
        mapListHelper.addRow("2");
        Assertions.assertFalse(mapListHelper.verify(result));
        mapListHelper.clear();

        mapListHelper.setColumnName("a");
        mapListHelper.addRow("1");
        mapListHelper.addRow("3");
        mapListHelper.addRow("2");
        Assertions.assertFalse(mapListHelper.verify(result));
        mapListHelper.clear();
    }

    @Test
    @DisplayName("주어진 column의 수와 data의 수가 맞지 않으면 AssertionError를 던진다")
    void testSizeMismatch() {
        mapListHelper.setColumnName("a");
        Assertions.assertThrows(AssertionError.class, () -> mapListHelper.addRow("1", "2"));
        mapListHelper.clear();

        mapListHelper.setColumnName("a", "b");
        Assertions.assertThrows(AssertionError.class, () -> mapListHelper.addRow("1"));
        mapListHelper.clear();

        mapListHelper.setColumnName("a");
        Assertions.assertDoesNotThrow(() -> mapListHelper.addRow("1"));
    }

}