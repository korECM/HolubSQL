package com.holub.database;

import com.holub.text.ParseFailure;
import com.holub.util.MapListHelper;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrderByQueryTest {

    static Database database;
    Table result;
    static MapListHelper mapListHelper;

    @BeforeAll
    static void setDatabase() throws IOException, ParseFailure {
        mapListHelper = new MapListHelper();
        database = new Database("");
        database.createTable("school", Arrays.asList("name", "year", "isGood"));
        String[] names = new String[]{"CAU", "SNU", "HYU", "SU"};
        String[] years = new String[]{"1", "3", "3", "2"};
        String[] isGoods = new String[]{"true", "false", "false", "false"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO school VALUES(\"%s\", \"%s\", \"%s\")", names[i], years[i],
                    isGoods[i]));
        }

        database.createTable("person", Arrays.asList("name", "age", "schoolName"));
        names = new String[]{"A", "B", "C", "D"};
        String[] ages = new String[]{"3", "3", "3", "5"};
        String[] schoolNames = new String[]{"CAU", "HYU", "SNU", "CAU"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO person VALUES(\"%s\", \"%s\",\"%s\")", names[i], ages[i],
                    schoolNames[i]));
        }
    }

    @AfterEach
    void clear(){
        mapListHelper.clear();
    }

    private boolean isSorted(Cursor c, String columnName, boolean isAsc) {
        List<String> data = new ArrayList<>();
        while (c.advance()) {
            data.add(c.column(columnName).toString());
        }
        List<String> sortedList = data.stream().sorted().collect(Collectors.toList());
        if (!isAsc) Collections.reverse(sortedList);
        return sortedList.equals(data);
    }

    private boolean doesColumnExist(Cursor c, String columnName) {
        for (int i = 0; i < c.columnCount(); i++) {
            if (c.columnName(i).equals(columnName)) return true;
        }
        return false;
    }

    @Test
    @DisplayName("column 1개에 대해 정렬 테스트")
    void orderByOneColumn() throws IOException, ParseFailure {
        result = database.execute("SELECT name, year FROM school ORDER BY year");
        Assertions.assertTrue(isSorted(result.rows(), "year", true));

        result = database.execute("SELECT name, year FROM school ORDER BY year DESC");
        Assertions.assertTrue(isSorted(result.rows(), "year", false));
    }

    @Test
    @DisplayName("column 여러개에 대한 정렬 테스트")
    void orderByMultipleColumn() throws IOException, ParseFailure {
        mapListHelper.setColumnName("name", "year");
        mapListHelper.addRow("CAU", "1");
        mapListHelper.addRow("SU", "2");
        mapListHelper.addRow("HYU", "3");
        mapListHelper.addRow("SNU", "3");

        result = database.execute("SELECT name, year FROM school ORDER BY year, name");

        Assertions.assertTrue(mapListHelper.verify(result));

    }

    @Test
    @DisplayName("여러개의 column에 대해 방향이 다르게 주어질 때 정렬 테스트")
    void orderByMultipleColumnWithMultipleDirection() throws IOException, ParseFailure {
        mapListHelper.setColumnName("name", "year");
        mapListHelper.addRow("HYU", "3");
        mapListHelper.addRow("SNU", "3");
        mapListHelper.addRow("SU", "2");
        mapListHelper.addRow("CAU", "1");

        result = database.execute("SELECT name, year FROM school ORDER BY year DESC, name ASC");

        Assertions.assertTrue(mapListHelper.verify(result));

    }

    @Test
    @DisplayName("ORDER BY에 주어진 column이 SELECT에 없는 경우 결과 테이블에 이 column이 존재해서는 안된다")
    void orderByColumnWhichIsNotExistInSelect() throws IOException, ParseFailure {
        mapListHelper.setColumnName("name", "year");
        mapListHelper.addRow("SNU", "3");
        mapListHelper.addRow("HYU", "3");
        mapListHelper.addRow("SU", "2");
        mapListHelper.addRow("CAU", "1");

        result = database.execute("SELECT name, year FROM school ORDER BY isGood");

        Assertions.assertTrue(mapListHelper.verify(result));
        Assertions.assertFalse(doesColumnExist(result.rows(), "isGood"));

        mapListHelper.clear();
        mapListHelper.setColumnName("name", "year");
        mapListHelper.addRow("CAU", "1");
        mapListHelper.addRow("SNU", "3");
        mapListHelper.addRow("HYU", "3");
        mapListHelper.addRow("SU", "2");

        result = database.execute("SELECT name, year FROM school ORDER BY isGood DESC");

        Assertions.assertTrue(mapListHelper.verify(result));
        Assertions.assertFalse(doesColumnExist(result.rows(), "isGood"));

    }

    @Test
    @DisplayName("SELECT *와 ORDER BY를 함께 사용할 때도 정렬이 정상적으로 되어야한다")
    void orderWithSelectAsterisk() throws IOException, ParseFailure {
        result = database.execute("SELECT * FROM school ORDER BY year DESC");
        System.out.println(result.toString());
        Assertions.assertTrue(isSorted(result.rows(), "year", false));
    }

    @Test
    @DisplayName("JOIN과 ORDER BY를 함께 사용할 때도 정렬이 되어야 한다")
    void orderWithJoin() throws IOException, ParseFailure {
        result = database.execute("SELECT * FROM person, school WHERE person.schoolName=school.name ORDER BY name " +
                "DESC");
        Assertions.assertTrue(isSorted(result.rows(), "name", false));
    }

    @Test
    void orderByWithOutColumnName() {
        Assertions.assertThrows(ParseFailure.class, () -> database.execute("SELECT name, year FROM school ORDER BY"));
    }
}
