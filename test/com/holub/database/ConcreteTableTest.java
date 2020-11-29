package com.holub.database;

import com.holub.text.ParseFailure;
import com.holub.util.MapListHelper;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

class ConcreteTableTest {

    static Database database;
    static Table name;
    static Table address;
    static MapListHelper mapListHelper;

    @BeforeAll
    static void setDatabase() throws IOException {
        mapListHelper = new MapListHelper();
        name = TableFactory.create(
                "name", new String[]{"name", "addrId"});
        address = TableFactory.create(
                "address", new String[]{"addrId", "street", "city"});

        name.insert(Arrays.asList("A", "1"));
        name.insert(Arrays.asList("B", "2"));
        name.insert(Arrays.asList("C", "2"));

        address.insert(Arrays.asList("1", "1 Seoul Street", "Seoul"));
        address.insert(Arrays.asList("2", "123 Seoul Street", "Seoul"));
        address.insert(Arrays.asList("3", "10 Busan Street", "Busan"));

        database = new Database(new File(""), new Table[]{name, address});
    }

    @AfterEach
    void clear(){
        mapListHelper.clear();
    }

    @Test
    @DisplayName("SELECT에 *이 주어지는 경우를 table에 직접 실행했을 때 JOIN 결과를 반환해야한다.")
    void joinWithAsteriskUnitTest() {
        // Parser가 SELECT 뒤에 idList를 처리할 때
        // *로 시작하는 경우 요청된 comlumn을 null로 넘겨줌으로써
        // ALl Select를 처리한다

        mapListHelper.setColumnName("addrId", "street", "city", "name");

        mapListHelper.addRow("1", "1 Seoul Street", "Seoul", "A");
        mapListHelper.addRow("2", "123 Seoul Street", "Seoul", "B");
        mapListHelper.addRow("2", "123 Seoul Street", "Seoul", "C");

        Table result = address.select(new Selector.Adapter() {
            @Override
            public boolean approve(Cursor[] tables) {
                return tables[0].column("addrId").equals(tables[1].column("addrId"));
            }
        }, null, new Table[]{name});

        Assertions.assertTrue(mapListHelper.verify(result));
    }

    @Test
    @DisplayName("SELECT *을 SQL로 직접 실행했을 때 JOIN 결과를 반환해야한다.")
    void joinWithAsteriskSQLTest() throws IOException, ParseFailure {
        mapListHelper.setColumnName("addrId", "street", "city", "name");
        mapListHelper.addRow("1", "1 Seoul Street", "Seoul", "A");
        mapListHelper.addRow("2", "123 Seoul Street", "Seoul", "B");
        mapListHelper.addRow("2", "123 Seoul Street", "Seoul", "C");

        Table result = database.execute("select * from address, name where address.addrId = name.addrId");

        Assertions.assertTrue(mapListHelper.verify(result));

    }

    @Test
    @DisplayName("SELECT에 column이 주어지는 경우를 table에 직접 실행했을 때 JOIN 결과를 반환해야한다.")
    void joinWithColumnNameUnitTest() {
        mapListHelper.setColumnName("street");
        mapListHelper.addRow("1 Seoul Street");
        mapListHelper.addRow("123 Seoul Street");
        mapListHelper.addRow("123 Seoul Street");

        Table result = address.select(new Selector.Adapter() {
            @Override
            public boolean approve(Cursor[] tables) {
                return tables[0].column("addrId").equals(tables[1].column("addrId"));
            }
        }, new String[]{"street"}, new Table[]{name});

        Assertions.assertTrue(mapListHelper.verify(result));
    }

    @Test
    @DisplayName("SELECT에 column이 주어지는 경우를 SQL로 실행했을 때 정상적으로 JOIN 결과를 반환해야한다.")
    void joinWithColumnNameSQLTest() throws IOException, ParseFailure {

        mapListHelper.setColumnName("street");
        mapListHelper.addRow("1 Seoul Street");
        mapListHelper.addRow("123 Seoul Street");
        mapListHelper.addRow("123 Seoul Street");

        Table result = database.execute("select street from address, name where address.addrId = name.addrId");

        Assertions.assertTrue(mapListHelper.verify(result));
    }
}