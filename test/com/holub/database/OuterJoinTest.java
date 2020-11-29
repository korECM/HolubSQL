package com.holub.database;

import com.holub.text.ParseFailure;
import com.holub.util.MapListHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

public class OuterJoinTest {

    static Database database;
    Table result;
    static MapListHelper mapListHelper;

    @BeforeAll
    static void setDatabase() throws IOException, ParseFailure {
        mapListHelper = new MapListHelper();

        database = new Database("");
        database.createTable("test1", Arrays.asList("name", "b"));
        String[] names = new String[]{"1", "2", "3", "4"};
        String[] b = new String[]{"1", "3", "2", "5"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO test1 VALUES(\"%s\", \"%s\")", names[i], b[i]));
        }

        database.createTable("test2", Arrays.asList("a", "d"));
        names = new String[]{"1", "2", "3", "4"};
        b = new String[]{"b-1", "b-2", "b-3", "b-4"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO test2 VALUES(\"%s\", \"%s\")", names[i], b[i]));
        }

    }

    @AfterEach
    void clear(){
        mapListHelper.clear();
    }

    @Test
    void leftOuterJoinTest() throws IOException, ParseFailure {
        mapListHelper.setColumnName("name", "b", "a", "d");
        mapListHelper.addRow("1", "1", "1", "b-1");
        mapListHelper.addRow("2", "3", "3", "b-3");
        mapListHelper.addRow("3", "2", "2", "b-2");
        mapListHelper.addRow("4", "5", "null", "null");

        result = database.execute("SELECT * FROM test1 LEFT OUTER JOIN test2 ON test1.b = test2.a");

        Assertions.assertTrue(mapListHelper.verify(result));
        mapListHelper.clear();

        mapListHelper.setColumnName("name", "a");
        mapListHelper.addRow("1", "1");
        mapListHelper.addRow("2", "3");
        mapListHelper.addRow("3", "2");
        mapListHelper.addRow("4", "null");

        result = database.execute("SELECT name, a FROM test1 LEFT OUTER JOIN test2 ON test1.b = test2.a");
        Assertions.assertTrue(mapListHelper.verify(result));
    }

    @Test
    void rightOuterJoinTest() throws IOException, ParseFailure {
        mapListHelper.setColumnName("name", "b", "a", "d");
        mapListHelper.addRow("1", "1", "1", "b-1");
        mapListHelper.addRow("2", "3", "3", "b-3");
        mapListHelper.addRow("3", "2", "2", "b-2");
        mapListHelper.addRow("null", "null", "4", "b-4");

        result = database.execute("SELECT * FROM test1 RIGHT OUTER JOIN test2 ON test1.b = test2.a");
        Assertions.assertTrue(mapListHelper.verify(result));
        mapListHelper.clear();

        mapListHelper.setColumnName("name", "a");
        mapListHelper.addRow("1", "1");
        mapListHelper.addRow("2", "3");
        mapListHelper.addRow("3", "2");
        mapListHelper.addRow("null", "4");

        result = database.execute("SELECT name, a FROM test1 RIGHT OUTER JOIN test2 ON test1.b = test2.a");
        Assertions.assertTrue(mapListHelper.verify(result));
    }

    @Test
    void tempTableShoutNotLeftInDatabase() throws IOException, ParseFailure {
        database.execute("SELECT * FROM test1 RIGHT OUTER JOIN test2 ON test1.b = test2.a");

        Assertions.assertThrows(RuntimeException.class, () -> database.execute("SELECT * FROM test1RIGHTOUTERJOINtest2"));

    }
}
