package com.holub.database;

import com.holub.text.ParseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DistinctQueryTest {
    static Database database;
    Table result;

    @BeforeAll
    static void setDatabase() throws IOException, ParseFailure {
        database = new Database("");
        database.createTable("school", Arrays.asList("name", "year", "address"));
        String[] names = new String[]{"CAU", "SNU"};
        String[] years = new String[]{"1", "3"};
        String[] addresses = new String[]{"seoul", "seoul"};
        for (int i = 0; i < 2; i++) {
            database.execute(String.format("INSERT INTO school VALUES(\"%s\", \"%s\", \"%s\")", names[i], years[i],
                    addresses[i]));
        }
        database.createTable("person", Arrays.asList("name", "age", "schoolName"));

        names = new String[]{"A", "B", "C", "D"};
        String[] ages = new String[]{"3", "3", "3", "5"};
        String[] schoolNames = new String[]{"CAU", "CAU", "SNU", "CAU"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO person VALUES(\"%s\", \"%s\",\"%s\")", names[i], ages[i],
                    schoolNames[i]));
        }
    }

    /**
     * 테이블이 중복된 항을 가지고 있는지 검사해서 반환
     *
     * @param result 검사하고자 하는 테이블
     * @return 중복이 존재하면 true
     */
    private Boolean verifyDuplicateTermExist(Table result) {
        List<Map<String, String>> tableMap = TableHelper.tableToMapList(result);

        return !tableMap.stream().distinct().collect(Collectors.toList()).equals(tableMap);
    }

    @Test
    @DisplayName("DISTINCT 키워드를 사용하면 중복된 항을 제거해야만 한다")
    void distinctTestWithOutJoin() throws IOException, ParseFailure {
        result = database.execute("SELECT age, schoolName FROM person");
        Assertions.assertTrue(verifyDuplicateTermExist(result));


        result = database.execute("SELECT DISTINCT age, schoolName FROM person");
        Assertions.assertFalse(verifyDuplicateTermExist(result));
    }

    @Test
    @DisplayName("DISTINCT 키워드를 JOIN과 함께 사용해도 작동해야 한다")
    void distinctTestWithJoin() throws IOException, ParseFailure {
        result = database.execute("SELECT age, schoolName, address FROM person, school WHERE person.schoolName " +
                "= " +
                "school.name");
        Assertions.assertTrue(verifyDuplicateTermExist(result));


        result = database.execute("SELECT DISTINCT age, schoolName, address FROM person, school WHERE person" +
                ".schoolName = " +
                "school.name");
        Assertions.assertFalse(verifyDuplicateTermExist(result));
    }

}
