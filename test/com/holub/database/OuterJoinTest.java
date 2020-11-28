package com.holub.database;

import com.holub.text.ParseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class OuterJoinTest {

    Database database;
    List<Map<String, String>> expected;
    Table result;

    @BeforeEach
    void setUp() throws IOException, ParseFailure {

        expected = new LinkedList<>();

        database = new Database("");
        database.execute("CREATE TABLE test1(" +
                "name INT NOT NULL," +
                "b INT NOT NULL" +
                ")");
        String[] names = new String[]{"1", "2", "3", "4"};
        String[] b = new String[]{"1", "3", "2", "5"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO test1 VALUES(\"%s\", \"%s\")", names[i], b[i]));
        }

        database.execute("CREATE TABLE test2(" +
                "a INT NOT NULL," +
                "d INT NOT NULL" +
                ")");
        names = new String[]{"1", "2", "3", "4"};
        b = new String[]{"b-1", "b-2", "b-3", "b-4"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO test2 VALUES(\"%s\", \"%s\")", names[i], b[i]));
        }

    }

    private boolean equals(Cursor c, List<Map<String, String>> expected) {
        List<Map<String, String>> data = new ArrayList<>();
        while (c.advance()) {
            Map<String, String> row = new LinkedHashMap<>();
            for (int i = 0; i < c.columnCount(); i++) {
                row.put(c.columnName(i), c.column(c.columnName(i)).toString());
            }
            data.add(row);
        }

        return data.equals(expected);
    }

    private void makeRow(Map<String, String> row, String[] columnNames, String[] data) {
        assert columnNames.length == data.length : "Size Mismatch";

        IntStream
                .range(0, columnNames.length)
                .forEach(i -> row.put(columnNames[i], data[i]));

    }

    private void addRow(List<Map<String, String>> expected, String[] columnNames, String[] data) {
        Map<String, String> row = new LinkedHashMap<>();
        makeRow(row, columnNames, data);
        expected.add(row);
    }

    @Test
    void leftOuterJoinTest() throws IOException, ParseFailure {
        result = database.execute("SELECT * FROM test1 LEFT OUTER JOIN test2 ON test1.b = test2.a");
        addRow(expected, new String[]{"name", "b", "a", "d"}, new String[]{"1", "1", "1", "b-1"});
        addRow(expected, new String[]{"name", "b", "a", "d"}, new String[]{"2", "3", "3", "b-3"});
        addRow(expected, new String[]{"name", "b", "a", "d"}, new String[]{"3", "2", "2", "b-2"});
        addRow(expected, new String[]{"name", "b", "a", "d"}, new String[]{"4", "5", "null", "null"});
        Assertions.assertTrue(equals(result.rows(), expected));

        expected.clear();

        result = database.execute("SELECT name, a FROM test1 LEFT OUTER JOIN test2 ON test1.b = test2.a");
        addRow(expected, new String[]{"name", "a"}, new String[]{"1", "1"});
        addRow(expected, new String[]{"name", "a"}, new String[]{"2", "3"});
        addRow(expected, new String[]{"name", "a"}, new String[]{"3", "2"});
        addRow(expected, new String[]{"name", "a"}, new String[]{"4", "null"});
        Assertions.assertTrue(equals(result.rows(), expected));
    }

    @Test
    void rightOuterJoinTest() throws IOException, ParseFailure {
        result = database.execute("SELECT * FROM test1 RIGHT OUTER JOIN test2 ON test1.b = test2.a");
        addRow(expected, new String[]{"name", "b", "a", "d"}, new String[]{"1", "1", "1", "b-1"});
        addRow(expected, new String[]{"name", "b", "a", "d"}, new String[]{"2", "3", "3", "b-3"});
        addRow(expected, new String[]{"name", "b", "a", "d"}, new String[]{"3", "2", "2", "b-2"});
        addRow(expected, new String[]{"name", "b", "a", "d"}, new String[]{"null", "null", "4", "b-4"});
        Assertions.assertTrue(equals(result.rows(), expected));

        expected.clear();

        result = database.execute("SELECT name, a FROM test1 RIGHT OUTER JOIN test2 ON test1.b = test2.a");
        addRow(expected, new String[]{"name", "a"}, new String[]{"1", "1"});
        addRow(expected, new String[]{"name", "a"}, new String[]{"2", "3"});
        addRow(expected, new String[]{"name", "a"}, new String[]{"3", "2"});
        addRow(expected, new String[]{"name", "a"}, new String[]{"null", "4"});
        Assertions.assertTrue(equals(result.rows(), expected));
    }

    @Test
    void tempTableShoutNotLeftInDatabase() throws IOException, ParseFailure {
        database.execute("SELECT * FROM test1 RIGHT OUTER JOIN test2 ON test1.b = test2.a");

        Assertions.assertThrows(RuntimeException.class, () -> database.execute("SELECT * FROM test1RIGHTOUTERJOINtest2"));

    }
}
