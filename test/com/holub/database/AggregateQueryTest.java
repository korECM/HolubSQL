package com.holub.database;

import com.holub.text.ParseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AggregateQueryTest {
    Database database;
    List<Map<String, String>> expected;
    Table result;

    @BeforeEach
    void setUp() throws IOException, ParseFailure {

        expected = new LinkedList<>();

        database = new Database("");
        database.execute("CREATE TABLE school(" +
                "year INT NOT NULL" +
                ")");
        String[] years = new String[]{"1", "3", "3", "5"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO school VALUES(\"%s\")", years[i]));
        }

    }

    private String column(Table t, String columnName) {
        Cursor c = t.rows();
        c.advance();
        return c.column(columnName).toString();
    }

    @Test
    void max() throws IOException, ParseFailure {
        result = database.execute("SELECT MAX(year) FROM school");
        Assertions.assertEquals("5", column(result, "MAX(year)"));
    }

    @Test
    void min() throws IOException, ParseFailure {
        result = database.execute("SELECT MIN(year) FROM school");
        Assertions.assertEquals("1", column(result, "MIN(year)"));
    }

    @Test
    void avg() throws IOException, ParseFailure {
        result = database.execute("SELECT AVG(year) FROM school");
        Assertions.assertEquals("3", column(result, "AVG(year)"));
    }

    @Test
    void multipleAggregate() throws IOException, ParseFailure {
        result = database.execute("SELECT MAX(year), MIN(year), AVG(year) FROM school");
        Assertions.assertEquals("5", column(result, "MAX(year)"));
        Assertions.assertEquals("1", column(result, "MIN(year)"));
        Assertions.assertEquals("3", column(result, "AVG(year)"));
    }

    @Test
    void aggregateShouldNotBeUsedWithNormalColumn() throws IOException, ParseFailure {
        Assertions.assertThrows(ParseFailure.class, () -> database.execute("SELECT MAX(year), year FROM school"));
        Assertions.assertThrows(ParseFailure.class, () -> database.execute("SELECT year, MIN(year) FROM school"));
    }
}
