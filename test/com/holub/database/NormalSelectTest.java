package com.holub.database;

import com.holub.text.ParseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NormalSelectTest {

    Table result;
    Database database;

    @BeforeEach
    void setUp() throws IOException, ParseFailure {

        database = new Database("");
        database.execute("CREATE TABLE test(" +
                "a INT NOT NULL," +
                "b INT NOT NULL," +
                "c INT NOT NULL" +
                ")");
        String[] a = new String[]{"1", "2", "3", "4"};
        String[] b = new String[]{"1", "2", "3", "4"};
        String[] c = new String[]{"1", "2", "3", "4"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO test VALUES(\"%s\", \"%s\", \"%s\")", a[i], b[i], c[i]));
        }

    }

    private boolean containColumn(Table t, String columnName) {
        Cursor c = t.rows();
        List<String> columns = new ArrayList<>();
        int length = c.columnCount();
        for (int i = 0; i < length; i++) {
            columns.add(c.columnName(i));
        }

        return columns.contains(columnName);
    }

    @Test
    void SelectAllContainsAllColumns() throws IOException, ParseFailure {
        result = database.execute("SELECT * FROM test");
        Assertions.assertTrue(containColumn(result, "a"));
        Assertions.assertTrue(containColumn(result, "b"));
        Assertions.assertTrue(containColumn(result, "c"));
    }

    @Test
    void ColumnNotSelectShouldNotExistInResult() throws IOException, ParseFailure {
        result = database.execute("SELECT a FROM test");
        Assertions.assertTrue(containColumn(result, "a"));
        Assertions.assertFalse(containColumn(result, "b"));
        Assertions.assertFalse(containColumn(result, "c"));
        result = database.execute("SELECT b, c FROM test");
        Assertions.assertFalse(containColumn(result, "a"));
        Assertions.assertTrue(containColumn(result, "b"));
        Assertions.assertTrue(containColumn(result, "c"));
    }
}
