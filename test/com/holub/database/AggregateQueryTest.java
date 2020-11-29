package com.holub.database;

import com.holub.text.ParseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

public class AggregateQueryTest {
    static Database database;
    Table result;

    @BeforeAll
    static void setDatabase() throws IOException, ParseFailure {
        database = new Database("");
        database.createTable("school", Collections.singletonList("year"));
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
    @DisplayName("Aggregate MAX 기능을 지원해야한다")
    void max() throws IOException, ParseFailure {
        result = database.execute("SELECT MAX(year) FROM school");
        Assertions.assertEquals("5", column(result, "MAX(year)"));
    }

    @Test
    @DisplayName("Aggregate MIN 기능을 지원해야한다")
    void min() throws IOException, ParseFailure {
        result = database.execute("SELECT MIN(year) FROM school");
        Assertions.assertEquals("1", column(result, "MIN(year)"));
    }

    @Test
    @DisplayName("Aggregate AVG 기능을 지원해야한다")
    void avg() throws IOException, ParseFailure {
        result = database.execute("SELECT AVG(year) FROM school");
        Assertions.assertEquals("3", column(result, "AVG(year)"));
    }

    @Test
    @DisplayName("여러 개의 Aggregate column이 주어진 경우에도 지원해야한다")
    void multipleAggregate() throws IOException, ParseFailure {
        result = database.execute("SELECT MAX(year), MIN(year), AVG(year) FROM school");
        Assertions.assertEquals("5", column(result, "MAX(year)"));
        Assertions.assertEquals("1", column(result, "MIN(year)"));
        Assertions.assertEquals("3", column(result, "AVG(year)"));
    }

    @Test
    void aggregateShouldNotBeUsedWithNormalColumn(){
        Assertions.assertThrows(ParseFailure.class, () -> database.execute("SELECT MAX(year), year FROM school"));
        Assertions.assertThrows(ParseFailure.class, () -> database.execute("SELECT year, MIN(year) FROM school"));
    }
}
