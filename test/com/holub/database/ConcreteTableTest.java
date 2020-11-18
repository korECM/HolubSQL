package com.holub.database;

import com.holub.text.ParseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class ConcreteTableTest {

    Table name;
    Table address;

    @BeforeEach
    void setUp() {
        name = TableFactory.create(
                "name", new String[]{"name", "addrId"});
        address = TableFactory.create(
                "address", new String[]{"addrId", "street", "city"});

        name.insert(new Object[]{"A", "1"});
        name.insert(new Object[]{"B", "2"});
        name.insert(new String[]{"addrId", "name"}, new Object[]{"2", "C"});

        List l = new ArrayList();
        l.add("1");
        l.add("1 Seoul Street");
        l.add("Seoul");
        address.insert(l);

        l.clear();
        l.add("2");
        l.add("123 Seoul Street");
        l.add("Seoul");
        address.insert(l);

        l.clear();
        l.add("3");
        l.add("10 Busan Street");
        l.add("Busan");
        address.insert(l);
    }

    @Test
    void joinWithAsteriskUnitTest() {
        // Parser가 SELECT 뒤에 idList를 처리할 때
        // *로 시작하는 경우 요청된 comlumn을 null로 넘겨줌으로써
        // ALl Select를 처리한다

        List<String[]> expectedRows = new ArrayList<String[]>();
        expectedRows.add(new String[]{"1", "1 Seoul Street", "Seoul", "A"});
        expectedRows.add(new String[]{"2", "123 Seoul Street", "Seoul", "B"});
        expectedRows.add(new String[]{"2", "123 Seoul Street", "Seoul", "C"});

        Table result = address.select(new Selector.Adapter() {
            @Override
            public boolean approve(Cursor[] tables) {
                return tables[0].column("addrId").equals(tables[1].column("addrId"));
            }
        }, null, new Table[]{name});

        Cursor c = result.rows();
        int index = 0;
        while (c.advance()) {
            Iterator it = c.columns();
            while (it.hasNext()) {
                String value = it.next().toString();
                Assertions.assertTrue(Arrays.stream(expectedRows.get(index)).anyMatch(e -> e.equals(value)));
            }
            index++;
        }
    }

    @Test
    void joinWithAsteriskSQLTest() throws IOException, ParseFailure {
        Database database = new Database(new File(""), new Table[]{name, address});

        List<String[]> expectedRows = new ArrayList<String[]>();
        expectedRows.add(new String[]{"1", "1 Seoul Street", "Seoul", "A"});
        expectedRows.add(new String[]{"2", "123 Seoul Street", "Seoul", "B"});
        expectedRows.add(new String[]{"2", "123 Seoul Street", "Seoul", "C"});

        Table result = database.execute("select * from address, name where address.addrId = name.addrId");

        Cursor c = result.rows();
        int index = 0;
        while (c.advance()) {
            Iterator it = c.columns();
            while (it.hasNext()) {
                String value = it.next().toString();
                Assertions.assertTrue(Arrays.stream(expectedRows.get(index)).anyMatch(e -> e.equals(value)));
            }
            index++;
        }
    }

    @Test
    void joinWithColumnNameUnitTest() {
        List<String> expectedRows = new ArrayList<String>();
        expectedRows.add("1 Seoul Street");
        expectedRows.add("123 Seoul Street");

        Table result = address.select(new Selector.Adapter() {
            @Override
            public boolean approve(Cursor[] tables) {
                return tables[0].column("addrId").equals(tables[1].column("addrId"));
            }
        }, new String[]{"street"}, new Table[]{name});

        Cursor c = result.rows();
        while (c.advance()) {
            Iterator it = c.columns();
            while (it.hasNext()) {
                Assertions.assertTrue(expectedRows.contains(it.next()));
            }
        }
    }

    @Test
    void joinWithColumnNameSQLTest() throws IOException, ParseFailure {
        Database database = new Database(new File(""), new Table[]{name, address});

        List<String> expectedRows = new ArrayList<String>();
        expectedRows.add("1 Seoul Street");
        expectedRows.add("123 Seoul Street");

        Table result = database.execute("select street from address, name where address.addrId = name.addrId");

        Cursor c = result.rows();
        while (c.advance()) {
            Iterator it = c.columns();
            while (it.hasNext()) {
                Assertions.assertTrue(expectedRows.contains(it.next()));
            }
        }
    }
}