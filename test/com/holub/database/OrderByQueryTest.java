package com.holub.database;

import com.holub.text.ParseFailure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OrderByQueryTest {

    Database database;
    List<Map<String, String>> expected;

    @BeforeEach
    void setUp() throws IOException, ParseFailure {

        expected = new LinkedList<>();

        database = new Database("");
        database.execute("CREATE TABLE school(" +
                "name VARCHAR NOT NULL, " +
                "year INT NOT NULL, " +
                "isGood VARCHAR NOT NULL, " +
                "PRIMARY KEY(name) " +
                ")");
        String[] names = new String[]{"CAU", "SNU", "HYU", "SU"};
        String[] years = new String[]{"1", "3", "3", "2"};
        String[] isGoods = new String[]{"true", "false", "false", "false"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO school VALUES(\"%s\", \"%s\", \"%s\")", names[i], years[i],
                    isGoods[i]));
        }

        database.execute("CREATE TABLE person(" +
                "name VARCHAR NOT NULL, " +
                "age INT NOT NULL, " +
                "schoolName INT NOT NULL, " +
                "PRIMARY KEY(name) " +
                ")");

        names = new String[]{"A", "B", "C", "D"};
        String[] ages = new String[]{"3", "3", "3", "5"};
        String[] schoolNames = new String[]{"CAU", "HYU", "SNU", "CAU"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO person VALUES(\"%s\", \"%s\",\"%s\")", names[i], ages[i],
                    schoolNames[i]));
        }
    }

    @AfterEach
    void clear() {
        expected.clear();
    }

    private boolean isSorted(Cursor c, String columnName, boolean isAsc) {
        List<String> data = new ArrayList<>();
        while (c.advance()) {
            data.add(c.column(columnName).toString());
        }
        List<String> sortedList = data.stream().sorted().collect(Collectors.toList());
        if (isAsc == false) Collections.reverse(sortedList);
        return sortedList.equals(data);
    }

    private boolean isSorted(Cursor c, List<Map<String, String>> expected) {
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

    private boolean doesColumnExist(Cursor c, String columnName) {
        for (int i = 0; i < c.columnCount(); i++) {
            if (c.columnName(i).equals(columnName)) return true;
        }
        return false;
    }

    @Test
    void orderByOneColumn() throws IOException, ParseFailure {
        Table result;

        result = database.execute("SELECT name, year FROM school ORDER BY year");
        Assertions.assertTrue(isSorted(result.rows(), "year", true));

        result = database.execute("SELECT name, year FROM school ORDER BY year DESC");
        Assertions.assertTrue(isSorted(result.rows(), "year", false));
    }

    @Test
    void orderByMultipleColumn() throws IOException, ParseFailure {
        Table result;

        result = database.execute("SELECT name, year FROM school ORDER BY year, name");

        addRow(expected, new String[]{"name", "year"}, new String[]{"CAU", "1"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"SU", "2"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"HYU", "3"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"SNU", "3"});

        Assertions.assertTrue(isSorted(result.rows(), expected));

    }

    @Test
    void orderByMultipleColumnWithMultipleDirection() throws IOException, ParseFailure {
        Table result;

        result = database.execute("SELECT name, year FROM school ORDER BY year DESC, name ASC");

        addRow(expected, new String[]{"name", "year"}, new String[]{"HYU", "3"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"SNU", "3"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"SU", "2"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"CAU", "1"});

        Assertions.assertTrue(isSorted(result.rows(), expected));

    }

    @Test
    void orderByColumnWhichIsNotExistInSelect() throws IOException, ParseFailure {
        Table result;

        result = database.execute("SELECT name, year FROM school ORDER BY isGood");

        addRow(expected, new String[]{"name", "year"}, new String[]{"SNU", "3"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"HYU", "3"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"SU", "2"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"CAU", "1"});

        Assertions.assertTrue(isSorted(result.rows(), expected));
        Assertions.assertTrue(doesColumnExist(result.rows(), "isGood") == false);

        result = database.execute("SELECT name, year FROM school ORDER BY isGood DESC");

        expected.clear();
        addRow(expected, new String[]{"name", "year"}, new String[]{"CAU", "1"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"SNU", "3"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"HYU", "3"});
        addRow(expected, new String[]{"name", "year"}, new String[]{"SU", "2"});

        Assertions.assertTrue(isSorted(result.rows(), expected));
        Assertions.assertTrue(doesColumnExist(result.rows(), "isGood") == false);

    }

    @Test
    void orderWithSelectAsterisk() throws IOException, ParseFailure {
        Table result;

        result = database.execute("SELECT * FROM school ORDER BY year DESC");
        Assertions.assertTrue(isSorted(result.rows(), "year", false));
    }

    @Test
    void orderWithJoin() throws IOException, ParseFailure {
        Table result;
        result = database.execute("SELECT * FROM person, school WHERE person.schoolName=school.name ORDER BY name " +
                "DESC");
        Assertions.assertTrue(isSorted(result.rows(), "name", false));
    }

    @Test
    void orderByWithOutColumnName() throws IOException, ParseFailure {
        Assertions.assertThrows(ParseFailure.class, () -> database.execute("SELECT name, year FROM school ORDER BY"));
    }
}
