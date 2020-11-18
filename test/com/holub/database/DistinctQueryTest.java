package com.holub.database;

import com.holub.text.ParseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class DistinctQueryTest {
    Database database;

    @BeforeEach
    void setUp() throws IOException, ParseFailure {
        database = new Database("");
        database.execute("CREATE TABLE school(" +
                "name VARCHAR NOT NULL, " +
                "year INT NOT NULL, " +
                "address VARCHAR NOT NULL, " +
                "PRIMARY KEY(name) " +
                ")");
        String[] names = new String[]{"CAU", "SNU"};
        String[] years = new String[]{"1", "3"};
        String[] addresses = new String[]{"seoul", "seoul"};
        for (int i = 0; i < 2; i++) {
            database.execute(String.format("INSERT INTO school VALUES(\"%s\", \"%s\", \"%s\")", names[i], years[i],
                    addresses[i]));
        }

        database.execute("CREATE TABLE person(" +
                "name VARCHAR NOT NULL, " +
                "age INT NOT NULL, " +
                "schoolName INT NOT NULL, " +
                "PRIMARY KEY(name) " +
                ")");

        names = new String[]{"A", "B", "C", "D"};
        String[] ages = new String[]{"3", "3", "3", "5"};
        String[] schoolNames = new String[]{"CAU", "CAU", "SNU", "CAU"};
        for (int i = 0; i < 4; i++) {
            database.execute(String.format("INSERT INTO person VALUES(\"%s\", \"%s\",\"%s\")", names[i], ages[i],
                    schoolNames[i]));
        }
    }

    // 테이블이 중복을 가지고 있을 때 판단 가능
    // 테이블이 원래부터 중복이 없는 경우 이 함수가 false를 반환한다고 해도
    // DISTINCT에 의해 걸러진 것인지, 원래부터 중복이 없었는지 판단 불가능
    private Boolean hasTableDistinctRow(Table result) {
        HashSet<String> hashSet = new HashSet<>();

        Cursor c = result.rows();
        int rowCount = 0;
        while (c.advance()) {
            rowCount++;
            StringBuilder sb = new StringBuilder();
            Iterator it = c.columns();
            while (it.hasNext()) {
                sb.append(it.next().toString());
            }
            hashSet.add(sb.toString());
        }

        return hashSet.size() != rowCount;
    }

    @Test
    void distinctTestWithOutJoin() throws IOException, ParseFailure {
        Table result;
        result = database.execute("SELECT age, schoolName FROM person");
        Assertions.assertTrue(hasTableDistinctRow(result));


        result = database.execute("SELECT DISTINCT age, schoolName FROM person");
        Assertions.assertTrue(hasTableDistinctRow(result) == false);
    }

    @Test
    void distinctTestWithJoin() throws IOException, ParseFailure {
        Table result;
        result = database.execute("SELECT age, schoolName, address FROM person, school WHERE person.schoolName " +
                "= " +
                "school.name");
        Assertions.assertTrue(hasTableDistinctRow(result));


        result = database.execute("SELECT DISTINCT age, schoolName, address FROM person, school WHERE person" +
                ".schoolName = " +
                "school.name");
        Assertions.assertTrue(hasTableDistinctRow(result) == false);
    }

}
