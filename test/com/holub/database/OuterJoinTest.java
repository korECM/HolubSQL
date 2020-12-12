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
        database.createTable("user", Arrays.asList("id", "name", "university"));
        database.execute("INSERT INTO user VALUES('1', 'jack', 'cau')");
        database.execute("INSERT INTO user VALUES('2', 'jason', 'snu')");
        database.execute("INSERT INTO user VALUES('3', 'Alex', 'cau')");
        database.execute("INSERT INTO user VALUES('4', 'Alice', 'hyu')");
        database.execute("INSERT INTO user VALUES('5', 'Aaron', 'kaist')");

        database.createTable("university", Arrays.asList("university", "score"));
        database.execute("INSERT INTO university VALUES('cau', '100')");
        database.execute("INSERT INTO university VALUES('snu', '20')");
        database.execute("INSERT INTO university VALUES('su', '10')");

        database.createTable("project", Arrays.asList("userId", "projectScore"));
        database.execute("INSERT INTO project VALUES('1', '5')");
        database.execute("INSERT INTO project VALUES('3', '3')");
        database.execute("INSERT INTO project VALUES('4', '2')");

    }

    @AfterEach
    void clear(){
        mapListHelper.clear();
    }

    @Test
    void leftOuterJoinTest() throws IOException, ParseFailure {
        mapListHelper.setColumnName("id", "name", "university", "score");
        mapListHelper.addRow("1", "jack", "cau", "100");
        mapListHelper.addRow("2", "jason", "snu", "20");
        mapListHelper.addRow("3", "Alex", "cau", "100");
        mapListHelper.addRow("4", "Alice", "hyu", "null");
        mapListHelper.addRow("5", "Aaron", "kaist", "null");

        result = database.execute("SELECT * FROM user LEFT OUTER JOIN university ON user.university = university.university");

        Assertions.assertTrue(mapListHelper.verify(result));
        mapListHelper.clear();

        mapListHelper.setColumnName("name", "score");
        mapListHelper.addRow("jack", "100");
        mapListHelper.addRow("jason", "20");
        mapListHelper.addRow("Alex", "100");
        mapListHelper.addRow("Alice", "null");
        mapListHelper.addRow("Aaron", "null");

        result = database.execute("SELECT name, score FROM user LEFT OUTER JOIN university ON user.university = university.university");
        Assertions.assertTrue(mapListHelper.verify(result));
    }

    @Test
    void rightOuterJoinTest() throws IOException, ParseFailure {
        mapListHelper.setColumnName("id", "name", "university", "score");
        mapListHelper.addRow("1", "jack", "cau", "100");
        mapListHelper.addRow("2", "jason", "snu", "20");
        mapListHelper.addRow("3", "Alex", "cau", "100");
        mapListHelper.addRow("null", "null", "su", "10");

        result = database.execute("SELECT * FROM user RIGHT OUTER JOIN university ON user.university = university.university");
        Assertions.assertTrue(mapListHelper.verify(result));
        mapListHelper.clear();

        mapListHelper.setColumnName("name", "score");
        mapListHelper.addRow("jack", "100");
        mapListHelper.addRow("jason", "20");
        mapListHelper.addRow("Alex", "100");
        mapListHelper.addRow("null", "10");

        result = database.execute("SELECT name, score FROM user RIGHT OUTER JOIN university ON user.university = university.university");
        Assertions.assertTrue(mapListHelper.verify(result));
    }

    @Test
    void multipleOuterJoin() throws IOException, ParseFailure{
        mapListHelper.setColumnName("id", "name", "university", "score", "userId", "projectScore");
        mapListHelper.addRow("1", "jack", "cau", "100", "1", "5");
        mapListHelper.addRow("1", "jack", "cau", "100", "3", "3");
        mapListHelper.addRow("1", "jack", "cau", "100", "4", "2");
        mapListHelper.addRow("1", "jack", "cau", "100", "null", "null");
        mapListHelper.addRow("1", "jack", "cau", "100", "null", "null");
        mapListHelper.addRow("2", "jason", "snu", "20", "1", "5");
        mapListHelper.addRow("2", "jason", "snu", "20", "3", "3");
        mapListHelper.addRow("2", "jason", "snu", "20", "4", "2");
        mapListHelper.addRow("2", "jason", "snu", "20", "null", "null");
        mapListHelper.addRow("2", "jason", "snu", "20", "null", "null");
        mapListHelper.addRow("3", "Alex", "cau", "100", "1", "5");
        mapListHelper.addRow("3", "Alex", "cau", "100", "3", "3");
        mapListHelper.addRow("3", "Alex", "cau", "100", "4", "2");
        mapListHelper.addRow("3", "Alex", "cau", "100", "null", "null");
        mapListHelper.addRow("3", "Alex", "cau", "100", "null", "null");
        mapListHelper.addRow("4", "Alice", "hyu", "null", "1", "5");
        mapListHelper.addRow("4", "Alice", "hyu", "null", "3", "3");
        mapListHelper.addRow("4", "Alice", "hyu", "null", "4", "2");
        mapListHelper.addRow("4", "Alice", "hyu", "null", "null", "null");
        mapListHelper.addRow("4", "Alice", "hyu", "null", "null", "null");
        mapListHelper.addRow("5", "Aaron", "kaist", "null", "1", "5");
        mapListHelper.addRow("5", "Aaron", "kaist", "null", "3", "3");
        mapListHelper.addRow("5", "Aaron", "kaist", "null", "4", "2");
        mapListHelper.addRow("5", "Aaron", "kaist", "null", "null", "null");
        mapListHelper.addRow("5", "Aaron", "kaist", "null", "null", "null");
        result = database.execute("SELECT * FROM user LEFT OUTER JOIN university ON user.university = university.university LEFT OUTER JOIN project ON user.id = project.userId");
        Assertions.assertTrue(mapListHelper.verify(result));
    }

    @Test
    void multipleOuterJoinWithWHere() throws IOException, ParseFailure {
        mapListHelper.setColumnName("id", "name", "university", "score", "userId", "projectScore");
        mapListHelper.addRow("1", "jack", "cau", "100", "1", "5");
        mapListHelper.addRow("1", "jack", "cau", "100", "3", "3");
        mapListHelper.addRow("1", "jack", "cau", "100", "4", "2");
        mapListHelper.addRow("1", "jack", "cau", "100", "null", "null");
        mapListHelper.addRow("1", "jack", "cau", "100", "null", "null");
        mapListHelper.addRow("3", "Alex", "cau", "100", "1", "5");
        mapListHelper.addRow("3", "Alex", "cau", "100", "3", "3");
        mapListHelper.addRow("3", "Alex", "cau", "100", "4", "2");
        mapListHelper.addRow("3", "Alex", "cau", "100", "null", "null");
        mapListHelper.addRow("3", "Alex", "cau", "100", "null", "null");
        result = database.execute("SELECT * FROM user LEFT OUTER JOIN university ON user.university = university.university LEFT OUTER JOIN project ON user.id = project.userId WHERE university = 'cau'");
        Assertions.assertTrue(mapListHelper.verify(result));
    }

    @Test
    void tempTableShoutNotLeftInDatabase() throws IOException, ParseFailure {
        database.execute("SELECT * FROM user RIGHT OUTER JOIN university ON user.university = university.university");

        Assertions.assertThrows(RuntimeException.class, () -> database.execute("SELECT * FROM userRIGHTOUTERJOINuniversity"));

    }
}
